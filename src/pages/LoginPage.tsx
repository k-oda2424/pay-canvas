import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { loginApi } from '../api/auth';
import './LoginPage.css';

export const LoginPage = () => {
  const navigate = useNavigate();
  const { onLogin } = useAuth();
  const [email, setEmail] = useState('admin@paycanvas.io');
  const [password, setPassword] = useState('password');
  const [error, setError] = useState('');

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      const result = await loginApi({ email, password });
      onLogin(result);
      navigate('/dashboard');
    } catch (err) {
      setError('ログインに失敗しました');
    }
  };

  return (
    <div className="login-container">
      <form className="login-card" onSubmit={handleSubmit}>
        <h1>payCanvas</h1>
        <p className="subtitle">美容室向け給与計算プラットフォーム</p>
        <label>
          <span>メールアドレス</span>
          <input value={email} onChange={(event) => setEmail(event.currentTarget.value)} />
        </label>
        <label>
          <span>パスワード</span>
          <input type="password" value={password} onChange={(event) => setPassword(event.currentTarget.value)} />
        </label>
        {error ? <div className="error-message">{error}</div> : null}
        <button type="submit">ログイン</button>
        <div className="login-hint">サンプル: admin@paycanvas.io / password</div>
      </form>
    </div>
  );
};
