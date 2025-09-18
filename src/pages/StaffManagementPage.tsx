import { FormEvent, useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { Section } from '../components/Section';
import { DataTable } from '../components/DataTable';
import { createEmployee, deleteEmployee, fetchEmployees, updateEmployee, EmployeePayload } from '../api/staff';
import {
  createStore,
  deleteStore,
  fetchStores,
  updateStore,
  createGrade,
  deleteGrade,
  fetchGrades,
  updateGrade,
  createSalaryTier,
  deleteSalaryTier,
  fetchSalaryTiers,
  updateSalaryTier
} from '../api/masters';
import { EmployeeMaster, GradeMaster, SalaryTierMaster, StoreMaster } from '../types/models';
import './StaffManagementPage.css';

type MasterKey = 'employees' | 'stores' | 'grades' | 'salary';
type InnerTabKey = 'register' | 'list' | 'edit';

const masterMeta: Record<MasterKey, { title: string; description: string }> = {
  employees: {
    title: '従業員マスタ',
    description: '従業員の基本情報や紐づく等級・給与プランを管理します。'
  },
  stores: {
    title: '店舗マスタ',
    description: '店舗名称や種別・所在地など、従業員配属先となる店舗情報を管理します。'
  },
  grades: {
    title: '等級マスタ',
    description: '歩合率などの等級設定を管理します。変更前に従業員への影響をご確認ください。'
  },
  salary: {
    title: '給与プランマスタ',
    description: '月間休日数や基本給といった給与プランを管理します。適用前に内容をご確認ください。'
  }
};

export const StaffManagementPage = () => {
  const params = useParams<{ section?: string }>();
  const navigate = useNavigate();

  const activeKey: MasterKey = useMemo(() => {
    const section = params.section as MasterKey | undefined;
    return section && section in masterMeta ? section : 'employees';
  }, [params.section]);

  useEffect(() => {
    if (!params.section || !(params.section in masterMeta)) {
      navigate('/staff/employees', { replace: true });
    }
  }, [params.section, navigate]);

  const renderContent = () => {
    switch (activeKey) {
      case 'employees':
        return <EmployeesMaster />;
      case 'stores':
        return <StoresMaster />;
      case 'grades':
        return <GradesMaster />;
      case 'salary':
        return <SalaryTiersMaster />;
      default:
        return null;
    }
  };

  const meta = masterMeta[activeKey];

  return (
    <div className="staff-page">
      <Section title={meta.title} description={meta.description}>
        {renderContent()}
      </Section>
    </div>
  );
};

// -------------------- Employees --------------------

const emptyEmployeeForm = {
  name: '',
  employmentType: '',
  gradeId: '',
  salaryTierId: '',
  storeId: '',
  guaranteedMinimumSalary: '',
  managerAllowance: ''
};

type EmployeeFormState = typeof emptyEmployeeForm;

type EmployeeFormProps = {
  values: EmployeeFormState;
  onChange: (field: keyof EmployeeFormState, value: string) => void;
  grades: GradeMaster[];
  salaryTiers: SalaryTierMaster[];
  stores: StoreMaster[];
  autoFocus?: boolean;
};

const EmployeeFormFields = ({ values, onChange, grades, salaryTiers, stores, autoFocus }: EmployeeFormProps) => (
  <div className="form-grid">
    <label>
      氏名
      <input
        value={values.name}
        onChange={(event) => onChange('name', event.target.value)}
        placeholder="例: 山田 太郎"
        autoFocus={autoFocus}
      />
    </label>
    <label>
      雇用区分
      <input
        value={values.employmentType}
        onChange={(event) => onChange('employmentType', event.target.value)}
        placeholder="例: 正社員"
      />
    </label>
    <label>
      等級
      <select value={values.gradeId} onChange={(event) => onChange('gradeId', event.target.value)}>
        <option value="">未設定</option>
        {grades.map((grade) => (
          <option key={grade.id} value={grade.id}>
            {grade.gradeName}
          </option>
        ))}
      </select>
    </label>
    <label>
      給与プラン
      <select value={values.salaryTierId} onChange={(event) => onChange('salaryTierId', event.target.value)}>
        <option value="">未設定</option>
        {salaryTiers.map((tier) => (
          <option key={tier.id} value={tier.id}>
            {tier.planName}
          </option>
        ))}
      </select>
    </label>
    <label>
      所属店舗
      <select value={values.storeId} onChange={(event) => onChange('storeId', event.target.value)}>
        <option value="">未設定</option>
        {stores.map((store) => (
          <option key={store.id} value={store.id}>
            {store.name}
          </option>
        ))}
      </select>
    </label>
    <label>
      最低保障給与（任意）
      <input
        type="number"
        min="0"
        value={values.guaranteedMinimumSalary}
        onChange={(event) => onChange('guaranteedMinimumSalary', event.target.value)}
        placeholder="例: 200000"
      />
    </label>
    <label>
      管理職手当（任意）
      <input
        type="number"
        min="0"
        value={values.managerAllowance}
        onChange={(event) => onChange('managerAllowance', event.target.value)}
        placeholder="例: 30000"
      />
    </label>
  </div>
);

const EmployeesMaster = () => {
  const queryClient = useQueryClient();
  const employeesQuery = useQuery({ queryKey: ['employees'], queryFn: fetchEmployees });
  const storesQuery = useQuery({ queryKey: ['stores'], queryFn: fetchStores });
  const gradesQuery = useQuery({ queryKey: ['grades'], queryFn: fetchGrades });
  const salaryTiersQuery = useQuery({ queryKey: ['salaryTiers'], queryFn: fetchSalaryTiers });

  const [activeTab, setActiveTab] = useState<InnerTabKey>('register');

  const [createForm, setCreateForm] = useState<EmployeeFormState>(emptyEmployeeForm);
  const [createMessage, setCreateMessage] = useState('');
  const [createError, setCreateError] = useState('');

  const [editForm, setEditForm] = useState<(EmployeeFormState & { id: number }) | null>(null);
  const [editMessage, setEditMessage] = useState('');
  const [editError, setEditError] = useState('');

  const createMutation = useMutation({
    mutationFn: createEmployee,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['employees'] })
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; body: EmployeePayload }) => updateEmployee(payload.id, payload.body),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['employees'] })
  });
  const deleteMutation = useMutation({
    mutationFn: deleteEmployee,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['employees'] })
  });

  const sortedEmployees = useMemo(() => {
    if (!employeesQuery.data) {
      return [] as EmployeeMaster[];
    }
    return [...employeesQuery.data].sort((a, b) => a.name.localeCompare(b.name, 'ja'));
  }, [employeesQuery.data]);

  const handleCreateSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateMessage('');
    setCreateError('');

    if (!createForm.name.trim()) {
      setCreateError('従業員名を入力してください');
      return;
    }
    if (!createForm.employmentType.trim()) {
      setCreateError('雇用区分を入力してください');
      return;
    }

    const payload: EmployeePayload = {
      name: createForm.name.trim(),
      employmentType: createForm.employmentType.trim(),
      gradeId: createForm.gradeId ? Number(createForm.gradeId) : null,
      salaryTierId: createForm.salaryTierId ? Number(createForm.salaryTierId) : null,
      storeId: createForm.storeId ? Number(createForm.storeId) : null,
      guaranteedMinimumSalary: createForm.guaranteedMinimumSalary
        ? Number(createForm.guaranteedMinimumSalary)
        : null,
      managerAllowance: createForm.managerAllowance ? Number(createForm.managerAllowance) : null
    };

    createMutation.mutate(payload, {
      onSuccess: (response) => {
        setCreateMessage(`${response.name} を登録しました`);
        setCreateForm(emptyEmployeeForm);
      },
      onError: () => setCreateError('登録に失敗しました。入力内容を確認してください')
    });
  };

  const handleEditSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editForm) {
      return;
    }
    setEditMessage('');
    setEditError('');

    if (!editForm.name.trim()) {
      setEditError('従業員名を入力してください');
      return;
    }
    if (!editForm.employmentType.trim()) {
      setEditError('雇用区分を入力してください');
      return;
    }

    const payload: EmployeePayload = {
      name: editForm.name.trim(),
      employmentType: editForm.employmentType.trim(),
      gradeId: editForm.gradeId ? Number(editForm.gradeId) : null,
      salaryTierId: editForm.salaryTierId ? Number(editForm.salaryTierId) : null,
      storeId: editForm.storeId ? Number(editForm.storeId) : null,
      guaranteedMinimumSalary: editForm.guaranteedMinimumSalary ? Number(editForm.guaranteedMinimumSalary) : null,
      managerAllowance: editForm.managerAllowance ? Number(editForm.managerAllowance) : null
    };

    updateMutation.mutate(
      { id: editForm.id, body: payload },
      {
        onSuccess: (response) => {
          setEditMessage(`${response.name} を更新しました`);
          setEditForm(null);
        },
        onError: () => setEditError('更新に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleDelete = (id: number) => {
    if (!window.confirm('この従業員を削除しますか？')) {
      return;
    }
    deleteMutation.mutate(id, {
      onSuccess: () => {
        if (editForm?.id === id) {
          setEditForm(null);
        }
      },
      onError: () => alert('削除に失敗しました。時間をおいて再度お試しください')
    });
  };

  const beginEdit = (employee: EmployeeMaster) => {
    setEditForm({
      id: employee.id,
      name: employee.name,
      employmentType: employee.employmentType,
      gradeId: employee.gradeId ? String(employee.gradeId) : '',
      salaryTierId: employee.salaryTierId ? String(employee.salaryTierId) : '',
      storeId: employee.storeId ? String(employee.storeId) : '',
      guaranteedMinimumSalary: employee.guaranteedMinimumSalary?.toString() ?? '',
      managerAllowance: employee.managerAllowance?.toString() ?? ''
    });
    setEditMessage('');
    setEditError('');
    setActiveTab('edit');
  };

  const grades = gradesQuery.data ?? [];
  const stores = storesQuery.data ?? [];
  const salaryTiers = salaryTiersQuery.data ?? [];

  return (
    <div className="master-card">
      <InnerTabs active={activeTab} onChange={setActiveTab} />

      {activeTab === 'register' ? (
        <form className="master-form" onSubmit={handleCreateSubmit}>
          <EmployeeFormFields
            values={createForm}
            onChange={(field, value) => setCreateForm((prev) => ({ ...prev, [field]: value }))}
            grades={grades}
            salaryTiers={salaryTiers}
            stores={stores}
            autoFocus
          />
          {createMessage ? <div className="form-success">{createMessage}</div> : null}
          {createError ? <div className="form-error">{createError}</div> : null}
          <div className="form-actions">
            <button className="primary" type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? '登録中...' : '従業員を登録'}
            </button>
          </div>
        </form>
      ) : null}

      {activeTab === 'list' ? (
        <div className="master-list">
          {employeesQuery.isLoading ? (
            <div className="page-loading">従業員情報を読み込んでいます...</div>
          ) : (
            <DataTable
              data={sortedEmployees}
              columns={[
                { header: 'ID', accessor: (row: EmployeeMaster) => row.id },
                { header: '氏名', accessor: (row: EmployeeMaster) => row.name },
                { header: '等級', accessor: (row: EmployeeMaster) => row.grade ?? '-' },
                { header: '雇用区分', accessor: (row: EmployeeMaster) => row.employmentType },
                { header: '給与プラン', accessor: (row: EmployeeMaster) => row.salaryPlan ?? '-' },
                { header: '所属店舗', accessor: (row: EmployeeMaster) => row.storeName ?? '-' },
                {
                  header: '操作',
                  accessor: (row: EmployeeMaster) => (
                    <button className="table-action" type="button" onClick={() => beginEdit(row)}>
                      編集
                    </button>
                  )
                }
              ]}
            />
          )}
          <p className="list-hint">一覧の「編集」をクリックすると編集タブが開きます。</p>
        </div>
      ) : null}

      {activeTab === 'edit' ? (
        <div className="master-edit">
          {editForm ? (
            <form className="master-form" onSubmit={handleEditSubmit}>
              <EmployeeFormFields
                values={editForm}
                onChange={(field, value) =>
                  setEditForm((prev) => (prev ? { ...prev, [field]: value } : prev))
                }
                grades={grades}
                salaryTiers={salaryTiers}
                stores={stores}
              />
              {editMessage ? <div className="form-success">{editMessage}</div> : null}
              {editError ? <div className="form-error">{editError}</div> : null}
              <div className="form-actions">
                <button className="ghost" type="button" onClick={() => setEditForm(null)}>
                  編集をキャンセル
                </button>
                <button className="primary" type="submit" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? '更新中...' : '保存する'}
                </button>
              </div>
            </form>
          ) : (
            <div className="empty-edit">編集する従業員を一覧から選択してください。</div>
          )}
        </div>
      ) : null}

      {deleteMutation.isPending ? <div className="form-info">削除を実行中です...</div> : null}
    </div>
  );
};

// -------------------- Stores --------------------

const StoresMaster = () => {
  const queryClient = useQueryClient();
  const storesQuery = useQuery({ queryKey: ['stores'], queryFn: fetchStores });

  const [activeTab, setActiveTab] = useState<InnerTabKey>('register');

  const [createForm, setCreateForm] = useState({ name: '', storeType: '', address: '' });
  const [createMessage, setCreateMessage] = useState('');
  const [createError, setCreateError] = useState('');

  const [editForm, setEditForm] = useState<(typeof createForm & { id: number }) | null>(null);
  const [editMessage, setEditMessage] = useState('');
  const [editError, setEditError] = useState('');

  const createMutation = useMutation({
    mutationFn: createStore,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['stores'] })
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; name: string; storeType?: string; address?: string }) =>
      updateStore(payload.id, {
        name: payload.name,
        storeType: payload.storeType,
        address: payload.address
      }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['stores'] })
  });
  const deleteMutation = useMutation({
    mutationFn: deleteStore,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['stores'] })
  });

  const sortedStores = useMemo(() => {
    if (!storesQuery.data) {
      return [] as StoreMaster[];
    }
    return [...storesQuery.data].sort((a, b) => a.name.localeCompare(b.name, 'ja'));
  }, [storesQuery.data]);

  const handleCreateSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateMessage('');
    setCreateError('');

    if (!createForm.name.trim()) {
      setCreateError('店舗名を入力してください');
      return;
    }

    const payload = {
      name: createForm.name.trim(),
      storeType: createForm.storeType.trim() || undefined,
      address: createForm.address.trim() || undefined
    };

    createMutation.mutate(payload, {
      onSuccess: (response) => {
        setCreateMessage(`${response.name} を登録しました`);
        setCreateForm({ name: '', storeType: '', address: '' });
      },
      onError: () => setCreateError('登録に失敗しました。入力内容を確認してください')
    });
  };

  const handleEditSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editForm) {
      return;
    }
    setEditMessage('');
    setEditError('');

    if (!editForm.name.trim()) {
      setEditError('店舗名を入力してください');
      return;
    }

    const payload = {
      name: editForm.name.trim(),
      storeType: editForm.storeType.trim() || undefined,
      address: editForm.address.trim() || undefined
    };

    updateMutation.mutate(
      { id: editForm.id, ...payload },
      {
        onSuccess: (response) => {
          setEditMessage(`${response.name} を更新しました`);
          setEditForm(null);
        },
        onError: () => setEditError('更新に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleDelete = (id: number) => {
    if (!window.confirm('この店舗を削除しますか？')) {
      return;
    }
    deleteMutation.mutate(id);
  };

  const beginEdit = (store: StoreMaster) => {
    setEditForm({
      id: store.id,
      name: store.name,
      storeType: store.storeType ?? '',
      address: store.address ?? ''
    });
    setEditMessage('');
    setEditError('');
    setActiveTab('edit');
  };

  return (
    <div className="master-card">
      <InnerTabs active={activeTab} onChange={setActiveTab} />

      {activeTab === 'register' ? (
        <form className="master-form" onSubmit={handleCreateSubmit}>
          <div className="form-grid">
            <label>
              店舗名
              <input
                value={createForm.name}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, name: event.target.value }))}
                placeholder="例: 表参道店"
                autoFocus
              />
            </label>
            <label>
              種別
              <input
                value={createForm.storeType}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, storeType: event.target.value }))}
                placeholder="例: フラッグシップ"
              />
            </label>
            <label>
              住所
              <input
                value={createForm.address}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, address: event.target.value }))}
                placeholder="例: 東京都渋谷区..."
              />
            </label>
          </div>
          {createMessage ? <div className="form-success">{createMessage}</div> : null}
          {createError ? <div className="form-error">{createError}</div> : null}
          <div className="form-actions">
            <button className="primary" type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? '登録中...' : '店舗を登録'}
            </button>
          </div>
        </form>
      ) : null}

      {activeTab === 'list' ? (
        <div className="master-list">
          {storesQuery.isLoading ? (
            <div className="page-loading">店舗情報を読み込んでいます...</div>
          ) : (
            <DataTable
              data={sortedStores}
              columns={[
                { header: 'ID', accessor: (row: StoreMaster) => row.id },
                { header: '店舗名', accessor: (row: StoreMaster) => row.name },
                { header: '種別', accessor: (row: StoreMaster) => row.storeType ?? '-' },
                { header: '住所', accessor: (row: StoreMaster) => row.address ?? '-' },
                {
                  header: '操作',
                  accessor: (row: StoreMaster) => (
                    <button className="table-action" type="button" onClick={() => beginEdit(row)}>
                      編集
                    </button>
                  )
                }
              ]}
            />
          )}
          <p className="list-hint">編集したい店舗は一覧から選択してください。</p>
        </div>
      ) : null}

      {activeTab === 'edit' ? (
        <div className="master-edit">
          {editForm ? (
            <form className="master-form" onSubmit={handleEditSubmit}>
              <div className="form-grid">
                <label>
                  店舗名
                  <input
                    value={editForm.name}
                    onChange={(event) => setEditForm((prev) => (prev ? { ...prev, name: event.target.value } : prev))}
                  />
                </label>
                <label>
                  種別
                  <input
                    value={editForm.storeType}
                    onChange={(event) =>
                      setEditForm((prev) => (prev ? { ...prev, storeType: event.target.value } : prev))
                    }
                  />
                </label>
                <label>
                  住所
                  <input
                    value={editForm.address}
                    onChange={(event) => setEditForm((prev) => (prev ? { ...prev, address: event.target.value } : prev))}
                  />
                </label>
              </div>
              {editMessage ? <div className="form-success">{editMessage}</div> : null}
              {editError ? <div className="form-error">{editError}</div> : null}
              <div className="form-actions">
                <button className="ghost" type="button" onClick={() => setEditForm(null)}>
                  編集をキャンセル
                </button>
                <button className="primary" type="submit" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? '更新中...' : '保存する'}
                </button>
              </div>
            </form>
          ) : (
            <div className="empty-edit">編集対象の店舗が選択されていません。</div>
          )}
        </div>
      ) : null}
    </div>
  );
};

// -------------------- Grades --------------------

const GradesMaster = () => {
  const queryClient = useQueryClient();
  const gradesQuery = useQuery({ queryKey: ['grades'], queryFn: fetchGrades });

  const [activeTab, setActiveTab] = useState<InnerTabKey>('register');

  const [createForm, setCreateForm] = useState({ gradeName: '', commissionRatePercent: '' });
  const [createMessage, setCreateMessage] = useState('');
  const [createError, setCreateError] = useState('');

  const [editForm, setEditForm] = useState<(typeof createForm & { id: number }) | null>(null);
  const [editMessage, setEditMessage] = useState('');
  const [editError, setEditError] = useState('');

  const createMutation = useMutation({
    mutationFn: (payload: { gradeName: string; commissionRatePercent: number }) => createGrade(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['grades'] })
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; gradeName: string; commissionRatePercent: number }) =>
      updateGrade(payload.id, { gradeName: payload.gradeName, commissionRatePercent: payload.commissionRatePercent }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['grades'] })
  });
  const deleteMutation = useMutation({
    mutationFn: deleteGrade,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['grades'] })
  });

  const tableData = useMemo(
    () =>
      (gradesQuery.data ?? []).map((grade) => ({
        ...grade,
        displayRate: `${(grade.commissionRate * 100).toFixed(1)}%`
      })),
    [gradesQuery.data]
  );

  const handleCreateSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateMessage('');
    setCreateError('');

    if (!createForm.gradeName.trim()) {
      setCreateError('等級名を入力してください');
      return;
    }
    const percent = Number(createForm.commissionRatePercent);
    if (Number.isNaN(percent) || percent < 0) {
      setCreateError('歩合率を正しく入力してください');
      return;
    }

    createMutation.mutate(
      { gradeName: createForm.gradeName.trim(), commissionRatePercent: percent },
      {
        onSuccess: (response) => {
          setCreateMessage(`${response.gradeName} を登録しました`);
          setCreateForm({ gradeName: '', commissionRatePercent: '' });
        },
        onError: () => setCreateError('登録に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleEditSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editForm) {
      return;
    }
    setEditMessage('');
    setEditError('');

    if (!editForm.gradeName.trim()) {
      setEditError('等級名を入力してください');
      return;
    }
    const percent = Number(editForm.commissionRatePercent);
    if (Number.isNaN(percent) || percent < 0) {
      setEditError('歩合率を正しく入力してください');
      return;
    }

    updateMutation.mutate(
      { id: editForm.id, gradeName: editForm.gradeName.trim(), commissionRatePercent: percent },
      {
        onSuccess: (response) => {
          setEditMessage(`${response.gradeName} を更新しました`);
          setEditForm(null);
        },
        onError: () => setEditError('更新に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleDelete = (id: number) => {
    if (!window.confirm('この等級を削除しますか？')) {
      return;
    }
    deleteMutation.mutate(id);
  };

  const beginEdit = (grade: GradeMaster) => {
    setEditForm({
      id: grade.id,
      gradeName: grade.gradeName,
      commissionRatePercent: (grade.commissionRate * 100).toString()
    });
    setEditMessage('');
    setEditError('');
    setActiveTab('edit');
  };

  return (
    <div className="master-card">
      <InnerTabs active={activeTab} onChange={setActiveTab} />

      {activeTab === 'register' ? (
        <form className="master-form" onSubmit={handleCreateSubmit}>
          <div className="form-grid">
            <label>
              等級名
              <input
                value={createForm.gradeName}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, gradeName: event.target.value }))}
                placeholder="例: S1"
                autoFocus
              />
            </label>
            <label>
              歩合率（%）
              <input
                type="number"
                min="0"
                step="0.1"
                value={createForm.commissionRatePercent}
                onChange={(event) =>
                  setCreateForm((prev) => ({ ...prev, commissionRatePercent: event.target.value }))
                }
                placeholder="例: 45"
              />
            </label>
          </div>
          {createMessage ? <div className="form-success">{createMessage}</div> : null}
          {createError ? <div className="form-error">{createError}</div> : null}
          <div className="form-actions">
            <button className="primary" type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? '登録中...' : '等級を登録'}
            </button>
          </div>
        </form>
      ) : null}

      {activeTab === 'list' ? (
        <div className="master-list">
          {gradesQuery.isLoading ? (
            <div className="page-loading">等級情報を読み込んでいます...</div>
          ) : (
            <DataTable
              data={tableData}
              columns={[
                { header: 'ID', accessor: (row) => row.id },
                { header: '等級名', accessor: (row) => row.gradeName },
                { header: '歩合率', accessor: (row) => row.displayRate },
                {
                  header: '操作',
                  accessor: (row) => (
                    <button className="table-action" type="button" onClick={() => beginEdit(row)}>
                      編集
                    </button>
                  )
                }
              ]}
            />
          )}
          <p className="list-hint">編集したい等級を一覧から選択してください。</p>
        </div>
      ) : null}

      {activeTab === 'edit' ? (
        <div className="master-edit">
          {editForm ? (
            <form className="master-form" onSubmit={handleEditSubmit}>
              <div className="form-grid">
                <label>
                  等級名
                  <input
                    value={editForm.gradeName}
                    onChange={(event) =>
                      setEditForm((prev) => (prev ? { ...prev, gradeName: event.target.value } : prev))
                    }
                  />
                </label>
                <label>
                  歩合率（%）
                  <input
                    type="number"
                    min="0"
                    step="0.1"
                    value={editForm.commissionRatePercent}
                    onChange={(event) =>
                      setEditForm((prev) =>
                        prev ? { ...prev, commissionRatePercent: event.target.value } : prev
                      )
                    }
                  />
                </label>
              </div>
              {editMessage ? <div className="form-success">{editMessage}</div> : null}
              {editError ? <div className="form-error">{editError}</div> : null}
              <div className="form-actions">
                <button className="ghost" type="button" onClick={() => setEditForm(null)}>
                  編集をキャンセル
                </button>
                <button className="primary" type="submit" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? '更新中...' : '保存する'}
                </button>
              </div>
            </form>
          ) : (
            <div className="empty-edit">編集する等級が選択されていません。</div>
          )}
        </div>
      ) : null}
    </div>
  );
};

// -------------------- Salary tiers --------------------

const SalaryTiersMaster = () => {
  const queryClient = useQueryClient();
  const salaryQuery = useQuery({ queryKey: ['salaryTiers'], queryFn: fetchSalaryTiers });

  const [activeTab, setActiveTab] = useState<InnerTabKey>('register');

  const [createForm, setCreateForm] = useState({ planName: '', monthlyDaysOff: '', baseSalary: '' });
  const [createMessage, setCreateMessage] = useState('');
  const [createError, setCreateError] = useState('');

  const [editForm, setEditForm] = useState<(typeof createForm & { id: number }) | null>(null);
  const [editMessage, setEditMessage] = useState('');
  const [editError, setEditError] = useState('');

  const createMutation = useMutation({
    mutationFn: (payload: { planName: string; monthlyDaysOff: number; baseSalary: number }) =>
      createSalaryTier(payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['salaryTiers'] })
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; planName: string; monthlyDaysOff: number; baseSalary: number }) =>
      updateSalaryTier(payload.id, {
        planName: payload.planName,
        monthlyDaysOff: payload.monthlyDaysOff,
        baseSalary: payload.baseSalary
      }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['salaryTiers'] })
  });
  const deleteMutation = useMutation({
    mutationFn: deleteSalaryTier,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['salaryTiers'] })
  });

  const handleCreateSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateMessage('');
    setCreateError('');

    if (!createForm.planName.trim()) {
      setCreateError('給与プラン名を入力してください');
      return;
    }
    const monthlyDaysOff = Number(createForm.monthlyDaysOff);
    const baseSalary = Number(createForm.baseSalary);
    if (Number.isNaN(monthlyDaysOff) || monthlyDaysOff < 0 || Number.isNaN(baseSalary) || baseSalary < 0) {
      setCreateError('休日数と基本給を正しく入力してください');
      return;
    }

    createMutation.mutate(
      { planName: createForm.planName.trim(), monthlyDaysOff, baseSalary },
      {
        onSuccess: (response) => {
          setCreateMessage(`${response.planName} を登録しました`);
          setCreateForm({ planName: '', monthlyDaysOff: '', baseSalary: '' });
        },
        onError: () => setCreateError('登録に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleEditSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!editForm) {
      return;
    }
    setEditMessage('');
    setEditError('');

    if (!editForm.planName.trim()) {
      setEditError('給与プラン名を入力してください');
      return;
    }
    const monthlyDaysOff = Number(editForm.monthlyDaysOff);
    const baseSalary = Number(editForm.baseSalary);
    if (Number.isNaN(monthlyDaysOff) || monthlyDaysOff < 0 || Number.isNaN(baseSalary) || baseSalary < 0) {
      setEditError('休日数と基本給を正しく入力してください');
      return;
    }

    updateMutation.mutate(
      { id: editForm.id, planName: editForm.planName.trim(), monthlyDaysOff, baseSalary },
      {
        onSuccess: (response) => {
          setEditMessage(`${response.planName} を更新しました`);
          setEditForm(null);
        },
        onError: () => setEditError('更新に失敗しました。入力内容を確認してください')
      }
    );
  };

  const handleDelete = (id: number) => {
    if (!window.confirm('この給与プランを削除しますか？')) {
      return;
    }
    deleteMutation.mutate(id);
  };

  const beginEdit = (tier: SalaryTierMaster) => {
    setEditForm({
      id: tier.id,
      planName: tier.planName,
      monthlyDaysOff: tier.monthlyDaysOff.toString(),
      baseSalary: tier.baseSalary.toString()
    });
    setEditMessage('');
    setEditError('');
    setActiveTab('edit');
  };

  return (
    <div className="master-card">
      <InnerTabs active={activeTab} onChange={setActiveTab} />

      {activeTab === 'register' ? (
        <form className="master-form" onSubmit={handleCreateSubmit}>
          <div className="form-grid">
            <label>
              プラン名
              <input
                value={createForm.planName}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, planName: event.target.value }))}
                placeholder="例: 週休2日プラン"
                autoFocus
              />
            </label>
            <label>
              月間休日数
              <input
                type="number"
                min="0"
                value={createForm.monthlyDaysOff}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, monthlyDaysOff: event.target.value }))}
              />
            </label>
            <label>
              基本給
              <input
                type="number"
                min="0"
                value={createForm.baseSalary}
                onChange={(event) => setCreateForm((prev) => ({ ...prev, baseSalary: event.target.value }))}
              />
            </label>
          </div>
          {createMessage ? <div className="form-success">{createMessage}</div> : null}
          {createError ? <div className="form-error">{createError}</div> : null}
          <div className="form-actions">
            <button className="primary" type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? '登録中...' : '給与プランを登録'}
            </button>
          </div>
        </form>
      ) : null}

      {activeTab === 'list' ? (
        <div className="master-list">
          {salaryQuery.isLoading ? (
            <div className="page-loading">給与プランを読み込んでいます...</div>
          ) : (
            <DataTable
              data={salaryQuery.data ?? []}
              columns={[
                { header: 'ID', accessor: (row) => row.id },
                { header: 'プラン名', accessor: (row) => row.planName },
                { header: '月間休日数', accessor: (row) => `${row.monthlyDaysOff}日` },
                { header: '基本給', accessor: (row) => `¥${row.baseSalary.toLocaleString()}` },
                {
                  header: '操作',
                  accessor: (row) => (
                    <button className="table-action" type="button" onClick={() => beginEdit(row)}>
                      編集
                    </button>
                  )
                }
              ]}
            />
          )}
          <p className="list-hint">一覧の「編集」をクリックすると詳細が表示されます。</p>
        </div>
      ) : null}

      {activeTab === 'edit' ? (
        <div className="master-edit">
          {editForm ? (
            <form className="master-form" onSubmit={handleEditSubmit}>
              <div className="form-grid">
                <label>
                  プラン名
                  <input
                    value={editForm.planName}
                    onChange={(event) => setEditForm((prev) => (prev ? { ...prev, planName: event.target.value } : prev))}
                  />
                </label>
                <label>
                  月間休日数
                  <input
                    type="number"
                    min="0"
                    value={editForm.monthlyDaysOff}
                    onChange={(event) =>
                      setEditForm((prev) => (prev ? { ...prev, monthlyDaysOff: event.target.value } : prev))
                    }
                  />
                </label>
                <label>
                  基本給
                  <input
                    type="number"
                    min="0"
                    value={editForm.baseSalary}
                    onChange={(event) =>
                      setEditForm((prev) => (prev ? { ...prev, baseSalary: event.target.value } : prev))
                    }
                  />
                </label>
              </div>
              {editMessage ? <div className="form-success">{editMessage}</div> : null}
              {editError ? <div className="form-error">{editError}</div> : null}
              <div className="form-actions">
                <button className="ghost" type="button" onClick={() => setEditForm(null)}>
                  編集をキャンセル
                </button>
                <button className="primary" type="submit" disabled={updateMutation.isPending}>
                  {updateMutation.isPending ? '更新中...' : '保存する'}
                </button>
              </div>
            </form>
          ) : (
            <div className="empty-edit">編集する給与プランが選択されていません。</div>
          )}
        </div>
      ) : null}
    </div>
  );
};

// -------------------- Shared components --------------------

type InnerTabsProps = {
  active: InnerTabKey;
  onChange: (tab: InnerTabKey) => void;
};

const InnerTabs = ({ active, onChange }: InnerTabsProps) => (
  <div className="inner-tabs">
    <button
      type="button"
      className={`inner-tab${active === 'register' ? ' active' : ''}`}
      onClick={() => onChange('register')}
    >
      登録
    </button>
    <button
      type="button"
      className={`inner-tab${active === 'list' ? ' active' : ''}`}
      onClick={() => onChange('list')}
    >
      一覧
    </button>
    <button
      type="button"
      className={`inner-tab${active === 'edit' ? ' active' : ''}`}
      onClick={() => onChange('edit')}
    >
      編集
    </button>
  </div>
);

export default StaffManagementPage;
