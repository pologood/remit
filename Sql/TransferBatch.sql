use remit_test;
set names utf8;
CREATE TABLE `transfer_batch` (
  `id` INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `version` varchar(10) NOT NULL COMMENT '版本号',
  `channel` varchar(10) NOT NULL COMMENT '渠道',
  `appId` INT(4) NOT NULL COMMENT '业务线Id',
  `batchNo` varchar(32) NOT NULL COMMENT '批次号',
  `transferCount` INT(4) NOT NULL DEFAULT 1 COMMENT '付款笔数',
  `transferAmount` DECIMAL(12,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `memo` varchar(128) NOT NULL COMMENT '备注',
  `reserve` varchar(64) COMMENT '保留字段',
  
  `outAccountId` varchar(32) NOT NULL COMMENT '出款账号',
  `outAccountName` varchar(64) NOT NULL COMMENT '出款账户名',
  `loginName` varchar(32) NOT NULL COMMENT '登录用户名',
  `branchCode` varchar(8) NOT NULL COMMENT '分行号',
  `busiMode` varchar(8) NOT NULL COMMENT '业务模式',
  `busiCode` varchar(8) NOT NULL COMMENT '业务类别',
  `transType` varchar(8) NOT NULL COMMENT '交易类型',
  `currency` varchar(8) NOT NULL DEFAULT '10' COMMENT '币种',
  `settleChannel` varchar(8) DEFAULT NULL COMMENT '结算方式',
  
  `auditor` INT(10) DEFAULT NULL COMMENT '审批员id',
  `status` INT(11) DEFAULT 1 COMMENT '状态',
  `auditTimes` varchar(32) DEFAULT NULL COMMENT '审批时间',
  `auditOpinions` varchar(64) DEFAULT NULL COMMENT '审批意见',
  
  `outTradeNo` varchar(16) DEFAULT NULL COMMENT '外部交易号', 
  `successAmount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '成功金额',
  `successCount` INT(4) NOT NULL DEFAULT 0 COMMENT '成功笔数',
  `outErrMsg` varchar(128) DEFAULT NULL COMMENT '错误信息',
  `notifyFlag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '回调标志',
  
  `createTime` TIMESTAMP NOT NULL DEFAULT 0 COMMENT 'immut',
  `upDateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `appid_batchno_idx` (`appId`, `batchNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

