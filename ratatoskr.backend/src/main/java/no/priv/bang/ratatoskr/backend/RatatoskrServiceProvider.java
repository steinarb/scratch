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
package no.priv.bang.ratatoskr.backend;

import static java.util.Optional.ofNullable;
import static no.priv.bang.ratatoskr.services.RatatoskrConstants.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.ratatoskr.asvocabulary.ActivityStreamObject;
import no.priv.bang.ratatoskr.asvocabulary.ActivityStreamObjectType;
import no.priv.bang.ratatoskr.asvocabulary.Article;
import no.priv.bang.ratatoskr.asvocabulary.Link;
import no.priv.bang.ratatoskr.asvocabulary.LinkOrObject;
import no.priv.bang.ratatoskr.services.RatatoskrException;
import no.priv.bang.ratatoskr.services.RatatoskrService;
import no.priv.bang.ratatoskr.services.activitypub.Like;
import no.priv.bang.ratatoskr.services.activitypub.Person;
import no.priv.bang.ratatoskr.services.activitypub.Status;
import no.priv.bang.ratatoskr.services.beans.Account;
import no.priv.bang.ratatoskr.services.beans.CounterBean;
import no.priv.bang.ratatoskr.services.beans.CounterIncrementStepBean;
import no.priv.bang.ratatoskr.services.beans.LocaleBean;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;

@Component(service=RatatoskrService.class, immediate=true, property= { "defaultlocale=nb_NO" })
public class RatatoskrServiceProvider implements RatatoskrService {

    private static final String DISPLAY_TEXT_RESOURCES = "i18n.Texts";
    private Logger logger;
    private DataSource datasource;
    private UserManagementService useradmin;
    private Locale defaultLocale;

    @Reference
    public void setLogservice(LogService logservice) {
        this.logger = logservice.getLogger(RatatoskrServiceProvider.class);
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/ratatoskr)")
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Reference
    public void setUseradmin(UserManagementService useradmin) {
        this.useradmin = useradmin;
    }

    @Activate
    public void activate(Map<String, Object> config) {
        defaultLocale = Locale.forLanguageTag(((String) config.get("defaultlocale")).replace('_', '-'));
        addRolesIfNotpresent();
    }

    @Override
    public boolean lazilyCreateAccount(String username) {
        try(var connection = datasource.getConnection()) {
            var accountid = findAccount(connection, username);

            if (accountid != -1) {
                return false;
            }

            try(var createAccount = connection.prepareStatement("insert into ratatoskr_accounts (username) values (?)")) {
                createAccount.setString(1, username);
                createAccount.executeUpdate();
            }

            accountid = findAccount(connection, username);
            try(var createIncrementStep = connection.prepareStatement("insert into counter_increment_steps (account_id) values (?)")) {
                createIncrementStep.setInt(1, accountid);
                createIncrementStep.executeUpdate();
            }

            try(var createCounter = connection.prepareStatement("insert into counters (account_id) values (?)")) {
                createCounter.setInt(1, accountid);
                createCounter.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            logger.warn("Failed to create ratatoskr account for username \"{}\"", username, e);
        }

        return false;
    }

    @Override
    public List<Account> getAccounts() {
        var accounts = new ArrayList<Account>();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var results = statement.executeQuery("select account_id, username from ratatoskr_accounts")) {
                    while(results.next()) {
                        var accountId = results.getInt(1);
                        var username = results.getString(2);
                        var user = useradmin.getUser(username);
                        var account = Account.with().accountId(accountId).user(user).build();
                        accounts.add(account);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Ingen ratatoskr", e);
        }

        return accounts;
    }

    @Override
    public Optional<Person> addPerson(Person person) {
        var sql = "insert into profiles (url_id, username, display_name, description, inbox_url_id, following_url_id, followers_url_id, liked_url_id, icon_url_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(var connection = datasource.getConnection()) {
            var idUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.id());
            var inboxUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.inbox());
            var followingUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.following());
            var followersUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.followers());
            var likedUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.liked());
            var iconUrlId = findExistingUrlIdOrAddUrlIfMissing(connection, person.icon());
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, idUrlId);
                statement.setString(2, person.preferredUsername());
                statement.setString(3, person.name());
                statement.setString(4, person.summary());
                statement.setObject(5, inboxUrlId);
                statement.setObject(6, followingUrlId);
                statement.setObject(7, followersUrlId);
                statement.setObject(8, likedUrlId);
                statement.setObject(9, iconUrlId);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Failed to save Person object", e);
            return Optional.empty();
        }

        return findPerson(person.id());
    }

    @Override
    public Optional<Person> findPerson(String id) {
        var sql = "SELECT p.profile_id, i.url AS id, username, display_name, description, b.url AS inbox, f.url AS following, e.url AS followers, l.url AS liked, c.url AS icon FROM (((((profiles p LEFT JOIN urls i ON p.url_id = i.url_id) LEFT JOIN urls b ON p.inbox_url_id = b.url_id) LEFT JOIN urls f ON p.following_url_id = f.url_id) LEFT JOIN urls e ON p.followers_url_id = e.url_id) LEFT JOIN urls l ON p.liked_url_id = l.url_id) LEFT JOIN urls c ON p.icon_url_id = c.url_id WHERE i.url = ?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return unpackPerson(results);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to fetch actor", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Article> addArticle(Article article) {
        var sql = "insert into posts (url_id, type, title, content, author_id, published_time) values (?, ?, ?, ?, ?, ?)";
        try(var connection = datasource.getConnection()) {
            var urlId = findExistingUrlIdOrAddUrlIfMissing(connection, article.id());
            var authorId = findProfileIdFromUrlId(connection, article.attributedTo());
            try(var statement = connection.prepareStatement(sql)) {
                statement.setObject(1, urlId);
                statement.setString(2, ActivityStreamObjectType.Article.name());
                statement.setString(3, article.name());
                statement.setString(4, article.content());
                statement.setInt(5, authorId);
                statement.setTimestamp(6, ofNullable(article.published()).map(published -> Timestamp.from(published.toInstant())).orElse(null));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to add article", e);
        }

        return findArticle(article.id());
    }

    @Override
    public Optional<Article> findArticle(String id) {
        var sql = "select p.post_id, i.url as id, p.title, p.content, a.url as attributed_to, p.published_time from posts p join urls i on p.url_id=i.url_id join profiles r on p.author_id=r.profile_id join urls a on r.url_id=a.url_id where i.url=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return unpackArticle(results);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to fetch article", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Person> findPersonWithUsername(String username) {
        var sql = "select p.profile_id, i.url AS id, username, display_name, description, b.url AS inbox, f.url AS following, e.url AS followers, l.url AS liked, c.url AS icon FROM (((((profiles p LEFT JOIN urls i ON p.url_id = i.url_id) LEFT JOIN urls b ON p.inbox_url_id = b.url_id) LEFT JOIN urls f ON p.following_url_id = f.url_id) LEFT JOIN urls e ON p.followers_url_id = e.url_id) LEFT JOIN urls l ON p.liked_url_id = l.url_id) LEFT JOIN urls c ON p.icon_url_id = c.url_id WHERE username=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return unpackPerson(results);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to find actor using username", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Person> findFollowersWithUsername(String username) {
        var list = new ArrayList<Person>();
        var sql = "select u.url as id, follower.username, follower.display_name, follower.description, inbox.url as inbox, following.url as following, followers.url as followers, liked.url as liked, icon.url as icon from follows f join profiles follower on f.follows_id=follower.profile_id join urls u on follower.url_id=u.url_id join urls inbox on follower.inbox_url_id=inbox.url_id join urls following on follower.following_url_id=following.url_id join urls followers on follower.followers_url_id=followers.url_id join urls liked on follower.liked_url_id=liked.url_id join urls icon on follower.icon_url_id=icon.url_id join profiles followed on f.followed_id=followed.profile_id where followed.username=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        unpackPerson(results).ifPresent(list::add);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Error fetching followers list", e);
        }

        return list;
    }

    @Override
    public List<Person> addFollowerToUsername(String followedUsername, String followerUrlId) {
        var sql = "INSERT INTO follows (local, follows_id, followed_id) SELECT ? AS local, f1.profile_id AS follows_id, f2.profile_id AS followed_id FROM profiles f1 JOIN urls u ON f1.url_id = u.url_id CROSS JOIN profiles f2 WHERE u.url = ? AND f2.username = ?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, false);
                statement.setString(2, followerUrlId);
                statement.setString(3, followedUsername);
                var updateCount = statement.executeUpdate();
                if (updateCount < 1) {
                    logger.warn("No follower with id {} added to user with username {}", followerUrlId, followedUsername);
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Error adding to followers list", e);
        }

        return findFollowersWithUsername(followedUsername);
    }

    @Override
    public List<Person> findProfilesFollowedByUsername(String username) {
        var list = new ArrayList<Person>();
        var sql = "select u.url as id, followed.username, followed.display_name, followed.description, inbox.url as inbox, following.url as following, followers.url as followers, liked.url as liked, icon.url as icon from follows f join profiles followed on f.followed_id=followed.profile_id join urls u on followed.url_id=u.url_id join urls inbox on followed.inbox_url_id=inbox.url_id join urls following on followed.following_url_id=following.url_id join urls followers on followed.followers_url_id=followers.url_id join urls liked on followed.liked_url_id=liked.url_id join urls icon on followed.icon_url_id=icon.url_id join profiles follower on f.follows_id=follower.profile_id where follower.username=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        unpackPerson(results).ifPresent(list::add);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Error fetching following list", e);
        }

        return list;
    }

    @Override
    public List<Person> addUsernameAsFollowerOfProfile(String followerUsername, String followedIdUrl) {
        var sql = "INSERT INTO follows (local, follows_id, followed_id) SELECT ?, follower.profile_id, followed.profile_id FROM profiles follower CROSS JOIN profiles followed JOIN urls u ON followed.url_id= u.url_id WHERE follower.username = ? AND u.url = ?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, false);
                statement.setString(2, followerUsername);
                statement.setString(3, followedIdUrl);
                var addedrows = statement.executeUpdate();
                System.out.println("addedrows: " + addedrows);
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Error adding to following list", e);
        }

        return findProfilesFollowedByUsername(followerUsername);
    }

    @Override
    public List<Like> findLikedWithUsername(String username) {
        var list = new ArrayList<Like>();
        var sql = "select l.like_id as id, a.url as actor, p.url as post, l.published_time from likes l join profiles r on l.actor_id=r.profile_id join urls a on r.url_id=a.url_id join posts o on l.post_id=o.post_id join urls p on o.url_id=p.url_id where r.username=?";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        unpackLike(results).ifPresent(list::add);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Error fetching following list", e);
        }

        return list;
    }

    @Override
    public List<Like> addLikeToArticleByUsername(Article article, String username) {
        var sql = "insert into likes (local, actor_id, post_id, published_time) values (?, (select profile_id from profiles where username=?), (select p.post_id from posts p join urls u on p.url_id=u.url_id where u.url=?), ?)";
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, true);
                statement.setString(2, username);
                statement.setString(3, article.id());
                statement.setTimestamp(4, Timestamp.from(ZonedDateTime.now().toInstant()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to add like", e);
        }

        // It is a bit confusing to return the users likes instead of the likes of the article
        // (but it is the most useful return value)
        return findLikedWithUsername(username);
    }

    @Override
    public List<Like> userLikeArticle(String username, Article article, String localWebContext) {
        try(var connection = datasource.getConnection()) {
            Integer likeId = null;
            createNewLike(connection, username, article);
            likeId = findLikeIdOfMostRecentlyCreatedLike(connection);
            updateLikesToSetIdOfCreatedLike(connection, username, likeId, localWebContext);
        } catch (SQLException e) {
            throw new RatatoskrException("Error liking article for user", e);
        }

        return findLikedWithUsername(username);
    }

    private void createNewLike(Connection connection, String username, Article article) throws SQLException {
        try(var statement = connection.prepareStatement("insert into likes (local, actor_id, post_id, published_time) values (?, (select profile_id from profiles where username=?), (select p.post_id from posts p join urls u on p.url_id=u.url_id where u.url=?), ?)")) {
            statement.setBoolean(1, true);
            statement.setString(2, username);
            statement.setString(3, article.id());
            statement.setTimestamp(4, Timestamp.from(Instant.now()));
            statement.executeUpdate();
        }
    }

    private Integer findLikeIdOfMostRecentlyCreatedLike(Connection connection) throws SQLException {
        Integer likeId = null;
        try(var statement = connection.createStatement()) {
            try(var results = statement.executeQuery("select like_id from likes order by like_id desc")) {
                if (results.next()) {
                    likeId = results.getInt("like_id");
                }
            }
        }

        return likeId;
    }

    private void updateLikesToSetIdOfCreatedLike(Connection connection, String username, Integer likeId, String localWebContext) throws SQLException {
        var id = localWebContext + "liked/" + username + "/" + Optional.ofNullable(likeId).orElse(0).toString();
        var url_id = findExistingUrlIdOrAddUrlIfMissing(connection, id);
        try(var statement = connection.prepareStatement("update likes set url_id=? where like_id=?")) {
            statement.setInt(1, url_id);
            statement.setInt(2, likeId);
            statement.executeUpdate();
        }
    }

    @Override
    public List<ActivityStreamObject> listInbox(Person actor) {
        return Collections.emptyList();
    }

    @Override
    public List<ActivityStreamObject> postToInbox(Person actor, ActivityStreamObject message) {
        return Collections.emptyList();
    }

    @Override
    public List<ActivityStreamObject> listOutbox(Person actor) {
        return Collections.emptyList();
    }

    @Override
    public List<ActivityStreamObject> postToOutbox(Person actor, ActivityStreamObject message) {
        return Collections.emptyList();
    }

    @Override
    public Optional<CounterIncrementStepBean> getCounterIncrementStep(String username) {
        try(var connection = datasource.getConnection()) {
            var counterIncrementStep = findCounterIncrementStep(connection, username);
            if (counterIncrementStep != null) {
                var bean = CounterIncrementStepBean.with()
                    .username(username)
                    .counterIncrementStep(counterIncrementStep)
                    .build();
                return Optional.of(bean);
            }
        } catch (SQLException e) {
            logger.error("No increment steps could be found for user \"{}\"", username, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<CounterIncrementStepBean> updateCounterIncrementStep(CounterIncrementStepBean updatedIncrementStep) {
        var username = updatedIncrementStep.username();
        try(var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("update counter_increment_steps set counter_increment_step=? where account_id in (select account_id from ratatoskr_accounts where username=?)")) {
                statement.setInt(1, updatedIncrementStep.counterIncrementStep());
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Unable to update increment step for user \"{}\"", username, e);
            return Optional.empty();
        }

        return getCounterIncrementStep(username);
    }

    @Override
    public Optional<CounterBean> getCounter(String username) {
        try(var connection = datasource.getConnection()) {
            return findAndCreateCounterBean(connection, username);
        } catch (SQLException e) {
            logger.error("No counter could be found for user \"{}\"", username, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<CounterBean> incrementCounter(String username) {
        try(var connection = datasource.getConnection()) {
            var incrementStep = findCounterIncrementStep(connection, username);
            var counter = findCounter(connection, username);

            try(var statement = connection.prepareStatement("update counters set counter=? where account_id in (select account_id from ratatoskr_accounts where username=?)")) {
                statement.setInt(1, counter + incrementStep);
                statement.setString(2, username);
                statement.executeUpdate();
            }

            return findAndCreateCounterBean(connection, username);
        } catch (SQLException e) {
            logger.warn("Failed to increment counter for user \"{}\"", username, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<CounterBean> decrementCounter(String username) {
        try(var connection = datasource.getConnection()) {
            var incrementStep = findCounterIncrementStep(connection, username);
            var counter = findCounter(connection, username);

            try(var statement = connection.prepareStatement("update counters set counter=? where account_id in (select account_id from ratatoskr_accounts where username=?)")) {
                statement.setInt(1, counter - incrementStep);
                statement.setString(2, username);
                statement.executeUpdate();
            }

            return findAndCreateCounterBean(connection, username);
        } catch (SQLException e) {
            logger.warn("Failed to decrement counter for user \"{}\"", username, e);
        }

        return Optional.empty();
    }

    @Override
    public Locale defaultLocale() {
        return defaultLocale;
    }

    @Override
    public List<LocaleBean> availableLocales() {
        return Arrays.asList(Locale.forLanguageTag("nb-NO"), Locale.UK).stream().map(l -> LocaleBean.with().locale(l).build()).toList();
    }

    @Override
    public Map<String, String> displayTexts(Locale locale) {
        return transformResourceBundleToMap(locale);
    }

    @Override
    public String displayText(String key, String locale) {
        var active = locale == null || locale.isEmpty() ? defaultLocale : Locale.forLanguageTag(locale.replace('_', '-'));
        var bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, active);
        return bundle.getString(key);
    }

    Integer findExistingUrlIdOrAddUrlIfMissing(Connection connection, String url) throws SQLException {
        if (url == null || url.isEmpty()) {
            return null;
        }

        var urlId = findUrlId(connection, url);
        if (urlId > 0) {
            return urlId;
        }

        var sql = "insert into urls (url) values (?)";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, url);
            statement.executeUpdate();
        }

        return findUrlId(connection, url);
    }

    private int findUrlId(Connection connection, String idUrl) throws SQLException {
        var sql2 = "select url_id from urls where url=?";
        try(var statement = connection.prepareStatement(sql2)) {
            statement.setString(1, idUrl);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("url_id");
                }
            }
        }
        return -1;
    }

    private int findProfileIdFromUrlId(Connection connection, LinkOrObject linkOrObject) throws SQLException {
        var urlId = findId(linkOrObject);
        var sql = "select p.profile_id from profiles p join urls u on p.url_id=u.url_id where u.url=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, urlId);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("profile_id");
                }
            }
        }
        return -1;
    }

    private int findAccount(Connection connection, String username) throws SQLException {
        try(var findAccount = connection.prepareStatement("select account_id from ratatoskr_accounts where username=?")) {
            findAccount.setString(1, username);
            try(var results = findAccount.executeQuery()) {
                while (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    int findProfileId(String id) {
        try(var connection = datasource.getConnection()) {
            var sql = "select p.profile_id from profiles p join urls u on p.url_id=u.url_id where u.url=?";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("profile_id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RatatoskrException("Unable to find actors.actor_id", e);
        }

        return -1;
    }

    private Integer findCounterIncrementStep(Connection connection, String username) throws SQLException {
        try(var statement = connection.prepareStatement("select counter_increment_step from counter_increment_steps c join ratatoskr_accounts a on c.account_id=a.account_id where a.username=?")) {
            statement.setString(1, username);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("counter_increment_step");
                }
            }
        }

        return null;
    }

    private Integer findCounter(Connection connection, String username) throws SQLException {
        try(var statement = connection.prepareStatement("select counter from counters c join ratatoskr_accounts a on c.account_id=a.account_id where a.username=?")) {
            statement.setString(1, username);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("counter");
                }
            }
        }

        return null;
    }

    private Optional<CounterBean> findAndCreateCounterBean(Connection connection, String username) throws SQLException {
        var counter = findCounter(connection, username);
        return counter != null ?
            Optional.of(CounterBean.with().counter(counter).build()) :
            Optional.empty();
    }

    private void addRolesIfNotpresent() {
        var ratatoskroles = Map.of(
            RATATOSKRUSER_ROLE, "User of activitypub server ratatoskr",
            RATATOSKRADMIN_ROLE, "Administrator of activitypub server ratatoskr");
        var existingroles = useradmin.getRoles().stream().map(Role::rolename).collect(Collectors.toSet());
        ratatoskroles.entrySet().stream()
            .filter(r -> !existingroles.contains(r.getKey()))
            .forEach(r ->  useradmin.addRole(Role.with().id(-1).rolename(r.getKey()).description(r.getValue()).build()));
    }

    private Optional<Person> unpackPerson(ResultSet results) throws SQLException {
        return Optional.of(Person.with()
            .id(stringOrNull(results, "id"))
            .preferredUsername(results.getString("username"))
            .name(results.getString("display_name"))
            .summary(results.getString("description"))
            .inbox(results.getString("inbox"))
            .following(results.getString("following"))
            .followers(results.getString("followers"))
            .liked(results.getString("liked"))
            .icon(results.getString("icon"))
            .build());
    }

    private Optional<Article> unpackArticle(ResultSet results) throws SQLException {
        return Optional.of(Article.with()
            .id(stringOrNull(results, "id"))
            .name(results.getString("title"))
            .content(results.getString("content"))
            .attributedTo(Link.with().href(results.getString("attributed_to")).build())
            .published(ofNullable(results.getTimestamp("published_time")).map(published_time -> published_time.toInstant().atZone(ZoneId.systemDefault())).orElse(null))
            .build());
    }

    private Optional<Like> unpackLike(ResultSet results) throws SQLException {
        return Optional.of(Like.with()
            .id(stringOrNull(results, "id"))
            .authoredBy(Person.with().id(results.getString("actor")).build())
            .inReplyTo(Status.with().id(results.getString("post")).build())
            .published(zonedDateTimeOrNull(results, "published_time"))
            .build());
    }

    private String stringOrNull(ResultSet results, String columnName) throws SQLException {
        var returnValue = results.getString(columnName);
        return results.wasNull() ? null : returnValue;
    }

    ZonedDateTime zonedDateTimeOrNull(ResultSet results, String columnName) throws SQLException {
        var timestamp = results.getTimestamp(columnName);
        return results.wasNull() ? null : ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }

    String findId(LinkOrObject linkOrObject) {
        return switch(linkOrObject) {
            case ActivityStreamObject asobject -> asobject.id();
            case Link aslink -> aslink.href();
            default -> throw new RatatoskrException("Did not get the expected type when parsing");
        };
    }

    String findHref(LinkOrObject linkOrObject) {
        return linkOrObject == null ? null : switch (linkOrObject) {
            case Link link -> link.href();
            default -> null;
        };
    }

    String findName(LinkOrObject linkOrObject) {
        return linkOrObject == null ? null : switch (linkOrObject) {
            case Link link -> link.name();
            case ActivityStreamObject activityStreamObject -> activityStreamObject.name();
            default -> null;
        };
    }

    Map<String, String> transformResourceBundleToMap(Locale locale) {
        var map = new HashMap<String, String>();
        var bundle = ResourceBundle.getBundle(DISPLAY_TEXT_RESOURCES, locale);
        var keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            var key = keys.nextElement();
            map.put(key, bundle.getString(key));
        }

        return map;
    }

}
