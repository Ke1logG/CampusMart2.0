-- CampusMart 单体架构功能升级脚本
-- 在原有 campusmart 数据库上执行
-- 包含：商品点赞/收藏、订单、支付、钱包、支付宝绑定

-- 1. message 表增加关联商品字段
ALTER TABLE message
    ADD COLUMN goodID BIGINT NULL AFTER sendTime,
    ADD INDEX idx_message_goodID (goodID);

-- 2. 商品点赞表
CREATE TABLE IF NOT EXISTS goods_like
(
    likeID    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    userID    BIGINT      NOT NULL COMMENT '用户ID',
    goodID    BIGINT      NOT NULL COMMENT '商品ID',
    createTime DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_good_like (userID, goodID),
    INDEX idx_goodID_like (goodID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '商品点赞表';

-- 3. 商品收藏表
CREATE TABLE IF NOT EXISTS goods_collection
(
    collectionID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    userID       BIGINT      NOT NULL COMMENT '用户ID',
    goodID       BIGINT      NOT NULL COMMENT '商品ID',
    createTime   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_good_collection (userID, goodID),
    INDEX idx_goodID_collection (goodID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '商品收藏表';

-- 4. 支付宝账号绑定表
CREATE TABLE IF NOT EXISTS alipay_account_binds
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '绑定ID',
    userID      BIGINT       NOT NULL COMMENT '用户ID',
    account     VARCHAR(64)  NOT NULL COMMENT '支付宝账号',
    realName    VARCHAR(64)  NOT NULL COMMENT '真实姓名',
    createTime  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_userID (userID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '支付宝账号绑定表';

-- 5. 支付单表
CREATE TABLE IF NOT EXISTS payments
(
    paymentID      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付单ID',
    orderID        BIGINT          NOT NULL COMMENT '订单ID',
    userID         BIGINT          NOT NULL COMMENT '支付用户ID',
    amount         DECIMAL(18, 2)  NOT NULL COMMENT '支付金额',
    status         VARCHAR(32)     NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PAID/CLOSED',
    alipayTradeNo  VARCHAR(128)    NULL COMMENT '支付宝交易号',
    payTime        DATETIME        NULL COMMENT '支付时间',
    expireTime     DATETIME        NOT NULL COMMENT '过期时间',
    createTime     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_orderID (orderID),
    INDEX idx_userID (userID),
    INDEX idx_status_expireTime (status, expireTime)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '支付单表';

-- 6. 订单表
CREATE TABLE IF NOT EXISTS orders
(
    orderID        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    goodID         BIGINT          NOT NULL COMMENT '商品ID',
    buyerID        BIGINT          NOT NULL COMMENT '买家ID',
    sellerID       BIGINT          NOT NULL COMMENT '卖家ID',
    amount         DECIMAL(18, 2)  NOT NULL COMMENT '订单金额',
    status         VARCHAR(32)     NOT NULL DEFAULT 'CREATED' COMMENT '状态：CREATED/PAID/SETTLED/CANCELLED',
    payTimeout     DATETIME        NOT NULL COMMENT '支付超时时间',
    createTime     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_buyerID (buyerID),
    INDEX idx_sellerID (sellerID),
    INDEX idx_status (status),
    INDEX idx_payTimeout (payTimeout)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '订单表';

-- 7. 用户钱包表
CREATE TABLE IF NOT EXISTS user_wallets
(
    walletID    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '钱包ID',
    userID      BIGINT           NOT NULL COMMENT '用户ID',
    balance     DECIMAL(18, 2)   NOT NULL DEFAULT 0.00 COMMENT '余额',
    version     BIGINT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    createTime  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_userID_wallet (userID)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '用户钱包表';

-- 8. 钱包流水表
CREATE TABLE IF NOT EXISTS wallet_flows
(
    flowID      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '流水ID',
    userID      BIGINT           NOT NULL COMMENT '用户ID',
    flowType    VARCHAR(32)      NOT NULL COMMENT '类型：RECHARGE/INCOME/EXPENSE/WITHDRAW',
    amount      DECIMAL(18, 2)   NOT NULL COMMENT '金额',
    balance     DECIMAL(18, 2)   NOT NULL COMMENT '变动后余额',
    relatedID   BIGINT           NULL COMMENT '关联订单/支付单ID',
    remark      VARCHAR(255)     NULL COMMENT '备注',
    createTime  DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_userID (userID),
    INDEX idx_createTime (createTime)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '钱包流水表';
