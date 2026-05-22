-- 角色权限关联表需要先删除，避免外键约束影响主表重建。
drop table if exists sys_role_permission;
drop table if exists sys_user_role;
drop table if exists sys_permission;
drop table if exists sys_role;
drop table if exists sys_user;

-- 系统用户表：存储后台管理用户基础信息。
create table sys_user (
  id bigint primary key auto_increment,
  username varchar(64) not null,
  nickname varchar(64),
  password varchar(128) not null,
  phone varchar(32),
  email varchar(128),
  status tinyint not null default 1,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  constraint uk_sys_user_username unique (username)
);

-- 系统角色表：用于聚合一组权限，用户通过角色获得权限。
create table sys_role (
  id bigint primary key auto_increment,
  role_code varchar(64) not null,
  role_name varchar(64) not null,
  description varchar(255),
  status tinyint not null default 1,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  constraint uk_sys_role_code unique (role_code)
);

-- 系统权限表：菜单和按钮都作为权限节点维护。
create table sys_permission (
  id bigint primary key auto_increment,
  parent_id bigint not null default 0,
  permission_code varchar(128) not null,
  permission_name varchar(64) not null,
  permission_type varchar(16) not null,
  path varchar(255),
  component varchar(255),
  sort_order int not null default 0,
  status tinyint not null default 1,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp,
  constraint uk_sys_permission_code unique (permission_code)
);

-- 用户角色关联表：用户不直接绑定权限，只通过角色继承权限。
create table sys_user_role (
  id bigint primary key auto_increment,
  user_id bigint not null,
  role_id bigint not null,
  created_at timestamp not null default current_timestamp,
  constraint uk_sys_user_role unique (user_id, role_id),
  constraint fk_sys_user_role_user foreign key (user_id) references sys_user(id) on delete cascade,
  constraint fk_sys_user_role_role foreign key (role_id) references sys_role(id) on delete cascade
);

-- 角色权限关联表：维护角色拥有的权限集合。
create table sys_role_permission (
  id bigint primary key auto_increment,
  role_id bigint not null,
  permission_id bigint not null,
  created_at timestamp not null default current_timestamp,
  constraint uk_sys_role_permission unique (role_id, permission_id),
  constraint fk_sys_role_permission_role foreign key (role_id) references sys_role(id) on delete cascade,
  constraint fk_sys_role_permission_permission foreign key (permission_id) references sys_permission(id) on delete cascade
);

-- 初始化超级管理员用户，默认密码为 admin123。
insert into sys_user(id, username, nickname, password, phone, email, status)
values
  (1, 'admin', '超级管理员', 'admin123', '13800000000', 'admin@yq-ai.local', 1);

-- 初始化系统内置角色。
insert into sys_role(id, role_code, role_name, description, status)
values
  (1, 'SUPER_ADMIN', '超级管理员', '拥有系统全部管理权限', 1),
  (2, 'OPERATOR', '运营人员', '拥有基础运营查看和维护权限', 1);

-- 初始化系统管理菜单和按钮权限。
insert into sys_permission(id, parent_id, permission_code, permission_name, permission_type, path, component, sort_order, status)
values
  (1, 0, 'system', '系统管理', 'MENU', '/system', 'Layout', 10, 1),
  (2, 1, 'system:user', '用户管理', 'MENU', '/system/users', 'system/user/index', 11, 1),
  (3, 2, 'system:user:create', '新增用户', 'BUTTON', null, null, 12, 1),
  (4, 2, 'system:user:update', '编辑用户', 'BUTTON', null, null, 13, 1),
  (5, 2, 'system:user:delete', '删除用户', 'BUTTON', null, null, 14, 1),
  (6, 1, 'system:role', '角色管理', 'MENU', '/system/roles', 'system/role/index', 20, 1),
  (7, 6, 'system:role:create', '新增角色', 'BUTTON', null, null, 21, 1),
  (8, 6, 'system:role:update', '编辑角色', 'BUTTON', null, null, 22, 1),
  (9, 6, 'system:role:delete', '删除角色', 'BUTTON', null, null, 23, 1),
  (10, 1, 'system:permission', '权限管理', 'MENU', '/system/permissions', 'system/permission/index', 30, 1),
  (11, 10, 'system:permission:create', '新增权限', 'BUTTON', null, null, 31, 1),
  (12, 10, 'system:permission:update', '编辑权限', 'BUTTON', null, null, 32, 1),
  (13, 10, 'system:permission:delete', '删除权限', 'BUTTON', null, null, 33, 1);

-- 超级管理员绑定超级管理员角色。
insert into sys_user_role(user_id, role_id)
values (1, 1);

-- 超级管理员角色默认拥有全部权限。
insert into sys_role_permission(role_id, permission_id)
select 1, id from sys_permission;
