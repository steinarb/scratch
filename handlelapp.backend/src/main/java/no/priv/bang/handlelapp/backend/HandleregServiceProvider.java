/*
 * Copyright 2021 Steinar Bang
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
package no.priv.bang.handlelapp.backend;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.handlelapp.services.HandlelappService;
import no.priv.bang.handlelapp.services.Vare;
import no.priv.bang.osgiservice.users.UserManagementService;

@Component(service=HandlelappService.class, immediate=true)
public class HandleregServiceProvider implements HandlelappService {

    private Logger logger;
    private DataSource datasource;
    private UserManagementService useradmin;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(HandleregServiceProvider.class);
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/handlereg)")
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Reference
    public void setUseradmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Activate
    public void activate() {
        // Will be called when component is activated
    }

    @Override
    public List<Vare> getHandlelapp() {
        useradmin.getRoles();
        List<Vare> handlelapp = new ArrayList<>();
        try(Connection connection = datasource.getConnection()) {

        } catch (SQLException e) {
            logger.error("Ingen handlelapp");
        }

        return handlelapp;
    }

}
