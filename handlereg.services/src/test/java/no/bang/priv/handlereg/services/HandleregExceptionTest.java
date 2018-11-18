/*
 * Copyright 2018 Steinar Bang
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
package no.bang.priv.handlereg.services;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class HandleregExceptionTest {

    @Test
    void testCreate() {
        String message1 = "just a message";
        HandleregException exception1 = new HandleregException(message1);
        assertEquals(message1, exception1.getMessage());
        assertNull(exception1.getCause());

        String message2 = "message with cause";
        Exception cause2 = new SQLException();
        HandleregException exception2 = new HandleregException(message2, cause2);
        assertEquals(message2, exception2.getMessage());
        assertEquals(cause2, exception2.getCause());

        HandleregException exception3 = new HandleregException(message2, cause2, true, true);
        assertEquals(message2, exception3.getMessage());
        assertEquals(cause2, exception3.getCause());

        HandleregException exception4 = new HandleregException(cause2);
        assertEquals(cause2.getClass().getName(), exception4.getMessage());
        assertEquals(cause2, exception4.getCause());
    }

}
