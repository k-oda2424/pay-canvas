import { useQuery } from '@tanstack/react-query';
import { fetchDailyData } from '../api/daily';
import { Section } from '../components/Section';
import { DataTable } from '../components/DataTable';
import { StatusBadge } from '../components/StatusBadge';
import './DailyMetricsPage.css';

export const DailyMetricsPage = () => {
  const { data, isLoading } = useQuery({ queryKey: ['dailyData'], queryFn: fetchDailyData });

  if (isLoading || !data) {
    return <div className="page-loading">データを読み込んでいます...</div>;
  }

  return (
    <div className="daily-page">
      <Section title="勤怠実績" description="外部システムから取り込んだ勤怠データを一覧表示します">
        <DataTable
          data={data.attendances}
          columns={[
            { header: '日付', accessor: (row) => row.date },
            { header: 'スタッフ', accessor: (row) => row.staffName },
            { header: '店舗', accessor: (row) => row.storeName },
            { header: '出勤', accessor: (row) => row.checkIn },
            { header: '退勤', accessor: (row) => row.checkOut },
            { header: '実働', accessor: (row) => `${row.workHours}h` },
            { header: '遅刻', accessor: (row) => `${row.tardyMinutes}分` },
            {
              header: 'ステータス',
              accessor: (row) => <StatusBadge tone={row.status === '承認済' ? 'success' : 'warning'} status={row.status} />
            }
          ]}
        />
      </Section>

      <Section title="店舗売上" description="店舗別の売上実績と稼働時間のサマリーです">
        <DataTable
          data={data.storeMetrics}
          columns={[
            { header: '日付', accessor: (row) => row.date },
            { header: '店舗', accessor: (row) => row.storeName },
            { header: '売上', accessor: (row) => `¥${row.sales.toLocaleString()}` },
            { header: '値引額', accessor: (row) => `¥${row.discount.toLocaleString()}` },
            { header: '総稼働時間', accessor: (row) => `${row.totalHours}h` }
          ]}
        />
      </Section>

      <Section title="個人売上" description="スタイリストごとの売上・商品販売実績です">
        <DataTable
          data={data.personalMetrics}
          columns={[
            { header: '日付', accessor: (row) => row.date },
            { header: 'スタッフ', accessor: (row) => row.staffName },
            { header: '技術売上', accessor: (row) => `¥${row.sales.toLocaleString()}` },
            { header: '商品売上', accessor: (row) => `¥${row.productSales.toLocaleString()}` }
          ]}
        />
      </Section>
    </div>
  );
};
