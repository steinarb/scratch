/*
 * Copyright 2020-2022 Steinar Bang
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
package no.priv.bang.oldalbum.web.api.resources;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.oldalbum.services.bean.LoginResult;
import no.priv.bang.osgiservice.users.UserManagementService;



@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    Logger logger;

    @Inject
    public UserManagementService useradmin;

    @Inject
    public OldAlbumService oldalbum;

    @Inject
    void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(getClass());
    }

    @GET
    @Path("/login")
    public LoginResult loginCheck() {
        Subject subject = SecurityUtils.getSubject();
        boolean remembered = subject.isAuthenticated();
        boolean canModifyAlbum = checkIfUserCanModifyAlbum(subject);
        boolean canLogin = shiroRoleOldalbumadminExists();
        return LoginResult.with()
            .success(remembered)
            .username((String) subject.getPrincipal())
            .errormessage("")
            .canModifyAlbum(remembered && canModifyAlbum)
            .canLogin(canLogin)
            .build();
    }

    @POST
    @Path("/login")
    public LoginResult login(@QueryParam("locale")String locale, Credentials credentials) {
        Subject subject = SecurityUtils.getSubject();
        var originalRequestUri = findOriginalRequestUri().orElse(null);

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword().toCharArray(), true);
        boolean canLogin = shiroRoleOldalbumadminExists();
        try {
            subject.login(token);
            boolean canModifyAlbum = checkIfUserCanModifyAlbum(subject);
            return LoginResult.with()
                .success(true)
                .username((String) subject.getPrincipal())
                .errormessage("")
                .canModifyAlbum(canModifyAlbum)
                .canLogin(canLogin)
                .originalRequestUri(originalRequestUri)
                .build();
        } catch(UnknownAccountException e) {
            logger.warn("Login error: unknown account", e);
            return LoginResult.with().success(false).errormessage(oldalbum.displayText("unknownaccount", locale)).canModifyAlbum(false).canLogin(canLogin).build();
        } catch (IncorrectCredentialsException  e) {
            logger.warn("Login error: wrong password", e);
            return LoginResult.with().success(false).errormessage(oldalbum.displayText("wrongpassword", locale)).canModifyAlbum(false).canLogin(canLogin).build();
        } catch (LockedAccountException  e) {
            logger.warn("Login error: locked account", e);
            return LoginResult.with().success(false).errormessage(oldalbum.displayText("lockedaccount", locale)).canModifyAlbum(false).canLogin(canLogin).build();
        } catch (AuthenticationException e) {
            logger.warn("Login error: general authentication error", e);
            return LoginResult.with().success(false).errormessage(oldalbum.displayText("unknownloginerror", locale)).canModifyAlbum(false).canLogin(canLogin).build();
        } catch (Exception e) {
            logger.error("Login error: internal server error", e);
            throw new InternalServerErrorException();
        } finally {
            token.clear();
        }
    }

    @GET
    @Path("/logout")
    public LoginResult logout(Credentials credentials) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        boolean canLogin = shiroRoleOldalbumadminExists();

        return LoginResult.with().success(false).errormessage("Logged out").canModifyAlbum(false).canLogin(canLogin).build();
    }

    private boolean checkIfUserCanModifyAlbum(Subject subject) {
        try {
            subject.checkRole("oldalbumadmin");
            return true;
        } catch (AuthorizationException e) {
            // Skip and continue
        }
        return false;
    }

    private boolean shiroRoleOldalbumadminExists() {
        return useradmin.getRoles().stream().anyMatch(r -> "oldalbumadmin".equals(r.getRolename()));
    }

    private Optional<String> findOriginalRequestUri() {
        return Optional.ofNullable(WebUtils.getSavedRequest(null))
            .map(SavedRequest::getRequestURI)
            .map(u -> u.replace("/oldalbum", ""));
    }

}
