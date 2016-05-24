#------------------------------------------------------------------------------------------------------------------
# Radiology Module Database Test Data
#  This file is used to setup the DB for the Radiology Module.
#------------------------------------------------------------------------------------------------------------------
# Setup Role Radiology: Referring physician 
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Add Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Add Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Add Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Add Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Delete Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Edit Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Edit Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Edit Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Care Settings');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Concepts');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Encounter Roles');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Observations');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Patients');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Providers');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Users');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Visit Attribute Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Visit Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Get Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','Patient Dashboard - View Radiology Section');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','View Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Referring physician','View Patients');

# Setup User Raphael Referrer, Radiology: Referring physician, Password is "RaphaelReferrer12345"
INSERT INTO `person` VALUES (8,'M',NULL,0,0,NULL,NULL,1,'2016-05-24 09:14:38',NULL,NULL,0,NULL,NULL,NULL,'21062575-c29e-4409-8c0d-beb533dc22bb',0,NULL);
INSERT INTO `person_name` VALUES (7,0,8,NULL,'Raphael',NULL,NULL,'Referrer',NULL,NULL,NULL,1,'2016-05-24 09:14:38',0,NULL,NULL,NULL,NULL,NULL,'3da0978b-60fb-4444-9de2-0a1f976848ed');
INSERT INTO `users` VALUES (6,'6-7','RaphaelR','eeb705814293c6bc835ab72ba55467ea739bca1cfc81e4da22e266d62ff792632ce9c5ecbcfb51485b0371c734e68c9969a90e872e51ee21658e6629f15da07a','36a22064b91cbcf0ce45980beff5d79ce1e9d410108d7145594ce4afe246c707f97bb885fda5008e3eab1e6f6f4768286667c726724e36e2da710121cb76177d','',NULL,1,'2016-05-24 09:14:38',1,'2016-05-24 09:14:38',8,0,NULL,NULL,NULL,'4592ced3-7c55-4b2a-9a6b-bdea83a64e0e');

# Assign User to Role
INSERT INTO `user_role` VALUES (6,'Radiology: Referring physician');

##############################################################################################################################################

# Setup Role Radiology: Scheduler
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Care Settings');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Concepts');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Encounter Roles');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Patients');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Providers');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Users');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Visit Attribute Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Visit Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Get Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','Patient Dashboard - View Radiology Section');
INSERT INTO `role_privilege` VALUES ('Radiology: Scheduler','View Orders');


# Setup User Steve Scheduler, Radiology: Scheduler, Password is "SteveScheduler12345"
INSERT INTO `person` VALUES (9,'M',NULL,0,0,NULL,NULL,1,'2016-05-24 12:59:42',NULL,NULL,0,NULL,NULL,NULL,'4e51c587-cf16-48cc-acce-9187d307f59c',0,NULL);
INSERT INTO `person_name` VALUES (8,0,9,NULL,'Steve',NULL,NULL,'Scheduler',NULL,NULL,NULL,1,'2016-05-24 12:59:42',0,NULL,NULL,NULL,NULL,NULL,'ca499806-d45e-4743-9c21-1daaf99af7b7');
INSERT INTO `users` VALUES (7,'7-5','SteveS','c37405d96fa04edb1c8fe754aa42c037edd2e9453affad273fb442197a10e2e22224b1dacb3fe0366f635569b1217816dbc042f14454e293acac61b1a0d55457','85d0473febd6915da0730e959008cb64a60447f2bf48ea233fb3a55bf16187386b1837b0ed62eb48b7c025f35b1c8abb90bc06a9a8df1ea095b04b12b1a2ba7d','',NULL,1,'2016-05-24 12:59:42',1,'2016-05-24 12:59:42',9,0,NULL,NULL,NULL,'086444a6-10df-4b51-b690-bb201800b1da');

# Assign User to Role
INSERT INTO `user_role` VALUES (7,'Radiology: Scheduler');

##############################################################################################################################################

# Setup Role Radiology: Performing physician
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Care Settings');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Concepts');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Encounter Roles');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Patients');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Providers');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Users');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Visit Attribute Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Visit Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Get Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','Patient Dashboard - View Radiology Section');
INSERT INTO `role_privilege` VALUES ('Radiology: Performing physician','View Orders');

# Setup User Peter Performer, Radiology: Performing physician, Password is "PeterPerformer12345"
INSERT INTO `person` VALUES (10,'M',NULL,0,0,NULL,NULL,1,'2016-05-24 14:00:28',NULL,NULL,0,NULL,NULL,NULL,'9a096a0c-14b8-4fb1-ac60-4b9fbbb3e407',0,NULL);
INSERT INTO `person_name` VALUES (9,0,10,NULL,'Peter',NULL,NULL,'Performer',NULL,NULL,NULL,1,'2016-05-24 14:00:28',0,NULL,NULL,NULL,NULL,NULL,'95d4b275-1d15-4d28-90a8-08bebdc55776');
INSERT INTO `users` VALUES (8,'8-3','PeterP','808fba33e51ef8ac3bfa57aa03f9a134366ec3e2683505b58b2ab4a8ff78367920e61b5fc4fe0789909c35f9c15a9c045827a6a1a832fb4141ccffcff946f9a6','05de7c1a22e333f89c6f32faecff90b5a02fd125bbdbd991b7dd3e958423a4488a7bc3f99c26b1f8b4fa2b07c96d502b88ed8641651d71c5d5a488ef94fe683b','',NULL,1,'2016-05-24 14:00:28',1,'2016-05-24 14:00:28',10,0,NULL,NULL,NULL,'21db08e3-97ab-4133-8c71-e9de4273c6d6');

# Assign User to Role
INSERT INTO `user_role` VALUES (8,'Radiology: Performing physician');

##############################################################################################################################################

# Setup Role Radiology: Reading physician
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Add Radiology Reports');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Delete Radiology Reports');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Edit Radiology Reports');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Care Settings');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Concepts');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Encounter Roles');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Encounters');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Patients');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Providers');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Radiology Orders');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Radiology Reports');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Users');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Visit Attribute Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Visit Types');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Get Visits');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','Patient Dashboard - View Radiology Section');
INSERT INTO `role_privilege` VALUES ('Radiology: Reading physician','View Orders');

# Setup User Rady Reader, Radiology: Reading physician, Password is "RadyReader12345"
INSERT INTO `person` VALUES (11,'M',NULL,0,0,NULL,NULL,1,'2016-05-24 14:38:56',NULL,NULL,0,NULL,NULL,NULL,'6b4f1559-cdbd-48e0-9aef-747a234e083a',0,NULL);
INSERT INTO `person_name` VALUES (10,0,11,NULL,'Rady',NULL,NULL,'Reader',NULL,NULL,NULL,1,'2016-05-24 14:38:56',0,NULL,NULL,NULL,NULL,NULL,'90324075-ffa7-4eb7-b4b7-83831068547d');
INSERT INTO `users` VALUES (9,'9-1','RadyR','186c304947d0cef29a7d13cae3b4f02bc109c0d4b119c522928ccf3d6888e0aadeafa14e8c8f7198a0ee10ed6fd7e883b0667db3de8fe777c2d5363e7222ecd9','fc7a3f8b91ecb4a3d160715c0da2068379e05e823cee622bb5b98ce9bc16501bdcee66e6759c4de6f244f64f37791becee7cd7492aea07be06afd453b7a27e8e','',NULL,1,'2016-05-24 14:38:56',1,'2016-05-24 14:38:56',11,0,NULL,NULL,NULL,'c23d87c1-efd2-4c77-84b8-af58d684d22c');

# Assign User to Role
INSERT INTO `user_role` VALUES (9,'Radiology: Reading physician');

##############################################################################################################################################
