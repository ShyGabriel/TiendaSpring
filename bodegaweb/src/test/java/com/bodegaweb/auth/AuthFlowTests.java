package com.bodegaweb.auth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba la seguridad real (JWT): registro publico, login con token,
 * 401 sin token, 403 sin rol ADMIN y endpoints publicos.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTests {

    private static final String CLIENTE_EMAIL = "cliente@bodegaweb.com";
    private static final String CLIENTE_PASSWORD = "clave-cliente-123";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    void registroLoginYProteccionDeEndpoints() throws Exception {
        mvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Cliente","apellido":"Test","email":"%s","password":"%s"}
                                """.formatted(CLIENTE_EMAIL, CLIENTE_PASSWORD)))
                .andExpect(status().isCreated());

        String loginBody = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(CLIENTE_EMAIL, CLIENTE_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.tipo", is("Bearer")))
                .andExpect(jsonPath("$.data.usuario.email", is(CLIENTE_EMAIL)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginBody).at("/data/token").asText();

        mvc.perform(get("/api/carritos/usuario/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void loginConMalasCredencialesRetorna401() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nadie@bodegaweb.com","password":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinTokenSeRetorna401() throws Exception {
        mvc.perform(get("/api/carritos/usuario/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointPublicoAccesibleSinToken() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioNormalNoPuedeCrearProductos() throws Exception {
        String token = login(
                "autora@bodegaweb.com",
                "clave-autora-123",
                "Autora",
                "Lopez");

        mvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"NAD-001","nombre":"Ilegal","precio":1.0}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCreaProducto() throws Exception {
        String token = loginOnly("admin@bodegaweb.com", "admin123");

        mvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku":"ADM-001","nombre":"Producto admin","precio":99.90}
                                """))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password, String nombre, String apellido) throws Exception {
        mvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"%s","apellido":"%s","email":"%s","password":"%s"}
                                """.formatted(nombre, apellido, email, password)))
                .andExpect(status().isCreated());

        return loginOnly(email, password);
    }

    private String loginOnly(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(body).at("/data/token").asText();
    }
}