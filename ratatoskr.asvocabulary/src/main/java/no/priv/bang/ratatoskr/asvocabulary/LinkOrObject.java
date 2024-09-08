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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible=true, defaultImpl = UntypedObject.class)
@JsonSubTypes({
    @Type(value = ActivityStreamObjectRecord.class, name = ActivityStreamObjectType.Names.OBJECT),
    @Type(value = ActivityRecord.class, names = { ActivityStreamObjectType.Names.ACTIVITY, "http://www.verbs.example/Check" }),
    @Type(value = AcceptRecord.class, name = ActivityStreamObjectType.Names.ACCEPT),
    @Type(value = Announce.class, name = ActivityStreamObjectType.Names.ANNOUNCE),
    @Type(value = Block.class, name = ActivityStreamObjectType.Names.BLOCK),
    @Type(value = Create.class, name = ActivityStreamObjectType.Names.CREATE),
    @Type(value = Delete.class, name = ActivityStreamObjectType.Names.DELETE),
    @Type(value = Dislike.class, name = ActivityStreamObjectType.Names.DISLIKE),
    @Type(value = Flag.class, name = ActivityStreamObjectType.Names.FLAG),
    @Type(value = Follow.class, name = ActivityStreamObjectType.Names.FOLLOW),
    @Type(value = IgnoreRecord.class, name = ActivityStreamObjectType.Names.IGNORE),
    @Type(value = Join.class, name = ActivityStreamObjectType.Names.JOIN),
    @Type(value = Leave.class, name = ActivityStreamObjectType.Names.LEAVE),
    @Type(value = Like.class, name = ActivityStreamObjectType.Names.LIKE),
    @Type(value = Listen.class, name = ActivityStreamObjectType.Names.LISTEN),
    @Type(value = Move.class, name = ActivityStreamObjectType.Names.MOVE),
    @Type(value = OfferRecord.class, name = ActivityStreamObjectType.Names.OFFER),
    @Type(value = Question.class, name = ActivityStreamObjectType.Names.QUESTION),
    @Type(value = RejectRecord.class, name = ActivityStreamObjectType.Names.REJECT),
    @Type(value = Read.class, name = ActivityStreamObjectType.Names.READ),
    @Type(value = Remove.class, name = ActivityStreamObjectType.Names.REMOVE),
    @Type(value = Undo.class, name = ActivityStreamObjectType.Names.UNDO),
    @Type(value = Update.class, name = ActivityStreamObjectType.Names.UPDATE),
    @Type(value = View.class, name = ActivityStreamObjectType.Names.VIEW),
    @Type(value = TentativeAccept.class, name = ActivityStreamObjectType.Names.TENTATIVE_ACCEPT),
    @Type(value = TentativeReject.class, name = ActivityStreamObjectType.Names.TENTATIVE_REJECT),
    @Type(value = Add.class, name = ActivityStreamObjectType.Names.ADD),
    @Type(value = Invite.class, name = ActivityStreamObjectType.Names.INVITE),
    @Type(value = Arrive.class, name = ActivityStreamObjectType.Names.ARRIVE),
    @Type(value = Travel.class, name = ActivityStreamObjectType.Names.TRAVEL),
    @Type(value = CollectionRecord.class, name = ActivityStreamObjectType.Names.COLLECTION),
    @Type(value = OrderedCollectionRecord.class, name = ActivityStreamObjectType.Names.ORDERED_COLLECTION),
    @Type(value = CollectionPage.class, name = ActivityStreamObjectType.Names.COLLECTION_PAGE),
    @Type(value = OrderedCollectionPage.class, name = ActivityStreamObjectType.Names.ORDERED_COLLECTION_PAGE),
    @Type(value = Application.class, name = ActivityStreamObjectType.Names.APPLICATION),
    @Type(value = Article.class, name = ActivityStreamObjectType.Names.ARTICLE),
    @Type(value = Event.class, name = ActivityStreamObjectType.Names.EVENT),
    @Type(value = Group.class, name = ActivityStreamObjectType.Names.GROUP),
    @Type(value = DocumentRecord.class, name = ActivityStreamObjectType.Names.DOCUMENT),
    @Type(value = Audio.class, name = ActivityStreamObjectType.Names.AUDIO),
    @Type(value = Image.class, name = ActivityStreamObjectType.Names.IMAGE),
    @Type(value = Page.class, name = ActivityStreamObjectType.Names.PAGE),
    @Type(value = Video.class, name = ActivityStreamObjectType.Names.VIDEO),
    @Type(value = Note.class, name = ActivityStreamObjectType.Names.NOTE),
    @Type(value = Organization.class, name = ActivityStreamObjectType.Names.ORGANIZATION),
    @Type(value = Person.class, name = ActivityStreamObjectType.Names.PERSON),
    @Type(value = Place.class, name = ActivityStreamObjectType.Names.PLACE),
    @Type(value = Relationship.class, name = ActivityStreamObjectType.Names.RELATIONSHIP),
    @Type(value = Profile.class, name = ActivityStreamObjectType.Names.PROFILE),
    @Type(value = Tombstone.class, name = ActivityStreamObjectType.Names.TOMBSTONE),
    @Type(value = Service.class, name = ActivityStreamObjectType.Names.SERVICE),
    @Type(value = LinkRecord.class, name = ActivityStreamObjectType.Names.LINK),
    @Type(value = Mention.class, name = ActivityStreamObjectType.Names.MENTION)
})
public sealed interface LinkOrObject permits Link, ActivityStreamObject, UntypedObject, LinkOrObjectList {

    public Object context();
    @JsonDeserialize(converter = ListToActivityStreamObjectTypeConverter.class)
    public ActivityStreamObjectType type();

}
