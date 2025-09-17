import { PropsWithChildren, ReactNode } from 'react';
import './Section.css';

type SectionProps = {
  title: string;
  description?: string;
  actions?: ReactNode;
};

export const Section = ({ title, description, actions, children }: PropsWithChildren<SectionProps>) => {
  return (
    <section className="section">
      <header className="section-header">
        <div>
          <h2>{title}</h2>
          {description ? <p>{description}</p> : null}
        </div>
        {actions ? <div className="section-actions">{actions}</div> : null}
      </header>
      <div className="section-body">{children}</div>
    </section>
  );
};
