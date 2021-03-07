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

public class UserRoles {

    private User user;
    private List<Role> roles;

    private UserRoles() {}

    public User getUser() {
        return user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public static UserRolesBuilder with() {
        return new UserRolesBuilder();
    }

    public static class UserRolesBuilder {
        private User user;
        private List<Role> roles;

        private UserRolesBuilder() {}

        public UserRoles build() {
            UserRoles userRoles = new UserRoles();
            userRoles.user = this.user;
            userRoles.roles = this.roles;
            return userRoles;
        }

        public UserRolesBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserRolesBuilder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }
    }
}
