--liquibase formatted sql
--changeset sb:example_albumentries
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/', 0, true, 'Picture archive', '', '', '', 0);
