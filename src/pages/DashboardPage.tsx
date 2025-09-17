import { useQuery } from '@tanstack/react-query';
import { fetchDashboardSummary } from '../api/dashboard';
import { SummaryCard } from '../components/SummaryCard';
import { Section } from '../components/Section';
import './DashboardPage.css';

export const DashboardPage = () => {
  const { data, isLoading } = useQuery({ queryKey: ['summaryMetrics'], queryFn: fetchDashboardSummary });

  if (isLoading || !data) {
    return <div className="page-loading">読み込み中...</div>;
  }

  return (
    <div className="dashboard-page">
      <div className="summary-grid">
        {data.metrics.map((metric) => (
          <SummaryCard key={metric.id} metric={metric} />
        ))}
      </div>

      <div className="split-columns">
        <Section title="対応が必要なタスク" description="月次給与確定に向けて優先度の高いアクションを一覧化しています">
          <ul className="task-list">
            {data.tasks.map((task) => (
              <li key={task.id}>
                <div>
                  <strong>{task.title}</strong>
                  <p>{task.description}</p>
                </div>
                {task.dueDate ? <span className="due-date">期限: {task.dueDate}</span> : null}
              </li>
            ))}
          </ul>
        </Section>

        <Section title="お知らせ" description="システムメンテナンスや連携機能のアップデート情報を配信します">
          <ul className="announcement-list">
            {data.announcements.map((announcement) => (
              <li key={announcement.id}>
                <span className="announcement-date">{announcement.date}</span>
                <span>{announcement.message}</span>
              </li>
            ))}
          </ul>
        </Section>
      </div>
    </div>
  );
};
