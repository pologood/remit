use remit_test;
set names utf8;
CREATE TABLE `bank_info` (
  `id` INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `channel` varchar(10) NOT NULL COMMENT '渠道',  
  `accountId` varchar(32) NOT NULL COMMENT '出款账号',
  `accountName` varchar(64) NOT NULL COMMENT '出款账户名',
  `loginName` varchar(32) NOT NULL COMMENT '登录用户名',
  `branchCode` varchar(8) NOT NULL COMMENT '分行号',
  `busiMode` varchar(8) NOT NULL COMMENT '业务模式',
  `busiCode` varchar(8) NOT NULL COMMENT '业务类别',
  `transType` varchar(8) NOT NULL COMMENT '交易类型',
  `currency` varchar(8) NOT NULL DEFAULT '10' COMMENT '币种',
  `settleChannel` varchar(8) DEFAULT NULL COMMENT '结算方式'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;