--liquibase formatted sql
--changeset sb:example_albumentries
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/', 0, true, 'Picture archive', '', '', '', 0);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/', 1, true, 'Motorcycle pictures', '', '', '', 1);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/places/', 2, true, 'Motorcycle meeting places', '', '', '', 1);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/', 2, true, 'My VFR750F in 1996', 'In may 1996, I bought a 1995 VFR750F, registered in october 1995, with 3400km on the clock when I bought it. This picture archive, contains pictures from my first (but hopefully not last) season, on a VFR.', '', '', 2);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/places/grava1', 3, false, '', 'Tyrigrava roadhouse, just south of Oslo is a popular motorcycle spot for Oslo, Norway, and the surrounding area. On Wednesdays it feels like anybody with a powered two wheeler drops by', 'https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif', 1);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/places/grava2', 3, false, '', 'Tyrigrava, view from the south. Lots of bikes', 'https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif', 2);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/places/grava3', 3, false, '', 'Tyrigrava, view from the north. Lotsa bikes here too', 'https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif', 3);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/places/hove', 3, false, '', 'Hove Fjellgard. A popular MC meeting spot in Hallingdal, Norway', 'https://www.bang.priv.no/sb/pics/moto/places/hove.jpg', 'https://www.bang.priv.no/sb/pics/moto/places/icons/hove.gif', 4);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/acirc1', 4, false, '', 'My VFR 750F, in front of Polarsirkelsenteret. Arctic Circle, Rana municipality, Northern Norway.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif', 1);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/acirc2', 4, false, '', 'West view of the arctic circle. As a kid, I used to think that the markers went all around the globe, and I used to wonder about the people who had put them up. Now I know better... but it''s like there''s a bit of magic missing...', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc2.gif', 2);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/acirc3', 4, false, '', 'My VFR 750F at the arctic circle.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif', 3);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/dirtroad', 4, false, '', 'The VFR as a dirt bike. There was a road marked from Ål in Hallingdal, to Hemsedal. It started out as an asphalt road. What to do when I hit dirt? Turn around? Nah!', 'https://www.bang.priv.no/sb/pics/moto/vfr96/dirtroad.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/dirtroad.gif', 4);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/enga1', 4, false, '', 'Engabréen, an arm of the Svartisen glacier, Norway''s third largest glacier, seen from the seat of my VFR, on route 17, "kystriksvegen".', 'https://www.bang.priv.no/sb/pics/moto/vfr96/enga1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/enga1.gif', 5);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/enga2', 4, false, '', 'A zoom in on Engabréen, showing how it stretches almost all the way to the sea.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/enga2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/enga2.gif', 6);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/fjell1', 4, false, '', 'Climbing up Sognefjellet, the markers for each 100 metre zips past pretty quickly. Kinda steep... This is the 1000 metre line.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/fjell1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/fjell1.gif', 7);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/fjell2', 4, false, '', 'The highest point of the road, on the Sognefjellet crossing. 1434 metres. Cold, windy, rainy (August 1996).', 'https://www.bang.priv.no/sb/pics/moto/vfr96/fjell2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/fjell2.gif', 8);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/hovevfr', 4, false, '', 'My VFR 750F outside Hove fjellgard. I''d hoped for better weather, and their outdoors ''restaurant'' to be operating. But this is Norway after all,... too cold and wet', 'https://www.bang.priv.no/sb/pics/moto/vfr96/hovevfr.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/hovevfr.gif', 9);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/rv306', 4, false, '', 'The roadsign that trancends languages. The text part of the sign goes "The road between Hvarnes and Nes, is". The rest is left as an excercise for the reader.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/rv306.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/rv306.gif', 10);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/vfr1', 4, false, '', 'View front right, of my shiny new VFR, sporting the rear seat cover.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/vfr1.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/vfr1.gif', 11);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/vfr2', 4, false, '', 'View rear right, of my shiny new VFR, sporting the rear seat cover.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/vfr2.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/vfr2.gif', 12);
insert into albumentries (localpath, parent, album, title, description, imageurl, thumbnailurl, sort) values ('/moto/vfr96/wintervfr-ef', 4, false, '', 'My Viffer surrounded by an unexpected snowfall, on the weekend I had planned to get it into storage (weeken November 9 to November 10, 1996)', 'https://www.bang.priv.no/sb/pics/moto/vfr96/wintervfr-ef.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/wintervfr-ef.gif', 13);
