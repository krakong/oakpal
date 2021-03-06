/*
 * Copyright 2019 Mark Adamcin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.adamcin.oakpal.webster;

import org.apache.jackrabbit.api.JackrabbitWorkspace;
import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.junit.Test;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static net.adamcin.oakpal.api.Fun.resultNothing1;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PrivilegeXmlExporterTest {

    final File testBaseDir = new File("target/repos/PrivilegeXmlExporterTest");

    @Test
    public void testWritePrivileges() throws Exception {
        final File tempDir = new File(testBaseDir, "testWritePrivileges");
        final File fromRepoDir = new File(tempDir, "fromRepo/segmentstore");
        final File toRepoDir = new File(tempDir, "toRepo/segmentstore");
        final File exportedXml = new File(tempDir, "exported.xml");

        TestUtil.prepareRepo(fromRepoDir, session -> {
            final URL aemPrivileges = getClass().getResource("/aem_privileges.xml");
            TestUtil.installPrivilegesFromURL(session, aemPrivileges);
        });

        if (exportedXml.exists()) {
            exportedXml.delete();
        }

        TestUtil.withReadOnlyFixture(fromRepoDir, session -> {
            final Workspace workspace = session.getWorkspace();
            final PrivilegeManager privilegeManager = ((JackrabbitWorkspace) workspace).getPrivilegeManager();

            assertNotNull("crx:replicate should be imported",
                    privilegeManager.getPrivilege("crx:replicate"));
            assertNotNull("cq:storeUGC should be imported",
                    privilegeManager.getPrivilege("cq:storeUGC"));

            PrivilegeXmlExporter.writePrivileges(() -> new OutputStreamWriter(
                            new FileOutputStream(exportedXml)),
                    session, Arrays.asList("crx:replicate"), false);
        });

        TestUtil.prepareRepo(toRepoDir, session -> {
            final URL exportedUrl = exportedXml.toURL();
            TestUtil.installPrivilegesFromURL(session, exportedUrl);
        });

        TestUtil.withReadOnlyFixture(toRepoDir, session -> {
            final Workspace workspace = session.getWorkspace();
            final PrivilegeManager privilegeManager = ((JackrabbitWorkspace) workspace).getPrivilegeManager();

            assertNotNull("crx:replicate should be imported",
                    privilegeManager.getPrivilege("crx:replicate"));
            boolean thrown = false;
            try {
                privilegeManager.getPrivilege("cq:storeUGC");
            } catch (Exception e) {
                thrown = true;
            }
            assertTrue("cq:storeUGC should NOT be imported", thrown);
        });
    }

    @Test
    public void testWritePrivileges_defaults() throws Exception {
        final File tempDir = new File(testBaseDir, "testWritePrivileges_defaults");
        final File fromRepoDir = new File(tempDir, "fromRepo/segmentstore");
        final File toRepoDir = new File(tempDir, "toRepo/segmentstore");
        final File exportedXml = new File(tempDir, "exported.xml");

        TestUtil.prepareRepo(fromRepoDir, session -> {
            final URL aemPrivileges = getClass().getResource("/aem_privileges.xml");
            TestUtil.installPrivilegesFromURL(session, aemPrivileges);
        });

        if (exportedXml.exists()) {
            exportedXml.delete();
        }

        TestUtil.withReadOnlyFixture(fromRepoDir, session -> {
            final Workspace workspace = session.getWorkspace();
            final PrivilegeManager privilegeManager = ((JackrabbitWorkspace) workspace).getPrivilegeManager();

            assertNotNull("crx:replicate should be imported",
                    privilegeManager.getPrivilege("crx:replicate"));
            assertNotNull("cq:storeUGC should be imported",
                    privilegeManager.getPrivilege("cq:storeUGC"));

            PrivilegeXmlExporter.writePrivileges(() -> new OutputStreamWriter(
                            new FileOutputStream(exportedXml)),
                    session, null, true);
        });

        TestUtil.prepareRepo(toRepoDir, session -> {
            final URL exportedUrl = exportedXml.toURL();
            resultNothing1((URL url) -> TestUtil.installPrivilegesWithIndivResults(session, url)).apply(exportedUrl);
        });

        TestUtil.withReadOnlyFixture(toRepoDir, session -> {
            final Workspace workspace = session.getWorkspace();
            final PrivilegeManager privilegeManager = ((JackrabbitWorkspace) workspace).getPrivilegeManager();

            assertNotNull("crx:replicate should be imported",
                    privilegeManager.getPrivilege("crx:replicate"));
        });
    }

    @Test(expected = RepositoryException.class)
    public void testWritePrivileges_throws() throws Exception {
        final Session session = mock(Session.class);
        final Workspace workspace = mock(Workspace.class);
        when(session.getWorkspace()).thenReturn(workspace);
        PrivilegeXmlExporter.writePrivileges(StringWriter::new, session, Collections.emptyList(), false);
    }
}
