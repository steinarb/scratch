/*
 * Copyright 2020-2021 Steinar Bang
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
package no.priv.bang.oldalbum.services.bean;

public class LoginResult {

    private boolean success;
    private String username;
    private String errormessage;
    private boolean canModifyAlbum;
    private boolean canLogin;

    private LoginResult() {}

    public boolean getSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public boolean isCanModifyAlbum() {
        return canModifyAlbum;
    }

    public boolean isCanLogin() {
        return canLogin;
    }

    public static LoginResultBuilder with() {
        return new LoginResultBuilder();
    }

    public static class LoginResultBuilder {
        private boolean success;
        private String username;
        private String errormessage;
        private boolean canModifyAlbum;
        private boolean canLogin;

        private LoginResultBuilder() {}

        public LoginResult build() {
            LoginResult loginResult = new LoginResult();
            loginResult.success = this.success;
            loginResult.username = this.username;
            loginResult.errormessage = this.errormessage;
            loginResult.canModifyAlbum = this.canModifyAlbum;
            loginResult.canLogin = this.canLogin;
            return loginResult;
        }

        public LoginResultBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public LoginResultBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResultBuilder errormessage(String errormessage) {
            this.errormessage = errormessage;
            return this;
        }

        public LoginResultBuilder canModifyAlbum(boolean canModifyAlbum) {
            this.canModifyAlbum = canModifyAlbum;
            return this;
        }

        public LoginResultBuilder canLogin(boolean canLogin) {
            this.canLogin = canLogin;
            return this;
        }
    }
}
