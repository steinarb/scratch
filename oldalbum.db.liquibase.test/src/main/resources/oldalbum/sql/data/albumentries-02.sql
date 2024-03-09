--liquibase formatted sql
--changeset sb:example_albumentries_that_require_login
insert into albumentries (require_login, localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values (true, '/slides/', 1, true, 'Slides 1968-1977', 'Gamle bilder', '', '', 3);
insert into albumentries (require_login, localpath, parent, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (false, '/slides/berglia', 22, false, 'Berglia', 'Berglia gård, Plurdalen, 1966 eller 1967', 'http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0001.JPG', '', 1, '2022-02-17 12:49:45', 'image/jpeg', 5825572);
insert into albumentries (require_login, localpath, parent, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (true, '/slides/brennasen', 22, false, 'Brennåsen, 1967', 'Husan der heime', 'http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0006.JPG', '', 2, '2022-02-17 12:50:31', 'image/jpeg', 6022755);
--changeset sb:albums_with_default_not_group_on_year
update albumentries set group_by_year=false where albumentry_id=1;
