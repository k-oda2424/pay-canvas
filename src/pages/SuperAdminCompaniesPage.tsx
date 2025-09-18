import { FormEvent, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Section } from '../components/Section';
import { DataTable } from '../components/DataTable';
import {
  CompanyPayload,
  CompanySummary,
  createCompany,
  fetchCompanies,
  updateCompany
} from '../api/super-admin';
import './SuperAdminCompaniesPage.css';

const emptyForm: CompanyPayload = {
  name: '',
  postalCode: '',
  address: '',
  phone: '',
  contactName: '',
  contactKana: '',
  contactEmail: ''
};

type CompanyField = keyof CompanyPayload;

const CompanyFormFields = ({
  values,
  onChange,
  autoFocusName
}: {
  values: CompanyPayload;
  onChange: (field: CompanyField, value: string) => void;
  autoFocusName?: boolean;
}) => {
  return (
    <div className="company-form-layout">
      <div className="form-section">
        <h4 className="form-section-title">利用企業情報</h4>
        <div className="form-grid">
          <label>
            利用企業名
            <input
              value={values.name}
              onChange={(event) => onChange('name', event.target.value)}
              placeholder="例: 株式会社ペイキャンバス"
              autoFocus={autoFocusName}
            />
          </label>
          <label>
            郵便番号
            <input
              value={values.postalCode}
              onChange={(event) => onChange('postalCode', event.target.value)}
              placeholder="例: 150-0001"
              inputMode="numeric"
            />
          </label>
          <label>
            住所
            <input
              value={values.address}
              onChange={(event) => onChange('address', event.target.value)}
              placeholder="例: 東京都渋谷区神宮前1-1-1"
            />
          </label>
          <label>
            電話番号
            <input
              value={values.phone}
              onChange={(event) => onChange('phone', event.target.value)}
              placeholder="例: 03-0000-0000"
              inputMode="tel"
            />
          </label>
        </div>
      </div>
      <div className="form-section">
        <h4 className="form-section-title">担当者情報</h4>
        <div className="form-grid">
          <label>
            担当者氏名
            <input
              value={values.contactName}
              onChange={(event) => onChange('contactName', event.target.value)}
              placeholder="例: 佐藤 花子"
            />
          </label>
          <label>
            担当者カナ
            <input
              value={values.contactKana}
              onChange={(event) => onChange('contactKana', event.target.value)}
              placeholder="例: サトウ ハナコ"
            />
          </label>
          <label>
            連絡用メールアドレス
            <input
              type="email"
              value={values.contactEmail}
              onChange={(event) => onChange('contactEmail', event.target.value)}
              placeholder="example@paycanvas.io"
            />
          </label>
        </div>
      </div>
    </div>
  );
};

type TabKey = 'register' | 'list' | 'edit';

export const SuperAdminCompaniesPage = () => {
  const queryClient = useQueryClient();
  const companiesQuery = useQuery({ queryKey: ['super-companies'], queryFn: fetchCompanies });

  const createMutation = useMutation<CompanySummary, Error, CompanyPayload>({
    mutationFn: createCompany,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['super-companies'] })
  });

  const updateMutation = useMutation<CompanySummary, Error, { id: number; payload: CompanyPayload }>({
    mutationFn: (args) => updateCompany(args.id, args.payload),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['super-companies'] })
  });

  const [createForm, setCreateForm] = useState<CompanyPayload>(emptyForm);
  const [createMessage, setCreateMessage] = useState('');
  const [createError, setCreateError] = useState('');

  const [editForm, setEditForm] = useState<(CompanyPayload & { id: number }) | null>(null);
  const [editMessage, setEditMessage] = useState('');
  const [editError, setEditError] = useState('');
  const [activeTab, setActiveTab] = useState<TabKey>('register');

  const tableData = useMemo(() => {
    if (!companiesQuery.data) {
      return [] as CompanySummary[];
    }
    return [...companiesQuery.data].sort((a, b) => a.name.localeCompare(b.name, 'ja'));
  }, [companiesQuery.data]);

  const handleCreate = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateMessage('');
    setCreateError('');

    if (!createForm.name.trim()) {
      setCreateError('利用企業名を入力してください');
      return;
    }

    createMutation.mutate(createForm, {
      onSuccess: (response) => {
        setCreateMessage(`${response.name} を登録しました`);
        setCreateForm(emptyForm);
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
      setEditError('利用企業名を入力してください');
      return;
    }

    updateMutation.mutate(
      { id: editForm.id, payload: editForm },
      {
        onSuccess: (response) => {
          setEditMessage(`${response.name} を更新しました`);
          setEditForm(null);
        },
        onError: () => setEditError('更新に失敗しました。入力内容を確認してください')
      }
    );
  };

  const companiesLoading = companiesQuery.isLoading;

  const handleEditSelect = (company: CompanySummary) => {
    setEditForm({
      id: company.id,
      name: company.name,
      postalCode: company.postalCode ?? '',
      address: company.address ?? '',
      phone: company.phone ?? '',
      contactName: company.contactName ?? '',
      contactKana: company.contactKana ?? '',
      contactEmail: company.contactEmail ?? ''
    });
    setActiveTab('edit');
    setEditMessage('');
    setEditError('');
  };

  return (
    <div className="company-page">
      <Section
        title="利用企業管理"
        description="登録・一覧・編集をタブで切り替えながら利用企業を管理できます。"
      >
        <div className="company-tabs">
          <button
            className={`company-tab-button${activeTab === 'register' ? ' active' : ''}`}
            type="button"
            onClick={() => setActiveTab('register')}
          >
            登録
          </button>
          <button
            className={`company-tab-button${activeTab === 'list' ? ' active' : ''}`}
            type="button"
            onClick={() => setActiveTab('list')}
          >
            一覧
          </button>
          <button
            className={`company-tab-button${activeTab === 'edit' ? ' active' : ''}`}
            type="button"
            onClick={() => setActiveTab('edit')}
          >
            編集
          </button>
        </div>

        <div className="company-tab-panel">
          {activeTab === 'register' ? (
            <form className="company-form" onSubmit={handleCreate}>
              <CompanyFormFields
                values={createForm}
                onChange={(field, value) => setCreateForm((prev) => ({ ...prev, [field]: value }))}
                autoFocusName
              />
              {createMessage ? <div className="form-success">{createMessage}</div> : null}
              {createError ? <div className="form-error">{createError}</div> : null}
              <div className="form-actions">
                <button className="primary" type="submit" disabled={createMutation.isPending}>
                  {createMutation.isPending ? '登録中...' : '利用企業を登録'}
                </button>
              </div>
            </form>
          ) : null}

          {activeTab === 'list' ? (
            <div className="company-table-area">
              {companiesLoading ? (
                <div className="page-loading">利用企業を読み込んでいます...</div>
              ) : (
                <div className="company-table-wrapper">
                  <DataTable
                    data={tableData}
                    columns={[
                      { header: 'ID', accessor: (row: CompanySummary) => row.id },
                      { header: '利用企業名', accessor: (row) => row.name },
                      {
                        header: '所在地',
                        accessor: (row) => `${row.postalCode ?? ''} ${row.address ?? ''}`.trim()
                      },
                      { header: '電話番号', accessor: (row) => row.phone ?? '-' },
                      { header: '担当者', accessor: (row) => `${row.contactName ?? '-'} (${row.contactKana ?? '-'})` },
                      { header: 'メール', accessor: (row) => row.contactEmail ?? '-' },
                      {
                        header: '操作',
                        accessor: (row) => (
                          <button className="table-action-button" type="button" onClick={() => handleEditSelect(row)}>
                            編集
                          </button>
                        )
                      }
                    ]}
                  />
                </div>
              )}
              <div className="list-helper">
                <span>編集したい場合は行の「編集」をクリックしてください。</span>
              </div>
            </div>
          ) : null}

          {activeTab === 'edit' ? (
            <div className="company-edit-panel">
              <div className="edit-header">
                <h3>利用企業情報の更新</h3>
                <p>タブの一覧から選択するか、ここで利用企業を選択して編集できます。</p>
              </div>
              <div className="edit-selector">
                <label>
                  利用企業を選択
                  <select
                    value={editForm?.id.toString() ?? ''}
                    onChange={(event) => {
                      const selected = tableData.find((company) => company.id.toString() === event.target.value);
                      if (selected) {
                        handleEditSelect(selected);
                      } else {
                        setEditForm(null);
                      }
                    }}
                  >
                    <option value="">選択してください</option>
                    {tableData.map((company) => (
                      <option key={company.id} value={company.id}>
                        {company.name}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
              {editMessage ? <div className="form-success">{editMessage}</div> : null}
              {editError ? <div className="form-error">{editError}</div> : null}
              {editForm ? (
                <form className="company-form" onSubmit={handleEditSubmit}>
                  <CompanyFormFields
                    values={editForm}
                    onChange={(field, value) =>
                      setEditForm((prev) => (prev ? { ...prev, [field]: value } : prev))
                    }
                  />
                  <div className="form-actions">
                    <button className="ghost" type="button" onClick={() => setEditForm(null)}>
                      編集をクリア
                    </button>
                    <button className="primary" type="submit" disabled={updateMutation.isPending}>
                      {updateMutation.isPending ? '更新中...' : '保存する'}
                    </button>
                  </div>
                </form>
              ) : (
                <div className="company-editor-empty">
                  <p>編集対象の利用企業が選択されていません。</p>
                  <span>一覧で「編集」をクリックするか、上の選択欄から利用企業を選択してください。</span>
                </div>
              )}
            </div>
          ) : null}
        </div>
      </Section>
    </div>
  );
};
