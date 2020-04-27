/*
 * Copyright 2020 Mark Adamcin
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

package net.adamcin.oakpal.core.checks;

import net.adamcin.oakpal.api.ProgressCheck;
import net.adamcin.oakpal.core.CheckReport;
import net.adamcin.oakpal.testing.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import static net.adamcin.oakpal.api.JavaxJson.arr;
import static net.adamcin.oakpal.api.JavaxJson.key;
import static org.junit.Assert.assertTrue;

public class SubpackagesTest extends ProgressCheckTestBase {

    @Test
    public void testDenyAll() throws Exception {
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(key("denyAll", false).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("denyAll:false", report);
            Assert.assertEquals("no violations", 0, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(key("denyAll", true).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("denyAll:false", report);
            Assert.assertEquals("two violations", 2, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
    }

    @Test
    public void testPatterns() throws Exception {
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(key("rules", arr()).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:[]", report);
            Assert.assertEquals("no violations", 0, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(
                    key("rules", arr(key("type", "deny").key("pattern", "my_packages:sub_.*"))).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:sub_.*", report);
            Assert.assertEquals("two violations", 2, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(
                    key("rules", arr()
                            .val(key("type", "deny").key("pattern", "my_packages:sub_.*"))
                            .val(key("type", "allow").key("pattern", "my_packages:sub_a"))
                    ).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:sub_.* - sub_a", report);
            Assert.assertEquals("one violation", 1, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(
                    key("rules", arr(key("type", "deny").key("pattern", "my_packages:sub_a"))).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:sub_a", report);
            Assert.assertEquals("one violation", 1, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(
                    key("rules", arr(key("type", "deny").key("pattern", "my_packages:sub_b"))).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:sub_b", report);
            Assert.assertEquals("one violation", 1, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
        TestUtil.testBlock(() -> {
            ProgressCheck check = new Subpackages().newInstance(
                    key("rules", arr(key("type", "deny").key("pattern", "my_packages:sub_c"))).get());
            CheckReport report = scanWithCheck(check, "subtest_with_content.zip");
            logViolations("testPatterns:sub_c", report);
            Assert.assertEquals("one violation", 0, report.getViolations().size());
            assertTrue("all violations have packageIds", report.getViolations().stream()
                    .allMatch(viol -> !viol.getPackages().isEmpty()));
        });
    }
}
