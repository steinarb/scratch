/*
 * Copyright 2020-2024 Steinar Bang
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

import no.priv.bang.beans.immutable.Immutable;

public class LoginResult extends Immutable { // NOSONAR Immutable handles added fields

    private boolean success;
    private String username;
    private String errormessage;
    private boolean canModifyAlbum;
    private boolean canLogin;
    private String originalRequestUri;

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

    public String getOriginalRequestUri() {
        return this.originalRequestUri;
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String username;
        private String errormessage;
        private boolean canModifyAlbum;
        private boolean canLogin;
        private String originalRequestUri;

        private Builder() {}

        public LoginResult build() {
            var loginResult = new LoginResult();
            loginResult.success = this.success;
            loginResult.username = this.username;
            loginResult.errormessage = this.errormessage;
            loginResult.canModifyAlbum = this.canModifyAlbum;
            loginResult.canLogin = this.canLogin;
            loginResult.originalRequestUri = this.originalRequestUri;
            return loginResult;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder errormessage(String errormessage) {
            this.errormessage = errormessage;
            return this;
        }

        public Builder canModifyAlbum(boolean canModifyAlbum) {
            this.canModifyAlbum = canModifyAlbum;
            return this;
        }

        public Builder canLogin(boolean canLogin) {
            this.canLogin = canLogin;
            return this;
        }

        public Builder originalRequestUri(String originalRequestUri) {
            this.originalRequestUri = originalRequestUri;
            return this;
        }
    }
}
