# CodeEaseScheduledExecutor

#### 介绍

为了解决项目中定时器任务相关业务，我们推出了自己的定时器框架。

#### 软件架构

1. ScheduledExecutorService
2. redis
3. mysql

#### 安装教程

#### 使用说明

1. 配置redis
2. 配置mysql
3. 启动执行器

#### 参与贡献

1. jdk提供的定时器接口服务ScheduledExecutorService
2. redis的发布订阅功能
3. springboot提供的RestTemplate

#### 特技

1. 支持cron时间任务
2. 支持暂停任务
3. 支持修改任务
4. 支持删除任务
5. 支持任务事件回调
6. 支持执行器集群化
7. 支持业务系统集群化
8. 支持集群节点故障转移
9. 支持单机故障任务恢复