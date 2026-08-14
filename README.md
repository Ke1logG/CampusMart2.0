# CampusMart 后端服务

## 项目简介

CampusMart 是一款面向校园场景的二手闲置物品交易平台，采用 Spring Boot 单体架构实现，为前端 App 提供API 接口。

后端聚焦商品发布、订单交易、消息沟通、钱包流水等核心业务，并通过 Redis 缓存、RabbitMQ 延时队列、Redisson 分布式锁等机制保障并发安全与性能。

---

## 项目亮点

- **JWT + ThreadLocal 无状态认证**：统一拦截器解析 Token，ThreadLocal存用户信息
- **Redis 多级缓存策略**：旁路缓存 + 空值缓存 + 随机 TTL，结合 Redisson 分布式锁解决热点数据缓存击穿，穿透，雪崩
- **订单超时自动取消**：RabbitMQ 死信队列实现 30min 延迟消息 + 定时任务兜底扫描，保证支付链路状态最终一致
- **支付单状态机**：定义 CREATED/PAYING/PAID/CLOSED 状态流转，回调幂等校验防止重复更新
- **SQL 性能优化**：Druid 慢 SQL 监控（阈值 1000ms），核心查询补充联合索引，N+1 查询优化为多表联查
- **MyBatis-Plus 乐观锁**：卖家钱包余额更新通过 `version` 字段控制并发


---

## 技术栈

| 分类       | 技术                                     | 用途           |
| :------- | :------------------------------------- | :----------- |
| **核心框架** | Spring Boot 3.0.5 / Java 17            | 后端主框架        |
| **数据访问** | MyBatis-Plus 3.5.3.1 / Druid 1.2.23    | ORM 与连接池监控   |
| **缓存**   | Redis / Redisson 3.23.5 / Spring Cache | 分布式缓存与锁      |
| **消息队列** | RabbitMQ                               | 延时队列、异步解耦    |
| **存储**   | MySQL 8.x / 阿里云 OSS                    | 业务数据与文件存储    |
| **安全**   | JWT / MP 乐观锁                           | 认证与并发安全      |
| **工具**   | Maven / Lombok / Hutool / Knife4j      | 构建、代码简化、接口文档 |

---

## 项目结构

```text
CampusMart/
├── server/                              # 后端服务根目录
│   ├── model/                           # 数据层：Entity、VO、Enums
│   │   └── src/main/java/org/example/CampusMart/model/
│   │       ├── entity/                  # 数据库实体（User、Goods、Order、Payment 等）
│   │       └── enums/                   # 业务枚举（基础枚举接口等）
│   │
│   ├── common/                          # 公共模块
│   │   └── src/main/java/org/example/CampusMart/common/
│   │       ├── exception/               # 全局异常体系（自定义异常、全局异常处理）
│   │       ├── result/                  # 统一响应封装（Result、ResultCodeEnum）
│   │       ├── login/                   # 登录用户信息上下文（LoginUser、LoginUserHolder）
│   │       ├── oss/                     # 阿里云 OSS 配置与属性
│   │       ├── mybatisplus/             # MyBatis-Plus 配置（乐观锁等）
│   │       └── utils/                   # 工具类（JWT、验证码等）
│   │
│   └── base-service/                    # 核心业务服务
│       ├── src/main/java/org/example/CampusMart/
│       │   ├── WebApplication.java      # Spring Boot 启动类
│       │   └── web/
│       │       ├── config/              # 基础配置（Redis、RabbitMQ、Druid、Alipay 等）
│       │       ├── custom/
│       │       │   ├── config/          # 自定义配置（WebMvc、Knife4j 等）
│       │       │   ├── converter/       # 类型转换器（StringToEnumConverterFactory）
│       │       │   └── interceptor/     # JWT 登录认证拦截器
│       │       ├── controller/          # API 接口层（按模块划分：goods、trade、payment 等）
│       │       ├── service/             # 业务逻辑层（接口 + impl 实现）
│       │       ├── mapper/              # 数据访问层（MyBatis-Plus Mapper）
│       │       ├── vo/                  # 视图对象（请求/响应 VO）
│       │       ├── listener/            # RabbitMQ 消费者（订单超时监听）
│       │       └── job/                 # 定时任务（订单超时扫描、支付超时关闭）
│       └── src/main/resources/
│           ├── mapper/                  # MyBatis XML 映射文件
│           └── application.yml          # 主配置文件
│
├── database generater/                  # 数据库初始化脚本
│   ├── campusmart-build-database.sql    # 初始建表脚本
│   ├── campusmart-upgrade-monolithic.sql
│   └── campusmart-upgrade-poc.sql
├── .gitignore
└── README.md
```
---

## 后端功能模块

- **用户模块**：注册、登录、JWT 认证、个人信息管理
- **商品模块**：商品发布、编辑、删除、列表查询、模糊搜索、图片上传、收藏与点赞
- **订单模块**：下单、订单状态流转、超时自动取消、买家/卖家订单列表
- **支付模块**：支付状态同步、待支付订单关闭
- **钱包模块**：余额查询、充值、消费流水、乐观锁防并发
- **消息模块**：买卖双方消息沟通（前端轮询）、最近会话列表、系统通知  

---


## 快速开始

1. 环境要求
    - JDK 17+
    - Maven 3.8+
    - MySQL 8.0+
    - Redis 7.0+
    - RabbitMQ 3.11+

2. 克隆项目

3. 初始化数据库

4. 修改配置（yml文件）

5. 启动服务  

快速测试：服务启动后访问：http://localhost:8080/doc.html （Knife4j 接口文档）测试  

---  

## 接口自动化测试  

1. 将[API-Automation-Testing-Platform](https://github.com/Ke1logG/API-Automation-Testing-Platform)部署到本地
2. 配置好自动化测试项目的配置
3. 启动后端服务
4. 终端输入：mvn clean test 执行全部测试用例
5. 终端输入：mvn allure:serve 查看测试allure报告


---

## 接口文档

本地启动服务后，通过 Knife4j 查看在线文档：
地址：http://localhost:8080/doc.html
包含：请求参数、响应示例、认证说明

