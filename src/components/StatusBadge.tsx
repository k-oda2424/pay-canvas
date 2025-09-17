import './StatusBadge.css';

type StatusBadgeProps = {
  status: string;
  tone?: 'neutral' | 'success' | 'warning';
};

export const StatusBadge = ({ status, tone = 'neutral' }: StatusBadgeProps) => {
  return <span className={`status-badge ${tone}`}>{status}</span>;
};
