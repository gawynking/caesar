
-- 菜单表
drop table if exists caesar_menu;
create table caesar_menu(
    id          int not null auto_increment comment '菜单ID',
    parent_id   int comment '父节点ID,根父节点为0',
    menu_index  varchar(128) comment 'Menu索引',
    menu_name   varchar(128) comment 'Menu名称',
    location    int comment '位置: 1-头部菜单栏 2-侧边菜单栏',
    node_type   int comment '类型: 1-分支节点 2-叶子节点',
    menu_type   int comment '菜单类型: 1-静态菜单 2-动态菜单',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id),
    unique key(menu_index),
    unique key(menu_name)
) engine = innodb default charset=utf8mb4
comment '菜单表'
;


-- 头部菜单初始化
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(1,1,2,0,1,'task','任务管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(2,1,2,0,1,'system','系统管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(3,1,1,0,2,'user','用户中心');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(4,1,2,3,1,'exit','退出登录');
-- 任务管理菜单初始化
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(5,2,1,1,1,'offline','离线任务');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(6,2,1,5,1,'ods','ODS');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(7,2,1,5,1,'dw','DW');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(8,2,1,5,1,'ads','ADS');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(9,2,2,6,1,'ingestion','数据接入');
-- 系统管理菜单初始化
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(10,2,2,2,1,'role','角色管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(11,2,2,2,1,'user-group','用户组管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(12,2,2,2,1,'user-info','用户管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(13,2,1,2,1,'env','环境管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(14,2,2,13,1,'datasource','数据源管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(15,2,2,13,1,'schedule','调度管理');
insert into caesar_menu(id,location,node_type,parent_id,menu_type,menu_index,menu_name)values(16,2,2,13,1,'engine','引擎管理');


-- 用户组(任务编辑权限)
drop table if exists caesar_team_group;
create table caesar_team_group(
    id          int not null auto_increment comment '组ID',
    group_name  varchar(64)  comment '组名称',
    group_desc  varchar(512) comment '组描述',
    owner_id    int          comment '负责人ID',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户组'
;
insert into caesar_team_group(id,group_name,group_desc,owner_id)values(1,'默认组','默认团队',1);


-- 菜单角色表
drop table if exists caesar_menu_role;
create table caesar_menu_role(
    id         int not null auto_increment comment '角色ID',
    role_name  varchar(64)  comment '角色名称',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户组'
;
insert into caesar_menu_role(id,role_name)values(1,'超级管理员');
insert into caesar_menu_role(id,role_name)values(2,'组管理员');
insert into caesar_menu_role(id,role_name)values(3,'数据开发');
insert into caesar_menu_role(id,role_name)values(4,'一般用户');


drop table if exists caesar_menu_role_permission;
create table caesar_menu_role_permission(
    id                    int not null auto_increment comment 'ID',
    role_id               int comment '角色ID',
    menu_id               int comment '菜单ID',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '用户组'
;

-- 用户表
drop table if exists caesar_user;
create table caesar_user(
    id           int not null auto_increment comment '用户ID',
    username     varchar(64)   comment '用户名称',
    password     varchar(128)  comment '密码',
    email        varchar(128)  comment '邮箱',
    phone        varchar(32)   comment '电话',
    team_group   varchar(128)  comment '用户所属组',
    role_id      int comment '角色ID',
    is_effective int default 0 comment '是否激活: 1-是 0-否',
    create_time  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id),
    unique key(email),
    unique key(phone)
) engine = innodb default charset=utf8mb4
comment '用户表'
;
insert into caesar_user(id,username,password,email,phone,role_id,team_group,is_effective)values(1,'GawynKing','123','gawyn@gmail.com','15999998888',1,1,1);


-- 任务表
drop table if exists caesar_task;
create table caesar_task(
    id           int not null auto_increment comment '任务ID',
    menu_id      int comment '任务所属菜单',
    task_type    int comment '任务类型: 1-离线任务 2-实时任务 3-数据服务',
    task_name    varchar(32) comment '任务名称(格式: 分层.表名称[.标识符],ex: caesar_dim.dim_date),不可更改',
    exec_engine  int comment '执行引擎: 1-Hive 2-Spark 3-Flink 4-Doris 5-MySQL',
    version      int comment '版本号',
    created_user int comment '创建人',
    updated_user int comment '最后更新人',
    group_id     int comment '任务所属组标识',
    task_script  mediumtext comment '任务原始脚本',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id),
    unique key(version)
) engine = innodb default charset=utf8mb4
comment '任务表'
;
insert into caesar_task(menu_id,task_type,created_user,updated_user,task_name,exec_engine,version,group_id,task_script)values(6,1,1,1,'dim.dim_date',1,1,1,'');
insert into caesar_task(menu_id,task_type,created_user,updated_user,task_name,exec_engine,version,group_id,task_script)values(6,1,1,1,'dim.dim_city',1,2,1,'');


-- 任务模板
drop table if exists caesar_task_template;
create table caesar_task_template(
    id          int not null auto_increment comment '模板ID',
    task_type   int comment '任务类型: 1-离线任务 2-实时任务 3-数据服务',
    owner_id    int comment '创建人',
    exec_engine int comment '执行引擎: 1-Hive 2-Spark 3-Flink 4-Doris 5-MySQL',
    task_script mediumtext comment '模板脚本',
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间戳',
    update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间戳',
    primary key(id)
) engine = innodb default charset=utf8mb4
comment '任务模板表'
;

