/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.oldalbum.web.security;

import org.apache.shiro.config.Ini;
import org.apache.shiro.web.env.IniWebEnvironment;

import no.priv.bang.oldalbum.services.OldAlbumService;

public class OldAlbumWebEnvironment extends IniWebEnvironment {

    private Ini ini;

    public OldAlbumWebEnvironment(OldAlbumService oldalbum) {
        var shiroProtectedUrls = oldalbum.findShiroProtectedUrls();
        ini = new Ini();
        var urlSection = ini.addSection("urls");
        urlSection.putAll(shiroProtectedUrls);
    }

    @Override
    protected Ini getFrameworkIni() {
        return ini;
    }

}
