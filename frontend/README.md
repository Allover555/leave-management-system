# 高校学生请假管理系统 — 前端

基于 Vue 3 + Vite + Element Plus 的请假管理系统前端项目。

## 技术栈

- **Vue 3** — 前端框架（Composition API + `<script setup>`）
- **Vite 8** — 构建工具
- **Element Plus** — UI 组件库
- **Vue Router** — 路由管理
- **Pinia** — 状态管理
- **Axios** — HTTP 请求
- **ECharts / vue-echarts** — 数据可视化

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器 (默认 http://localhost:5173)
npm run dev

# 构建生产版本
npm run build
```

## 项目结构

```
src/
├── api/            # 接口请求（auth、leave、admin、message、statistics、user）
├── assets/         # 静态资源
├── components/     # 公共组件
├── layout/         # 页面布局（MainLayout）
├── router/         # 路由配置
├── stores/         # Pinia 状态管理
├── styles/         # 全局样式
├── utils/          # 工具函数（request 封装）
└── views/          # 页面视图
    ├── Login.vue        # 登录
    ├── Register.vue     # 注册
    ├── Dashboard.vue    # 仪表盘
    ├── Profile.vue      # 个人中心
    ├── Messages.vue     # 消息中心
    ├── student/         # 学生页面
    ├── counselor/       # 辅导员页面
    └── admin/           # 管理员页面
```

## 开发代理

开发模式下，`/api` 请求会自动代理到后端 `http://localhost:8088`，无需手动处理跨域。
