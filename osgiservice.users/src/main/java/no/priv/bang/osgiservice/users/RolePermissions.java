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

import java.util.List;

/**
 * A JSON-friendly bean used to POST a {@link Role} object
 * together with a list of {@link Permission} objects in
 * REST APIs.
 */
public class RolePermissions {

    private Role role;
    private List<Permission> permissions;

    public RolePermissions(Role role, List<Permission> permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    /**
     * No-args constructor mandated by jackson.  All properties
     * are null when this constructor is used.
     */
    public RolePermissions() {
        // Jackson requires a No-args constructor
    }

    public Role getRole() {
        return role;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

}
