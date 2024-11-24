/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.ratatoskr.asvocabulary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ToZonedDateTimeConverter extends StdConverter<Object, ZonedDateTime> {
    static ObjectMapper mapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();

    @Override
    public ZonedDateTime convert(Object value) {
        try {
            return mapper.convertValue(value, ZonedDateTime.class);
        } catch (Exception e) {
            return switch(value) {
                case String string -> LocalDateTime.parse(string).atZone(ZoneId.of("UTC"));
                default -> throw e;
            };
        }
    }
}
