--liquibase formatted sql
--changeset sb:saved_albumentries
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (1, 0, '/', true, 'Picture archive', '', '', '', 0, null, null, 0);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (2, 1, '/moto/', true, 'Motorcycle pictures', '', '', '', 1, null, null, 0);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (3, 2, '/moto/places/', true, 'Motorcycle meeting places', '', '', '', 1, null, null, 0);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (4, 2, '/moto/vfr96/', true, 'My VFR750F in 1996', 'In may 1996, I bought a 1995 VFR750F, registered in october 1995, with 3400km on the clock when I bought it. This picture archive, contains pictures from my first (but hopefully not last) season, on a VFR.', '', '', 2, null, null, 0);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (5, 3, '/moto/places/grava1', false, '', 'Tyrigrava roadhouse, just south of Oslo is a popular motorcycle spot for Oslo, Norway, and the surrounding area. On Wednesdays it feels like anybody with a powered two wheeler drops by', 'https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif', 1, '1995-05-12 10:49:45', 'image/jpeg', 128186);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (6, 3, '/moto/places/grava2', false, '', 'Tyrigrava, view from the south. Lots of bikes', 'https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif', 2, '1995-05-12 10:50:31', 'image/jpeg', 86379);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (7, 3, '/moto/places/grava3', false, '', 'Tyrigrava, view from the north. Lotsa bikes here too', 'https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif', 3, '1995-05-12 10:51:19', 'image/jpeg', 100224);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (8, 3, '/moto/places/hove', false, '', 'Hove Fjellgard. A popular MC meeting spot in Hallingdal, Norway', 'https://www.bang.priv.no/sb/pics/moto/places/hove.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/hove.gif', 4, '1995-05-12 10:51:31', 'image/jpeg', 22383);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (9, 4, '/moto/vfr96/acirc1', false, '', 'My VFR 750F, in front of Polarsirkelsenteret. Arctic Circle, Rana municipality, Northern Norway.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif', 1, '1996-10-04 18:28:46', 'image/jpeg', 71072);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (10, 4, '/moto/vfr96/acirc2', false, '', 'West view of the arctic circle. As a kid, I used to think that the markers went all around the globe, and I used to wonder about the people who had put them up. Now I know better... but it''s like there''s a bit of magic missing...', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc2.gif', 2, '1996-10-04 18:28:52', 'image/jpeg', 77179);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (11, 4, '/moto/vfr96/acirc3', false, '', 'My VFR 750F at the arctic circle.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif', 3, '1996-10-04 18:28:58', 'image/jpeg', 57732);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (12, 4, '/moto/vfr96/dirtroad', false, '', 'The VFR as a dirt bike. There was a road marked from Ål in Hallingdal, to Hemsedal. It started out as an asphalt road. What to do when I hit dirt? Turn around? Nah!', 'https://www.bang.priv.no/sb/pics/moto/vfr96/dirtroad.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/dirtroad.gif', 4, '1996-10-04 18:29:04', 'image/jpeg', 65967);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (13, 4, '/moto/vfr96/enga1', false, '', 'Engabréen, an arm of the Svartisen glacier, Norway''s third largest glacier, seen from the seat of my VFR, on route 17, "kystriksvegen".', 'https://www.bang.priv.no/sb/pics/moto/vfr96/enga1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/enga1.gif', 5, '1996-10-04 18:29:10', 'image/jpeg', 71856);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (14, 4, '/moto/vfr96/enga2', false, '', 'A zoom in on Engabréen, showing how it stretches almost all the way to the sea.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/enga2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/enga2.gif', 6, '1996-10-04 18:29:16', 'image/jpeg', 40379);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (15, 4, '/moto/vfr96/fjell1', false, '', 'Climbing up Sognefjellet, the markers for each 100 metre zips past pretty quickly. Kinda steep... This is the 1000 metre line.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/fjell1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/fjell1.gif', 7, '1996-10-04 18:29:22', 'image/jpeg', 60535);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (16, 4, '/moto/vfr96/fjell2', false, '', 'The highest point of the road, on the Sognefjellet crossing. 1434 metres. Cold, windy, rainy (August 1996).', 'https://www.bang.priv.no/sb/pics/moto/vfr96/fjell2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/fjell2.gif', 8, '1996-10-04 18:29:28', 'image/jpeg', 56518);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (17, 4, '/moto/vfr96/hovevfr', false, '', 'My VFR 750F outside Hove fjellgard. I''d hoped for better weather, and their outdoors ''restaurant'' to be operating. But this is Norway after all,... too cold and wet', 'https://www.bang.priv.no/sb/pics/moto/vfr96/hovevfr.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/hovevfr.gif', 9, '1996-10-04 18:29:34', 'image/jpeg', 80063);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (18, 4, '/moto/vfr96/rv306', false, '', 'The roadsign that trancends languages. The text part of the sign goes "The road between Hvarnes and Nes, is". The rest is left as an excercise for the reader.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/rv306.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/rv306.gif', 10, '1996-09-28 21:02:22', 'image/jpeg', 118618);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (19, 4, '/moto/vfr96/vfr1', false, '', 'View front right, of my shiny new VFR, sporting the rear seat cover.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/vfr1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/vfr1.gif', 11, '1996-10-04 17:49:30', 'image/jpeg', 84323);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (20, 4, '/moto/vfr96/vfr2', false, '', 'View rear right, of my shiny new VFR, sporting the rear seat cover.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/vfr2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/vfr2.gif', 12, '1996-10-04 17:49:34', 'image/jpeg', 81276);
insert into albumentries (albumentry_id, parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (21, 4, '/moto/vfr96/wintervfr-ef', false, '', 'My Viffer surrounded by an unexpected snowfall, on the weekend I had planned to get it into storage (weeken November 9 to November 10, 1996)', 'https://www.bang.priv.no/sb/pics/moto/vfr96/wintervfr-ef.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/wintervfr-ef.gif', 13, '1997-01-19 17:09:43', 'image/jpeg', 55945);
ALTER TABLE albumentries ALTER COLUMN albumentry_id RESTART WITH 22