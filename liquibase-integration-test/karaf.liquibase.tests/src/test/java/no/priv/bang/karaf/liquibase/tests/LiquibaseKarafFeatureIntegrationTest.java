/*
 * Copyright 2022-2023 Steinar Bang
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
package no.priv.bang.karaf.liquibase.tests;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.karaf.liquibase.sample.services.Account;
import no.priv.bang.karaf.liquibase.sample.services.SampleLiquibaseDatasourceReceiverService;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.util.stream.Stream;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class LiquibaseKarafFeatureIntegrationTest extends KarafTestSupport {

    @Configuration
    public Option[] config() {
        final var jacksonCore = mavenBundle().groupId("com.fasterxml.jackson.core").artifactId("jackson-core").versionAsInProject();
        final var jacksonDatabind = mavenBundle().groupId("com.fasterxml.jackson.core").artifactId("jackson-databind").versionAsInProject();
        final var jacksonAnnotations = mavenBundle().groupId("com.fasterxml.jackson.core").artifactId("jackson-annotations").versionAsInProject();
        final MavenArtifactUrlReference sampleappFeatureRepo = maven()
            .groupId("no.priv.bang.karaf")
            .artifactId("karaf.liquibase.sample.datasource.receiver")
            .version("LATEST")
            .type("xml")
            .classifier("features");
        Option[] options = new Option[] {
            jacksonCore,
            jacksonDatabind,
            jacksonAnnotations,
            features(sampleappFeatureRepo)
        };
        return Stream.of(super.config(), options).flatMap(Stream::of).toArray(Option[]::new);
    }

    @Test
    public void testLoadFeature() throws Exception { // NOSONAR this test has an assert, just not an assert sonar recognizes
        var objectMapper = new ObjectMapper();
        System.out.println("testLoadFeature(1)");
        installAndAssertFeature("karaf-liquibase-sample-datasource-receiver");
        System.out.println("testLoadFeature(2)");
        var service = getOsgiService(SampleLiquibaseDatasourceReceiverService.class);
        System.out.println("testLoadFeature(3)");
        var initialAccounts = service.accounts();
        System.out.println("testLoadFeature(4) initialAccounts: " + objectMapper.writeValueAsString(initialAccounts));
        assertEquals(1, initialAccounts.size());
        var initialAccount = initialAccounts.get(0);
        System.out.println("testLoadFeature(5)");
        assertEquals("jod", initialAccount.getUsername());
        var newAccount = Account.with().username("jad").build();
        System.out.println("testLoadFeature(6)");
        var accountsAfterAdd = service.addAccount(newAccount);
        System.out.println("testLoadFeature(7)");
        assertEquals(2, accountsAfterAdd.size());
        System.out.println("testLoadFeature(8)");
        var addedAccount = accountsAfterAdd.get(1);
        System.out.println("testLoadFeature(9)");
        assertEquals(initialAccount.getId() + 1, addedAccount.getId());
        System.out.println("testLoadFeature(10)");
        assertEquals("jad", addedAccount.getUsername());
        System.out.println("testLoadFeature(11)");
    }

}
