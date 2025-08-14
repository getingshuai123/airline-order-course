# --- 阶段 1: 构建阶段 (使用带有Maven和JDK的镜像) ---
# 使用一个官方的、包含构建工具的基础镜像
FROM maven:3.8-openjdk-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖，这一步可以被Docker缓存，加快后续构建速度
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制所有源代码到镜像中
COPY src ./src

# 运行 Maven 打包命令，生成可执行的 JAR 文件
RUN mvn package -DskipTests


# --- 阶段 2: 运行阶段 (使用一个非常小的纯Java运行环境) ---
# 这是一个非常小的基础镜像，只包含运行Java程序所必需的东西
FROM eclipse-temurin:17-jre

# 设置工作目录
WORKDIR /app

# 从第一阶段(build)中，将打包好的 JAR 文件复制到当前阶段
# Spring Boot 打包后的 jar 通常在 target/ 目录下
COPY --from=build /app/target/*.jar app.jar

# 声明您的Java应用将要监听的端口
EXPOSE 8080

# 容器启动时要执行的命令：运行您的Java应用
ENTRYPOINT ["java", "-jar", "app.jar"]