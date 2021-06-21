/*
 * Copyright 2017-2021 Steinar Bang
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
package no.priv.bang.maven.repository.snapshotpruner;

import static org.junit.jupiter.api.Assertions.*;
import static no.priv.bang.maven.repository.snapshotpruner.MavenProperties.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

public class MavenRepositoryTest {

    @Test
    public void testFindMavenMetadataFiles() throws IOException {
        Path repositoryDirectory = Paths.get(maven.getProperty("repository.top"));
        MavenRepository repository = new MavenRepository(repositoryDirectory);
        List<Path> metadataFiles = repository.findMavenMetadataFiles();
        assertEquals(4, metadataFiles.size());
    }

    @Test
    public void testParseMavenMetadataFile() throws IOException, JDOMException {
        Path repositoryDirectory = Paths.get(maven.getProperty("repository.top"));
        MavenRepository repository = new MavenRepository(repositoryDirectory);

        Path mavenMetadataFileWithSnapshotVersion = Paths.get(repositoryDirectory.toString(), "no/priv/bang/ukelonn/ukelonn.api/1.0.0-SNAPSHOT/maven-metadata.xml");
        MavenMetadata mavenMetadataWithSnapshotVersion = repository.parseMavenMetdata(mavenMetadataFileWithSnapshotVersion);
        assertTrue(mavenMetadataWithSnapshotVersion.hasSnapshotVersion());
        assertEquals("1.0.0-20170922.181212-25", mavenMetadataWithSnapshotVersion.getSnapshotVersion());
        assertEquals(mavenMetadataFileWithSnapshotVersion, mavenMetadataWithSnapshotVersion.getPath());

        Path mavenMetadataFileWithoutSnapshotVersion = Paths.get(repositoryDirectory.toString(), "no/priv/bang/ukelonn/ukelonn.api/maven-metadata.xml");
        MavenMetadata mavenMetadataWithoutSnapshotVersion = repository.parseMavenMetdata(mavenMetadataFileWithoutSnapshotVersion);
        assertFalse(mavenMetadataWithoutSnapshotVersion.hasSnapshotVersion());
    }

    @Test
    public void testFindMavenMetadataFilesWithSnapshotVersion() throws IOException, JDOMException {
        Path repositoryDirectory = Paths.get(maven.getProperty("repository.top"));
        MavenRepository repository = new MavenRepository(repositoryDirectory);
        List<MavenMetadata> metadataFiles = repository.findMavenMetadataFilesWithSnapshotVersion();
        assertEquals(2, metadataFiles.size());
    }

    @Test
    public void testPruneSnapshotsInRepository() throws IOException, JDOMException {
        copyMockMavenSnapshotRepository();
        Path repositoryDirectory = Paths.get(maven.getProperty("repository.top"));
        MavenRepository repository = new MavenRepository(repositoryDirectory);

        int totalNumberOfDeletedFiles = repository.pruneSnapshots();
        assertEquals(400, totalNumberOfDeletedFiles);
    }

}
