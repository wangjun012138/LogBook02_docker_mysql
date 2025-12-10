# LogBook02_docker_mysql
1. Maven 打包
首先，我们需要把 Java 代码编译成 JAR 包。在项目根目录下运行 CMD：



mvn clean package -DskipTests

注意：加上 -DskipTests 是因为测试代码里可能还在尝试连 localhost 的数据库，而构建时容器还没起起来，可能会报错。先跳过测试只打包。
验证：打包完成后，去 target 文件夹里看一眼，确保有一个叫 text-proof-platform-0.0.1-SNAPSHOT.jar 的文件。

2. Docker 一键启动
在项目根目录下运行：

docker compose up -d --build

up: 启动服务。

-d: 后台运行（不占用控制台）。

--build: 强制重新构建 Java 镜像（确保用的是最新的 JAR 包）。

第四步：验证是否成功
检查容器状态：



docker compose ps

你应该能看到两个状态为 Up 的容器。

查看日志（排错必备）： 如果想看 Java 程序是否启动成功，运行：

docker logs -f text-proof-app

如果你看到 Started TextProofApplication in ... seconds，说明成功了！

测试接口： 现在你可以像之前一样用 curl 测试了，因为我们把容器的 8080 映射到了电脑的 8080。

curl -X POST "http://localhost:8080/api/auth/code?email=docker_test@example.com"

