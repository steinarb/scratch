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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type" )
@JsonSubTypes({
    @Type(value = ActivityStreamObjectRecord.class, name = ActivityStreamObjectType.Names.OBJECT),
    @Type(value = ActivityRecord.class, name = ActivityStreamObjectType.Names.ACTIVITY),
    @Type(value = Travel.class, name = ActivityStreamObjectType.Names.TRAVEL),
    @Type(value = CollectionRecord.class, name = ActivityStreamObjectType.Names.COLLECTION),
    @Type(value = OrderedCollectionRecord.class, name = ActivityStreamObjectType.Names.ORDERED_COLLECTION),
    @Type(value = CollectionPage.class, name = ActivityStreamObjectType.Names.COLLECTION_PAGE),
    @Type(value = OrderedCollectionPage.class, name = ActivityStreamObjectType.Names.ORDERED_COLLECTION_PAGE),
    @Type(value = Person.class, name = ActivityStreamObjectType.Names.PERSON),
    @Type(value = NoteRecord.class, name = ActivityStreamObjectType.Names.NOTE),
    @Type(value = Place.class, name = ActivityStreamObjectType.Names.PLACE),
    @Type(value = Link.class, name = ActivityStreamObjectType.Names.LINK)
})
public sealed interface LinkOrObject permits Link, ActivityStreamObject {

    public ActivityStreamObjectType type();

}
