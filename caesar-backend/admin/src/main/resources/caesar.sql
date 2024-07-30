
-- 系统菜单表
drop table if exists caesar_menu;
create table caesar_menu(
    id                int not null auto_increment                                              comment '菜单ID',
    parent_id         int                                                                      comment '父节点ID,根父节点为0',
    menu_index        varchar(128)                                                             comment 'Menu索引',
    menu_name         varchar(128)                                                             comment 'Menu名称',
    location          int                                                                      comment '位置: 1-头部菜单栏 2-侧边菜单栏',
    node_type         int                                                                      comment '类型: 0-根节点 1-分支节点 2-叶子节点',
    menu_type         int                                                                      comment '菜单类型: 1-静态菜单 2-动态菜单',
    extend_properties varchar(512)                                                             comment '扩展属性,JSON格式',
    create_time       timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time       timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id),
    unique key(menu_index)
) engine = innodb default charset=utf8mb4
comment '系统菜单表'
;


-- 头部菜单初始化
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(1, 0, 'task', '任务管理', 1, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(2, 0, 'system', '系统管理', 1, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(3, 0, 'user', '用户中心', 1, 1, 2,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(4, 3, 'exit', '退出登录', 1, 2, 1,null);

-- 侧边菜单栏初始化
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(5, 1, 'offline', '离线任务', 2, 1, 1,'{"task_type":1}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(6, 5, 'ods', 'ODS', 2, 1, 1,'{"task_type":1}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(7, 5, 'dw', 'DW', 2, 1, 1,'{"task_type":1}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(8, 5, 'ads', 'ADS', 2, 1, 1,'{"task_type":1}');

insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(10,2, 'role', '角色管理', 2, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(11,2, 'user-group', '用户组管理', 2, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(12,2, 'user-info', '用户管理', 2, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(13,2, 'env', '环境管理', 2, 1, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(14,13,'datasource', '数据源管理', 2, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(15,13,'schedule', '调度管理', 2, 2, 1,null);
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(16,13,'engine', '引擎管理', 2, 2, 1,null);

insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(17, 1, 'realtime', '实时任务', 2, 1, 1,'{"task_type":2}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(18, 17, 'rt-ods', 'ODS', 2, 1, 1,'{"task_type":2}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(19, 17, 'rt-dw', 'DW', 2, 1, 1,'{"task_type":2}');
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(20, 17, 'rt-ads', 'ADS', 2, 1, 1,'{"task_type":2}');

-- 根节点
insert into caesar_menu (id, parent_id, menu_index, menu_name, location, node_type, menu_type,extend_properties) values(0, 0, 'root', '根页面', 0, 0, 1,null);
update caesar_menu set id = 0 where menu_index = 'root';


-- 菜单角色表
drop table if exists caesar_role;
create table caesar_role(
    id          int not null auto_increment                                              comment '角色ID',
    role_name   varchar(64) not null                                                     comment '角色名称',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '菜单角色表'
;
insert into caesar_role(id,role_name)values(1,'超级管理员');
insert into caesar_role(id,role_name)values(2,'组管理员');
insert into caesar_role(id,role_name)values(3,'数据开发');
insert into caesar_role(id,role_name)values(4,'一般用户');


-- 角色权限表
drop table if exists caesar_role_menu;
create table caesar_role_menu(
    id          int not null auto_increment                                              comment 'ID',
    role_id     int                                                                      comment '角色ID',
    menu_id     int                                                                      comment '菜单ID',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '角色权限表'
;


-- 用户角色表
drop table if exists caesar_user_role;
create table caesar_user_role(
    id          int not null auto_increment                                              comment 'ID',
    user_id     int not null                                                             comment '用户ID',
    role_id     int not null                                                             comment '角色ID',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户表'
;
insert into caesar_user_role(user_id,role_id)values(1,1);


-- 用户组(任务编辑权限)，组内用户可编辑，跨组用户可浏览
drop table if exists caesar_team_group;
create table caesar_team_group(
    id          int not null auto_increment                                              comment '组ID',
    group_name  varchar(64)                                                              comment '组名称',
    group_desc  varchar(512)                                                             comment '组描述',
    owner_id    int                                                                      comment '负责人ID',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户组'
;
insert into caesar_team_group(id,group_name,group_desc,owner_id)values(1,'默认组','默认团队',1);


-- 用户表
drop table if exists caesar_user;
create table caesar_user(
    id           int not null auto_increment                                             comment '用户ID',
    username     varchar(64)                                                             comment '用户名称',
    password     varchar(128)                                                            comment '密码',
    email        varchar(128)                                                            comment '邮箱',
    phone        varchar(32)                                                             comment '电话',
    is_activated int default 0                                                           comment '是否激活: 1-是 0-否',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id),
    unique key(email),
    unique key(phone),
    unique key(username)
) engine = innodb default charset=utf8mb4
comment '用户表'
;
insert into caesar_user(id,username,password,email,phone,is_activated)values(1,'GawynKing','123','gawynking@gmail.com','15999998888',1);


-- 用户组表
drop table if exists caesar_user_group;
create table caesar_user_group(
    id          int not null auto_increment                                              comment 'ID',
    user_id     int not null                                                             comment '用户ID',
    group_id    int not null                                                             comment '组ID',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户组表'
;
insert into caesar_user_group(user_id,group_id)values(1,1);


-- 组服务表
drop table if exists caesar_group_service;
create table caesar_group_service(
    id          int not null auto_increment                                              comment 'ID',
    group_id    int not null                                                             comment '组ID',
    service_tag varchar(64) not null                                                     comment '服务标识',
    level_tag   varchar(64) not null                                                     comment '分层标识',
    is_test     int default 0 not null                                                   comment '是否测试: 1-是 0-否',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '组服务表'
;
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','ods',0);
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','dim',0);
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','dwd',0);
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','dws',0);
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','ads',0);
insert into caesar_group_service(group_id,service_tag,level_tag,is_test)values(1,'default','tmp_db',1);


-- 任务表
drop table if exists caesar_task;
create table caesar_task(
    id                 int not null auto_increment                                       comment '任务ID',
    menu_id            int                                                               comment '任务所属菜单(父菜单标识)', -- 一个任务必然所属一个菜单
    task_type          int                                                               comment '任务类型: 1-离线任务 2-实时任务 3-数据服务',
    task_name          varchar(32)                                                       comment '任务名称(格式: 分层.表名称[.标识符],ex: caesar_dim.dim_date),不可更改',
    datasource_info    varchar(256)                                                      comment '数据源信息,JSON结构{"prod":"1","pre":"2","test":"3"}', -- 根据引擎匹配
    engine             int                                                               comment '执行引擎: 1-Hive 2-Spark 3-Flink 4-Doris 5-MySQL 6-Hbase',
    version            int                                                               comment '版本号', -- 每次保存生成唯一版本号
    group_id           int                                                               comment '任务所属组标识', -- 任务所属组在创建时根据创建人所属组赋值
    is_released        int                                                               comment '是否发布版本: 1-是 0-否',
    is_online          int                                                               comment '是否在线: 1-是 0-否',
    is_deleted         int default 0                                                     comment '是否删除任务: 1-是 0-否',
    task_script        mediumtext                                                        comment '任务脚本',
    checksum           varchar(256)                                                      comment '任务脚本唯一码',
    created_user       int                                                               comment '创建人',
    updated_user       int                                                               comment '最后更新人',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id),
    unique key(version)
) engine = innodb default charset=utf8mb4
comment '任务表'
;


-- 任务模板
drop table if exists caesar_task_template;
create table caesar_task_template(
    id          int not null auto_increment                                              comment '模板ID',
    temp_name   varchar(256)                                                             comment '模板名称',
    task_type   int                                                                      comment '任务类型: 1-离线任务 2-实时任务 3-数据服务',
    owner_id    int                                                                      comment '创建人',
    is_default  int default 0                                                            comment '是否默认: 1-是 0-否',
    task_script mediumtext                                                               comment '模板脚本',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '任务模板表'
;


-- 数据源表
drop table if exists caesar_datasource;
create table caesar_datasource(
    id                int not null auto_increment                                        comment '数据源ID',
    datasource_name   varchar(256)                                                       comment '数据源名称',
    datasource_type   int                                                                comment '数据源类型: 1-测试 2-预发 3-生产',
    engine            int                                                                comment '执行引擎',
    datasource_info   varchar(2048)                                                      comment '数据源信息JSON',
    owner_id          int                                                                comment '创建人',
    create_time timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '数据源表'
;
insert into caesar_datasource(datasource_name,datasource_type,engine,datasource_info,owner_id)values("MySQL测试数据源",1,401,'{"driver":"com.mysql.cj.jdbc.Driver","url":"jdbc:mysql://localhost:3306/chavin?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","username":"root","password":"mysql"}',1);
insert into caesar_datasource(datasource_name,datasource_type,engine,datasource_info,owner_id)values("MySQL预发数据源",2,401,'{"driver":"com.mysql.cj.jdbc.Driver","url":"jdbc:mysql://localhost:3306/chavin?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","username":"root","password":"mysql"}',1);
insert into caesar_datasource(datasource_name,datasource_type,engine,datasource_info,owner_id)values("MySQL生产数据源",3,401,'{"driver":"com.mysql.cj.jdbc.Driver","url":"jdbc:mysql://localhost:3306/chavin?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","username":"root","password":"mysql"}',1);



-- 任务开发参数表
drop table if exists caesar_task_parameter;
create table caesar_task_parameter(
	id             int auto_increment                                                       comment 'ID',
	param_name     varchar(64) not null                                                     comment '参数名称',
	param_desc     varchar(1024) not null                                                   comment '参数说明',
	expression     varchar(512) not null                                                    comment '表达式',
    create_time    timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time    timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '任务开发参数表'
;
insert into caesar_task_parameter(param_name,param_desc,expression)values('\$\{start_date\}','ETL调度日期,默认昨天,格式: yyyy-MM-dd','getStartDate');
insert into caesar_task_parameter(param_name,param_desc,expression)values('\$\{end_date\}','ETL调度日期,默认昨天,格式: yyyy-MM-dd','getEndDate');


-- 数据引擎表
drop table if exists caesar_engine;
create table caesar_engine(
	id             int auto_increment                                                       comment '引擎ID',
	engine_type    varchar(64)                                                              comment '引擎类型: hive spark flink',
    engine_name    varchar(64)                                                              comment '引擎名称',
    engine_version varchar(64)                                                              comment '引擎版本',
    is_activated   int default 0                                                            comment '是否有效: 1-是 0-否',
    create_time    timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time    timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
)engine = innodb default charset=utf8mb4
comment '数据引擎表'
;
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(1,'None','none','1',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(101,'Hive','hive','2',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(102,'Spark','spark','3',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(103,'Flink','flink','1.8',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(301,'Doris','doris','2',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(302,'StarRocks','starrocks','2',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(401,'MySQL','mysql','8',1);
insert into caesar_engine(id,engine_type,engine_name,engine_version,is_activated)values(501,'Hbase','hbase','2',1);


-- 任务执行记录
drop table if exists caesar_task_execute_record;
create table caesar_task_execute_record(
	id             int auto_increment                                                       comment '执行ID',
	uuid           varchar(64)                                                              comment 'UUID',
    task_id        int not null                                                             comment '任务ID',
    task_name      varchar(256) not null                                                    comment '任务名称',
    parameter      varchar(512)                                                             comment '预定义参数,JSON格式',
    environment    varchar(32)                                                              comment '执行环境: test staging production',
    begin_time     datetime not null default current_timestamp                              comment '执行开始时间戳',
    end_time       datetime                                                                 comment '执行结束时间戳',
    is_success     int not null default 0                                                   comment '是否执行成功: 1-是 0-否',
    task_log       mediumtext                                                               comment '任务日志',
    create_time    timestamp not null default current_timestamp                             comment '创建时间戳',
    update_time    timestamp not null default current_timestamp on update current_timestamp comment '更新时间戳',
    primary key(id)
)engine = innodb default charset=utf8mb4
comment '任务执行记录'
;
