package com.bodegaweb.db.migration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.api.migration.Context;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V2__SeedAdminAndEncryptPasswordsTest {
     private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final Context context = mock(Context.class);
    private final Connection conn = mock(Connection.class);
    private final Statement st = mock(Statement.class);
    private final PreparedStatement update = mock(PreparedStatement.class);
    private final PreparedStatement insert = mock(PreparedStatement.class);
    private final ResultSet usuarios = mock(ResultSet.class);
    private final ResultSet conteoAdmin = mock(ResultSet.class);

    private void prepararMocks() throws Exception {
        when(encoder.encode(anyString())).thenReturn("$2a$hash");
        when(context.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(st);
        when(conn.prepareStatement(startsWith("update"))).thenReturn(update);
        when(conn.prepareStatement(startsWith("insert"))).thenReturn(insert);
        when(st.executeQuery("select id, password from usuarios")).thenReturn(usuarios);
        when(st.executeQuery(startsWith("select count"))).thenReturn(conteoAdmin);
        when(conteoAdmin.next()).thenReturn(true);
    }

    @Test
    void hasheaPasswordsPlanasYCreaAdminSiNoExiste() throws Exception {
        prepararMocks();
        // 3 filas: una plana, una ya hasheada y una nula
        when(usuarios.next()).thenReturn(true, true, true, false);
        when(usuarios.getString("password")).thenReturn("plana123", "$2a$ya", null);
        when(usuarios.getString("id")).thenReturn("1");
        when(conteoAdmin.getLong(1)).thenReturn(0L);

        new V2__SeedAdminAndEncryptPasswords(encoder).migrate(context);

        verify(update).setString(1, "$2a$hash");
        verify(update).setLong(2, 1L);
        verify(update).executeUpdate();
        verify(insert).setString(3, "admin@bodegaweb.com");
        verify(insert).executeUpdate();
    }

    @Test
    void noCreaAdminSiYaExiste() throws Exception {
        prepararMocks();
        when(usuarios.next()).thenReturn(false);
        when(conteoAdmin.getLong(1)).thenReturn(1L);

        new V2__SeedAdminAndEncryptPasswords(encoder).migrate(context);

        verify(insert, never()).executeUpdate();
    }
}
