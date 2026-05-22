-- 权限增量脚本：在 init.sql 初始化完成后执行。
-- 该文件只维护前端新增按钮权限和后端 API 权限，不改动原始 init.sql 文件。

-- 新增权限描述字段，用于权限管理页面展示说明文案。
set @permission_description_exists := (
  select count(*)
  from information_schema.columns
  where table_schema = database()
    and table_name = 'sys_permission'
    and column_name = 'description'
);
set @permission_description_ddl := if(
  @permission_description_exists = 0,
  'alter table sys_permission add column description varchar(255) null after component',
  'select 1'
);
prepare permission_description_stmt from @permission_description_ddl;
execute permission_description_stmt;
deallocate prepare permission_description_stmt;

-- 前端菜单名称与当前页面菜单保持一致。
update sys_permission
set permission_name = '系统配置',
    description = '左侧导航中的系统配置父级菜单',
    updated_at = current_timestamp
where permission_code = 'system';

-- 补齐已有按钮权限的排序，方便前端权限树展示。
update sys_permission set description = '系统配置下的用户管理菜单', updated_at = current_timestamp where permission_code = 'system:user';
update sys_permission set description = '系统配置下的角色管理菜单', updated_at = current_timestamp where permission_code = 'system:role';
update sys_permission set description = '系统配置下的权限管理菜单', updated_at = current_timestamp where permission_code = 'system:permission';
update sys_permission set sort_order = 103, description = '用户管理页面的新增用户按钮', updated_at = current_timestamp where permission_code = 'system:user:create';
update sys_permission set sort_order = 104, description = '用户管理页面的编辑用户按钮', updated_at = current_timestamp where permission_code = 'system:user:update';
update sys_permission set sort_order = 106, description = '用户管理页面的删除用户按钮', updated_at = current_timestamp where permission_code = 'system:user:delete';
update sys_permission set sort_order = 203, description = '角色管理页面的新增角色按钮', updated_at = current_timestamp where permission_code = 'system:role:create';
update sys_permission set sort_order = 204, description = '角色管理页面的编辑角色按钮', updated_at = current_timestamp where permission_code = 'system:role:update';
update sys_permission set sort_order = 206, description = '角色管理页面的删除角色按钮', updated_at = current_timestamp where permission_code = 'system:role:delete';
update sys_permission set sort_order = 303, description = '权限管理页面的新增权限按钮', updated_at = current_timestamp where permission_code = 'system:permission:create';
update sys_permission set sort_order = 304, description = '权限管理页面的编辑权限按钮', updated_at = current_timestamp where permission_code = 'system:permission:update';
update sys_permission set sort_order = 305, description = '权限管理页面的删除权限按钮', updated_at = current_timestamp where permission_code = 'system:permission:delete';

-- 新增前端按钮权限和后端 API 权限。
insert into sys_permission(parent_id, permission_code, permission_name, permission_type, path, component, description, sort_order, status)
values
  -- 用户管理页面按钮权限。
  (2, 'system:user:query', '查询用户', 'BUTTON', null, null, '用户管理页面的查询按钮', 101, 1),
  (2, 'system:user:reset', '重置用户查询', 'BUTTON', null, null, '用户管理页面的重置按钮', 102, 1),
  (2, 'system:user:assign-role', '分配角色', 'BUTTON', null, null, '用户管理页面的分配角色按钮', 105, 1),

  -- 角色管理页面按钮权限。
  (6, 'system:role:query', '查询角色', 'BUTTON', null, null, '角色管理页面的查询按钮', 201, 1),
  (6, 'system:role:reset', '重置角色查询', 'BUTTON', null, null, '角色管理页面的重置按钮', 202, 1),
  (6, 'system:role:assign-permission', '分配权限', 'BUTTON', null, null, '角色管理页面的分配权限按钮', 205, 1),

  -- 权限管理页面按钮权限。
  (10, 'system:permission:query', '查询权限', 'BUTTON', null, null, '权限管理页面的查询按钮', 301, 1),
  (10, 'system:permission:reset', '重置权限查询', 'BUTTON', null, null, '权限管理页面的重置按钮', 302, 1),

  -- 用户管理后端 API 权限。
  (2, 'api:system:user:list', '用户分页列表接口', 'API', '/api/system/users', 'GET', '查询用户分页列表的后端接口', 1001, 1),
  (2, 'api:system:user:detail', '用户详情接口', 'API', '/api/system/users/{id}', 'GET', '查询用户详情的后端接口', 1002, 1),
  (2, 'api:system:user:create', '新增用户接口', 'API', '/api/system/users', 'POST', '新增用户的后端接口', 1003, 1),
  (2, 'api:system:user:update', '编辑用户接口', 'API', '/api/system/users/{id}', 'PUT', '编辑用户的后端接口', 1004, 1),
  (2, 'api:system:user:delete', '删除用户接口', 'API', '/api/system/users/{id}', 'DELETE', '删除用户的后端接口', 1005, 1),
  (2, 'api:system:user:assign-role', '用户分配角色接口', 'API', '/api/system/users/{id}/roles', 'PUT', '用户分配角色的后端接口', 1006, 1),

  -- 角色管理后端 API 权限。
  (6, 'api:system:role:list', '角色分页列表接口', 'API', '/api/system/roles', 'GET', '查询角色分页列表的后端接口', 1201, 1),
  (6, 'api:system:role:detail', '角色详情接口', 'API', '/api/system/roles/{id}', 'GET', '查询角色详情的后端接口', 1202, 1),
  (6, 'api:system:role:create', '新增角色接口', 'API', '/api/system/roles', 'POST', '新增角色的后端接口', 1203, 1),
  (6, 'api:system:role:update', '编辑角色接口', 'API', '/api/system/roles/{id}', 'PUT', '编辑角色的后端接口', 1204, 1),
  (6, 'api:system:role:delete', '删除角色接口', 'API', '/api/system/roles/{id}', 'DELETE', '删除角色的后端接口', 1205, 1),
  (6, 'api:system:role:assign-permission', '角色分配权限接口', 'API', '/api/system/roles/{id}/permissions', 'PUT', '角色分配权限的后端接口', 1206, 1),

  -- 权限管理后端 API 权限。
  (10, 'api:system:permission:list', '权限分页列表接口', 'API', '/api/system/permissions', 'GET', '查询权限分页列表的后端接口', 1401, 1),
  (10, 'api:system:permission:detail', '权限详情接口', 'API', '/api/system/permissions/{id}', 'GET', '查询权限详情的后端接口', 1402, 1),
  (10, 'api:system:permission:create', '新增权限接口', 'API', '/api/system/permissions', 'POST', '新增权限的后端接口', 1403, 1),
  (10, 'api:system:permission:update', '编辑权限接口', 'API', '/api/system/permissions/{id}', 'PUT', '编辑权限的后端接口', 1404, 1),
  (10, 'api:system:permission:delete', '删除权限接口', 'API', '/api/system/permissions/{id}', 'DELETE', '删除权限的后端接口', 1405, 1)
on duplicate key update
  parent_id = values(parent_id),
  permission_name = values(permission_name),
  permission_type = values(permission_type),
  path = values(path),
  component = values(component),
  description = values(description),
  sort_order = values(sort_order),
  status = values(status),
  updated_at = current_timestamp;

-- 超级管理员补齐新增权限。
insert into sys_role_permission(role_id, permission_id)
select 1, p.id
from sys_permission p
where not exists (
  select 1
  from sys_role_permission rp
  where rp.role_id = 1
    and rp.permission_id = p.id
);
