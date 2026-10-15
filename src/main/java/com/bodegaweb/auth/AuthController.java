package com.bodegaweb.auth;

import com.bodegaweb.common.ApiResponse;
import com.bodegaweb.common.exception.InvalidCredentialsException;
import com.bodegaweb.usuario.dto.UsuarioResponse;
import com.bodegaweb.usuario.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            String token = jwtService.emitToken(authentication);
            Usuario usuario = ((UsuarioPrincipal) authentication.getPrincipal()).getUsuario();
            LoginResponse response = new LoginResponse(
                    token,
                    "Bearer",
                    jwtService.getExpirationMs(),
                    toResponse(usuario));
            return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Credenciales invalidas");
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreatedAt());
    }
}