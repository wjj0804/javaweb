import { DeleteOutlined, EditOutlined, PlusOutlined, SafetyOutlined, SearchOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tree,
  message,
} from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import type { DataNode } from 'antd/es/tree';
import { useEffect, useMemo, useState } from 'react';
import { permissionApi, roleApi } from '../api/system';
import { StatusTag } from '../components/StatusTag';
import type { PermissionDetailRes, RoleCreateReq, RoleDetailRes, RoleUpdateReq } from '../types/system';

type RoleFormValues = RoleCreateReq;

function toTreeData(permissions: PermissionDetailRes[]): DataNode[] {
  return permissions.map((permission) => ({
      key: permission.id,
      title: `${permission.permissionName}（${permission.permissionCode}）`,
      children: permission.children?.length ? toTreeData(permission.children) : undefined,
  }));
}

export function RolePage() {
  const [form] = Form.useForm<RoleFormValues>();
  const [records, setRecords] = useState<RoleDetailRes[]>([]);
  const [permissions, setPermissions] = useState<PermissionDetailRes[]>([]);
  const [checkedPermissionIds, setCheckedPermissionIds] = useState<number[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [editing, setEditing] = useState<RoleDetailRes | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  const loadRoles = async (nextPage = pageNum) => {
    setLoading(true);
    try {
      const page = await roleApi.list({ keyword, pageNum: nextPage, pageSize });
      setRecords(page.records);
      setTotal(page.total);
    } finally {
      setLoading(false);
    }
  };

  const loadPermissions = async () => {
    const page = await permissionApi.list({ pageNum: 1, pageSize: 500 });
    setPermissions(page.records);
  };

  useEffect(() => {
    void loadRoles();
  }, [pageNum, pageSize]);

  useEffect(() => {
    void loadPermissions();
  }, []);

  const permissionTree = useMemo(() => toTreeData(permissions), [permissions]);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ status: 1 });
    setCheckedPermissionIds([]);
    setModalOpen(true);
  };

  const openEdit = async (record: RoleDetailRes) => {
    const detail = await roleApi.get(record.id);
    setEditing(record);
    form.setFieldsValue({
      roleCode: detail.roleCode,
      roleName: detail.roleName,
      description: detail.description ?? undefined,
      status: detail.status,
    });
    setCheckedPermissionIds(detail.permissions?.map((permission) => permission.id) ?? []);
    setModalOpen(true);
  };

  const submit = async () => {
    const values = await form.validateFields();
    if (editing) {
      const payload: RoleUpdateReq = {
        roleName: values.roleName,
        description: values.description,
        status: values.status,
        permissionIds: checkedPermissionIds,
      };
      await roleApi.update(editing.id, payload);
      message.success('角色已更新');
    } else {
      await roleApi.create({ ...values, permissionIds: checkedPermissionIds });
      message.success('角色已创建');
    }
    setModalOpen(false);
    await loadRoles();
  };

  const remove = async (id: number) => {
    await roleApi.remove(id);
    message.success('角色已删除');
    await loadRoles();
  };

  const columns: ColumnsType<RoleDetailRes> = [
    { title: '角色名称', dataIndex: 'roleName', width: 160 },
    { title: '角色编码', dataIndex: 'roleCode', width: 180 },
    { title: '描述', dataIndex: 'description', ellipsis: true, render: (value) => value || '-' },
    { title: '状态', dataIndex: 'status', width: 100, render: (value) => <StatusTag value={value} /> },
    {
      title: '操作',
      key: 'actions',
      width: 170,
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)} />
          <Popconfirm title="确认删除该角色？" onConfirm={() => remove(record.id)}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const search = async () => {
    setPageNum(1);
    await loadRoles(1);
  };

  const resetSearch = async () => {
    setKeyword('');
    setPageNum(1);
    const page = await roleApi.list({ keyword: '', pageNum: 1, pageSize });
    setRecords(page.records);
    setTotal(page.total);
  };

  const onTableChange = (pagination: TablePaginationConfig) => {
    setPageNum(pagination.current ?? 1);
    setPageSize(pagination.pageSize ?? 10);
  };

  return (
    <div className="page page-surface">
      <div className="page-header">
        <div>
          <h1 className="page-title">角色管理</h1>
          <p className="page-description">维护后台角色信息，并为角色分配菜单和按钮权限。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          新增角色
        </Button>
      </div>

      <div className="toolbar">
        <div className="toolbar-left">
          <Input
            allowClear
            placeholder="角色名称 / 编码"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            onPressEnter={search}
            style={{ width: 260 }}
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
          dataSource={records}
          pagination={{ current: pageNum, pageSize, total, showSizeChanger: true, showTotal: (count) => `共 ${count} 条` }}
          onChange={onTableChange}
        />
      </div>

      <Modal
        title={editing ? '编辑角色' : '新增角色'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={submit}
        destroyOnClose
        width={720}
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="roleCode" label="角色编码" rules={[{ required: true }]}>
            <Input disabled={Boolean(editing)} placeholder="SUPER_ADMIN" />
          </Form.Item>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
            <Input placeholder="超级管理员" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="角色说明" />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]} />
          </Form.Item>
          <Form.Item label="权限分配">
            <div className="table-panel">
              <Space style={{ marginBottom: 10 }}>
                <SafetyOutlined />
                <span>选择该角色拥有的菜单和按钮权限</span>
              </Space>
              <Tree
                checkable
                defaultExpandAll
                treeData={permissionTree}
                checkedKeys={checkedPermissionIds}
                onCheck={(keys) => setCheckedPermissionIds(keys as number[])}
              />
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
