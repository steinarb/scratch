package no.priv.bang.osgiservice.users;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class RolePermissionsTest {

    @Test
    void testCreate() {
        Role role = Role.with().id(42).rolename("admin").description("Adminstrate stuff").build();
        List<Permission> permissions = Arrays.asList(Permission.with().id(36).permissionname("adminapiwrite").description("PUT and POST and DELETE to the admin REST API").build());
        RolePermissions bean = RolePermissions.with().role(role).permissions(permissions).build();
        assertEquals(role, bean.getRole());
        assertEquals(permissions.get(0), bean.getPermissions().get(0));
    }

    @Test
    void testNoargsConstructor() {
        RolePermissions bean = RolePermissions.with().build();
        assertNull(bean.getRole());
        assertNull(bean.getPermissions());
    }

}
