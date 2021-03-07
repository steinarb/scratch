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
 * Bean used in password related {@link UserManagementService} operations.
 * This bean contains a {@link User} object as well as two string objects
 * holding two copies of a password.
 */
public class UserAndPasswords {

    private User user;
    private String password1;
    private String password2;
    private boolean passwordsNotIdentical;

    private UserAndPasswords() {}

    public User getUser() {
        return user;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
    }

    public boolean isPasswordsNotIdentical() {
        return passwordsNotIdentical;
    }

    public static UserAndPasswordsBuilder with() {
        return new UserAndPasswordsBuilder();
    }

    public static class UserAndPasswordsBuilder {
        private User user;
        private String password1;
        private String password2;
        private boolean passwordsNotIdentical;

        private UserAndPasswordsBuilder() {}

        public UserAndPasswords build() {
            UserAndPasswords userAndPasswords = new UserAndPasswords();
            userAndPasswords.user = this.user;
            userAndPasswords.password1 = this.password1;
            userAndPasswords.password2 = this.password2;
            userAndPasswords.passwordsNotIdentical = this.passwordsNotIdentical;
            return userAndPasswords;
        }

        public UserAndPasswordsBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserAndPasswordsBuilder password1(String password1) {
            this.password1 = password1;
            return this;
        }

        public UserAndPasswordsBuilder password2(String password2) {
            this.password2 = password2;
            return this;
        }

        public UserAndPasswordsBuilder passwordsNotIdentical(boolean passwordsNotIdentical) {
            this.passwordsNotIdentical = passwordsNotIdentical;
            return this;
        }
    }

}
