--liquibase formatted sql
--changeset sb:example_favourites
insert into favourites (account_id, store_id, rekkefolge) values (1, 1, 2);
insert into favourites (account_id, store_id, rekkefolge) values (2, 1, 1);
insert into favourites (account_id, store_id, rekkefolge) values (1, 136, 1);
