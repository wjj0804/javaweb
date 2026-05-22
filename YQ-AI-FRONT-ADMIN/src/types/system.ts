export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResult<T> {
  total: number;
  records: T[];
}

export interface PermissionDetailRes {
  id: number;
  parentId: number;
  permissionCode: string;
  permissionName: string;
  permissionType: 'MENU' | 'BUTTON' | string;
  path?: string | null;
  component?: string | null;
  description?: string | null;
  sortOrder: number;
  status: number;
  createdAt?: string;
  updatedAt?: string;
  children?: PermissionDetailRes[] | null;
}

export interface RoleDetailRes {
  id: number;
  roleCode: string;
  roleName: string;
  description?: string | null;
  status: number;
  permissions?: PermissionDetailRes[];
  createdAt?: string;
  updatedAt?: string;
}

export interface UserDetailRes {
  id: number;
  username: string;
  nickname?: string | null;
  phone?: string | null;
  email?: string | null;
  status: number;
  roles: RoleDetailRes[];
  permissions: PermissionDetailRes[];
  createdAt?: string;
  updatedAt?: string;
}

export interface PageQuery {
  keyword?: string;
  pageNum: number;
  pageSize: number;
}

export interface PermissionQueryReq extends PageQuery {
  type?: string;
}

export interface UserCreateReq {
  username: string;
  password: string;
  nickname?: string;
  phone?: string;
  email?: string;
  status?: number;
  roleIds?: number[];
}

export interface UserUpdateReq {
  password?: string;
  nickname?: string;
  phone?: string;
  email?: string;
  status?: number;
  roleIds?: number[];
}

export interface RoleCreateReq {
  roleCode: string;
  roleName: string;
  description?: string;
  status?: number;
  permissionIds?: number[];
}

export interface RoleUpdateReq {
  roleName?: string;
  description?: string;
  status?: number;
  permissionIds?: number[];
}

export interface PermissionCreateReq {
  parentId?: number;
  permissionCode: string;
  permissionName: string;
  permissionType: string;
  path?: string;
  component?: string;
  description?: string;
  sortOrder?: number;
  status?: number;
}

export interface PermissionUpdateReq {
  parentId?: number;
  permissionName?: string;
  permissionType?: string;
  path?: string;
  component?: string;
  description?: string;
  sortOrder?: number;
  status?: number;
}
