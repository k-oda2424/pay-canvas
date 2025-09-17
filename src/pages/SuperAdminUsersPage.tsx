import { FormEvent, useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Section } from '../components/Section';
import { createCompanyAdmin, fetchCompanies } from '../api/super-admin';
import './SuperAdminUsersPage.css';

export const SuperAdminUsersPage = () => {
  const companiesQuery = useQuery({ queryKey: ['super-companies'], queryFn: fetchCompanies });
  const mutation = useMutation({ mutationFn: createCompanyAdmin });

  const [formState, setFormState] = useState({
    companyId: '',
    email: '',
    displayName: '',
    password: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setMessage('');
    setError('');

    if (!formState.companyId) {
      setError('会社を選択してください');
      return;
    }
    if (!formState.email.trim()) {
      setError('メールアドレスを入力してください');
      return;
    }
    if (!formState.displayName.trim()) {
      setError('表示名を入力してください');
      return;
    }
    if (formState.password.length < 8) {
      setError('パスワードは8文字以上で入力してください');
      return;
    }

    mutation.mutate(
      {
        companyId: Number(formState.companyId),
        email: formState.email.trim(),
        displayName: formState.displayName.trim(),
        password: formState.password
      },
      {
        onSuccess: (response) => {
          setMessage(`${response.displayName} を ${response.companyName} の管理者として登録しました`);
          setFormState({ companyId: formState.companyId, email: '', displayName: '', password: '' });
        },
        onError: () => {
          setError('登録に失敗しました。入力内容を確認してください');
        }
      }
    );
  };

  return (
    <Section
      title="会社管理者の登録"
      description="各テナントの管理者ユーザーを作成します。登録後、ログイン情報を通知してください。"
    >
      {companiesQuery.isLoading ? (
        <div className="page-loading">会社情報を読み込んでいます...</div>
      ) : (
        <form className="super-form" onSubmit={handleSubmit}>
          <label>
            会社
            <select
              value={formState.companyId}
              onChange={(event) => setFormState((prev) => ({ ...prev, companyId: event.target.value }))}
            >
              <option value="">選択してください</option>
              {companiesQuery.data?.map((company) => (
                <option key={company.id} value={company.id}>
                  {company.name} ({company.status})
                </option>
              ))}
            </select>
          </label>
          <label>
            メールアドレス
            <input
              type="email"
              value={formState.email}
              onChange={(event) => setFormState((prev) => ({ ...prev, email: event.target.value }))}
              placeholder="admin@example.com"
            />
          </label>
          <label>
            表示名
            <input
              value={formState.displayName}
              onChange={(event) => setFormState((prev) => ({ ...prev, displayName: event.target.value }))}
              placeholder="例: 佐藤 花子"
            />
          </label>
          <label>
            仮パスワード
            <input
              type="password"
              value={formState.password}
              onChange={(event) => setFormState((prev) => ({ ...prev, password: event.target.value }))}
              placeholder="8文字以上"
            />
          </label>
          {message ? <div className="form-success">{message}</div> : null}
          {error ? <div className="form-error">{error}</div> : null}
          <button className="primary" type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? '登録中...' : '管理者を登録'}
          </button>
        </form>
      )}
    </Section>
  );
};
