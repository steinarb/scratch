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
package no.priv.bang.oldalbum.services.bean;

public class LoginResult {

    private boolean success;
    private String username;
    private String errormessage;
    private boolean canModifyAlbum;

    public LoginResult(boolean success, String username, String errormessage, boolean canModifyAlbum) {
        this.success = success;
        this.username = username;
        this.errormessage = errormessage;
        this.canModifyAlbum = canModifyAlbum;
    }

    public LoginResult() {
        // Jackson krever no-args-konstruktør
    }

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

}
