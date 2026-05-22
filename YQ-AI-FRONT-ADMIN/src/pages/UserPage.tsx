import { PlusOutlined, SearchOutlined, TeamOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  message,
} from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import { useEffect, useMemo, useState } from 'react';
import { roleApi, userApi } from '../api/system';
import { StatusTag } from '../components/StatusTag';
import type { RoleDetailRes, UserCreateReq, UserDetailRes, UserUpdateReq } from '../types/system';

type UserFormValues = UserCreateReq;

export function UserPage() {
  const [form] = Form.useForm<UserFormValues>();
  const [records, setRecords] = useState<UserDetailRes[]>([]);
  const [roles, setRoles] = useState<RoleDetailRes[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState<number | undefined>();
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [editing, setEditing] = useState<UserDetailRes | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  const loadUsers = async (nextPage = pageNum, nextKeyword = keyword) => {
    setLoading(true);
    try {
      const page = await userApi.list({ keyword: nextKeyword, pageNum: nextPage, pageSize });
      setRecords(page.records);
      setTotal(page.total);
    } finally {
      setLoading(false);
    }
  };

  const loadRoles = async () => {
    const page = await roleApi.list({ pageNum: 1, pageSize: 500 });
    setRoles(page.records);
  };

  useEffect(() => {
    void loadUsers();
  }, [pageNum, pageSize]);

  useEffect(() => {
    void loadRoles();
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ status: 1, roleIds: [] });
    setModalOpen(true);
  };

  const openEdit = async (record: UserDetailRes) => {
    const detail = await userApi.get(record.id);
    setEditing(detail);
    form.setFieldsValue({
      username: detail.username,
      password: undefined,
      nickname: detail.nickname ?? undefined,
      phone: detail.phone ?? undefined,
      email: detail.email ?? undefined,
      status: detail.status,
      roleIds: detail.roles?.map((role) => role.id) ?? [],
    });
    setModalOpen(true);
  };

  const submit = async () => {
    const values = await form.validateFields();
    if (editing) {
      const payload: UserUpdateReq = {
        password: values.password,
        nickname: values.nickname,
        phone: values.phone,
        email: values.email,
        status: values.status,
        roleIds: values.roleIds,
      };
      await userApi.update(editing.id, payload);
      message.success('用户已更新');
    } else {
      await userApi.create(values);
      message.success('用户已创建');
    }
    setModalOpen(false);
    await loadUsers();
  };

  const remove = async (id: number) => {
    await userApi.remove(id);
    message.success('用户已删除');
    await loadUsers();
  };

  const displayRecords = useMemo(() => {
    if (status === undefined) {
      return records;
    }
    return records.filter((record) => record.status === status);
  }, [records, status]);

  const formatDateTime = (value?: string) => {
    if (!value) {
      return '-';
    }
    return value.replace('T', ' ').slice(0, 19);
  };

  const columns: ColumnsType<UserDetailRes> = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '用户名', dataIndex: 'username', width: 150 },
    { title: '昵称', dataIndex: 'nickname', width: 160, render: (value) => value || '-' },
    { title: '真实姓名', dataIndex: 'nickname', width: 160, render: (value) => value || '-' },
    { title: '手机号', dataIndex: 'phone', width: 150, render: (value) => value || '-' },
    { title: '邮箱', dataIndex: 'email', width: 190, render: (value) => value || '-' },
    { title: '状态', dataIndex: 'status', width: 90, render: (value) => <StatusTag value={value} /> },
    { title: '创建时间', dataIndex: 'createdAt', width: 190, render: formatDateTime },
    {
      title: '操作',
      key: 'actions',
      width: 220,
      fixed: 'right',
      render: (_, record) => (
        <Space size={18}>
          <Button type="link" className="table-action" onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Button type="link" className="table-action" onClick={() => openEdit(record)}>
            分配角色
          </Button>
          <Popconfirm title="确认删除该用户？" onConfirm={() => remove(record.id)}>
            <Button type="link" danger className="table-action">
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const search = async () => {
    setPageNum(1);
    await loadUsers(1, keyword);
  };

  const resetSearch = async () => {
    setKeyword('');
    setStatus(undefined);
    setPageNum(1);
    await loadUsers(1, '');
  };

  const onTableChange = (pagination: TablePaginationConfig) => {
    setPageNum(pagination.current ?? 1);
    setPageSize(pagination.pageSize ?? 10);
  };

  return (
    <div className="page page-surface">
      <div className="page-header">
        <div>
          <h1 className="page-title">用户管理</h1>
          <p className="page-description">管理后台用户基础信息，并为用户分配角色。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          新增用户
        </Button>
      </div>

      <div className="toolbar">
        <div className="toolbar-left">
          <Input
            allowClear
            placeholder="用户名/昵称/真实姓名"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            onPressEnter={search}
            style={{ width: 260 }}
          />
          <Select
            allowClear
            placeholder="状态"
            value={status}
            onChange={setStatus}
            options={[
              { label: '启用', value: 1 },
              { label: '禁用', value: 0 },
            ]}
            style={{ width: 150 }}
          />
          <Button type="primary" icon={<SearchOutlined />} onClick={search}>
            查询
          </Button>
          <Button onClick={resetSearch}>重置</Button>
        </div>
      </div>

      <div className="table-panel table-panel-plain">
        <Table
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={displayRecords}
          scroll={{ x: 1200 }}
          pagination={{
            current: pageNum,
            pageSize,
            total: status === undefined ? total : displayRecords.length,
            showSizeChanger: true,
            showTotal: (count) => `共 ${count} 条`,
          }}
          onChange={onTableChange}
        />
      </div>

      <Modal
        title={editing ? '编辑用户' : '新增用户'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={submit}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input disabled={Boolean(editing)} placeholder="admin" />
          </Form.Item>
          <Form.Item
            name="password"
            label={editing ? '新密码' : '密码'}
            rules={editing ? [] : [{ required: true }]}
          >
            <Input.Password placeholder={editing ? '不填写则保持不变' : '请输入密码'} />
          </Form.Item>
          <Form.Item name="nickname" label="昵称">
            <Input placeholder="超级管理员" />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input placeholder="13800000000" />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input placeholder="admin@yq-ai.local" />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]} />
          </Form.Item>
          <Form.Item name="roleIds" label="角色">
            <Select
              mode="multiple"
              optionFilterProp="label"
              placeholder="选择角色"
              suffixIcon={<TeamOutlined />}
              options={roles.map((role) => ({
                label: `${role.roleName}（${role.roleCode}）`,
                value: role.id,
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
