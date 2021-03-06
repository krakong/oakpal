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

package net.adamcin.oakpal.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.adamcin.oakpal.core.CheckSpec;
import net.adamcin.oakpal.core.ChecklistPlanner;
import org.junit.Test;

/**
 * Integration test to enforce expectations for the exported OakPAL checklist. Must be done during ITs because manifest
 * is generated at package time.
 */
public class ChecklistIT {
    public static final String OAKPAL_MODULE_NAME = "net.adamcin.oakpal.core";
    public static final String OAKPAL_CHECKLIST_BASIC = "basic";

    @Test
    public void testLoadChecklists() throws Exception {
        ChecklistPlanner planner = new ChecklistPlanner(Collections.singletonList(OAKPAL_CHECKLIST_BASIC));
        planner.discoverChecklists();

        assertEquals("expect one init stage, representing the one active checklist",
                1, planner.getInitStages().size());

        List<CheckSpec> specs = planner.getEffectiveCheckSpecs(Collections.emptyList());

        List<String> expectNames = Stream.of(
                "paths",
                "subpackages",
                "acHandling",
                "filterSets",
                "overlaps",
                "composite-store-alignment"
        ).map(name -> OAKPAL_MODULE_NAME + "/" + OAKPAL_CHECKLIST_BASIC + "/" + name)
                .collect(Collectors.toList());

        for (String expectName : expectNames) {
            assertTrue("expect effective check: " + expectName,
                    specs.stream().anyMatch(spec -> expectName.equals(spec.getName())));
        }
    }
}
