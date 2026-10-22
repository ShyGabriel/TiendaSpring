package com.bodegaweb.auth;

import com.bodegaweb.usuario.repository.UsuarioRepository;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Controla que cada usuario solo acceda a sus propios recursos (carrito, pedidos,
 * direcciones, pagos, reseñas). El usuario se obtiene del token JWT, nunca de la URL
 * o del body. Un ADMIN puede acceder a todo.
 *
 * <p>Si la seguridad está desactivada ({@code app.security.enabled=false}, usado en
 * algunos tests), las verificaciones no se aplican.
 */
@Service
public class AccesoService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UsuarioRepository usuarioRepository;
    private final boolean seguridadActiva;

    public AccesoService(UsuarioRepository usuarioRepository,
                         @Value("${app.security.enabled:true}") boolean seguridadActiva) {
        this.usuarioRepository = usuarioRepository;
        this.seguridadActiva = seguridadActiva;
    }

    /** Lanza 403 si el usuario del token no es el dueño indicado (salvo ADMIN). */
    public void verificarPropietario(Long usuarioId) {
        verificarPropietario(() -> usuarioId);
    }

    /**
     * Igual que {@link #verificarPropietario(Long)}, pero el dueño se calcula solo si hace
     * falta (por ejemplo, buscando el pedido en la BD). Así un ADMIN no paga esa consulta.
     */
    public void verificarPropietario(Supplier<Long> duenoId) {
        if (!seguridadActiva || esAdmin()) {
            return;
        }
        if (!Objects.equals(duenoId.get(), usuarioActualId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a este recurso");
        }
    }

    /** Lanza 403 si el usuario del token no es ADMIN. */
    public void soloAdmin() {
        if (seguridadActiva && !esAdmin()) {
            throw new AccessDeniedException("Solo un administrador puede realizar esta acción");
        }
    }

    public boolean esAdmin() {
        Authentication auth = autenticacion();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ROLE_ADMIN::equals);
    }

    public Long usuarioActualId() {
        Authentication auth = autenticacion();
        if (auth.getPrincipal() instanceof Jwt jwt) {
            Object uid = jwt.getClaim(JwtService.CLAIM_USUARIO_ID);
            if (uid instanceof Number numero) {
                return numero.longValue();
            }
        }
        // Tokens emitidos antes de agregar el claim "uid": se busca por email (subject).
        return usuarioRepository.findByEmail(auth.getName())
                .map(u -> u.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario del token no encontrado"));
    }

    private Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Debes iniciar sesión");
        }
        return auth;
    }
}
