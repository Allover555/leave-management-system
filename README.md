# 高校学生请假管理系统

基于 **Spring Boot + Vue 3** 的前后端分离高校学生请假管理系统，面向高校学生请假、审批、销假和统计场景。系统支持学生、辅导员、院系管理员、教务管理员等角色，并提供多级审批、消息通知、附件上传和数据统计导出功能。

## 技术栈

| 模块 | 技术 |
|------|------|
| 前端 | Vue 3、Vite 8、Element Plus、Pinia、Vue Router、Axios、ECharts |
| 后端 | Spring Boot 2.7、Spring Security、Spring Data JPA、JWT、Springdoc OpenAPI |
| 数据库 | MySQL 5.7+ / 8.x、utf8mb4 |
| 工具库 | Lombok、Apache POI、Commons IO |

## 核心功能

### 学生端

- 注册、登录、查看个人信息
- 提交请假申请，支持附件上传
- 查看请假审批进度和审批记录
- 撤销待审批申请
- 发起销假申请

### 辅导员端

- 查看待审批请假申请
- 审批或驳回学生请假
- 查看管理班级请假情况
- 审核销假申请
- 查看审批历史和班级统计

### 管理员端

- 用户管理：学生、辅导员、管理员
- 院系与班级管理
- 请假类型管理
- 审批流程配置
- 系统消息管理
- 数据统计与 Excel 导出

### 系统特性

- **JWT 认证**：基于 Token 的无状态鉴权
- **角色权限控制**：按角色区分菜单和接口访问权限
- **多级审批**：按请假天数自动匹配审批流程
- **消息通知**：推送审批结果、销假提醒和超期提醒
- **文件上传**：支持图片、PDF、Word 附件
- **数据可视化**：使用 ECharts 展示请假统计数据

## 项目结构

```text
请假系统/
├── backend/                         # 后端 Spring Boot 项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/leave/
│       │   ├── common/              # 通用响应、异常处理
│       │   ├── config/              # 初始化、安全、跨域配置
│       │   ├── controller/          # REST 接口
│       │   ├── dto/                 # 请求和响应对象
│       │   ├── entity/              # JPA 实体
│       │   ├── repository/          # 数据访问层
│       │   ├── security/            # JWT 与 Spring Security
│       │   └── service/             # 业务逻辑
│       └── resources/
│           ├── application.yml      # 后端配置
│           └── schema.sql           # 数据库初始化脚本
├── frontend/                        # 前端 Vue 3 项目
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                     # 接口请求封装
│       ├── layout/                  # 主布局
│       ├── router/                  # 路由配置
│       ├── stores/                  # Pinia 状态管理
│       ├── styles/                  # 全局样式
│       ├── utils/                   # Axios 封装
│       └── views/                   # 页面视图
├── init_db.bat                      # Windows 数据库初始化脚本
└── README.md
```

## 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 5.7+ 或 MySQL 8.x

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd 请假系统
```

### 2. 初始化数据库

数据库名称为 `leave_management`，默认字符集为 `utf8mb4`。

Windows 可以直接运行：

```bat
init_db.bat
```

如果 `mysql` 命令没有配置到系统 PATH，可以先指定 MySQL 客户端路径：

```bat
set MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
init_db.bat
```

也可以手动执行 SQL：

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

### 3. 配置后端

后端配置文件位于 `backend/src/main/resources/application.yml`。

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | `8088` | 后端服务端口 |
| `server.servlet.context-path` | `/api` | API 统一前缀 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/leave_management...` | 数据库连接地址 |
| `DB_USERNAME` | `root` | 数据库用户名 |
| `DB_PASSWORD` | `123456` | 数据库密码 |
| `JWT_SECRET` | 内置开发密钥 | JWT 签名密钥，生产环境请替换 |
| `JWT_EXPIRATION` | `86400000` | Token 过期时间，单位毫秒 |
| `FILE_UPLOAD_DIR` | `./uploads` | 附件上传目录 |
| `FILE_ALLOWED_TYPES` | `jpg,jpeg,png,gif,pdf,doc,docx` | 允许上传的文件类型 |
| `FILE_MAX_SIZE` | `10485760` | 单文件最大大小 |

如本机 MySQL 密码不是 `123456`，可以设置环境变量 `DB_PASSWORD`，或直接修改 `application.yml`。

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动成功后：

- 后端 API：`http://localhost:8088/api`
- Swagger 文档：`http://localhost:8088/api/swagger-ui.html`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认访问地址：`http://localhost:5173`

开发环境下，前端 `/api` 请求会由 Vite 代理到 `http://localhost:8088`。

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 教务管理员 | `admin` | `admin123` |

其他角色账号可通过注册页面创建，或由管理员在后台创建。

## 构建与运行

### 后端打包

```bash
cd backend
mvn clean package
```

打包后运行：

```bash
java -jar target/leave-management-1.0.0.jar
```

### 前端构建

```bash
cd frontend
npm run build
```

构建产物位于 `frontend/dist/`。

## 数据库设计

系统共 16 张核心表：

| 表名 | 说明 |
|------|------|
| `sys_role` | 角色表 |
| `sys_permission` | 权限表 |
| `sys_role_permission` | 角色-权限关联表 |
| `sys_user` | 用户表 |
| `department` | 院系表 |
| `class_info` | 班级表 |
| `counselor_class` | 辅导员-班级关联表 |
| `leave_type` | 请假类型表 |
| `approval_flow_config` | 审批流程配置表 |
| `approval_flow_node` | 审批流程节点表 |
| `leave_application` | 请假申请表 |
| `leave_attachment` | 请假附件表 |
| `approval_record` | 审批记录表 |
| `leave_cancellation` | 销假申请表 |
| `sys_message` | 系统消息表 |
| `cancellation_rule` | 销假规则配置表 |

## 常见问题

### 前端请求后端失败

确认后端已经启动，并检查 `frontend/vite.config.js` 中 `/api` 代理地址是否为 `http://localhost:8088`。

### 数据库连接失败

确认 MySQL 已启动、数据库 `leave_management` 已创建，并检查 `application.yml` 中的数据库用户名和密码。

### 初始化脚本运行失败

确认 MySQL 客户端可执行文件已加入 PATH，或通过 `MYSQL_CMD` 指定完整路径。

### 文件上传失败

确认 `file.upload-dir` 指向的目录可写，且上传文件类型和大小没有超过配置限制。

## 版本控制说明

仓库根目录提供 `.gitignore`，默认忽略依赖目录、构建产物、上传文件、日志和本地输出材料。提交代码前建议检查：

```bash
git status
```
