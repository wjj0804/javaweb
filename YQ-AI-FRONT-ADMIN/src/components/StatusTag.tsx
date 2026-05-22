import { Tag } from 'antd';

interface StatusTagProps {
  value?: number;
}

export function StatusTag({ value }: StatusTagProps) {
  const enabled = value !== 0;
  return <Tag color={enabled ? 'green' : 'red'}>{enabled ? '启用' : '禁用'}</Tag>;
}
