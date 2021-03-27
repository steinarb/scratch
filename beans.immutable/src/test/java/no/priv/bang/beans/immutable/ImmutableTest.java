package no.priv.bang.beans.immutable;
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ImmutableTest {
    class IntBean extends Immutable {
        private int field1;
        private int field2;

        public IntBean(int field1, int field2) {
            super();
            this.field1 = field1;
            this.field2 = field2;
        }

        public int getField1() {
            return field1;
        }

        public int getField2() {
            return field2;
        }
    }

    @Test
    void testIntBeanIdenticalFields() {
        IntBean bean1 = new IntBean(1, 42);
        IntBean bean2 = new IntBean(1, 42);
        assertEquals(bean1.hashCode(), bean2.hashCode());
        assertEquals(bean1, bean2);
        assertNotEquals(0, bean1.hashCode());
    }

    @Test
    void testIntBeanNull() {
        IntBean bean1 = new IntBean(1, 42);
        IntBean nullBean = null;
        assertNotEquals(bean1, nullBean);
    }

    @Test
    void testIntBeanDifferentClass() {
        IntBean bean1 = new IntBean(1, 42);
        String notABean = "not a bean";
        assertNotEquals(bean1, notABean); // NOSONAR The entire point here is to test bean equals against an instance of different class
    }

    @Test
    void testIntBeanSameBean() {
        IntBean bean1 = new IntBean(1, 42);
        assertEquals(bean1, bean1); // NOSONAR The entire point here is to test bean equals against the same instance
    }

    @Test
    void testIntBeanIFieldsNotdentical() {
        IntBean bean1 = new IntBean(1, 42);
        IntBean bean2 = new IntBean(0, 43);
        assertNotEquals(bean1.hashCode(), bean2.hashCode());
        assertNotEquals(bean1, bean2);
        assertNotEquals(0, bean2.hashCode());
    }

}
