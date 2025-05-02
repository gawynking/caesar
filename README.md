# caesar

## 一 架构介绍





## 二 安装部署

​	Caesar为前后端分离架构，因此需要分别部署前端系统和后端系统；

### 1 后端部署

1.   环境依赖

     >   Java: JDK1.8
     >
     >   Maven: 3.5

2.   下载源码

     ```shell
     git clone https://github.com/gawynking/caesar.git
     ```

3.   编译后端代码

     进入源码根目录，执行如下编译命令

     ```shell
     mvn clean install package -Dmaven.test.skip=true
     ```

4.   获取执行文件

     后端程序编译后执行文件在源码根目录下，以caesar-${version}.tar.gz格式命名，拷贝文件到安装目录，解析文件：

     ```shell
     tar -zxvf caesar-${version}.tar.gz
     ```

5.   编辑配置

     Caesar配置文件在解压后文件夹config/目录下，配置文件为:config/application.yml文件，需要修改如下配置：

    -   数据源：修改成自己本地环境信息

        ```yml
        spring:
          application:
            name: caesar
          datasource:
            url: jdbc:mysql://localhost:3306/caesar?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
            driver-class-name: com.mysql.cj.jdbc.Driver
            username: root
            password: mysql
        ```

        -   参数解释
            -   url、username、password: 需要替换成各自环境信息；

    -   调度配置:

        ```yml
        schedule:
          type: dolphin
          system: dolphinscheduler
          project: caesar___caesar
          base-url: http://localhost:12345/dolphinscheduler
          version: 3.2.0
          token: 86c184558e3e773655504a39e01a1eb6
          level: workflow
        ```

        -   参数解释
            -   type: 调度类型，维持默认值“dolphin”，目前仅集成DolphinScheduler系统，推荐版本3.2.0；
            -   system: 系统名称，维持默认值“dolphinscheduler”；
            -   version: dolphinscheduler版本，推荐3.2.0；
            -   level: 调度系统粒度，目前支持两种粒度：1-项目级别依赖；2-工作流级别依赖；推荐工作流依赖，维持默认值不变；
            -   base-url：DolphinScheduler地址，替换成各自环境地址
            -   project: Caesar维护调度依赖关系的容器，如果是工作流粒度，格式必须为：${project_name}___${workflow_name}，即需要同时指定Caesar可操作的项目下的哪个工作流，Caesar将在工作流内维护全局调度依赖，依赖管理无需人工参与。
            -   token: DolphinScheduler授权远程调用的token，请到各自dolphin项目申请；

    -   环境配置

        ```yml
        engine:
          priority: jdbc
          environment:
            mysql: /opt/homebrew/Cellar/mysql@5.7/5.7.32/bin/mysql
            hive: /opt/xxx
            spark: /opt/xxx
          none:
            code-dir: /Users/chavinking/test/caesar
        ```

        -   参数解释
            -   priority: 如果执行引擎支持jdbc，是否优先jdbc执行，无需变更；
            -   environment: 需要根据各自实际情况配置好命令地址映射
            -   none.code-dir: Caesar解析代码为可执行文件后存储路径，需要指定有权限方案的本地路径；

6.   初始化数据库

     Caesar初始化数据库脚本在源码目录地址如下：caesar/caesar-backend/admin/src/main/resources/caesar.sql

     将caesar.sql脚本到数据源配置中url配置数据库中初始化；

7.   程序管理

     解压后目录中bin/文件夹下已经存在程序运行脚本：

    -   启动程序

        ```shell
        bin/start.sh 
        ```

    -   关闭程序

        ```shell
        bin/stop.sh
        ```

    -   重启程序

        ```
        bin/restart.sh
        ```

     程序运行日志默认存储在安装目录下的logs/all.sh



### 2 前端部署

1.   环境依赖:

    -   nodejs：14.16.0
    -   nginx: 1.25 +

     提前安装对应版本nodejs，避免出现未知问题请安装推荐版本；进入源码目录：caesar/caesar-frontend目录分别执行如下命令：

     ```shell
     # npm config set registry https://repo.huaweicloud.com/repository/npm
     npm install -g @vue/cli@4.5.13
     npm install element-ui@2.15.14 -S
     npm install codemirror@5.65.5 -S --save
     npm install diff-match-patch@1.0.5 -S
     # npm install vue-codemirror@4.0.6 -S --save
     npm install -s vue-fragment@1.6.0
     npm install axios@1.7.4 --save
     npm install less-loader@6.2.0 --save-dev
     npm install moment-timezone@0.5.45
     ```

2.   编译源码

     在前端源码根目录下执行命令：

     ```
     npm run build
     ```

     编程成功会在前端源码根目录生成dist文件夹，该文件夹即前端部署程序；

3.   拷贝执行目录到安装路径

     ```shell
     cp -r dist/ xxx/
     ```

4.   修改nginx配置

     ```
     server {
         listen       8080;
         server_name  localhost;
     
         #charset koi8-r;
     
         #access_log  logs/host.access.log  main;
     
         location / {
             root   /Users/chavinking/caesar-ui; ## 前端安装程序地址 
             index  index.html index.htm;
             try_files $uri $uri/ @router;       ## 需要添加这行数据，避免刷新出现404错误
         }
     ...
     }
     ```

    -   参数解释
        -   listen：修改成前端程序运行对应端口号
        -   server_name: 前端程序运行服务器地址

     更多nginx内容请参考其他资料；

5.   重启nginx服务器

     登陆地址http://localhost:8080登陆页面

     默认用户名/密码：admin/admin





---

-   系列作者
    -   GawynKing
    -   飞鸿

-   微信群

