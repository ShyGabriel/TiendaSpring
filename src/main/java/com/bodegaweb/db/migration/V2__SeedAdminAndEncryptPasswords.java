package com.bodegaweb.db.migration;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Migración Java (corre después de V1): hashea las contraseñas planas que dejó
 * {@code ddl-auto=update} en la BD de desarrollo y siembra el usuario admin.
 */
@Component
public class V2__SeedAdminAndEncryptPasswords extends BaseJavaMigration {

    private final PasswordEncoder passwordEncoder;

    public V2__SeedAdminAndEncryptPasswords(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement st = context.getConnection().createStatement()) {
            encryptPlaintextPasswords(context, st);
            seedAdmin(context, st);
        }
    }

    private void encryptPlaintextPasswords(Context context, Statement st) throws Exception {
        List<String[]> pendientes = new ArrayList<>();
        try (ResultSet rs = st.executeQuery("select id, password from usuarios")) {
            while (rs.next()) {
                String password = rs.getString("password");
                if (password == null || password.startsWith("$2")) {
                    continue;
                }
                pendientes.add(new String[] { rs.getString("id"), password });
            }
        }
        for (String[] fila : pendientes) {
            try (Statement up = context.getConnection().createStatement()) {
                up.executeUpdate("update usuarios set password = '" + passwordEncoder.encode(fila[1])
                        + "' where id = " + fila[0]);
            }
        }
    }

    private void seedAdmin(Context context, Statement st) throws Exception {
        try (ResultSet rs = st.executeQuery("select count(*) from usuarios where email = 'admin@bodegaweb.com'")) {
            if (rs.next() && rs.getLong(1) > 0) {
                return;
            }
        }
        String adminPassword = System.getenv().getOrDefault("SEED_ADMIN_PASSWORD", "admin123");
        String hash = passwordEncoder.encode(adminPassword);
        try (Statement ins = context.getConnection().createStatement()) {
            ins.executeUpdate("insert into usuarios (nombre, apellido, email, password, rol, created_at) "
                    + "values ('Admin', 'Bodega', 'admin@bodegaweb.com', '" + hash
                    + "', 'ADMIN', CURRENT_TIMESTAMP)");
        }
    }
}