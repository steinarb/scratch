--liquibase formatted sql
--changeset sb:example_shoppingcarts
insert into shoppingcarts (account_id, store_id) values (1, 134);
