import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { FormEvent, useState } from 'react';
import { executePayroll, listPayrollJobs } from '../api/payroll';
import { Section } from '../components/Section';
import { DataTable } from '../components/DataTable';
import { StatusBadge } from '../components/StatusBadge';
import './PayrollExecutionPage.css';
import { availableYearMonths, formatYearMonthLabel } from '../utils/date';

export const PayrollExecutionPage = () => {
  const client = useQueryClient();
  const [targetMonth, setTargetMonth] = useState(availableYearMonths[0]);

  const jobsQuery = useQuery({ queryKey: ['payrollJobs'], queryFn: listPayrollJobs });

  const mutation = useMutation({
    mutationFn: executePayroll,
    onSuccess: () => {
      client.invalidateQueries({ queryKey: ['payrollJobs'] });
    }
  });

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    mutation.mutate(targetMonth);
  };

  return (
    <div className="payroll-page">
      <Section
        title="給与計算ウィザード"
        description="勤怠と売上のチェックが完了したら月次給与計算を実行してください"
        actions={<button className="execute-button" onClick={() => client.invalidateQueries({ queryKey: ['payrollJobs'] })}>最新化</button>}
      >
        <form className="payroll-form" onSubmit={handleSubmit}>
          <label>
            対象月
            <select value={targetMonth} onChange={(event) => setTargetMonth(event.currentTarget.value)}>
              {availableYearMonths.map((month) => (
                <option key={month} value={month}>
                  {formatYearMonthLabel(month)}
                </option>
              ))}
            </select>
          </label>
          <button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? '計算を開始しています...' : '給与計算を実行'}
          </button>
        </form>
        <p className="note">実行後はステージング状態で結果が保存され、問題なければ確定処理へ進みます。</p>
      </Section>

      <Section title="実行履歴" description="最新の給与計算ジョブの進捗状況を確認できます">
        {jobsQuery.isLoading ? (
          <div className="page-loading">読み込み中...</div>
        ) : (
          <DataTable
            data={jobsQuery.data ?? []}
            columns={[
              { header: 'ジョブID', accessor: (row) => row.id },
              { header: '対象月', accessor: (row) => formatYearMonthLabel(row.targetMonth) },
              {
                header: 'ステータス',
                accessor: (row) => (
                  <StatusBadge
                    status={row.status === 'COMPLETED' ? '完了' : row.status === 'RUNNING' ? '実行中' : '待機中'}
                    tone={row.status === 'COMPLETED' ? 'success' : row.status === 'RUNNING' ? 'warning' : 'neutral'}
                  />
                )
              },
              { header: '進捗', accessor: (row) => `${row.progress}%` },
              { header: '開始日時', accessor: (row) => row.startedAt }
            ]}
          />
        )}
      </Section>
    </div>
  );
};
