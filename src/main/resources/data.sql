DROP TABLE IF EXISTS "application_user" CASCADE;
CREATE TABLE "public"."application_user" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "association_id" integer NOT NULL,
    "hashed_password" character varying(255),
    "name" character varying(255),
    "profile_picture" oid,
    "username" character varying(255),
    CONSTRAINT "application_user_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "application_user" ("id", "version", "association_id", "username", "name", "hashed_password", "profile_picture") VALUES
(1, 1, 1,'user','John Normal','$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe',lo_from_bytea(0, '\xffd8ffe000104a46494600010101004800480000ffe20c584943435f50524f46494c4500010100000c484c696e6f021000006d6e74725247422058595a2007ce00020009000600310000616373704d5346540000000049454320735247420000000000000000000000000000f6d6000100000000d32d4850202000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001163707274000001500000003364657363000001840000006c77747074000001f000000014626b707400000204000000147258595a00000218000000146758595a0000022c000000146258595a0000024000000014646d6e640000025400000070646d6464000002c400000088767565640000034c0000008676696577000003d4000000246c756d69000003f8000000146d6561730000040c0000002474656368000004300000000c725452430000043c0000080c675452430000043c0000080c625452430000043c0000080c7465787400000000436f70797269676874202863292031393938204865776c6574742d5061636b61726420436f6d70616e790000646573630000000000000012735247422049454336313936362d322e31000000000000000000000012735247422049454336313936362d322e31000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000058595a20000000000000f35100010000000116cc58595a200000000000000000000000000000000058595a200000000000006fa2000038f50000039058595a2000000000000062990000b785000018da58595a2000000000000024a000000f840000b6cf64657363000000000000001649454320687474703a2f2f7777772e6965632e636800000000000000000000001649454320687474703a2f2f7777772e6965632e63680000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000064657363000000000000002e4945432036313936362d322e312044656661756c742052474220636f6c6f7572207370616365202d207352474200000000000000000000002e4945432036313936362d322e312044656661756c742052474220636f6c6f7572207370616365202d20735247420000000000000000000000000000000000000000000064657363000000000000002c5265666572656e63652056696577696e6720436f6e646974696f6e20696e2049454336313936362d322e3100000000000000000000002c5265666572656e63652056696577696e6720436f6e646974696f6e20696e2049454336313936362d322e31000000000000000000000000000000000000000000000000000076696577000000000013a4fe00145f2e0010cf140003edcc0004130b00035c9e0000000158595a2000000000004c09560050000000571fe76d6561730000000000000001000000000000000000000000000000000000028f0000000273696720000000004352542063757276000000000000040000000005000a000f00140019001e00230028002d00320037003b00400045004a004f00540059005e00630068006d00720077007c00810086008b00900095009a009f00a400a900ae00b200b700bc00c100c600cb00d000d500db00e000e500eb00f000f600fb01010107010d01130119011f0125012b01320138013e0145014c0152015901600167016e0175017c0183018b0192019a01a101a901b101b901c101c901d101d901e101e901f201fa0203020c0214021d0226022f02380241024b0254025d02670271027a0284028e029802a202ac02b602c102cb02d502e002eb02f50300030b03160321032d03380343034f035a03660372037e038a039603a203ae03ba03c703d303e003ec03f9040604130420042d043b0448045504630471047e048c049a04a804b604c404d304e104f004fe050d051c052b053a05490558056705770586059605a605b505c505d505e505f6060606160627063706480659066a067b068c069d06af06c006d106e306f507070719072b073d074f076107740786079907ac07bf07d207e507f8080b081f08320846085a086e0882089608aa08be08d208e708fb09100925093a094f09640979098f09a409ba09cf09e509fb0a110a270a3d0a540a6a0a810a980aae0ac50adc0af30b0b0b220b390b510b690b800b980bb00bc80be10bf90c120c2a0c430c5c0c750c8e0ca70cc00cd90cf30d0d0d260d400d5a0d740d8e0da90dc30dde0df80e130e2e0e490e640e7f0e9b0eb60ed20eee0f090f250f410f5e0f7a0f960fb30fcf0fec1009102610431061107e109b10b910d710f511131131114f116d118c11aa11c911e81207122612451264128412a312c312e31303132313431363138313a413c513e5140614271449146a148b14ad14ce14f01512153415561578159b15bd15e0160316261649166c168f16b216d616fa171d17411765178917ae17d217f7181b18401865188a18af18d518fa19201945196b199119b719dd1a041a2a1a511a771a9e1ac51aec1b141b3b1b631b8a1bb21bda1c021c2a1c521c7b1ca31ccc1cf51d1e1d471d701d991dc31dec1e161e401e6a1e941ebe1ee91f131f3e1f691f941fbf1fea20152041206c209820c420f0211c2148217521a121ce21fb22272255228222af22dd230a23382366239423c223f0241f244d247c24ab24da250925382568259725c725f726272657268726b726e827182749277a27ab27dc280d283f287128a228d429062938296b299d29d02a022a352a682a9b2acf2b022b362b692b9d2bd12c052c392c6e2ca22cd72d0c2d412d762dab2de12e162e4c2e822eb72eee2f242f5a2f912fc72ffe3035306c30a430db3112314a318231ba31f2322a3263329b32d4330d3346337f33b833f1342b3465349e34d83513354d358735c235fd3637367236ae36e937243760379c37d738143850388c38c839053942397f39bc39f93a363a743ab23aef3b2d3b6b3baa3be83c273c653ca43ce33d223d613da13de03e203e603ea03ee03f213f613fa23fe24023406440a640e74129416a41ac41ee4230427242b542f7433a437d43c044034447448a44ce45124555459a45de4622466746ab46f04735477b47c04805484b489148d7491d496349a949f04a374a7d4ac44b0c4b534b9a4be24c2a4c724cba4d024d4a4d934ddc4e254e6e4eb74f004f494f934fdd5027507150bb51065150519b51e65231527c52c75313535f53aa53f65442548f54db5528557555c2560f565c56a956f75744579257e0582f587d58cb591a596959b85a075a565aa65af55b455b955be55c355c865cd65d275d785dc95e1a5e6c5ebd5f0f5f615fb36005605760aa60fc614f61a261f56249629c62f06343639763eb6440649464e9653d659265e7663d669266e8673d679367e9683f689668ec6943699a69f16a486a9f6af76b4f6ba76bff6c576caf6d086d606db96e126e6b6ec46f1e6f786fd1702b708670e0713a719571f0724b72a67301735d73b87414747074cc7528758575e1763e769b76f8775677b37811786e78cc792a798979e77a467aa57b047b637bc27c217c817ce17d417da17e017e627ec27f237f847fe5804780a8810a816b81cd8230829282f4835783ba841d848084e3854785ab860e867286d7873b879f8804886988ce8933899989fe8a648aca8b308b968bfc8c638cca8d318d988dff8e668ece8f368f9e9006906e90d6913f91a89211927a92e3934d93b69420948a94f4955f95c99634969f970a977597e0984c98b89924999099fc9a689ad59b429baf9c1c9c899cf79d649dd29e409eae9f1d9f8b9ffaa069a0d8a147a1b6a226a296a306a376a3e6a456a4c7a538a5a9a61aa68ba6fda76ea7e0a852a8c4a937a9a9aa1caa8fab02ab75abe9ac5cacd0ad44adb8ae2daea1af16af8bb000b075b0eab160b1d6b24bb2c2b338b3aeb425b49cb513b58ab601b679b6f0b768b7e0b859b8d1b94ab9c2ba3bbab5bb2ebba7bc21bc9bbd15bd8fbe0abe84beffbf7abff5c070c0ecc167c1e3c25fc2dbc358c3d4c451c4cec54bc5c8c646c6c3c741c7bfc83dc8bcc93ac9b9ca38cab7cb36cbb6cc35ccb5cd35cdb5ce36ceb6cf37cfb8d039d0bad13cd1bed23fd2c1d344d3c6d449d4cbd54ed5d1d655d6d8d75cd7e0d864d8e8d96cd9f1da76dafbdb80dc05dc8add10dd96de1cdea2df29dfafe036e0bde144e1cce253e2dbe363e3ebe473e4fce584e60de696e71fe7a9e832e8bce946e9d0ea5beae5eb70ebfbec86ed11ed9cee28eeb4ef40efccf058f0e5f172f1fff28cf319f3a7f434f4c2f550f5def66df6fbf78af819f8a8f938f9c7fa57fae7fb77fc07fc98fd29fdbafe4bfedcff6dffffffdb004300090606080605090807080a09090a0d160e0d0c0c0d1a131410161f1c21201f1c1e1e2327322a23252f251e1e2b3b2c2f3335383838212a3d413c364132373835ffdb004301090a0a0d0b0d190e0e1935241e243535353535353535353535353535353535353535353535353535353535353535353535353535353535353535353535353535ffc0001108001a001a03012200021101031101ffc4001a000002020300000000000000000000000005060107030408ffc4002e10000103020404030901000000000000000102030400110506122113314161075171151623243234425263d1ffc40017010101010100000000000000000000000001030002ffc4001a110003000301000000000000000000000000010203122131ffda000c03010002110311003f00796d8bf4a0f27376090a7ae2bf21c41697c371ee02cb28579172da47973b5e995b481b9e94baec44c9c36630f14f001712b6cb62ca049ff68c95a2298637e0614ca4a2e2c41dc11c8d63e0d46070bd9d97e1425385d547610d6b50dd5615b5a3b5764d8b39c33fc7cb0fa61351d52e6adbd653ab4a1b04eda8f7b1e40d2546f112ecc97a74171f9ab376d285da3a7b904f4f3b1268467d5139f315b927e327aff3450547d07d6b54aaf422dcf516165df1516db8db38fb2d9413a4cb676d3dd483d3d0d59a14149052a4949dc1bd736382e8503cad56b6013247bb9877cc3bf6ad7e67f414a03fffd9')),
(2, 1, 1,'admin','Emma Executive','$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.',lo_from_bytea(0, '\xffd8ffe000104a46494600010101004800480000ffe20c584943435f50524f46494c4500010100000c484c696e6f021000006d6e74725247422058595a2007ce00020009000600310000616373704d5346540000000049454320735247420000000000000000000000000000f6d6000100000000d32d4850202000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001163707274000001500000003364657363000001840000006c77747074000001f000000014626b707400000204000000147258595a00000218000000146758595a0000022c000000146258595a0000024000000014646d6e640000025400000070646d6464000002c400000088767565640000034c0000008676696577000003d4000000246c756d69000003f8000000146d6561730000040c0000002474656368000004300000000c725452430000043c0000080c675452430000043c0000080c625452430000043c0000080c7465787400000000436f70797269676874202863292031393938204865776c6574742d5061636b61726420436f6d70616e790000646573630000000000000012735247422049454336313936362d322e31000000000000000000000012735247422049454336313936362d322e31000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000058595a20000000000000f35100010000000116cc58595a200000000000000000000000000000000058595a200000000000006fa2000038f50000039058595a2000000000000062990000b785000018da58595a2000000000000024a000000f840000b6cf64657363000000000000001649454320687474703a2f2f7777772e6965632e636800000000000000000000001649454320687474703a2f2f7777772e6965632e63680000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000064657363000000000000002e4945432036313936362d322e312044656661756c742052474220636f6c6f7572207370616365202d207352474200000000000000000000002e4945432036313936362d322e312044656661756c742052474220636f6c6f7572207370616365202d20735247420000000000000000000000000000000000000000000064657363000000000000002c5265666572656e63652056696577696e6720436f6e646974696f6e20696e2049454336313936362d322e3100000000000000000000002c5265666572656e63652056696577696e6720436f6e646974696f6e20696e2049454336313936362d322e31000000000000000000000000000000000000000000000000000076696577000000000013a4fe00145f2e0010cf140003edcc0004130b00035c9e0000000158595a2000000000004c09560050000000571fe76d6561730000000000000001000000000000000000000000000000000000028f0000000273696720000000004352542063757276000000000000040000000005000a000f00140019001e00230028002d00320037003b00400045004a004f00540059005e00630068006d00720077007c00810086008b00900095009a009f00a400a900ae00b200b700bc00c100c600cb00d000d500db00e000e500eb00f000f600fb01010107010d01130119011f0125012b01320138013e0145014c0152015901600167016e0175017c0183018b0192019a01a101a901b101b901c101c901d101d901e101e901f201fa0203020c0214021d0226022f02380241024b0254025d02670271027a0284028e029802a202ac02b602c102cb02d502e002eb02f50300030b03160321032d03380343034f035a03660372037e038a039603a203ae03ba03c703d303e003ec03f9040604130420042d043b0448045504630471047e048c049a04a804b604c404d304e104f004fe050d051c052b053a05490558056705770586059605a605b505c505d505e505f6060606160627063706480659066a067b068c069d06af06c006d106e306f507070719072b073d074f076107740786079907ac07bf07d207e507f8080b081f08320846085a086e0882089608aa08be08d208e708fb09100925093a094f09640979098f09a409ba09cf09e509fb0a110a270a3d0a540a6a0a810a980aae0ac50adc0af30b0b0b220b390b510b690b800b980bb00bc80be10bf90c120c2a0c430c5c0c750c8e0ca70cc00cd90cf30d0d0d260d400d5a0d740d8e0da90dc30dde0df80e130e2e0e490e640e7f0e9b0eb60ed20eee0f090f250f410f5e0f7a0f960fb30fcf0fec1009102610431061107e109b10b910d710f511131131114f116d118c11aa11c911e81207122612451264128412a312c312e31303132313431363138313a413c513e5140614271449146a148b14ad14ce14f01512153415561578159b15bd15e0160316261649166c168f16b216d616fa171d17411765178917ae17d217f7181b18401865188a18af18d518fa19201945196b199119b719dd1a041a2a1a511a771a9e1ac51aec1b141b3b1b631b8a1bb21bda1c021c2a1c521c7b1ca31ccc1cf51d1e1d471d701d991dc31dec1e161e401e6a1e941ebe1ee91f131f3e1f691f941fbf1fea20152041206c209820c420f0211c2148217521a121ce21fb22272255228222af22dd230a23382366239423c223f0241f244d247c24ab24da250925382568259725c725f726272657268726b726e827182749277a27ab27dc280d283f287128a228d429062938296b299d29d02a022a352a682a9b2acf2b022b362b692b9d2bd12c052c392c6e2ca22cd72d0c2d412d762dab2de12e162e4c2e822eb72eee2f242f5a2f912fc72ffe3035306c30a430db3112314a318231ba31f2322a3263329b32d4330d3346337f33b833f1342b3465349e34d83513354d358735c235fd3637367236ae36e937243760379c37d738143850388c38c839053942397f39bc39f93a363a743ab23aef3b2d3b6b3baa3be83c273c653ca43ce33d223d613da13de03e203e603ea03ee03f213f613fa23fe24023406440a640e74129416a41ac41ee4230427242b542f7433a437d43c044034447448a44ce45124555459a45de4622466746ab46f04735477b47c04805484b489148d7491d496349a949f04a374a7d4ac44b0c4b534b9a4be24c2a4c724cba4d024d4a4d934ddc4e254e6e4eb74f004f494f934fdd5027507150bb51065150519b51e65231527c52c75313535f53aa53f65442548f54db5528557555c2560f565c56a956f75744579257e0582f587d58cb591a596959b85a075a565aa65af55b455b955be55c355c865cd65d275d785dc95e1a5e6c5ebd5f0f5f615fb36005605760aa60fc614f61a261f56249629c62f06343639763eb6440649464e9653d659265e7663d669266e8673d679367e9683f689668ec6943699a69f16a486a9f6af76b4f6ba76bff6c576caf6d086d606db96e126e6b6ec46f1e6f786fd1702b708670e0713a719571f0724b72a67301735d73b87414747074cc7528758575e1763e769b76f8775677b37811786e78cc792a798979e77a467aa57b047b637bc27c217c817ce17d417da17e017e627ec27f237f847fe5804780a8810a816b81cd8230829282f4835783ba841d848084e3854785ab860e867286d7873b879f8804886988ce8933899989fe8a648aca8b308b968bfc8c638cca8d318d988dff8e668ece8f368f9e9006906e90d6913f91a89211927a92e3934d93b69420948a94f4955f95c99634969f970a977597e0984c98b89924999099fc9a689ad59b429baf9c1c9c899cf79d649dd29e409eae9f1d9f8b9ffaa069a0d8a147a1b6a226a296a306a376a3e6a456a4c7a538a5a9a61aa68ba6fda76ea7e0a852a8c4a937a9a9aa1caa8fab02ab75abe9ac5cacd0ad44adb8ae2daea1af16af8bb000b075b0eab160b1d6b24bb2c2b338b3aeb425b49cb513b58ab601b679b6f0b768b7e0b859b8d1b94ab9c2ba3bbab5bb2ebba7bc21bc9bbd15bd8fbe0abe84beffbf7abff5c070c0ecc167c1e3c25fc2dbc358c3d4c451c4cec54bc5c8c646c6c3c741c7bfc83dc8bcc93ac9b9ca38cab7cb36cbb6cc35ccb5cd35cdb5ce36ceb6cf37cfb8d039d0bad13cd1bed23fd2c1d344d3c6d449d4cbd54ed5d1d655d6d8d75cd7e0d864d8e8d96cd9f1da76dafbdb80dc05dc8add10dd96de1cdea2df29dfafe036e0bde144e1cce253e2dbe363e3ebe473e4fce584e60de696e71fe7a9e832e8bce946e9d0ea5beae5eb70ebfbec86ed11ed9cee28eeb4ef40efccf058f0e5f172f1fff28cf319f3a7f434f4c2f550f5def66df6fbf78af819f8a8f938f9c7fa57fae7fb77fc07fc98fd29fdbafe4bfedcff6dffffffdb004300090606080605090807080a09090a0d160e0d0c0c0d1a131410161f1c21201f1c1e1e2327322a23252f251e1e2b3b2c2f3335383838212a3d413c364132373835ffdb004301090a0a0d0b0d190e0e1935241e243535353535353535353535353535353535353535353535353535353535353535353535353535353535353535353535353535ffc0001108001a001a03012200021101031101ffc400190000020301000000000000000000000000040500020706ffc4002b100001030302050109000000000000000001020311000405061213213141517314152233376182a2b1ffc400160101010100000000000000000000000000020103ffc4001a110003010101010000000000000000000000010221031112ffda000c03010002110311003f00ccd02051d7f84c958636defae2dcb76f7090b6d532483d2476a0c2429241e7222b43ba2de574f58a5e7dd71a36282d927e127985a48fb181e68f4af9487ce15b68ce1077a4cf51536d428e15c2900c804a41ec62ad14d699bc1f6274c3d91c35c655e743166cee4a206e5bcb1d93e04f2934d34d241d0d93f783894b4d3db994adcd813b809dbf9f9eb4ef1ff4cf17e8b47f6a49af9b43790b1421094a38cb3b408131d6b57cd35a49b72f0e598c7a9ec81b70a0a093f311cc19ee0d303a695b8c5d263d334669648f666390ea3fa69dc0f15661781aa7e9ffd9'));


DROP TABLE IF EXISTS "association";
CREATE TABLE "public"."association" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "city" character varying(255),
    "name" character varying(255),
    "number" bigint NOT NULL,
    "postal_code" integer NOT NULL,
    "street" character varying(255),
    "street_number" character varying(255),
    "amount_member_subscription" double precision NOT NULL,
    "registration_date" date,
    CONSTRAINT "association_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "association" ("id", "version", "city", "name", "number", "postal_code", "street", "street_number", "amount_member_subscription", "registration_date") VALUES
(1, 1,	'Regensburg',	'BeispielVerein',	225552311,	93053,	'Graf-Zeppelin-Straße',	'2C', 30, '2024-07-01');


DROP TABLE IF EXISTS "member_subscription";
CREATE TABLE "public"."member_subscription" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "association_id" integer NOT NULL,
    "month" integer NOT NULL,
    "payed" boolean NOT NULL,
    "person_id" integer NOT NULL,
    "transaction_id" integer NOT NULL,
    "year" integer NOT NULL,
    CONSTRAINT "member_subscription_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "member_subscription" ("id", "version", "association_id", "month", "payed", "person_id", "transaction_id", "year") VALUES
(1003,	0,	1,	7,	'f',	4,	0,	2024),
(1005,	0,	1,	7,	'f',	6,	0,	2024),
(1006,	0,	1,	7,	'f',	7,	0,	2024),
(1007,	0,	1,	7,	'f',	8,	0,	2024),
(1008,	0,	1,	7,	'f',	9,	0,	2024),
(1009,	0,	1,	7,	'f',	10,	0,	2024),
(1010,	0,	1,	7,	'f',	11,	0,	2024),
(1011,	0,	1,	7,	'f',	12,	0,	2024),
(1012,	0,	1,	7,	'f',	13,	0,	2024),
(1013,	0,	1,	7,	'f',	14,	0,	2024),
(1014,	0,	1,	7,	'f',	15,	0,	2024),
(1015,	0,	1,	7,	'f',	16,	0,	2024),
(1016,	0,	1,	7,	'f',	17,	0,	2024),
(1017,	0,	1,	7,	'f',	18,	0,	2024),
(1018,	0,	1,	7,	'f',	19,	0,	2024),
(1019,	0,	1,	7,	'f',	20,	0,	2024),
(1020,	0,	1,	7,	'f',	21,	0,	2024),
(1021,	0,	1,	7,	'f',	22,	0,	2024),
(1022,	0,	1,	7,	'f',	23,	0,	2024),
(1023,	0,	1,	7,	'f',	24,	0,	2024),
(1024,	0,	1,	7,	'f',	25,	0,	2024),
(1025,	0,	1,	7,	'f',	26,	0,	2024),
(1026,	0,	1,	7,	'f',	27,	0,	2024),
(1027,	0,	1,	7,	'f',	28,	0,	2024),
(1028,	0,	1,	7,	'f',	29,	0,	2024),
(1029,	0,	1,	7,	'f',	30,	0,	2024),
(1031,	0,	1,	7,	'f',	32,	0,	2024),
(1032,	0,	1,	7,	'f',	33,	0,	2024),
(1033,	0,	1,	7,	'f',	34,	0,	2024),
(1034,	0,	1,	7,	'f',	35,	0,	2024),
(1035,	0,	1,	7,	'f',	36,	0,	2024),
(1036,	0,	1,	7,	'f',	37,	0,	2024),
(1037,	0,	1,	7,	'f',	38,	0,	2024),
(1038,	0,	1,	7,	'f',	39,	0,	2024),
(1039,	0,	1,	7,	'f',	40,	0,	2024),
(1040,	0,	1,	7,	'f',	41,	0,	2024),
(1041,	0,	1,	7,	'f',	42,	0,	2024),
(1042,	0,	1,	7,	'f',	43,	0,	2024),
(1043,	0,	1,	7,	'f',	44,	0,	2024),
(1044,	0,	1,	7,	'f',	45,	0,	2024),
(1045,	0,	1,	7,	'f',	46,	0,	2024),
(1046,	0,	1,	7,	'f',	47,	0,	2024),
(1047,	0,	1,	7,	'f',	48,	0,	2024),
(1048,	0,	1,	7,	'f',	49,	0,	2024),
(1049,	0,	1,	7,	'f',	50,	0,	2024),
(1050,	0,	1,	7,	'f',	51,	0,	2024),
(1051,	0,	1,	7,	'f',	52,	0,	2024),
(1052,	0,	1,	7,	'f',	53,	0,	2024),
(1053,	0,	1,	7,	'f',	54,	0,	2024),
(1054,	0,	1,	7,	'f',	55,	0,	2024),
(1055,	0,	1,	7,	'f',	56,	0,	2024),
(1056,	0,	1,	7,	'f',	57,	0,	2024),
(1057,	0,	1,	7,	'f',	58,	0,	2024),
(1058,	0,	1,	7,	'f',	59,	0,	2024),
(1059,	0,	1,	7,	'f',	60,	0,	2024),
(1060,	0,	1,	7,	'f',	61,	0,	2024),
(1061,	0,	1,	7,	'f',	62,	0,	2024),
(1062,	0,	1,	7,	'f',	63,	0,	2024),
(1063,	0,	1,	7,	'f',	64,	0,	2024),
(1064,	0,	1,	7,	'f',	65,	0,	2024),
(1065,	0,	1,	7,	'f',	66,	0,	2024),
(1066,	0,	1,	7,	'f',	67,	0,	2024),
(1067,	0,	1,	7,	'f',	68,	0,	2024),
(1068,	0,	1,	7,	'f',	69,	0,	2024),
(1069,	0,	1,	7,	'f',	70,	0,	2024),
(1070,	0,	1,	7,	'f',	71,	0,	2024),
(1071,	0,	1,	7,	'f',	72,	0,	2024),
(1072,	0,	1,	7,	'f',	73,	0,	2024),
(1073,	0,	1,	7,	'f',	74,	0,	2024),
(1074,	0,	1,	7,	'f',	75,	0,	2024),
(1075,	0,	1,	7,	'f',	76,	0,	2024),
(1076,	0,	1,	7,	'f',	77,	0,	2024),
(1077,	0,	1,	7,	'f',	78,	0,	2024),
(1078,	0,	1,	7,	'f',	79,	0,	2024),
(1079,	0,	1,	7,	'f',	80,	0,	2024),
(1080,	0,	1,	7,	'f',	81,	0,	2024),
(1081,	0,	1,	7,	'f',	82,	0,	2024),
(1082,	0,	1,	7,	'f',	83,	0,	2024),
(1083,	0,	1,	7,	'f',	84,	0,	2024),
(1084,	0,	1,	7,	'f',	85,	0,	2024),
(1085,	0,	1,	7,	'f',	86,	0,	2024),
(1086,	0,	1,	7,	'f',	87,	0,	2024),
(1087,	0,	1,	7,	'f',	88,	0,	2024),
(1088,	0,	1,	7,	'f',	89,	0,	2024),
(1089,	0,	1,	7,	'f',	90,	0,	2024),
(1090,	0,	1,	7,	'f',	91,	0,	2024),
(1091,	0,	1,	7,	'f',	92,	0,	2024),
(1092,	0,	1,	7,	'f',	93,	0,	2024),
(1093,	0,	1,	7,	'f',	94,	0,	2024),
(1094,	0,	1,	7,	'f',	95,	0,	2024),
(1095,	0,	1,	7,	'f',	96,	0,	2024),
(1096,	0,	1,	7,	'f',	97,	0,	2024),
(1097,	0,	1,	7,	'f',	98,	0,	2024),
(1098,	0,	1,	7,	'f',	99,	0,	2024),
(1099,	0,	1,	7,	'f',	100,	0,	2024),
(1000,	1,	1,	7,	't',	1,	0,	2024),
(1001,	1,	1,	7,	't',	2,	0,	2024),
(1030,	1,	1,	7,	't',	31,	0,	2024),
(1002,	1,	1,	7,	't',	3,	1451,	2024),
(1004,	1,	1,	7,	't',	5,	1452,	2024);

DROP TABLE IF EXISTS "output";
CREATE TABLE "public"."output" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "amount" double precision NOT NULL,
    "association_id" integer NOT NULL,
    "date" date,
    "note" character varying(255),
    "outdated" boolean NOT NULL,
    "person_id" integer NOT NULL,
    "strain_id" integer NOT NULL,
    CONSTRAINT "output_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "output" ("id", "version", "amount", "association_id", "date", "note", "outdated", "person_id", "strain_id") VALUES
(1351,	0,	1,	1,	'2024-07-04',	'',	'f',	1,	1103),
(1551,	0,	2,	1,	'2024-07-06',	'Erste Charge',	'f',	5,	1101),
(1552,	0,	2,	1,	'2024-07-08',	'',	'f',	4,	1103);


DROP TABLE IF EXISTS "person";
CREATE TABLE "public"."person" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "association_id" integer NOT NULL,
    "association_role" smallint,
    "date_of_birth" date,
    "date_of_higher_role" date,
    "date_of_registration" date,
    "email" character varying(255),
    "first_name" character varying(255),
    "important" boolean NOT NULL,
    "last_name" character varying(255),
    "phone" character varying(255),
    "member_number" integer NOT NULL,
    CONSTRAINT "person_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "person" ("version", "association_id", "id", "first_name", "last_name", "email", "phone", "date_of_birth", "association_role", "important", "date_of_registration", "member_number") VALUES
(1, 1, 1,'Eula','Lane','eula.lane@jigrormo.ye','(762) 526-5961','1954-10-14',0,false, '2024-06-21',92589),
(1, 1, 2,'Barry','Rodriquez','barry.rodriquez@zun.mm','(267) 955-5124','2013-10-14',0,false, '2024-06-22',85025),
(1, 1, 3,'Eugenia','Selvi','eugenia.selvi@capfad.vn','(680) 368-2192','1973-09-29',0,false, '2024-06-23',85527),
(1, 1, 4,'Alejandro','Miles','alejandro.miles@dec.bn','(281) 301-2039','2013-11-16',0,false, '2024-06-27',95762),
(1, 1, 5,'Cora','Tesi','cora.tesi@bivo.yt','(600) 616-7955','1972-01-14',0,false, '2024-06-10',76792),
(1, 1, 6,'Marguerite','Ishii','marguerite.ishii@judbilo.gn','(882) 813-1374','1937-10-11',0,false, '2024-06-15',58942),
(1, 1, 7,'Mildred','Jacobs','mildred.jacobs@joraf.wf','(642) 665-1763','1967-05-16',0,false, '2024-06-17',17082),
(1, 1, 8,'Gene','Goodman','gene.goodman@kem.tl','(383) 458-2132','2010-03-26',0,false, '2024-06-16',7761),
(1, 1, 9,'Lettie','Bennett','lettie.bennett@odeter.bb','(769) 335-6771','1959-05-31',0,false, '2024-06-20',55655),
(1, 1, 10,'Mabel','Leach','mabel.leach@lisohuje.vi','(803) 586-8035','1946-05-07',0,false, '2024-06-21',34366),
(1, 1, 11,'Jordan','Miccinesi','jordan.miccinesi@duod.gy','(531) 919-2280','1982-06-18',0,false, '2024-06-20',55525),
(1, 1, 12,'Marie','Parkes','marie.parkes@nowufpus.ph','(814) 667-8937','1943-04-19',0,false, '2024-06-19',51244),
(1, 1, 13,'Rose','Gray','rose.gray@kagu.hr','(713) 311-8766','1958-04-18',0,false, '2024-06-18',27672),
(1, 1, 14,'Garrett','Stokes','garrett.stokes@fef.bg','(381) 421-2371','2009-01-27',0,false, '2024-06-20',33845),
(1, 1, 15,'Barbara','Matthieu','barbara.matthieu@derwogi.jm','(940) 463-7299','1930-01-23',0,false, '2024-06-18',42070),
(1, 1, 16,'Jean','Rhodes','jean.rhodes@wehovuce.gu','(777) 435-9570','1949-07-02',0,false, '2024-06-15',95705),
(1, 1, 17,'Jack','Romoli','jack.romoli@zamum.bw','(517) 393-9630','1975-04-28',0,false, '2024-06-16',90561),
(1, 1, 18,'Pearl','Holden','pearl.holden@dunebuh.cr','(711) 904-3669','1949-08-23',0,false, '2024-06-17',84975),
(1, 1, 19,'Belle','Montero','belle.montero@repiwid.si','(935) 404-4792','1932-09-15',0,false, '2024-06-18',46724),
(1, 1, 20,'Olive','Molina','olive.molina@razuppa.ga','(935) 267-8492','1934-03-28',0,false, '2024-06-14',10149),
(1, 1, 21,'Minerva','Todd','minerva.todd@kulmenim.ad','(763) 948-4815','1950-08-29',0,false, '2024-06-21',51546),
(1, 1, 22,'Bobby','Pearson','bobby.pearson@ib.kg','(238) 240-2561','2015-01-21',0,true, '2024-06-20',79251),
(1, 1, 23,'Larry','Ciappi','larry.ciappi@ba.lk','(410) 257-1723','1996-02-06',0,false, '2024-06-20',97047),
(1, 1, 24,'Ronnie','Salucci','ronnie.salucci@tohhij.lv','(566) 726-3346','1974-08-10',0,false, '2024-06-21',35004),
(1, 1, 25,'Walter','Grossi','walter.grossi@tuvo.sa','(416) 906-7221','1987-09-28',0,false, '2024-06-22',35883),
(1, 1, 26,'Frances','Koopmans','frances.koopmans@foga.tw','(611) 712-1562','1966-11-19',0,false, '2024-06-20',64450),
(1, 1, 27,'Frances','Fujimoto','frances.fujimoto@uswuzzub.jp','(919) 887-8542','1936-04-24',0,false, '2024-06-14',66275),
(1, 1, 28,'Olivia','Vidal','olivia.vidal@hivwerip.vc','(982) 684-7650','1933-08-02',0,false, '2024-06-18',73281),
(1, 1, 29,'Edna','Henry','edna.henry@gugusu.rw','(811) 931-8202','1947-06-16',0,false, '2024-06-19',47588),
(1, 1, 30,'Lydia','Brun','lydia.brun@zedekak.md','(927) 400-3928','1929-07-29',0,false, '2024-06-21',26867),
(1, 1, 31,'Jay','Blake','jay.blake@ral.mk','(365) 345-1498','2009-08-16',0,false, '2024-06-22',89625),
(1, 1, 32,'Isabel','Serafini','isabel.serafini@turuhu.bh','(656) 968-9869','1973-08-24',0,false, '2024-06-15',71585),
(1, 1, 33,'Rebecca','Carter','rebecca.carter@omjo.et','(739) 612-6585','1959-03-13',0,false, '2024-06-18',16374),
(1, 1, 34,'Maurice','Fabbrini','maurice.fabbrini@rig.bh','(485) 521-2687','1993-01-16',0,false, '2024-06-20',70554),
(1, 1, 35,'Ollie','Turnbull','ollie.turnbull@sicewap.org','(835) 620-3330','1944-09-12',0,false, '2024-06-20',5487),
(1, 1, 36,'Jerry','Hopkins','jerry.hopkins@fo.mh','(211) 851-5960','2014-10-07',0,true, '2024-06-16',88080),
(1, 1, 37,'Nora','Lyons','nora.lyons@gegijap.na','(811) 311-5257','1945-07-21',0,false, '2024-06-10',92734),
(1, 1, 38,'Anne','Weiß','anne.weiß@kuvesa.pe','(843) 836-3759','1941-02-06',0,false, '2024-06-11',27389),
(1, 1, 39,'Louise','Gauthier','louise.gauthier@lapahu.mt','(913) 235-1856','1930-05-19',0,false, '2024-06-10',86762),
(1, 1, 40,'Lloyd','Fani','lloyd.fani@zev.ru','(467) 487-7239','1992-04-22',0,false, '2024-06-20',39648),
(1, 1, 41,'Maud','Dunn','maud.dunn@nabeaga.ni','(724) 340-3634','1955-07-15',0,false, '2024-06-21',92991),
(1, 1, 42,'Henry','Gigli','henry.gigli@kaot.ps','(413) 229-8428','1988-09-16',0,false, '2024-06-20',77794),
(1, 1, 43,'Virgie','Werner','virgie.werner@tawuctuj.cf','(886) 292-9749','1941-10-26',0,false, '2024-06-22',23709),
(1, 1, 44,'Gregory','Cozzi','gregory.cozzi@eh.ru','(418) 472-1239','1994-08-19',0,false, '2024-06-21',96969),
(1, 1, 45,'Lucinda','Gil','lucinda.gil@fajjusuz.kr','(961) 233-3461','1934-08-10',0,false, '2024-06-20',13843),
(1, 1, 46,'Gertrude','Verbeek','gertrude.verbeek@pave.cc','(605) 226-4037','1964-08-23',0,false, '2024-06-17',41001),
(1, 1, 47,'Mattie','Graham','mattie.graham@ispaviw.gt','(719) 765-1705','1957-06-21',0,false, '2024-06-20',94058),
(1, 1, 48,'Bryan','Shaw','bryan.shaw@ha.ee','(232) 228-5539','2019-02-15',0,true, '2024-06-18',50084),
(1, 1, 49,'Essie','Adams','essie.adams@iliat.cw','(768) 554-8377','1958-08-27',0,false, '2024-06-16',23583),
(1, 1, 50,'Gary','Osborne','gary.osborne@do.ga','(311) 731-7079','2009-06-15',0,false, '2024-06-24',15016),
(1, 1, 51,'Richard','Silva','richard.silva@wi.lc','(207) 554-6244','2015-01-25',0,true, '2024-06-19',48945),
(1, 1, 52,'Dustin','Pestelli','dustin.pestelli@iwage.la','(558) 913-2855','1978-05-31',0,false, '2024-06-24',1730),
(1, 1, 53,'Henrietta','Hilton','henrietta.hilton@joopoju.pn','(832) 759-6654','1944-03-24',0,false, '2024-06-20',38760),
(1, 1, 54,'Francisco','Giordano','francisco.giordano@gojawu.tn','(482) 736-8079','1988-08-31',0,false, '2024-06-24',39262),
(1, 1, 55,'Cynthia','Sardi','cynthia.sardi@afigoh.mm','(677) 345-2680','1974-02-19',0,false, '2024-06-24',8277),
(1, 1, 56,'Lula','Testi','lula.testi@benom.tj','(610) 374-7581','1971-12-17',0,false, '2024-06-24',97709),
(1, 1, 57,'Bess','Lucas','bess.lucas@jevakbe.cd','(982) 583-8067','1929-05-27',0,false, '2024-06-24',30896),
(1, 1, 58,'Linnie','Driessen','linnie.driessen@darhow.tr','(680) 266-3167','1968-02-25',0,false, '2024-06-20',91934),
(1, 1, 59,'Eva','Tesi','eva.tesi@dupid.cf','(611) 955-4652','1972-01-07',0,false, '2024-06-24',89626),
(1, 1, 60,'Augusta','Sakai','augusta.sakai@comouc.ee','(940) 714-8088','1936-10-14',0,false, '2024-06-20',67187),
(1, 1, 61,'Mathilda','Schwarz','mathilda.schwarz@igunisi.ao','(868) 481-5125','1942-02-07',0,false, '2024-06-20',99942),
(1, 1, 62,'Joe','Riley','joe.riley@pe.vu','(225) 395-2772','2017-07-18',0,true, '2024-06-22',64517),
(1, 1, 63,'Leon','McGee','leon.mcgee@puk.se','(365) 837-6888','2011-05-04',0,false, '2024-06-20',47705),
(1, 1, 64,'Florence','Viviani','florence.viviani@vegub.no','(606) 352-8734','1970-02-24',0,false, '2024-06-21',97952),
(1, 1, 65,'Lee','Miceli','lee.miceli@rucwi.pf','(555) 800-7339','1982-05-30',0,false, '2024-06-21',48104),
(1, 1, 66,'Celia','Sodi','celia.sodi@agijit.iq','(657) 357-3671','1973-03-22',0,false, '2024-06-20',17883),
(1, 1, 67,'Aaron','Misuri','aaron.misuri@loolu.lu','(523) 789-5485','1982-02-21',0,false, '2024-06-22',23185),
(1, 1, 68,'Fanny','Parkinson','fanny.parkinson@tupwovali.cw','(766) 966-7387','1949-12-03',0,false, '2024-06-20',97341),
(1, 1, 69,'Phoebe','Vitale','phoebe.vitale@hidge.fo','(672) 613-2954','1970-04-05',0,false, '2024-06-23',89402),
(1, 1, 70,'Edith','Brennan','edith.brennan@liowci.ir','(803) 549-9387','1948-09-14',0,false, '2024-06-22',78655),
(1, 1, 71,'Jeremy','Marilli','jeremy.marilli@vesa.pf','(526) 435-1819','1984-03-19',0,false, '2024-06-21',50273),
(1, 1, 72,'Kathryn','Huet','kathryn.huet@wupikdoh.by','(937) 855-5936','1927-09-06',0,false, '2024-06-20',6313),
(1, 1, 73,'Lelia','Matsuo','lelia.matsuo@dajsiphaj.az','(960) 335-6192','1935-09-17',0,false, '2024-06-19',13692),
(1, 1, 74,'Virginia','Woods','virginia.woods@soofpe.ht','(735) 809-2611','1955-06-25',0,false, '2024-06-18',87037),
(1, 1, 75,'Sally','Aoki','sally.aoki@aruzusjas.tc','(857) 797-7918','1937-08-26',0,false, '2024-06-18',51359),
(1, 1, 76,'Isabelle','de Ridder','isabelle.deridder@ufeco.in','(659) 331-1543','1963-06-12',0,false, '2024-06-20',17597),
(1, 1, 77,'Rosie','Murphy','rosie.murphy@uneehi.id','(759) 639-8597','1958-12-13',0,false, '2024-06-19',66957),
(1, 1, 78,'Lou','Meyer','lou.meyer@hahinaba.gm','(942) 352-4854','1929-09-07',0,false, '2024-06-17',34263),
(1, 1, 79,'Rodney','Love','rodney.love@zun.ph','(247) 867-8287','2013-01-01',0,false, '2024-06-13',17688),
(1, 1, 80,'Kenneth','Bianchini','kenneth.bianchini@jo.ws','(302) 793-9936','2001-12-24',0,false, '2024-06-19',57183),
(1, 1, 81,'Essie','Dietrich','essie.dietrich@goltuefo.mn','(861) 740-6628','1939-08-26',0,false, '2024-06-20',17360),
(1, 1, 82,'Leila','Simon','leila.simon@lupuwuzo.gw','(953) 866-9992','1931-05-23',0,false, '2024-06-20',886),
(1, 1, 83,'Eva','Pierre','eva.pierre@reduzris.ee','(915) 491-8384','1928-06-03',0,false, '2024-06-17',89617),
(1, 1, 84,'Landon','Moretti','landon.moretti@pubsav.sk','(584) 909-6235','1981-09-06',0,false, '2024-06-19',87820),
(1, 1, 85,'Mittie','Sardi','mittie.sardi@lullip.nf','(673) 849-4256','1974-02-14',0,false, '2024-06-12',5099),
(1, 1, 86,'Corey','McDaniel','corey.mcdaniel@aba.tc','(268) 208-9643','2013-04-20',0,false, '2024-06-12',18170),
(1, 1, 87,'Hester','Stein','hester.stein@kettujwo.eu','(873) 489-6641','1940-02-11',0,false, '2024-06-18',75378),
(1, 1, 88,'Danny','Lowe','danny.lowe@ju.sd','(243) 974-5539','2014-05-26',0,true, '2024-06-17',82595),
(1, 1, 89,'Lillie','Winter','lillie.winter@vioburez.vi','(816) 699-1291','1946-04-20',0,false, '2024-05-18',56478),
(1, 1, 90,'Brandon','Borchi','brandon.borchi@ig.al','(319) 401-1090','2001-02-09',0,false, '2024-06-15',61021),
(1, 1, 91,'Isaac','Bernardi','isaac.bernardi@omu.bj','(359) 691-6408','2002-07-23',0,false, '2024-06-11',88664),
(1, 1, 92,'Clyde','Crawford','clyde.crawford@luw.dz','(273) 892-4646','2019-07-26',0,true, '2024-06-09',52712),
(1, 1, 93,'Paul','Sherman','paul.sherman@pi.cf','(304) 610-2881','2009-01-01',0,false, '2024-06-20',13304),
(1, 1, 94,'Craig','Russell','craig.russell@zu.nz','(237) 969-2900','2020-10-29',0,true, '2024-06-06',49840),
(1, 1, 95,'John','Sutton','john.sutton@ag.ee','(207) 424-6468','2014-01-10',0,false, '2024-06-10',12635),
(1, 1, 96,'Francisco','Formigli','francisco.formigli@fopav.tn','(481) 661-8179','1990-12-25',0,false, '2024-06-4',35964),
(1, 1, 97,'Gary','Baker','gary.baker@ji.cf','(212) 510-3444','2023-01-14',0,true, '2024-06-20',48678),
(1, 1, 98,'Earl','Giovannoni','earl.giovannoni@lojet.ge','(433) 862-3076','1988-05-24',0,false, '2024-06-16',93058),
(1, 1, 99,'Helen','Zanieri','helen.zanieri@ukve.tn','(619) 506-4452','1969-10-06',0,false, '2024-06-10',69664),
(1, 1, 100,'Agnes','Toccafondi','agnes.toccafondi@viipo.ae','(616) 688-6883','1971-10-26',0,false, '2024-06-21', 15372);

DROP TABLE IF EXISTS "recurring_payment";
CREATE TABLE "public"."recurring_payment" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "amount" double precision NOT NULL,
    "association_id" integer NOT NULL,
    "day_of_payment" integer NOT NULL,
    "is_active" boolean NOT NULL,
    "note" character varying(255),
    "payment_method" smallint,
    "person_id" integer NOT NULL,
    "time_declaration" smallint,
    "timezone" smallint,
    "title" character varying(255),
    "type" smallint,
    CONSTRAINT "recurring_payment_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "recurring_payment" ("id", "version", "amount", "association_id", "day_of_payment", "is_active", "note", "payment_method", "person_id", "time_declaration", "timezone", "title", "type") VALUES
(1601,	1,	20,	1,	1,	't',	'',	2,	0,	7,	1,	'Internetvertrag',	0);

DROP TABLE IF EXISTS "recurring_payment_transactions";
CREATE TABLE "public"."recurring_payment_transactions" (
    "recurring_payment_id" bigint NOT NULL,
    "transactions_id" bigint NOT NULL,
    CONSTRAINT "uk_s60e3rw3kq10b9ergen0pvu7t" UNIQUE ("transactions_id")
) WITH (oids = false);

INSERT INTO "recurring_payment_transactions" ("recurring_payment_id", "transactions_id") VALUES
(1601,	1651);

DROP TABLE IF EXISTS "strain";
CREATE TABLE "public"."strain" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "amount" double precision NOT NULL,
    "amount_per_member" double precision NOT NULL,
    "association_id" integer NOT NULL,
    "date_finished" date,
    "date_planted" date,
    "name" character varying(255),
    "status" smallint,
    "thc" double precision NOT NULL,
    "amount_of_plants" integer NOT NULL,
    CONSTRAINT "strain_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "strain" ("id", "version", "amount", "amount_per_member", "association_id", "date_finished", "date_planted", "name", "status", "thc", "amount_of_plants") VALUES
(1102,	1,	400,	4,	1,	'2024-07-04',	'2024-07-04',	'White Widow',	4,	20,	0),
(1101,	1,	198,	2,	1,	'2024-07-04',	'2024-07-04',	'Silver Haze',	3,	15,	0),
(1103,	2,	97,	1,	1,	NULL,	'2024-07-04',	'Jack Herer',	1,	10,	0);


DROP TABLE IF EXISTS "transaction";
CREATE TABLE "public"."transaction" (
    "id" bigint NOT NULL,
    "amount" double precision NOT NULL,
    "association_id" integer NOT NULL,
    "date_of_transaction" date,
    "member_id" integer NOT NULL,
    "note" character varying(255),
    "payment_method" smallint,
    "type" smallint,
    "version" integer NOT NULL,
    CONSTRAINT "transaction_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "transaction" ("id", "amount", "association_id", "date_of_transaction", "member_id", "note", "payment_method", "type", "version") VALUES
(1251,	12,	1,	'2024-07-04',	1,	'',	0,	1,	0),
(1451,	30,	1,	'2024-07-06',	3,	'Mitgliedsbeitrag',	0,	1,	0),
(1651,	20,	1,	'2024-07-10',	0,	'',	2,	0,	0);

DROP TABLE IF EXISTS "user_roles";
CREATE TABLE "public"."user_roles" (
    "user_id" bigint NOT NULL,
    "roles" character varying(255)
) WITH (oids = false);
insert into user_roles (user_id, roles) values ('1', 'USER');
insert into user_roles (user_id, roles) values ('2', 'USER');
insert into user_roles (user_id, roles) values ('2', 'ADMIN');


DROP TABLE IF EXISTS "waiting_person";
CREATE TABLE "public"."waiting_person" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "association_id" integer NOT NULL,
    "date_of_birth" date,
    "date_of_registration" date,
    "email" character varying(255),
    "first_name" character varying(255),
    "last_name" character varying(255),
    "phone" character varying(255),
    CONSTRAINT "waiting_person_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "waiting_person" ("id", "version", "association_id", "date_of_birth", "date_of_registration", "email", "first_name", "last_name", "phone") VALUES
(1151,	0,	1,	'2000-08-16',	'2024-07-04',	'max.mustermann@mustermail.de',	'Max',	'Mustermann',	'+49123456789');


DROP TABLE IF EXISTS "working_unit";
CREATE TABLE "public"."working_unit" (
    "id" bigint NOT NULL,
    "version" integer NOT NULL,
    "association_id" integer NOT NULL,
    "category" character varying(255),
    "date_begin" date,
    "date_end" date,
    "note" character varying(255),
    "person_id" bigint,
    "person_name" character varying(255),
    "working_hours" integer NOT NULL,
    CONSTRAINT "working_unit_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "working_unit" ("id", "version", "association_id", "category", "date_begin", "date_end", "note", "person_id", "person_name", "working_hours") VALUES
(1301,	0,	1,	'Gärtnereiarbeiten',	'2024-07-04',	'2024-07-04',	'',	2,	'Barry Rodriquez',	60),
(1302,	0,	1,	'Verwaltungsdienst',	'2024-07-04',	'2024-07-04',	'',	4,	'Alejandro Miles',	120),
(1501,	0,	1,	'Gärtnereiarbeiten',	'2024-07-06',	'2024-07-06',	'test',	5,	'Cora Tesi',	119);

ALTER TABLE ONLY "public"."recurring_payment_transactions" ADD CONSTRAINT "fk1coqhk280spvwr82x2lv8i2n7" FOREIGN KEY (transactions_id) REFERENCES transaction(id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."recurring_payment_transactions" ADD CONSTRAINT "fk9nxklv5gicy3hl7vnwo0fb49e" FOREIGN KEY (recurring_payment_id) REFERENCES recurring_payment(id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."user_roles" ADD CONSTRAINT "fkq0h6vpf3crn504yyep1hdv0vc" FOREIGN KEY (user_id) REFERENCES application_user(id) NOT DEFERRABLE;
