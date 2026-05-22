import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  message,
} from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import { useEffect, useMemo, useState } from 'react';
import { permissionApi } from '../api/system';
import { StatusTag } from '../components/StatusTag';
import type { PermissionCreateReq, PermissionDetailRes, PermissionUpdateReq } from '../types/system';

type PermissionFormValues = PermissionCreateReq;

function flattenPermissions(permissions: PermissionDetailRes[]): PermissionDetailRes[] {
  return permissions.flatMap((permission) => [
    permission,
    ...flattenPermissions(permission.children ?? []),
  ]);
}

export function PermissionPage() {
  const [form] = Form.useForm<PermissionFormValues>();
  const [records, setRecords] = useState<PermissionDetailRes[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [keyword, setKeyword] = useState('');
  const [type, setType] = useState<string | undefined>();
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [editing, setEditing] = useState<PermissionDetailRes | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  const loadData = async () => {
    setLoading(true);
    try {
      const page = await permissionApi.list({ keyword, type, pageNum, pageSize });
      setRecords(page.records);
      setTotal(page.total);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, [pageNum, pageSize]);

  const parentOptions = useMemo(
    () =>
      flattenPermissions(records).map((item) => ({
        label: `${item.permissionName}（${item.permissionCode}）`,
        value: item.id,
      })),
    [records],
  );

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ parentId: 0, permissionType: 'MENU', sortOrder: 0, status: 1 });
    setModalOpen(true);
  };

  const openEdit = (record: PermissionDetailRes) => {
    setEditing(record);
    form.setFieldsValue({
      parentId: record.parentId,
      permissionCode: record.permissionCode,
      permissionName: record.permissionName,
      permissionType: record.permissionType,
      path: record.path ?? undefined,
      component: record.component ?? undefined,
      description: record.description ?? undefined,
      sortOrder: record.sortOrder,
      status: record.status,
    });
    setModalOpen(true);
  };

  const submit = async () => {
    const values = await form.validateFields();
    if (editing) {
      const payload: PermissionUpdateReq = {
        parentId: values.parentId,
        permissionName: values.permissionName,
        permissionType: values.permissionType,
        path: values.path,
        component: values.component,
        description: values.description,
        sortOrder: values.sortOrder,
        status: values.status,
      };
      await permissionApi.update(editing.id, payload);
      message.success('权限已更新');
    } else {
      await permissionApi.create(values);
      message.success('权限已创建');
    }
    setModalOpen(false);
    await loadData();
  };

  const remove = async (id: number) => {
    await permissionApi.remove(id);
    message.success('权限已删除');
    await loadData();
  };

  const columns: ColumnsType<PermissionDetailRes> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 120,
      className: 'permission-id-column',
      render: (value) => <span className="permission-id-text">{value}</span>,
    },
    { title: '权限编码', dataIndex: 'permissionCode', width: 230 },
    { title: '权限名称', dataIndex: 'permissionName', width: 180 },
    { title: '权限类型', dataIndex: 'permissionType', width: 120 },
    { title: '父权限ID', dataIndex: 'parentId', width: 120 },
    { title: '排序', dataIndex: 'sortOrder', width: 90 },
    { title: '描述', dataIndex: 'description', width: 300, ellipsis: true, render: (value) => value || '-' },
    { title: '状态', dataIndex: 'status', width: 90, render: (value) => <StatusTag value={value} /> },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      width: 190,
      render: (value?: string) => (value ? value.replace('T', ' ').slice(0, 19) : '-'),
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size={18}>
          <Button type="link" className="table-action" onClick={() => openEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确认删除该权限？" onConfirm={() => remove(record.id)}>
            <Button type="link" danger className="table-action">
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const onTableChange = (pagination: TablePaginationConfig) => {
    setPageNum(pagination.current ?? 1);
    setPageSize(pagination.pageSize ?? 10);
  };

  const search = async () => {
    setPageNum(1);
    setLoading(true);
    try {
      const page = await permissionApi.list({ keyword, type, pageNum: 1, pageSize });
      setRecords(page.records);
      setTotal(page.total);
    } finally {
      setLoading(false);
    }
  };

  const resetSearch = async () => {
    setKeyword('');
    setType(undefined);
    setPageNum(1);
    setLoading(true);
    try {
      const page = await permissionApi.list({ keyword: '', type: undefined, pageNum: 1, pageSize });
      setRecords(page.records);
      setTotal(page.total);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page page-surface">
      <div className="page-header">
        <div>
          <h1 className="page-title">权限管理</h1>
          <p className="page-description">维护系统菜单和按钮权限，供角色授权时使用。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          新增权限
        </Button>
      </div>

      <div className="toolbar">
        <div className="toolbar-left">
          <Input
            allowClear
            placeholder="权限名称 / 编码"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            onPressEnter={search}
            style={{ width: 260 }}
          />
          <Select
            allowClear
            placeholder="权限类型"
            value={type}
            onChange={setType}
            options={[
              { label: '菜单', value: 'MENU' },
              { label: '按钮', value: 'BUTTON' },
              { label: '接口', value: 'API' },
            ]}
            style={{ width: 160 }}
          />
          <Button type="primary" icon={<SearchOutlined />} onClick={search}>
            查询
          </Button>
          <Button onClick={resetSearch}>重置</Button>
        </div>
      </div>

      <div className="table-panel table-panel-plain">
        <Table
          className="permission-tree-table"
          rowKey="id"
          loading={loading}
          columns={columns}
          dataSource={records}
          scroll={{ x: 1500 }}
          expandable={{ defaultExpandAllRows: true, indentSize: 18 }}
          pagination={{ current: pageNum, pageSize, total, showSizeChanger: true, showTotal: (count) => `共 ${count} 条` }}
          onChange={onTableChange}
        />
      </div>

      <Modal
        title={editing ? '编辑权限' : '新增权限'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={submit}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          <Form.Item name="parentId" label="父级权限">
            <Select
              showSearch
              optionFilterProp="label"
              options={[{ label: '根节点', value: 0 }, ...parentOptions]}
            />
          </Form.Item>
          <Form.Item name="permissionCode" label="权限编码" rules={[{ required: true }]}>
            <Input disabled={Boolean(editing)} placeholder="system:user:create" />
          </Form.Item>
          <Form.Item name="permissionName" label="权限名称" rules={[{ required: true }]}>
            <Input placeholder="新增用户" />
          </Form.Item>
          <Form.Item name="permissionType" label="权限类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '菜单', value: 'MENU' },
                { label: '按钮', value: 'BUTTON' },
                { label: '接口', value: 'API' },
              ]}
            />
          </Form.Item>
          <Form.Item name="path" label="路由路径">
            <Input placeholder="/system/users" />
          </Form.Item>
          <Form.Item name="component" label="组件路径">
            <Input placeholder="system/user/index" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="说明该权限对应的菜单、按钮或接口用途" />
          </Form.Item>
          <Form.Item name="sortOrder" label="排序">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select options={[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
