package no.priv.bang.beans.immutable;
/*
 * Copyright 2019 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class reimplements {@link #hashCode()} and {@link #equals(Object)} for
 * immutable beans. The immutable bean needing a hashCode() and equals() method
 * should extend this class.
 */
public class Immutable {
    private int _cachedHash = 0; // NOSONAR This field has a non-standard name on purpose to not conflict with any actual field 

    /**
     * Uses {@link HashCodeBuilder#reflectionHashCode(Object, String...)} to
     * create a hashcode. The hashcode is lazily created the first time
     * the method is called, and then cached.
     *
     * This only works if the bean is immutable. So don't use Immutable as a base
     * class for beans with setters.
     */
    @Override
    public int hashCode() {
        if (_cachedHash == 0) {
            _cachedHash = HashCodeBuilder.reflectionHashCode(this);
        }

        return _cachedHash;
    }

    /**
     * Returns false if the compared object is null, has a different
     * class or has a different {@link #hashCode()}.
     *
     * Returns true if the compared object is the same object as
     * this object or if the compared object has the same hashCode().
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return hashCode() == obj.hashCode();
    }

}
