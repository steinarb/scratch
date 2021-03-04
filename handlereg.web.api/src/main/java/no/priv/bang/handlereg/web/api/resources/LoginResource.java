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
package no.priv.bang.handlereg.web.api.resources;

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
import org.apache.shiro.subject.Subject;
import org.osgi.service.log.LogService;

import no.priv.bang.handlereg.services.Credentials;
import no.priv.bang.handlereg.services.Loginresultat;


@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    LogService logservice;

    @POST
    @Path("/login")
    public Loginresultat login(Credentials credentials) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(credentials.getUsername(), credentials.getPassword().toCharArray(), true);
        try {
            subject.login(token);

            return Loginresultat.with().suksess(true).feilmelding("").build();
        } catch(UnknownAccountException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: unknown account", e);
            return Loginresultat.with().suksess(false).feilmelding("Ukjent konto").build();
        } catch (IncorrectCredentialsException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: wrong password", e);
            return Loginresultat.with().suksess(false).feilmelding("Feil passord").build();
        } catch (LockedAccountException  e) {
            logservice.log(LogService.LOG_WARNING, "Login error: locked account", e);
            return Loginresultat.with().suksess(false).feilmelding("Låst konto").build();
        } catch (AuthenticationException e) {
            logservice.log(LogService.LOG_WARNING, "Login error: general authentication error", e);
            return Loginresultat.with().suksess(false).feilmelding("Ukjent feil").build();
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Login error: internal server error", e);
            throw new InternalServerErrorException();
        } finally {
            token.clear();
        }
    }

    @GET
    @Path("/logout")
    public Loginresultat logout(Credentials credentials) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        return Loginresultat.with().suksess(false).feilmelding("Logget ut").build();
    }

}
