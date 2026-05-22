import {
  ApartmentOutlined,
  SettingOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
} from '@ant-design/icons';
import { ConfigProvider, Layout, Menu, Typography, theme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { useMemo, useState } from 'react';
import { PermissionPage } from './pages/PermissionPage';
import { RolePage } from './pages/RolePage';
import { UserPage } from './pages/UserPage';
import './styles/app.css';

const { Content, Sider } = Layout;

type MenuKey = 'users' | 'roles' | 'permissions';

const menuItems = [
  {
    key: 'system-config',
    icon: <SettingOutlined />,
    label: '系统配置',
    children: [
      { key: 'users', icon: <TeamOutlined />, label: '用户管理' },
      { key: 'roles', icon: <ApartmentOutlined />, label: '角色管理' },
      { key: 'permissions', icon: <SafetyCertificateOutlined />, label: '权限管理' },
    ],
  },
];

export default function App() {
  const [selectedKey, setSelectedKey] = useState<MenuKey>('users');

  const page = useMemo(() => {
    if (selectedKey === 'roles') return <RolePage />;
    if (selectedKey === 'permissions') return <PermissionPage />;
    return <UserPage />;
  }, [selectedKey]);

  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        algorithm: theme.defaultAlgorithm,
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 6,
          fontSize: 14,
        },
      }}
    >
      <Layout className="app-shell">
        <Sider width={232} theme="light" className="app-sider">
          <div className="brand">
            <div className="brand-mark">YQ</div>
            <div>
              <Typography.Title level={4}>燕雀教育管理系统</Typography.Title>
              <Typography.Text type="secondary">后台管理平台</Typography.Text>
            </div>
          </div>
          <Menu
            mode="inline"
            defaultOpenKeys={['system-config']}
            selectedKeys={[selectedKey]}
            items={menuItems}
            onClick={({ key }) => {
              if (key === 'users' || key === 'roles' || key === 'permissions') {
                setSelectedKey(key);
              }
            }}
          />
        </Sider>
        <Layout>
          <Content className="app-content">{page}</Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
}
