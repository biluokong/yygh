尚医通即为网上预约挂号系统，网上预约挂号是近年来开展的一项便民就医服务，旨在缓解看病难、挂号难的就医难题。

后端技术：
- SpringBoot：简化新Spring应用的初始搭建以及开发过程
- SpringCloud：基于Spring Boot实现的云原生应用开发工具，SpringCloud使用的技术：（SpringCloudGateway、Spring Cloud Alibaba Nacos、Spring - Cloud Alibaba Sentinel、SpringCloud Task和SpringCloudFeign等）
- MyBatis-Plus：持久层框架
- Redis：内存缓存
- RabbitMQ：消息中间件
- HTTPClient: Http协议客户端
- Swagger2：Api接口文档工具
- Nginx：负载均衡
- Lombok
- Mysql：关系型数据库
- MongoDB：面向文档的NoSQL数据库

前端技术：
- Vue.js：web 界面的渐进式框架
- Node.js： JavaScript 运行环境
- Axios：Axios 是一个基于 promise 的 HTTP 库
- NPM：包管理器
- Babel：转码器
- Webpack：打包工具

其他技术：
- Docker	：容器技术
- Git：代码管理工具 

当前只实现了视频中讲解的功能。

- 前端后台管理平台：`vue-admin-template-master`
- 前端预约挂号平台：`yygh-site`
- 后端项目：`yygh_parent`
- 医院接口模拟管理系统：`yygh_parent`下的`hospital-manage`模块，启动后访问 `http://localhost:9998/`



**node.js版本：14.1.0**，可以使用nvm工具切换到该版本后运行，否则可能会运行失败。

~~~bash
# 如果没有下载nodejs-14.1.0
nvm install 14.1.0
nvm use 14.1.0
# 设置淘宝npm源
npm config set registry https://registry.npmmirror.com/
# 下载依赖和运行项目（需要在mall4v文件夹下）
npm install
npm run dev
~~~



gitee地址：https://gitee.com/biluoer/project

