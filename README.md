# 灵枢智康 AI 多模态智慧健康管理平台

灵枢智康是一套前后端分离的智慧健康管理平台，面向个人用户、管理员和企业服务场景，集成 AI 问答、健康档案、健康报告分析、慢性病管理、用药提醒、药品查询、社区互动以及 AR/VR 健康资源等功能。

> 本项目提供健康管理和信息辅助能力，不能替代医生诊断、处方或紧急医疗服务。

## 功能概览

- AI 健康咨询：流式对话、对话历史、服务咨询和游戏化交互。
- AI 多模态分析：PDF/体检报告问答、病历自动分析、图片 AI 诊断和 OCR 识别。
- 健康管理：健康档案、健康日记、健康看板、趋势统计、健康预警和健康报告。
- 用药服务：药品查询、用药提醒、服药记录和药物相互作用查询。
- 慢病管理：血压、血糖、体重、心率等指标记录、统计和分析。
- 健康社区：帖子发布、编辑、删除、搜索、点赞、收藏、分类和热门内容。
- AR/VR 健康：人体器官、药品指导、冥想场景和康复计划等资源展示。
- 平台运营：会员套餐、充值订单、管理员用户管理、配额和业务统计。

## 技术栈

### 前端

- Vue 3、TypeScript、Vite、Vue Router、Pinia
- Element Plus、Naive UI
- Axios、Sass、Tailwind CSS
- ECharts、Three.js
- PDFTron WebViewer、jsPDF、marked、DOMPurify

### 后端

- Java 17、Spring Boot 3.4.3
- Spring AI（Ollama/OpenAI 兼容接口）
- MyBatis-Plus、MySQL 8
- MinIO 对象存储
- OpenCV、邮件服务、Lombok

## 项目结构

```text
.
├── lingshu-zhikang-ai-backend/   # Spring Boot 后端
│   ├── src/main/java/             # Controller、Service、Mapper、Entity 等
│   ├── src/main/resources/        # application 配置、Mapper XML、SQL 脚本
│   └── pom.xml
├── lingshu-zhikang-ai-vue/       # Vue 3 前端
│   ├── src/views/                 # 业务页面
│   ├── src/components/            # 通用组件
│   ├── src/router/                # 路由和登录/管理员守卫
│   └── package.json
├── docs/                          # 设计文档、流程图和 Visio 源文件
└── README.md
```

## 运行环境

开始前请准备：

- JDK 17+
- Maven 3.9+（或使用 IDE 内置 Maven）
- Node.js 20+ 和 npm
- MySQL 8+
- MinIO（报告、头像等文件上传功能需要）
- 可选：Ollama；或一个兼容 OpenAI Chat Completions 的 AI 服务

## 数据库初始化

1. 创建数据库：

   ```sql
   CREATE DATABASE course DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. 导入 [course.sql](lingshu-zhikang-ai-backend/src/main/resources/sql/course.sql)：

   ```bash
   mysql -uroot -p course < lingshu-zhikang-ai-backend/src/main/resources/sql/course.sql
   ```

3. 根据本地环境修改后端数据源配置。当前项目默认连接 `localhost:3306/course`。

## 后端配置

后端配置位于：

- `lingshu-zhikang-ai-backend/src/main/resources/application.yaml`
- `lingshu-zhikang-ai-backend/src/main/resources/application.yml`

启动前请至少检查以下配置：

| 配置                  | 说明                                         |
| --------------------- | -------------------------------------------- |
| `spring.datasource.*` | MySQL 地址、数据库名、用户名和密码           |
| `spring.ai.ollama.*`  | 本地 Ollama 地址和模型                       |
| `spring.ai.openai.*`  | OpenAI 兼容服务地址、API Key 和模型          |
| `minio.*`             | MinIO 地址、访问密钥、桶名称                 |
| `ai.provider`         | `qwen` 使用真实 AI 服务，`mock` 使用模拟结果 |
| `ocr.provider`        | `baidu` 使用百度 OCR，`mock` 使用模拟结果    |
| `baidu.ocr.*`         | 百度 OCR 的 API Key 和 Secret Key            |

不要将真实 API Key、数据库密码或 MinIO 密钥提交到 Git。建议通过环境变量、外部配置文件或启动参数注入生产环境配置，并轮换已经暴露过的凭据。

## 启动后端

在项目根目录执行：

```bash
cd lingshu-zhikang-ai-backend
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package
java -jar target/lingshu-zhikang-ai-0.0.1-SNAPSHOT.jar
```

后端默认端口为 `8080`。

## 启动前端

在另一个终端执行：

```bash
cd lingshu-zhikang-ai-vue
npm install
npm run dev
```

前端开发服务器默认端口为 `3000`，访问：<http://localhost:3000>。

Vite 已配置以下代理：

- `/api` → `http://localhost:8080`
- `/ai` → `http://localhost:8080`
- `/minio` → `http://localhost:9000`

因此本地开发时请保持后端运行在 `8080`、MinIO 运行在 `9000`；如需修改端口，请同步调整 `lingshu-zhikang-ai-vue/vite.config.ts` 和后端配置。

## 前端命令

```bash
npm run dev          # 启动开发服务器
npm run type-check   # TypeScript/Vue 类型检查
npm run build        # 类型检查并构建生产包
npm run preview      # 预览生产构建
```

## 后端测试

```bash
cd lingshu-zhikang-ai-backend
mvn test
```

仓库中包含社区、慢性病、健康日记、健康报告元数据和用药提醒等 Controller 集成测试。

## 主要页面路由

| 路径                         | 功能              |
| ---------------------------- | ----------------- |
| `/`                          | 首页              |
| `/login`                     | 登录与注册        |
| `/ai-chat`                   | AI 健康问答       |
| `/chat-pdf`                  | PDF/病历问答      |
| `/medical-diagnosis`         | 图像/医疗辅助分析 |
| `/daily-food-recommendation` | 每日饮食推荐      |
| `/medicine-query`            | 药品查询          |
| `/symptom-checker`           | 症状自检          |
| `/chronic-disease`           | 慢性病管理        |
| `/health-diary`              | 健康日记          |
| `/medicine-reminder`         | 用药提醒          |
| `/health-report`             | 健康报告          |
| `/health-dashboard`          | 健康数据看板      |
| `/health-butler`             | 健康管家          |
| `/admin`                     | 管理员控制台      |

除登录页外，大多数页面需要登录；`/admin` 还要求用户角色为 `ADMIN`。

## 主要后端接口分组

后端接口按业务划分在以下路径下：

```text
/api/user              用户登录、注册和资料
/api/ai-diagnosis      AI 诊断和报告
/api/health-report     健康报告上传、预览和下载
/api/health-diary      健康日记
/api/health-dashboard  健康看板和趋势
/api/health-warning    健康预警
/api/medicine          用药提醒和用药记录
/api/community         健康社区
/api/arvr-health       AR/VR 健康资源
/api/admin             管理员功能
/ai                    AI 对话、PDF 对话和历史记录
/api/v1                对外健康服务接口
```

## 常见问题

### 前端请求接口失败

确认后端已经启动并监听 `8080`，同时检查浏览器开发者工具中的请求是否以 `/api` 或 `/ai` 开头。前端开发代理只在 Vite 开发服务器中生效。

### AI 功能没有返回真实结果

检查 `application.yaml`/`application.yml` 中的 AI 服务地址、模型和密钥。开发或演示环境可以将 `ai.provider`、`ocr.provider` 设为 `mock`，以使用模拟结果。

### 上传文件失败

确认 MinIO 已启动、桶已创建，并检查 `minio.endpoint`、访问密钥和桶名称；同时注意后端上传大小限制默认为 100 MB。

### 数据库连接失败

确认 MySQL 已启动、`course` 数据库已创建并导入 SQL，随后核对 `spring.datasource.url`、用户名和密码。

## 安全与合规提示

- 请在部署前移除配置文件中的示例或历史密钥，并使用密钥管理方案。
- 生产环境应启用 HTTPS、严格的跨域策略、身份认证和权限校验。
- 健康数据、病历和上传文件属于敏感信息，应进行访问控制、最小化采集、加密存储和审计。
- AI 输出仅供健康信息参考，涉及急症、用药调整或诊断结论时应咨询专业医疗人员。

## 许可证

当前仓库未声明正式开源许可证。若要公开发布或二次分发，请先补充许可证和第三方依赖声明。
