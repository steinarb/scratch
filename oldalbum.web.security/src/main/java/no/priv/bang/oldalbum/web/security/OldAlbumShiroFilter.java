package no.priv.bang.oldalbum.web.security;
/*
 * Copyright 2020-2026 Steinar Bang
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

import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.priv.bang.authservice.definitions.AuthserviceShiroConfigService;
import no.priv.bang.authservice.definitions.CipherKeyService;
import no.priv.bang.authservice.web.security.shirofilter.AuthserviceShiroFilterBase;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.ReloadableShiroFilter;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import javax.servlet.Filter;

@Component(service= {Filter.class, ReloadableShiroFilter.class}, immediate=true)
@HttpWhiteboardContextSelect("(" + HTTP_WHITEBOARD_CONTEXT_NAME + "=oldalbum)")
@HttpWhiteboardFilterPattern("/*")
public class OldAlbumShiroFilter extends AuthserviceShiroFilterBase implements ReloadableShiroFilter { // NOSONAR Can't do anything about the inheritance of Shiro
    private static final Ini INI_FILE = new Ini();
    static {
        // Can't use the Ini.fromResourcePath(String) method because it can't find "shiro.ini" on the classpath in an OSGi context
        INI_FILE.load(OldAlbumShiroFilter.class.getClassLoader().getResourceAsStream("shiro.ini"));
    }
    private OldAlbumService oldalbum;
    private static final Logger logger = LoggerFactory.getLogger(OldAlbumShiroFilter.class);

    @Reference
    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    @Reference
    public void setSession(SessionDAO session) {
        this.session = session;
    }

    @Reference
    public void setCipherKeyService(CipherKeyService cipherKeyService) {
        this.cipherKeyService = cipherKeyService;
    }

    @Reference
    public void setShiroConfigService(AuthserviceShiroConfigService shiroConfigService) {
        this.shiroConfigService = shiroConfigService;
    }

    @Reference
    public void setOldAlbumService(OldAlbumService oldalbum) {
        this.oldalbum = oldalbum;
    }

    @Activate
    public void activate() {
        loadShiroConfiguration();
    }

    @Override
    public boolean reloadConfiguration() {
        return loadShiroConfiguration();
    }

    boolean loadShiroConfiguration() {
        logger.info("Configuring shiro filter");
        createShiroWebEnvironmentFromIniFile(getClass().getClassLoader(), INI_FILE);
        return true;
    }

    @Override
    protected IniWebEnvironment createShiroIniWebEnvironment() {
        return new OldAlbumWebEnvironment(oldalbum);
    }

}
