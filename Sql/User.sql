use remit_test;
set names utf8;
CREATE TABLE `user` (
  `id` INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `uno` INT(10) NOT NULL COMMENT '工号',  
  `name` varchar(32) NOT NULL COMMENT '用户名',
  `email` varchar(64) NOT NULL COMMENT '邮箱',
  `mobile` varchar(16) NOT NULL COMMENT '手机',
  `role` INT(10) NOT NULL COMMENT '角色'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;