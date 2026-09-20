# 校园活动报名系统（SchoolRegisterSystem）

软件系统分析与设计综合实践课程设计项目。学生用户可以注册登录、浏览/发布/编辑/删除校园活动，并对活动进行报名、取消报名、查看我的报名。

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 4.0.8、Java 25、MyBatis（注解 SQL）、Validation、Lombok |
| 数据库 | MySQL 8.x（库名 `school_register`，utf8mb4） |
| 前端 | Vue 3 + Vite、vue-router、Element Plus、axios |

## 目录结构

```
SchoolRegisterSystem/
├── sql/create_tables.sql        # 建库建表 + 示例数据
├── src/main/java/com/login/schoolregistersystem/
│   ├── common/                  # Result 统一返回、全局异常、登录拦截器、WebMvc 配置
│   ├── controller/              # UserController / ActivityController / ApplyController
│   ├── entity/                  # SysUser / Activity / ApplyRecord
│   ├── mapper/                  # MyBatis 注解 SQL（列别名映射驼峰字段）
│   ├── service/ + service/impl/ # 业务逻辑（报名四步校验等）
│   └── vo/                      # LoginVO / LoginRequestVO / RegisterVO / ApplyVO
└── frontend/
    └── src/
        ├── api/                 # axios 封装 + 用户/活动/报名接口
        ├── router/              # 路由 + 登录守卫
        ├── utils/               # token 存取、日期格式化
        └── views/               # 登录/注册/列表/详情/发布/编辑/我的报名 共7页
```

## 快速开始

### 1. 初始化数据库

修改 `sql/create_tables.sql` 无需改（使用默认配置 root/12345678 时），直接执行：

```bash
mysql -uroot -p < sql/create_tables.sql
```

> 数据库连接配置在 `src/main/resources/application.properties`，
> 如果本机 MySQL 账号密码不同，请修改其中的 url/username/password。

### 2. 启动后端（JDK 25）

```bash
./mvnw spring-boot:run
```

后端运行在 http://localhost:8080

### 3. 启动前端

```bash
cd frontend
npm install     # 首次运行
npm run dev
```

浏览器访问 http://localhost:5173 （开发服务器已配置 /api 代理到 8080，无跨域问题）

### 4. 测试账号

| 用户名 | 密码 | 姓名 |
|---|---|---|
| zhangsan | 123456 | 张三 |
| lisi | 123456 | 李四 |

## 功能与契约约定

- 角色只有**学生用户**（无管理员后台，管理员仅作报告扩展说明）
- 统一返回 `{code, msg, data}`：200 成功、400 业务错误、401 未登录/token失效、500 服务器异常
- 登录返回 `token`，前端存 localStorage，之后所有请求通过**请求头 `token`** 携带（不用 Bearer）
- 报名接口严格执行四步校验（顺序固定，任一失败即拒绝）：
  1. 活动是否存在
  2. 是否超过报名截止时间
  3. 名额是否已满
  4. 当前用户是否重复报名
- 活动编辑/删除仅限创建者本人；删除=下架，会同时删除该活动的报名记录
- 报名按钮状态：已报名（禁用）、已截止（置灰，点击提示"活动已截止"）、名额已满（置灰，点击提示"名额已满"）；前端置灰只是第一层防护，后端接口会再次四步校验
- 密码使用 SHA-256 摘要存储（字段 VARCHAR(64)）
- token 存储于 `sys_user.token` 字段，重新登录会刷新 token

## 接口总表

### 用户模块

| 接口 | 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|---|
| 登录 | POST | /api/user/login | username, password | Result\<LoginVO\>（token,userId,username,realName） |
| 注册 | POST | /api/user/register | username, password, realName, confirmPassword | Result\<userId\> |

### 活动模块

| 接口 | 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|---|
| 新增活动 | POST | /api/activity/add | Activity 对象 | Result\<activityId\> |
| 编辑活动 | PUT | /api/activity/update | Activity 对象 | Result |
| 删除活动 | DELETE | /api/activity/delete/{id} | id | Result |
| 活动列表 | GET | /api/activity/list | 无 | Result\<List\<Activity\>\> |
| 活动详情 | GET | /api/activity/detail/{id} | id | Result\<Activity\> |

### 报名模块

| 接口 | 方法 | 路径 | 参数 | 返回 |
|---|---|---|---|---|
| 活动报名 | POST | /api/apply/{activityId} | activityId | Result |
| 取消报名 | DELETE | /api/apply/{activityId} | activityId | Result |
| 我的报名 | GET | /api/apply/my | 无（token 识别用户） | Result\<List\<ApplyVO\>\> |

> 活动列表/详情返回中额外带 `appliedCount`（当前已报名人数，查询时 COUNT 统计），用于前端展示报名情况和名额已满判断；数据库表结构不变。

## 常见问题

- **后端启动报数据库连接失败**：确认 MySQL 已启动、`application.properties` 中的账号密码正确、已执行建表脚本。
- **前端页面 401**：token 失效（后端重启或重新登录过），重新登录即可。
- **编译需要 JDK 25**：项目按参考文档使用 Java 25 + Spring Boot 4.0.8。
