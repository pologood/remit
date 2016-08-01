use remit_test;
set names utf8;
CREATE TABLE `app` (
  `id` INT(10) NOT NULL PRIMARY KEY,
  `name` varchar(8) NOT NULL COMMENT '业务名',  
  `signKey` varchar(32) NOT NULL COMMENT '秘钥',
  `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;