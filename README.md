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

