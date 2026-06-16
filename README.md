# CampusMart

## 项目介绍

CampusMart是一款 **校园二手闲置物品交易平台**。在这里，你通过发帖，和同校的陌生同学快速交易你们的物品。由于省去了物流的时间，你们甚至可以交易一些实效性强的物品，比如中午点多了点奶茶！没当毕业季来临，你会不会对卖不出去的洗衣机，或者卖出去还抵不上运费的鞋柜感到烦恼？没关系！你可以使用CampusMart。使用CampusMart交易可以省下物流成本，同时将这些难以搬运的物品买给同校的同学无疑是一种跟便捷，方便的方式

CampusMart不仅仅是 **校园二手闲置物品交易平台**，同时也是解决同学们生活问题的好帮手。你有没有遇到过群聊消息太多，找不到重要通知到烦恼？没关系！CampusMart将和班主任，宿管合作，旨在只推送重要消息，想知道啥时候停水，啥时候考试？打开CampusMart！

在校园生活中，你有没有觉得校园生活服务的入口繁多而复杂，水费一个入口，网费一个入口，教务平台还有一个入口。打开CampusMart，实现一键跳转！CampusMart集成了主要的校园服务入口，在也不用在微信里面找半天啦！


CampusMart旨在为在校学生提供一个 **简洁、高效、低成本、近距离、高时效** 的闲置物品交易渠道

解决一般闲置物品交易平台在校园内交易场景下 **运费高、用户信任低、交易时间长、流程繁琐** 的问题

为毕业季学生出手专业书籍，生活用品，难以邮寄的生活电器，电子产品提供一个简短高效，低风险低成本的渠道

为平时学生就近快速出手难以邮寄的多买错买产品（如食品，一些生活用品等）提供简便的渠道

## 功能介绍
- 商品发帖
- 搜索商品
- 商品推送
- 在线和买家卖家沟通
- 修改，删除我的发帖
- 查看学校通知
- 查看最近聊天
- 生活服务入口一键跳转（水费，网费，教务平台）

## 项目目录
```bash
2025-Autumn-Aberdeen-3-CampusMart/
├── 文档文件
│   ├── Campus Mart App Business Plan Report.pdf #商业报告书        
│   ├── Campus Mart App Business Plan.pdf        #第一阶段报告ppt
│   ├── README.md
│   ├── 最终效果演示视频.mp4
│   ├── UI设计演示视频.mp4
│   ├── Test.pptx                                #第三阶段报告   
│   └── UI_design.pdf                            #第二阶段报告
├── 应用安装包
│   └── apks/
│       ├── app-offline.apk #离线板本安装包，用于UI展示
│       └── app-online.apk  #在线版本安装包，配合服务器使用，需配置服务器后更改android项目配置文件后（配置后端地址）生成
├── Android客户端
│   ├── .gradle/
│   ├── .idea/
│   ├── app/
│   │   ├── src/             #包含Java代码、XML布局、资源文件等等
│   │   └── build/
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── gradle/
│   ├── gradlew
│   ├── gradlew.bat
│   ├── local.properties
│   └── settings.gradle.kts
├── 后端服务
│   └── server/
│       ├── .idea/
│       ├── common/           #通用工具）
│       ├── model/            #数据库对应的实体类
│       ├── pom.xml  
│       └── web/              #Controller，DAO层实现，WebApplicat启动类，拦截器等后端代码/配置文件application.yml
└── 数据库脚本
    └── database generater/
        └── campusmart-build-database.sql #生成数据库的sql脚本
```

## 技术实现
1. android app实现
  - 使用 XML 布局文件定义界面结构，结合自定义 drawable 资源实现 UI 美化。
  - 核心页面均通过Activity实现，同时使用Activity+Fragment实现导航栏和多页面跳转，通过RecyclerView+Adpater实现商品列表等可滑动页面
  - 用OkHttp库发送网络请求，支持同步/异步请求，通过Request和RequestBody构建请求，Callback处理响应。
  - 使用Gson库解析服务器返回的 JSON 数据
  - SharedPreferences：用于存储轻量级用户数据，如登陆用户信息
  - 使用Glide加载商品图片，同时在上传商品图片时进行压缩
  - 通过包名com.tencent.mm获取微信启动意图，实现微信跳转。通过Intent.ACTION_VIEW启动系统浏览器打开指定 URL
2. 后端实现
  - 使用SpringMVC实现后端整体框架
  - 使用SpringBoot搭建项目
  - 使用Mysql存储结构化数据
  - 使用Mybatis实现DAO层逻辑
  - 使用Minio实现图像存储
  - 同时通过拦截器实现token校验等功能

## 如何部署
由于成本原因，项目暂为部署到云端服务器，需在本地部署后在同一网络下使用
1. 安装mysql
2. 启动mysql
3. 运行mysql脚本创建数据库
4. 安装minio
5. 配置minio [官方文档](https://min-io.cn/product/overview)
6. 启动minio服务
7. 更改后端配置文件 server/web/src/main/resources/application.yml
   - 对齐minio地址和端口号 （endpoint）
   - 对齐minio配置（access-key，secret-key）
   - 对齐mysql地址和端口号
```bash
url: jdbc:mysql://\<host\>:\<port\>/campusmart?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2b8
```
   - 对齐mysql配置 (username, password)
8. 启动后端服务（运行sever/web/src/main/java/org/example/CampusMart/WebApplication.java）
9. 更改前端配置文件 Android/app/src/main/res/values/config.xml
   - 改为后端ip+端口号
10. 生成apk
11. 安装后在同一网络下可以使用
     
