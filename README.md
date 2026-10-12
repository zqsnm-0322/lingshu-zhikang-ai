# 灵枢智康 AI 多模态智慧健康管理平台

## 项目简介

灵枢智康是一个前后端分离的智慧健康管理平台，融合大模型、PDF 文档分析、医学图像处理、健康数据统计和三维交互等能力，为用户提供健康咨询、报告解读、慢病管理、用药提醒、健康预警等服务。

> 本项目提供健康管理与辅助分析功能，不能替代专业医生的诊断和治疗建议。

## 技术栈

- 前端：Vue 3、TypeScript、Vite、Vue Router、Pinia、Element Plus、Naive UI
- 数据展示：ECharts、Three.js、PDFTron WebViewer
- 后端：Java 17、Spring Boot 3、Spring AI、MyBatis Plus
- 数据与存储：MySQL、MinIO
- AI 与多媒体：Ollama、OpenAI 兼容接口、通义千问、OpenCV、OCR

## 核心功能

- AI 健康问答与历史会话管理
- PDF 健康报告上传、预览、分析与问答
- 医学图片辅助分析及图像、视频处理
- 健康档案、健康日记和数据看板
- 慢性病管理、风险评估与健康预警
- 用药提醒、服药记录及药物相互作用检查
- 健康管家、饮食推荐、药品查询和症状自查
- 健康社区、会员充值、企业统计和管理后台
- 人体器官展示、VR 冥想及康复训练

## 项目结构

```text
lingshu-zhikang-ai/
├── lingshu-zhikang-ai-backend/   # Spring Boot 后端
│   ├── src/main/java/             # 控制器、服务、实体和数据访问层
│   ├── src/main/resources/        # 配置、SQL 和 Mapper 文件
│   └── src/test/                  # 集成测试
├── lingshu-zhikang-ai-vue/       # Vue 3 前端
│   ├── src/views/                 # 业务页面
│   ├── src/components/            # 公共组件
│   ├── src/services/              # API 服务
│   ├── src/router/                # 路由配置
│   └── src/stores/                # 状态管理
└── 项目描述.md
```

## 本地运行

### 1. 准备环境

安装 JDK 17、Maven、Node.js、MySQL 和 MinIO，并配置数据库、对象存储及 AI 服务参数。

### 2. 启动后端

```bash
cd lingshu-zhikang-ai-backend
mvn spring-boot:run
```

后端默认地址：`http://localhost:8080`

### 3. 启动前端

```bash
cd lingshu-zhikang-ai-vue
npm install
npm run dev
```

前端默认地址：`http://localhost:3000`

## 配置说明

项目运行前需要配置以下内容：

- MySQL 数据库连接
- MinIO 地址、存储桶和访问凭据
- Ollama 或其他兼容 OpenAI 协议的模型服务
- 通义千问、OCR 等第三方服务密钥

请使用环境变量或独立配置文件保存敏感信息，不要将真实密钥和密码提交到代码仓库。现有配置中如包含测试凭据，部署前应立即替换并轮换。

## 测试与构建

```bash
# 后端测试
cd lingshu-zhikang-ai-backend
mvn test

# 前端类型检查与生产构建
cd ../lingshu-zhikang-ai-vue
npm run build
```

