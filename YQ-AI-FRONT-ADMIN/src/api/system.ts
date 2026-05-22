import { request } from './http';
import type {
  PageQuery,
  PageResult,
  PermissionCreateReq,
  PermissionDetailRes,
  PermissionQueryReq,
  PermissionUpdateReq,
  RoleCreateReq,
  RoleDetailRes,
  RoleUpdateReq,
  UserCreateReq,
  UserDetailRes,
  UserUpdateReq,
} from '../types/system';

export const userApi = {
  list: (params: PageQuery) =>
    request<PageResult<UserDetailRes>>({ url: '/system/users', method: 'GET', params }),
  get: (id: number) => request<UserDetailRes>({ url: `/system/users/${id}`, method: 'GET' }),
  create: (data: UserCreateReq) => request<UserDetailRes>({ url: '/system/users', method: 'POST', data }),
  update: (id: number, data: UserUpdateReq) =>
    request<UserDetailRes>({ url: `/system/users/${id}`, method: 'PUT', data }),
  remove: (id: number) => request<void>({ url: `/system/users/${id}`, method: 'DELETE' }),
  assignRoles: (id: number, roleIds: number[]) =>
    request<void>({ url: `/system/users/${id}/roles`, method: 'PUT', data: { roleIds } }),
};

export const roleApi = {
  list: (params: PageQuery) =>
    request<PageResult<RoleDetailRes>>({ url: '/system/roles', method: 'GET', params }),
  get: (id: number) => request<RoleDetailRes>({ url: `/system/roles/${id}`, method: 'GET' }),
  create: (data: RoleCreateReq) => request<RoleDetailRes>({ url: '/system/roles', method: 'POST', data }),
  update: (id: number, data: RoleUpdateReq) =>
    request<RoleDetailRes>({ url: `/system/roles/${id}`, method: 'PUT', data }),
  remove: (id: number) => request<void>({ url: `/system/roles/${id}`, method: 'DELETE' }),
  assignPermissions: (id: number, permissionIds: number[]) =>
    request<void>({ url: `/system/roles/${id}/permissions`, method: 'PUT', data: { permissionIds } }),
};

export const permissionApi = {
  list: (params: PermissionQueryReq) =>
    request<PageResult<PermissionDetailRes>>({ url: '/system/permissions', method: 'GET', params }),
  get: (id: number) => request<PermissionDetailRes>({ url: `/system/permissions/${id}`, method: 'GET' }),
  create: (data: PermissionCreateReq) =>
    request<PermissionDetailRes>({ url: '/system/permissions', method: 'POST', data }),
  update: (id: number, data: PermissionUpdateReq) =>
    request<PermissionDetailRes>({ url: `/system/permissions/${id}`, method: 'PUT', data }),
  remove: (id: number) => request<void>({ url: `/system/permissions/${id}`, method: 'DELETE' }),
};
