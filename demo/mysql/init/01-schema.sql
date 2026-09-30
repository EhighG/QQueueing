-- 데모 백엔드(tes24)의 스키마.
-- 커밋 811abe4에서 삭제된 test/test_dump.sql의 테이블 정의를 옮겼다(데이터는 없었다).
-- MySQL 이미지는 데이터 디렉터리가 비어 있을 때 한 번만 이 파일을 MYSQL_DATABASE(demo)에 실행한다.

CREATE TABLE `members` (
  `member_id` int NOT NULL AUTO_INCREMENT,
  `access_token` varchar(256) DEFAULT NULL,
  `refresh_token` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `enqueue_logs` (
  `member_id` int NOT NULL,
  `enqueue_time` timestamp NOT NULL,
  `sequence_number` int DEFAULT NULL,
  KEY `member_id` (`member_id`),
  CONSTRAINT `enqueue_logs_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `dequeue_logs` (
  `member_id` int NOT NULL,
  `dequeue_time` timestamp NOT NULL,
  `sequence_number` int DEFAULT NULL,
  KEY `member_id` (`member_id`),
  CONSTRAINT `dequeue_logs_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
