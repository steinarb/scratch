/*
 * Copyright 2019-2021 Steinar Bang
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
package no.priv.bang.osgiservice.users;

import no.priv.bang.beans.immutable.Immutable;

/**
 * Bean used represent user roles in {@link UserManagementService} operations.
 */
public class Role extends Immutable { // NOSONAR Immutable handles added fields

    private int id;
    private String rolename;
    private String description;

    private Role() {}

    public int getId() {
        return id;
    }

    public String getRolename() {
        return rolename;
    }

    public String getDescription() {
        return description;
    }

    public static RoleBuilder with() {
        return new RoleBuilder();
    }

    public static RoleBuilder with(Role role) {
        RoleBuilder builder = new RoleBuilder();
        builder.id = role.id;
        builder.rolename = role.rolename;
        builder.description = role.description;
        return builder;
    }

    public static class RoleBuilder {
        private int id = -1;
        private String rolename;
        private String description;

        private RoleBuilder() {}

        public Role build() {
            Role role = new Role();
            role.id = this.id;
            role.rolename = this.rolename;
            role.description = this.description;
            return role;
        }

        public RoleBuilder id(int id) {
            this.id = id;
            return this;
        }

        public RoleBuilder rolename(String rolename) {
            this.rolename = rolename;
            return this;
        }

        public RoleBuilder description(String description) {
            this.description = description;
            return this;
        }
    }
}
