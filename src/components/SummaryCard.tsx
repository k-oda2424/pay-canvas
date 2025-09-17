import { SummaryMetric } from '../types/models';
import './SummaryCard.css';

type SummaryCardProps = {
  metric: SummaryMetric;
};

export const SummaryCard = ({ metric }: SummaryCardProps) => {
  return (
    <div className="summary-card">
      <span className="summary-label">{metric.label}</span>
      <strong className="summary-value">{metric.value}</strong>
      <span className={metric.positive ? 'summary-change positive' : 'summary-change negative'}>{metric.change}</span>
    </div>
  );
};
