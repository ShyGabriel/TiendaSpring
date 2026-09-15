package com.bodegaweb.carrito;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Recorrido de humo del módulo carrito/pedidos: crea el carrito con un producto
 * de catálogo (precio real), agrega items, descuenta el carrito al crear un pedido.
 */
@SpringBootTest
class CarritoFlowTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    private static final long USUARIO_ID = 1L;

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
    void flujoCarritoYPedidoConPrecioRealDelProducto() throws Exception {
        // 1. Producto de catálogo (sin categoría) con precio real
        String prodBody = mvc().perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "CR-001", "nombre": "Cable HDMI", "precio": 49.90}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long prodId = idDe(prodBody);

        // 2. El carrito se crea vacío al consultarlo
        mvc().perform(get("/api/carritos/usuario/{usuarioId}", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId", is((int) USUARIO_ID)))
                .andExpect(jsonPath("$.items", hasSize(0)));

        // 3. Se agrega un item x2 y usa el precio real del producto (no 0)
        mvc().perform(post("/api/carritos/usuario/{usuarioId}/items", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"productoId": %d, "cantidad": 2}
                                """.formatted(prodId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productoId", is((int) prodId)))
                .andExpect(jsonPath("$.items[0].cantidad", is(2)))
                .andExpect(jsonPath("$.items[0].precioUnitario").value(closeTo(49.90, 0.001)))
                .andExpect(jsonPath("$.items[0].subtotal").value(closeTo(99.80, 0.001)))
                .andExpect(jsonPath("$.total").value(closeTo(99.80, 0.001)));

        // 4. Se crea el pedido desde el carrito (PENDIENTE y total correcto)
        String pedidoBody = mvc().perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"usuarioId": %d, "direccionId": 1}
                                """.formatted(USUARIO_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId", is((int) USUARIO_ID)))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.total").value(closeTo(99.80, 0.001)))
                .andReturn().getResponse().getContentAsString();
        long pedidoId = idDe(pedidoBody);

        // 5. El pedido quedó guardado y el carrito quedó vacío
        mvc().perform(get("/api/pedidos/{id}", pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) pedidoId)));

        mvc().perform(get("/api/carritos/usuario/{usuarioId}", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    private long idDe(String responseBody) {
        Matcher m = ID_PATTERN.matcher(responseBody);
        if (!m.find()) {
            throw new AssertionError("No se encontró un id en: " + responseBody);
        }
        return Long.parseLong(m.group(1));
    }
}