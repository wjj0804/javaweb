import axios from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types/system';

export const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
});

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>;
    if (typeof body?.code === 'number' && body.code !== 0) {
      message.error(body.message || '请求失败');
      return Promise.reject(new Error(body.message || '请求失败'));
    }
    return response;
  },
  (error) => {
    message.error(error?.message || '网络请求失败');
    return Promise.reject(error);
  },
);

export async function request<T>(config: Parameters<typeof http.request>[0]): Promise<T> {
  const response = await http.request<ApiResponse<T>>(config);
  return response.data.data;
}
