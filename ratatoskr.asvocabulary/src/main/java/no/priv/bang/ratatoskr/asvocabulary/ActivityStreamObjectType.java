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

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum ActivityStreamObjectType {
    Object(Names.OBJECT),
    Activity(Names.ACTIVITY),
    Accept(Names.ACCEPT),
    Announce(Names.ANNOUNCE),
    Block(Names.BLOCK),
    Create(Names.CREATE),
    Delete(Names.DELETE),
    Dislike(Names.DISLIKE),
    Flag(Names.FLAG),
    Follow(Names.FOLLOW),
    Ignore(Names.IGNORE),
    Join(Names.JOIN),
    Leave(Names.LEAVE),
    Like(Names.LIKE),
    Listen(Names.LISTEN),
    Move(Names.MOVE),
    Offer(Names.OFFER),
    Question(Names.QUESTION),
    Read(Names.READ),
    Reject(Names.REJECT),
    Remove(Names.REMOVE),
    Undo(Names.UNDO),
    Update(Names.UPDATE),
    View(Names.VIEW),
    TentativeAccept(Names.TENTATIVE_ACCEPT),
    TentativeReject(Names.TENTATIVE_REJECT),
    Add(Names.ADD),
    Invite(Names.INVITE),
    Arrive(Names.ARRIVE),
    Travel(Names.TRAVEL),
    Collection(Names.COLLECTION),
    OrderedCollection(Names.ORDERED_COLLECTION),
    CollectionPage(Names.COLLECTION_PAGE),
    OrderedCollectionPage(Names.ORDERED_COLLECTION_PAGE),
    Application(Names.APPLICATION),
    Group(Names.GROUP),
    Organization(Names.ORGANIZATION),
    Person(Names.PERSON),
    Service(Names.SERVICE),
    Document(Names.DOCUMENT),
    Audio(Names.AUDIO),
    Image(Names.IMAGE),
    Page(Names.PAGE),
    Video(Names.VIDEO),
    Article(Names.ARTICLE),
    Note(Names.NOTE),
    Event(Names.EVENT),
    Place(Names.PLACE),
    Relationship(Names.RELATIONSHIP),
    Profile(Names.PROFILE),
    Tombstone(Names.TOMBSTONE),
    Link(Names.LINK),
    Mention(Names.MENTION),
    @JsonEnumDefaultValue Untyped(Names.UNTYPED);

    public class Names {
        public static final String OBJECT = "Object";
        public static final String ACTIVITY = "Activity";
        public static final String ACCEPT = "Accept";
        public static final String ANNOUNCE = "Announce";
        public static final String BLOCK = "Block";
        public static final String CREATE = "Create";
        public static final String DELETE = "Delete";
        public static final String DISLIKE = "Dislike";
        public static final String FOLLOW = "Follow";
        public static final String FLAG = "Flag";
        public static final String IGNORE = "Ignore";
        public static final String JOIN = "Join";
        public static final String LEAVE = "Leave";
        public static final String LIKE = "Like";
        public static final String LISTEN = "Listen";
        public static final String MOVE = "Move";
        public static final String OFFER = "Offer";
        public static final String QUESTION = "Question";
        public static final String READ = "Read";
        public static final String REJECT = "Reject";
        public static final String REMOVE = "Remove";
        public static final String UNDO = "Undo";
        public static final String UPDATE = "Update";
        public static final String VIEW = "View";
        public static final String TENTATIVE_ACCEPT = "TentativeAccept";
        public static final String TENTATIVE_REJECT = "TentativeReject";
        public static final String ADD = "Add";
        public static final String INVITE = "Invite";
        public static final String ARRIVE = "Arrive";
        public static final String TRAVEL = "Travel";
        public static final String COLLECTION = "Collection";
        public static final String ORDERED_COLLECTION = "OrderedCollection";
        public static final String COLLECTION_PAGE = "CollectionPage";
        public static final String ORDERED_COLLECTION_PAGE = "OrderedCollectionPage";
        public static final String APPLICATION = "Application";
        public static final String GROUP = "Group";
        public static final String ORGANIZATION = "Organization";
        public static final String PERSON = "Person";
        public static final String SERVICE = "Service";
        public static final String DOCUMENT = "Document";
        public static final String AUDIO = "Audio";
        public static final String PAGE = "Page";
        public static final String IMAGE = "Image";
        public static final String VIDEO = "Video";
        public static final String ARTICLE = "Article";
        public static final String NOTE = "Note";
        public static final String EVENT = "Event";
        public static final String PLACE = "Place";
        public static final String RELATIONSHIP = "Relationship";
        public static final String PROFILE = "Profile";
        public static final String TOMBSTONE = "Tombstone";
        public static final String LINK = "Link";
        public static final String MENTION = "Mention";
        public static final String UNTYPED = "Untyped";
    }

    private final String label;

    private ActivityStreamObjectType(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}
