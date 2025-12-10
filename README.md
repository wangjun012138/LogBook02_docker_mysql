

# 🐳 LogBook 02: Docker & MySQL 部署指南

本日志记录了从 Maven 打包到 Docker 容器化部署，以及数据库验证的完整流程。

-----

## 📦 1. Maven 打包

首先，需要将 Java 代码编译成可执行的 JAR 包。

**操作步骤：**
在项目根目录下打开终端（CMD/Terminal），运行以下命令：

```bash
mvn clean package -DskipTests
```

> **💡 注意：**
> 加上 `-DskipTests` 是为了跳过测试环节。因为测试代码可能尝试连接 `localhost` 数据库，而此时构建过程中数据库容器尚未启动，可能会导致构建失败。

**验证结果：**
打包完成后，检查 `target` 文件夹，确认存在类似 `text-proof-platform-0.0.1-SNAPSHOT.jar` 的文件。

-----

## 🚀 2. Docker 一键启动

使用 Docker Compose 编排并启动服务。

**操作步骤：**
在项目根目录下运行：

```bash
docker compose up -d --build
```

**参数解析：**

  * `up`: 启动服务。
  * `-d`: 后台运行（Detached mode，不占用当前控制台）。
  * `--build`: 强制重新构建镜像（确保 Docker 使用的是刚刚打包的最新 JAR 包）。

-----

## 🔍 3. 验证部署状态

### 3.1 检查容器状态

查看当前运行的容器列表：

```bash
docker compose ps
```

✅ **预期结果：** 你应该能看到 **2** 个状态为 `Up` 的容器。

### 3.2 查看应用日志（排错必备）

如果想确认 Java 程序是否完全启动，请查看应用容器日志：

```bash
docker logs -f text-proof-app
```

✅ **预期结果：** 当看到 `Started TextProofApplication in ... seconds` 字样时，说明启动成功！

### 3.3 测试接口

容器端口 `8080` 已映射到宿主机，可以直接测试 API：

```bash
curl -X POST "http://localhost:8080/api/auth/code?email=docker_test@example.com"
```

-----

## 🗄️ 4. 进入 MySQL 容器验证数据

### 4.1 进入数据库容器

首先确认数据库容器名称（通常为 `text-proof-mysql`），然后进入 MySQL 命令行。

**操作步骤：**

```bash
# 查看容器列表确认名字
docker ps

# 进入 MySQL (假设密码是 rootpassword)
docker exec -it text-proof-mysql mysql -u root -prootpassword
```

> **⚠️ 注意：** `-p` 和密码之间**没有空格**。或者你可以只输入 `-p` 回车，然后隐式输入密码。

### 4.2 执行 SQL 查询

进入 `mysql>` 界面后，依次执行以下 SQL 语句来检查数据：

```sql
-- 1. 切换到目标数据库
USE text_proof_db;

-- 2. 查看所有表
SHOW TABLES;

-- 3. 查看刚刚注册的用户
SELECT * FROM sys_user;

-- 4. 查看验证码记录
SELECT * FROM sys_verification_codes;
```

✅ **预期结果：** 你应该能在表中看到 `docker_admin` 或刚刚测试的用户数据。

### 4.3 退出

验证完毕后，输入以下命令退出容器：

```bash
exit
```
