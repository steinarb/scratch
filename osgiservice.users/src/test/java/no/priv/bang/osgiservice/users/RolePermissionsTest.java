package no.priv.bang.osgiservice.users;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class RolePermissionsTest {

    @Test
    void testCreate() {
        Role role = new Role(42, "admin", "Adminstrate stuff");
        List<Permission> permissions = Arrays.asList(new Permission(36, "adminapiwrite", "PUT and POST and DELETE to the admin REST API"));
        RolePermissions bean = new RolePermissions(role, permissions);
        assertEquals(role, bean.getRole());
        assertEquals(permissions.get(0), bean.getPermissions().get(0));
    }

    @Test
    void testNoargsConstructor() {
        RolePermissions bean = new RolePermissions();
        assertNull(bean.getRole());
        assertNull(bean.getPermissions());
    }

}
