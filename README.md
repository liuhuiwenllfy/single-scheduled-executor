# single-scheduled-executor

#### 介绍

为了解决项目中定时器任务相关业务，我们推出了自己的定时器框架。

#### 软件架构

![集群化定时器框架架构图.jpg](https://resource.liulingfengyu.cn/img/open-source/集群化定时器框架架构图.jpg)

#### 安装教程

#### 使用说明

1. 配置redis
2. 配置mysql
3. 配置rabbitMQ

#### 特技

1. 简单：支持通过Web页面对任务进行CRUD操作，操作简单，一分钟上手；
2. 动态：支持动态修改任务状态、启动/停止任务，以及终止运行中任务，即时生效；
3. 执行器（分布式）：任务分布式执行，任务”执行器”支持集群部署；
4. 注册表: 执行器会自动注册到redis注册表中；
5. 弹性扩容缩容：一旦有新执行器机器上线或者下线，下次调度时将会重新分配任务；
6. 触发策略：Cron触发；
7. 故障转移：执行器会固定发送心跳，当执行器检测不到心跳时，将重新选主，并且会将故障节点的任务重新分配。
8. 实时日志：支持在线查看执行日志；
9. 自定义任务参数：支持在线配置调度任务入参，即时生效；

---

## 作者信息

Author：刘凌枫羽

邮箱：1305156911@qq.com

Blog：[刘凌枫羽博客](https://blog.csdn.net/qq_38036909?type=blog)

公众号

[![](https://resource.liulingfengyu.cn/img/公众号二维码.jpg)](https://mp.weixin.qq.com/s?__biz=MzkxNDI2OTM0Nw==&amp;mid=2247483939&amp;idx=1&amp;sn=ee8438a9047d92798765cd502820c67c&amp;chksm=c171b7eff6063ef9a41b34f61ff6ac8c73259917505eb5d9a5b9a17e9ab3653da999e48a98d5&token=418204643&lang=zh_CN#rd)

---

官网地址：https://www.liulingfengyu.cn/