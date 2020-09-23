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
package no.priv.bang.oldalbum.web.api.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.oldalbum.services.bean.LoginResult;
import no.priv.bang.osgiservice.users.UserManagementService;



@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    LogService logservice;

    @Inject
    public UserManagementService useradmin;

    @GET
    @Path("/login")
    public LoginResult loginCheck() {
        Subject subject = SecurityUtils.getSubject();
        boolean remembered = subject.isAuthenticated();
        boolean canModifyAlbum = checkIfUserCanModifyAlbum(subject);
        boolean canLogin = shiroRoleOldalbumadminExists();
        return new LoginResult(remembered, (String) subject.getPrincipal(), "", remembered && canModifyAlbum, canLogin);
    }

    @POST
    @Path("/login")
    public LoginResult login(Credentials credentials) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword().toCharArray(), true);
        boolean canLogin = shiroRoleOldalbumadminExists();
        try {
            subject.login(token);
            boolean canModifyAlbum = checkIfUserCanModifyAlbum(subject);
            return new LoginResult(true, (String) subject.getPrincipal(), "", canModifyAlbum, canLogin);
        } catch(UnknownAccountException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: unknown account", e);
            return new LoginResult(false, null, "Unknown account", false, canLogin);
        } catch (IncorrectCredentialsException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: wrong password", e);
            return new LoginResult(false, null, "Wrong password", false, canLogin);
        } catch (LockedAccountException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: locked account", e);
            return new LoginResult(false, null, "Locked account", false, canLogin);
        } catch (AuthenticationException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: general authentication error", e);
            return new LoginResult(false, null, "Unknown login error", false, canLogin);
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Login error: internal server error", e);
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

        return new LoginResult(false, null, "Logged out", false, canLogin);
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

}
