package com.bodegaweb.catalogo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Recorrido de humo del feature/catalogo: categoría jerárquica -> producto -> inventario (sub-recurso).
 */
@SpringBootTest
class CatalogoFlowTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private MockMvc mvc() {
        if (mvc == null) {
            mvc = MockMvcBuilders.webAppContextSetup(context).build();
        }
        return mvc;
    }

    @Test
    void flujoCompletoCatalogo() throws Exception {
        // 1. Categoría raíz
        String raizBody = mvc().perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Electrónica"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.slug", is("electronica")))
                .andReturn().getResponse().getContentAsString();
        long raizId = idDe(raizBody);

        // 2. Subcategoría
        mvc().perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Laptops", "categoriaPadreId": %d}
                                """.formatted(raizId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoriaPadreId", is((int) raizId)));

        // 3. Árbol
        mvc().perform(get("/api/categorias/arbol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].subcategorias[0].nombre", is("Laptops")));

        // 3b. Listado sin padreId: solo raíces (no mezcla la subcategoría)
        mvc().perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].nombre", is("Electrónica")));

        // 3c. Listado con padreId: solo las hijas directas de esa categoría
        mvc().perform(get("/api/categorias").param("padreId", String.valueOf(raizId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].nombre", is("Laptops")));

        // 4. Producto con inventario inicial
        String prodBody = mvc().perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sku": "LAP-001",
                                  "nombre": "Laptop Pro 14",
                                  "precio": 4999.90,
                                  "categoriaId": %d,
                                  "inventarioInicial": {"stockDisponible": 10, "stockReservado": 0}
                                }
                                """.formatted(raizId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.inventario.stockDisponible", is(10)))
                .andReturn().getResponse().getContentAsString();
        long prodId = idDe(prodBody);

        // 5. Inventario: PUT absoluto
        mvc().perform(put("/api/productos/{id}/inventario", prodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"stockDisponible": 25, "stockReservado": 3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stockTotal", is(28)));

        // 6. Inventario: PATCH ajuste relativo negativo válido
        mvc().perform(patch("/api/productos/{id}/inventario/ajuste", prodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "DISPONIBLE", "delta": -5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stockDisponible", is(20)));

        // 7. Ajuste que dejaría stock negativo -> 422
        mvc().perform(patch("/api/productos/{id}/inventario/ajuste", prodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "RESERVADO", "delta": -100}
                                """))
                .andExpect(status().isUnprocessableEntity());

        // 7b. Pedir una pagina enorme queda topada por spring.data.web.pageable.max-page-size
        mvc().perform(get("/api/productos").param("size", "99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size", is(100)));

        // 8. Listado paginado con filtro
        mvc().perform(get("/api/productos").param("q", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(1)));

        // 9. Baja lógica
        mvc().perform(patch("/api/productos/{id}/estado", prodId).param("activo", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.activo", is(false)));

        // 10. SKU duplicado -> 409
        mvc().perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "LAP-001", "nombre": "Otro", "precio": 1.0}
                                """))
                .andExpect(status().isConflict());

        // 11. Borrado
        mvc().perform(delete("/api/productos/{id}", prodId))
                .andExpect(status().isNoContent());
        mvc().perform(get("/api/productos/{id}", prodId))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarCategoriaDesvinculaProductosAsociados() throws Exception {
        String catBody = mvc().perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Accesorios"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long catId = idDe(catBody);

        String prodBody = mvc().perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "ACC-001", "nombre": "Mouse inalámbrico", "precio": 49.90, "categoriaId": %d}
                                """.formatted(catId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long prodId = idDe(prodBody);

        // Antes del fix: esto fallaba con 409 (violación de integridad) porque
        // ddl-auto=update no genera ON DELETE SET NULL para productos.categoria_id.
        mvc().perform(delete("/api/categorias/{id}", catId))
                .andExpect(status().isNoContent());

        mvc().perform(get("/api/productos/{id}", prodId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoriaId", nullValue()));
    }

    @Test
    void ajusteStockConTipoInvalidoRetorna400NoInternalError() throws Exception {
        String prodBody = mvc().perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "BAD-001", "nombre": "Producto de prueba", "precio": 10.0}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long prodId = idDe(prodBody);

        // Antes del fix: un enum inválido en el body burbujeaba como 500.
        mvc().perform(patch("/api/productos/{id}/inventario/ajuste", prodId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "NO_EXISTE", "delta": 1}
                                """))
                .andExpect(status().isBadRequest());
    }

    private long idDe(String responseBody) {
        Matcher m = ID_PATTERN.matcher(responseBody);
        if (!m.find()) {
            throw new IllegalStateException("No se encontró 'id' en la respuesta: " + responseBody);
        }
        return Long.parseLong(m.group(1));
    }
}
