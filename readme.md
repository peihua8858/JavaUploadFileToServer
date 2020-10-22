# 日志服务器系统 #
# 框架实现 #
springboot + mybitsPlus + mysql，前端使用bootstap + thymeleaf（模板引擎）
## 功能包括 ##
- 日志控制台
- 历史日志列表
- 接口模拟功能
- 统计代码日志
- APP多语言管理列表
- App下载管理

## 日志控制台 ##
日志通过APP主动上传，系统通过webSocket实现日志推送打印

## 接口模拟功能 ##
通过spring boot全局异常处理机制实现
## App下载管理 ##
App 通过Jenkins插件上传到springboot服务器，并以列表方式呈现供用户下载
## APP多语言管理列表 ##
通过Jenkins插件上传到springboot服务器，并以列表方式呈现供用户筛选导出
Jenkins插件通过搜索android 编译路径app\build\intermediates\incremental\mergeDevDebugResources下资源文件，通过筛选并上传到springboot

## 打包命令 ##
- mvn package -DskipTests
- mvn clean package -DskipTests
