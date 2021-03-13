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

/**
 * Bean used represent permissions assigned to {@link Role}s in
 * {@link UserManagementService} operations.
 */
public class Permission {

    private int id;
    private String permissionname;
    private String description;

    private Permission() {}

    public int getId() {
        return id;
    }

    public String getPermissionname() {
        return permissionname;
    }

    public String getDescription() {
        return description;
    }

    public static PermissionBuilder with() {
        return new PermissionBuilder();
    }

    public static PermissionBuilder with(Permission permission) {
        PermissionBuilder builder = new PermissionBuilder();
        builder.id = permission.id;
        builder.permissionname = permission.permissionname;
        builder.description = permission.description;
        return builder;
    }

    public static class PermissionBuilder {
        private int id = -1;
        private String permissionname;
        private String description;

        private PermissionBuilder() {}

        public Permission build() {
            Permission permission = new Permission();
            permission.id = this.id;
            permission.permissionname = this.permissionname;
            permission.description = this.description;
            return permission;
        }

        public PermissionBuilder id(int id) {
            this.id = id;
            return this;
        }

        public PermissionBuilder permissionname(String permissionname) {
            this.permissionname = permissionname;
            return this;
        }

        public PermissionBuilder description(String description) {
            this.description = description;
            return this;
        }
    }
}
