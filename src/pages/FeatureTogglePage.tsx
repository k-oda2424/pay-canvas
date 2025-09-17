import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Section } from '../components/Section';
import { updateFeatureToggle, fetchFeatureToggles } from '../api/features';
import './FeatureTogglePage.css';

type TogglePayload = {
  id: string;
  isEnabled: boolean;
};

export const FeatureTogglePage = () => {
  const client = useQueryClient();
  const query = useQuery({ queryKey: ['featureToggles'], queryFn: fetchFeatureToggles });

  const mutation = useMutation({
    mutationFn: ({ id, isEnabled }: TogglePayload) => updateFeatureToggle(id, isEnabled),
    onSuccess: () => client.invalidateQueries({ queryKey: ['featureToggles'] })
  });

  return (
    <div className="feature-page">
      <Section title="機能フラグ管理" description="テナント毎に利用可能な機能を制御します">
        {query.isLoading ? (
          <div className="page-loading">読み込み中...</div>
        ) : (
          <ul className="feature-list">
            {query.data?.map((feature) => (
              <li key={feature.id}>
                <div>
                  <h3>{feature.name}</h3>
                  <p>{feature.description}</p>
                  <span className="meta">有効テナント: {feature.enabledTenants}社</span>
                </div>
                <label className="switch">
                  <input
                    type="checkbox"
                    checked={feature.isEnabled}
                    disabled={mutation.isPending}
                    onChange={(event) => mutation.mutate({ id: feature.id, isEnabled: event.currentTarget.checked })}
                  />
                  <span className="slider" />
                </label>
              </li>
            ))}
          </ul>
        )}
      </Section>
    </div>
  );
};
