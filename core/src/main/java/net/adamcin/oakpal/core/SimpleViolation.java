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

package net.adamcin.oakpal.core;

import net.adamcin.oakpal.api.Violation;
import org.apache.jackrabbit.vault.packaging.PackageId;
import org.osgi.annotation.versioning.ProviderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @deprecated 2.0.0 use {@link net.adamcin.oakpal.api.SimpleViolation}
 */
@Deprecated
@ProviderType
public class SimpleViolation implements Violation {
    private final net.adamcin.oakpal.core.Violation.Severity severity;
    private final String description;
    private final List<PackageId> packages;

    /**
     * Constructor.
     *
     * @param severity    the severity
     * @param description the description
     * @param packages    the package ids
     */
    public SimpleViolation(final net.adamcin.oakpal.core.Violation.Severity severity,
                           final String description, final PackageId... packages) {
        this(severity, description, packages != null ? Arrays.asList(packages) : null);
    }

    /**
     * Constructor.
     *
     * @param severity    the severity
     * @param description the description
     * @param packages    the package ids
     */
    public SimpleViolation(final net.adamcin.oakpal.core.Violation.Severity severity,
                           final String description, final List<PackageId> packages) {
        this.severity = severity != null ? severity : net.adamcin.oakpal.core.Violation.Severity.MAJOR;
        this.description = description;
        this.packages = packages == null || packages.isEmpty()
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(packages));
    }

    public net.adamcin.oakpal.api.Severity getSeverity() {
        return severity.getSeverity();
    }

    public String getDescription() {
        return description;
    }

    public List<PackageId> getPackages() {
        return packages;
    }

    @Override
    public String toString() {
        return "SimpleViolation{" +
                "severity=" + severity +
                ", description='" + description + '\'' +
                ", packages=" + packages +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleViolation that = (SimpleViolation) o;
        return severity == that.severity &&
                Objects.equals(description, that.description) &&
                packages.equals(that.packages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, description, packages);
    }
}
