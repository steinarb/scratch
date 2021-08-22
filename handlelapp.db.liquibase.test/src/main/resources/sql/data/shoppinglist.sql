--liquibase formatted sql
--changeset sb:example_shoppinglist
insert into shoppinglist (article_id, amount, shoppingcart_id) values (1, 1, 1);
insert into shoppinglist (article_id, amount) values (5, 1);
