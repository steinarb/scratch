/*
 * Copyright 2020 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.oldalbum.roleadder.test;

import java.util.Arrays;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;

@Component(immediate = true)
public class ShiroRoleAdderForOldalbumTestEnvironment {

    private UserManagementService useradmin;

    @Reference
    public void addUseradmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Activate
    public void activate() {
        Role role = addOldalbumadminRole();
        addRoleToAdmin(role);
    }

    public Role addOldalbumadminRole() {
        Optional<Role> existingrole = useradmin.getRoles().stream().filter(r -> "oldalbumadmin".equals(r.getRolename())).findFirst();
        if (existingrole.isPresent()) {
            return existingrole.get();
        }

        Role role = new Role(0, "oldalbumadmin", "Created by oldalbum.roleadder.test");
        useradmin.addRole(role);
        return role;
    }

    public UserRoles addRoleToAdmin(Role role) {
        User admin = useradmin.getUser("admin");
        if (admin == null) {
            return null;
        }
        UserRoles adminroles = new UserRoles(admin, Arrays.asList(role));
        useradmin.addUserRoles(adminroles);
        return adminroles;
    }

}
