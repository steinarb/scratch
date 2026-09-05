/*
 * Copyright 2023-2026 Steinar Bang
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
package no.priv.bang.ratatoskr.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import no.priv.bang.ratatoskr.asvocabulary.ActivityStreamObject;
import no.priv.bang.ratatoskr.asvocabulary.Article;
import no.priv.bang.ratatoskr.services.activitypub.Like;
import no.priv.bang.ratatoskr.services.activitypub.Person;
import no.priv.bang.ratatoskr.services.beans.Account;
import no.priv.bang.ratatoskr.services.beans.CounterBean;
import no.priv.bang.ratatoskr.services.beans.CounterIncrementStepBean;
import no.priv.bang.ratatoskr.services.beans.LocaleBean;

public interface RatatoskrService {

    public List<Account> getAccounts();

    Optional<Person> addPerson(Person person);

    Optional<Person> findPerson(String id);

    public Optional<Person> findPersonWithUsername(String username);

    Optional<Article> addArticle(Article article);

    Optional<Article> findArticle(String id);

    List<Person> findFollowersWithUsername(String username);

    List<Person> addFollowerToUsername(String username, String id);

    List<Person> findProfilesFollowedByUsername(String username);

    List<Person> addUsernameAsFollowerOfProfile(String username, String id);

    List<Like> findLikedWithUsername(String username);

    List<Like> addLikeToArticleByUsername(Article article, String username);

    List<Like> userLikeArticle(String username, Article article, String localWebContext);

    List<ActivityStreamObject> listInbox(Person actor);

    List<ActivityStreamObject> postToInbox(Person actor, ActivityStreamObject message);

    List<ActivityStreamObject> listOutbox(Person actor);

    List<ActivityStreamObject> postToOutbox(Person actor, ActivityStreamObject message);

    public Optional<CounterIncrementStepBean> getCounterIncrementStep(String username);

    public Optional<CounterIncrementStepBean> updateCounterIncrementStep(CounterIncrementStepBean cupdatedIncrementStep);

    public Optional<CounterBean> getCounter(String username);

    public Optional<CounterBean> incrementCounter(String username);

    public Optional<CounterBean> decrementCounter(String username);

    Locale defaultLocale();

    List<LocaleBean> availableLocales();

    public Map<String, String> displayTexts(Locale locale);

    public String displayText(String key, String locale);

    public boolean lazilyCreateAccount(String username);

}
