import { useQuery } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { DataTable } from '../components/DataTable';
import { Section } from '../components/Section';
import { StatusBadge } from '../components/StatusBadge';
import { fetchPayslips } from '../api/payslips';
import { availableYearMonths, formatYearMonthLabel } from '../utils/date';
import './PayslipListPage.css';

const statusFilterOptions = [
  { label: 'すべて', value: 'ALL' },
  { label: '確定のみ', value: '確定' },
  { label: 'ステージングのみ', value: 'ステージング' }
];

export const PayslipListPage = () => {
  const [filter, setFilter] = useState('ALL');
  const [targetMonth, setTargetMonth] = useState(availableYearMonths[0]);
  const query = useQuery({
    queryKey: ['payslips', targetMonth],
    queryFn: () => fetchPayslips(targetMonth)
  });

  const filtered = useMemo(() => {
    if (!query.data) {
      return [];
    }
    if (filter === 'ALL') {
      return query.data;
    }
    return query.data.filter((item) => item.status === filter);
  }, [filter, query.data]);

  return (
    <div className="payslip-page">
      <Section
        title="給与明細一覧"
        description={`${formatYearMonthLabel(targetMonth)}の従業員ごとの支給額を確認できます`}
        actions={
          <div className="filters">
            <select value={targetMonth} onChange={(event) => setTargetMonth(event.currentTarget.value)}>
              {availableYearMonths.map((month) => (
                <option key={month} value={month}>
                  {formatYearMonthLabel(month)}
                </option>
              ))}
            </select>
            <select value={filter} onChange={(event) => setFilter(event.currentTarget.value)}>
              {statusFilterOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </div>
        }
      >
        {query.isLoading ? (
          <div className="page-loading">読み込み中...</div>
        ) : (
          <DataTable
            data={filtered}
            emptyMessage="該当する給与明細がありません"
            columns={[
              { header: '従業員', accessor: (row) => row.employeeName },
              { header: '役職', accessor: (row) => row.role },
              { header: '基本給', accessor: (row) => `¥${row.baseSalary.toLocaleString()}` },
              { header: '手当', accessor: (row) => `¥${row.allowances.toLocaleString()}` },
              { header: '控除', accessor: (row) => `¥${row.deductions.toLocaleString()}` },
              { header: '支給額', accessor: (row) => `¥${row.netPay.toLocaleString()}` },
              {
                header: 'ステータス',
                accessor: (row) => (
                  <StatusBadge status={row.status} tone={row.status === '確定' ? 'success' : 'warning'} />
                )
              }
            ]}
          />
        )}
      </Section>
    </div>
  );
};
