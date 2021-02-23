CREATE TABLE `chat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT 0,
  `creator_id` bigint(20) NOT NULL,
  `organization_id` bigint(20),
  PRIMARY KEY (`id`),
  CONSTRAINT `chat_creator_id` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`),
  CONSTRAINT `chat_organization_id` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `chat_membership` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `admin` bit(1) NOT NULL DEFAULT 0,
  `user_id` bigint(20) NOT NULL,
  `chat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `chat_membership_user_chat` (`user_id`, `chat_id`),
  CONSTRAINT `chat_membership_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `chat_membership_chat_id` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `message` varchar(1000) COLLATE utf8_bin DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `chat_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `chat_message_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `chat_message_chat_id` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `chat_file_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `chat_message_id` bigint(20) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `chat_file_attachment_chat_message` FOREIGN KEY (`chat_message_id`) REFERENCES `chat_message` (`id`),
  CONSTRAINT `chat_file_attachment_file_id` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `chat_unread_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `organization_id` bigint(20),
  `chat_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `chat_message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_chat_unread_message` (`organization_id`, `chat_id`, `user_id`, `chat_message_id`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_bin;