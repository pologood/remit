use remit_test;
set names utf8;
CREATE TABLE `transfer_detail` (
  `id` INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `appId` INT(4) NOT NULL COMMENT '业务线Id',
  `batchNo` varchar(32) NOT NULL COMMENT '批次号',
  `transferId` varchar(128) NOT NULL COMMENT '转账流水号',
  `amount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `inAccountId` varchar(32) NOT NULL COMMENT '收款账号',
  `inAccountName` varchar(64) NOT NULL COMMENT '收款账户名',
  `bankName` varchar(64) DEFAULT NULL COMMENT '他行支行',
  `bankCity` varchar(64) DEFAULT NULL COMMENT '他行城市',
  `memo` varchar(128) DEFAULT NULL COMMENT '备注',
  `outErrMsg` varchar(128) DEFAULT NULL COMMENT '错误信息',
  `status` TINYINT(4) DEFAULT 0 COMMENT '状态',
  `upDateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `appid_batchno_transferId_idx` (`appId`, `batchNo`, `transferId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;