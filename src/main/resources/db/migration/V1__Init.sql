CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `organization` (created_at, updated_at, name) VALUES (NOW(), NOW(), 'Initial Organization');

CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `organization_role` bit(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `organization_role_name` (`organization_role`, `name`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `role` (id, name, organization_role) VALUES (1, 'SUPER_ADMIN', 0), (2, 'ORGANIZATION_OWNER', 1),
(3, 'ORGANIZATION_MEMBER', 1);

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT 0,
  `deleted` bit(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `organization_id` bigint(20),
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role` (`organization_id`, `user_id`, `role_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `user_role_organization_id` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `user` (created_at, updated_at, username, password, enabled) VALUES
(NOW(), NOW(), 'admin', '$2y$12$BqKJK7vx0JxRtdKt4/E0H.dZpewCA5E9uMq1lCMuODyTnplXi2stS', 1);

INSERT INTO `user_role` (user_id, role_id, created_at, updated_at, organization_id) VALUES
((SELECT MAX(id) FROM user), 1, NOW(), NOW(), null),
((SELECT MAX(id) FROM user), 2, NOW(), NOW(), (SELECT MAX(id) FROM organization));

CREATE TABLE `file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `uuid` varchar(255) COLLATE utf8_bin NOT NULL,
  `original_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `url` varchar(255) COLLATE utf8_bin NOT NULL,
  `file_type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `access_type` int NOT NULL DEFAULT 0,
  `size` bigint(20) NOT NULL,
  `width` bigint(20) DEFAULT NULL,
  `height` bigint(20) DEFAULT NULL,
  `length` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_uuid` (`uuid`),
  CONSTRAINT `file_organization_id` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `file_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;