import { FormEvent, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Section } from '../components/Section';
import { DataTable } from '../components/DataTable';
import { fetchEmployees } from '../api/staff';
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
import { GradeMaster, SalaryTierMaster, StoreMaster } from '../types/models';
import './StaffManagementPage.css';

const tabs = [
  { key: 'employees', label: '従業員' },
  { key: 'stores', label: '店舗' },
  { key: 'grades', label: '等級' },
  { key: 'salary', label: '給与プラン' }
] as const;

type TabKey = (typeof tabs)[number]['key'];

export const StaffManagementPage = () => {
  const [activeTab, setActiveTab] = useState<TabKey>('employees');

  return (
    <div className="staff-page">
      <div className="master-tabs">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            className={activeTab === tab.key ? 'tab-button active' : 'tab-button'}
            type="button"
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'employees' ? <EmployeesSection /> : null}
      {activeTab === 'stores' ? <StoresSection /> : null}
      {activeTab === 'grades' ? <GradesSection /> : null}
      {activeTab === 'salary' ? <SalaryTiersSection /> : null}
    </div>
  );
};

const EmployeesSection = () => {
  const query = useQuery({ queryKey: ['employees'], queryFn: fetchEmployees });
  return (
    <Section title="従業員マスタ" description="等級や給与プランの設定を確認・更新します">
      {query.isLoading ? (
        <div className="page-loading">読み込み中...</div>
      ) : (
        <DataTable
          data={query.data ?? []}
          columns={[
            { header: 'ID', accessor: (row) => row.id },
            { header: '氏名', accessor: (row) => row.name },
            { header: '等級', accessor: (row) => row.grade },
            { header: '雇用区分', accessor: (row) => row.employmentType },
            { header: '給与プラン', accessor: (row) => row.salaryPlan },
            { header: '所属店舗', accessor: (row) => row.store }
          ]}
        />
      )}
    </Section>
  );
};

const StoresSection = () => {
  const queryClient = useQueryClient();
  const storesQuery = useQuery({ queryKey: ['stores'], queryFn: fetchStores });
  const [formState, setFormState] = useState<{ id?: number; name: string; storeType: string; address: string }>({
    name: '',
    storeType: '',
    address: ''
  });
  const [error, setError] = useState('');

  const createMutation = useMutation({
    mutationFn: createStore,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stores'] });
    }
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; name: string; storeType?: string; address?: string }) =>
      updateStore(payload.id, {
        name: payload.name,
        storeType: payload.storeType,
        address: payload.address
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stores'] });
    }
  });
  const deleteMutation = useMutation({
    mutationFn: deleteStore,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['stores'] });
    }
  });

  const resetForm = () => {
    setFormState({ name: '', storeType: '', address: '' });
    setError('');
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!formState.name.trim()) {
      setError('店舗名を入力してください');
      return;
    }
    setError('');
    const payload = {
      name: formState.name.trim(),
      storeType: formState.storeType.trim() || undefined,
      address: formState.address.trim() || undefined
    };
    if (formState.id) {
      updateMutation.mutate({ id: formState.id, ...payload }, { onSuccess: resetForm });
    } else {
      createMutation.mutate(payload, { onSuccess: resetForm });
    }
  };

  const handleEdit = (store: StoreMaster) => {
    setFormState({
      id: store.id,
      name: store.name,
      storeType: store.storeType ?? '',
      address: store.address ?? ''
    });
  };

  const handleDelete = (id: number) => {
    if (window.confirm('この店舗を削除しますか？')) {
      deleteMutation.mutate(id);
    }
  };

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  return (
    <Section
      title="店舗マスタ"
      description="店舗情報を管理します。編集後は従業員マスタとも連携されます。"
      actions={
        formState.id ? (
          <button className="ghost" type="button" onClick={resetForm}>
            新規登録モードに戻す
          </button>
        ) : null
      }
    >
      <div className="master-layout">
        <div className="table-wrapper">
          {storesQuery.isLoading ? (
            <div className="page-loading">読み込み中...</div>
          ) : (
            <DataTable
              data={storesQuery.data ?? []}
              columns={[
                { header: 'ID', accessor: (row) => row.id },
                { header: '店舗名', accessor: (row) => row.name },
                { header: '種別', accessor: (row) => row.storeType ?? '-' },
                { header: '住所', accessor: (row) => row.address ?? '-' },
                {
                  header: '操作',
                  accessor: (row) => (
                    <div className="table-actions">
                      <button type="button" onClick={() => handleEdit(row)}>
                        編集
                      </button>
                      <button type="button" className="danger" onClick={() => handleDelete(row.id)}>
                        削除
                      </button>
                    </div>
                  )
                }
              ]}
            />
          )}
        </div>
        <form className="master-form" onSubmit={handleSubmit}>
          <h3>{formState.id ? '店舗を編集' : '新規店舗を登録'}</h3>
          <label>
            店舗名
            <input
              value={formState.name}
              onChange={(event) => setFormState((prev) => ({ ...prev, name: event.target.value }))}
              placeholder="例: 表参道店"
            />
          </label>
          <label>
            種別
            <input
              value={formState.storeType}
              onChange={(event) => setFormState((prev) => ({ ...prev, storeType: event.target.value }))}
              placeholder="例: フラッグシップ"
            />
          </label>
          <label>
            住所
            <input
              value={formState.address}
              onChange={(event) => setFormState((prev) => ({ ...prev, address: event.target.value }))}
              placeholder="例: 東京都渋谷区..."
            />
          </label>
          {error ? <div className="form-error">{error}</div> : null}
          <button className="primary" type="submit" disabled={isSubmitting}>
            {formState.id ? '更新する' : '登録する'}
          </button>
        </form>
      </div>
    </Section>
  );
};

const GradesSection = () => {
  const queryClient = useQueryClient();
  const gradesQuery = useQuery({ queryKey: ['grades'], queryFn: fetchGrades });
  const [formState, setFormState] = useState<{ id?: number; gradeName: string; commissionRatePercent: string }>({
    gradeName: '',
    commissionRatePercent: ''
  });
  const [error, setError] = useState('');

  const createMutation = useMutation({
    mutationFn: (payload: { gradeName: string; commissionRatePercent: number }) =>
      createGrade(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] });
    }
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; gradeName: string; commissionRatePercent: number }) =>
      updateGrade(payload.id, { gradeName: payload.gradeName, commissionRatePercent: payload.commissionRatePercent }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] });
    }
  });
  const deleteMutation = useMutation({
    mutationFn: deleteGrade,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['grades'] });
    }
  });

  const resetForm = () => {
    setFormState({ gradeName: '', commissionRatePercent: '' });
    setError('');
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!formState.gradeName.trim()) {
      setError('等級名を入力してください');
      return;
    }
    const percentValue = Number(formState.commissionRatePercent);
    if (Number.isNaN(percentValue) || percentValue < 0) {
      setError('歩合率を正しく入力してください');
      return;
    }
    setError('');
    const payload = { gradeName: formState.gradeName.trim(), commissionRatePercent: percentValue };
    if (formState.id) {
      updateMutation.mutate({ id: formState.id, ...payload }, { onSuccess: resetForm });
    } else {
      createMutation.mutate(payload, { onSuccess: resetForm });
    }
  };

  const handleEdit = (grade: GradeMaster) => {
    setFormState({
      id: grade.id,
      gradeName: grade.gradeName,
      commissionRatePercent: (grade.commissionRate * 100).toString()
    });
  };

  const handleDelete = (id: number) => {
    if (window.confirm('この等級を削除しますか？')) {
      deleteMutation.mutate(id);
    }
  };

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  const tableData = useMemo(
    () =>
      (gradesQuery.data ?? []).map((grade) => ({
        ...grade,
        displayRate: `${(grade.commissionRate * 100).toFixed(1)}%`
      })),
    [gradesQuery.data]
  );

  return (
    <Section
      title="等級マスタ"
      description="歩合率などを設定します。従業員に紐づくため事前に確認の上更新してください。"
      actions={
        formState.id ? (
          <button className="ghost" type="button" onClick={resetForm}>
            新規登録モードに戻す
          </button>
        ) : null
      }
    >
      <div className="master-layout">
        <div className="table-wrapper">
          {gradesQuery.isLoading ? (
            <div className="page-loading">読み込み中...</div>
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
                    <div className="table-actions">
                      <button type="button" onClick={() => handleEdit(row)}>
                        編集
                      </button>
                      <button type="button" className="danger" onClick={() => handleDelete(row.id)}>
                        削除
                      </button>
                    </div>
                  )
                }
              ]}
            />
          )}
        </div>
        <form className="master-form" onSubmit={handleSubmit}>
          <h3>{formState.id ? '等級を編集' : '新しい等級を登録'}</h3>
          <label>
            等級名
            <input
              value={formState.gradeName}
              onChange={(event) => setFormState((prev) => ({ ...prev, gradeName: event.target.value }))}
              placeholder="例: S1"
            />
          </label>
          <label>
            歩合率（%）
            <input
              value={formState.commissionRatePercent}
              onChange={(event) => setFormState((prev) => ({ ...prev, commissionRatePercent: event.target.value }))}
              placeholder="例: 45"
              type="number"
              min="0"
              step="0.1"
            />
          </label>
          {error ? <div className="form-error">{error}</div> : null}
          <button className="primary" type="submit" disabled={isSubmitting}>
            {formState.id ? '更新する' : '登録する'}
          </button>
        </form>
      </div>
    </Section>
  );
};

const SalaryTiersSection = () => {
  const queryClient = useQueryClient();
  const salaryQuery = useQuery({ queryKey: ['salaryTiers'], queryFn: fetchSalaryTiers });
  const [formState, setFormState] = useState<{ id?: number; planName: string; monthlyDaysOff: string; baseSalary: string }>({
    planName: '',
    monthlyDaysOff: '',
    baseSalary: ''
  });
  const [error, setError] = useState('');

  const createMutation = useMutation({
    mutationFn: (payload: { planName: string; monthlyDaysOff: number; baseSalary: number }) =>
      createSalaryTier(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaryTiers'] });
    }
  });
  const updateMutation = useMutation({
    mutationFn: (payload: { id: number; planName: string; monthlyDaysOff: number; baseSalary: number }) =>
      updateSalaryTier(payload.id, {
        planName: payload.planName,
        monthlyDaysOff: payload.monthlyDaysOff,
        baseSalary: payload.baseSalary
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaryTiers'] });
    }
  });
  const deleteMutation = useMutation({
    mutationFn: deleteSalaryTier,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['salaryTiers'] });
    }
  });

  const resetForm = () => {
    setFormState({ planName: '', monthlyDaysOff: '', baseSalary: '' });
    setError('');
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!formState.planName.trim()) {
      setError('給与プラン名を入力してください');
      return;
    }
    const monthly = Number(formState.monthlyDaysOff);
    const salary = Number(formState.baseSalary);
    if (Number.isNaN(monthly) || monthly < 0 || Number.isNaN(salary) || salary < 0) {
      setError('休日数と基本給を正しく入力してください');
      return;
    }
    setError('');
    const payload = { planName: formState.planName.trim(), monthlyDaysOff: monthly, baseSalary: salary };
    if (formState.id) {
      updateMutation.mutate({ id: formState.id, ...payload }, { onSuccess: resetForm });
    } else {
      createMutation.mutate(payload, { onSuccess: resetForm });
    }
  };

  const handleEdit = (tier: SalaryTierMaster) => {
    setFormState({
      id: tier.id,
      planName: tier.planName,
      monthlyDaysOff: tier.monthlyDaysOff.toString(),
      baseSalary: tier.baseSalary.toString()
    });
  };

  const handleDelete = (id: number) => {
    if (window.confirm('この給与プランを削除しますか？')) {
      deleteMutation.mutate(id);
    }
  };

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  return (
    <Section
      title="給与プラン"
      description="休日数と基本給の組み合わせを管理します。従業員への適用前に確認してください。"
      actions={
        formState.id ? (
          <button className="ghost" type="button" onClick={resetForm}>
            新規登録モードに戻す
          </button>
        ) : null
      }
    >
      <div className="master-layout">
        <div className="table-wrapper">
          {salaryQuery.isLoading ? (
            <div className="page-loading">読み込み中...</div>
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
                    <div className="table-actions">
                      <button type="button" onClick={() => handleEdit(row)}>
                        編集
                      </button>
                      <button type="button" className="danger" onClick={() => handleDelete(row.id)}>
                        削除
                      </button>
                    </div>
                  )
                }
              ]}
            />
          )}
        </div>
        <form className="master-form" onSubmit={handleSubmit}>
          <h3>{formState.id ? '給与プランを編集' : '新しい給与プランを登録'}</h3>
          <label>
            プラン名
            <input
              value={formState.planName}
              onChange={(event) => setFormState((prev) => ({ ...prev, planName: event.target.value }))}
              placeholder="例: 週休2日プラン"
            />
          </label>
          <label>
            月間休日数
            <input
              value={formState.monthlyDaysOff}
              onChange={(event) => setFormState((prev) => ({ ...prev, monthlyDaysOff: event.target.value }))}
              type="number"
              min="0"
            />
          </label>
          <label>
            基本給
            <input
              value={formState.baseSalary}
              onChange={(event) => setFormState((prev) => ({ ...prev, baseSalary: event.target.value }))}
              type="number"
              min="0"
            />
          </label>
          {error ? <div className="form-error">{error}</div> : null}
          <button className="primary" type="submit" disabled={isSubmitting}>
            {formState.id ? '更新する' : '登録する'}
          </button>
        </form>
      </div>
    </Section>
  );
};
