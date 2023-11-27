# 指定基础镜像，用于后续的指令构建。
FROM openjdk:8-jdk-alpine
# 添加镜像的元数据，使用键值对的形式。
LABEL authors=LLFY
# 声明容器运行时监听的特定网络端口。
EXPOSE 8988
# 将文件或目录复制到镜像中。
COPY ./code-ease-scheduled-executor-actuator.jar /app/code-ease-scheduled-executor-actuator.jar
# 设置后续指令的工作目录。
WORKDIR /app
# 为容器创建挂载点或声明卷。
VOLUME /opt/code-ease-scheduled-executor-actuator/log
# 在容器内部设置环境变量。
ENV LT=/etc/localtime
# 在构建过程中在镜像中执行命令。
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai $LT
# 指定容器创建时的默认命令。（可以被覆盖）
CMD ["java", "-jar", "code-ease-scheduled-executor-actuator.jar"]