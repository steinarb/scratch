--liquibase formatted sql
--changeset sb:example_handlelapp_accounts
insert into handlelapp_accounts (username) values ('jod');
insert into handlelapp_accounts (username) values ('jad');
