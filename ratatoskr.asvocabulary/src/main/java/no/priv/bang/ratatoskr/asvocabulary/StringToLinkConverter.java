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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdConverter;

public class StringToLinkConverter extends StdConverter<Object, LinkOrObject> {
    static ObjectMapper mapper = new ObjectMapper();

    @Override
    public LinkOrObject convert(Object value) {
        try {
            return convertListMember(value);
        } catch (Exception e) {
            if (value instanceof Map<?,?>) {
                var map = (Map<?,?>)value;
                return new UntypedObject(
                    null,
                    ActivityStreamObjectType.Untyped,
                    (String)map.get("name"),
                    (String)map.get("id"),
                    (String)map.get("summary"),
                    null,
                    null,
                    null);
            } else if (value instanceof List<?>) {
                var list = (List<?>)value;
                return new LinkOrObjectList(list.stream().map(StringToLinkConverter::convertListMember).toList());
            }

            throw new IllegalArgumentException("Argument can't be parsed as a String or LinkOrObject", e);
        }
    }

    private static LinkOrObject convertListMember(Object value){
        return switch (value) {
            case String strvalue -> Link.with().href(strvalue).build();
            default -> mapper.convertValue(value, LinkOrObject.class);
        };
    }
}
