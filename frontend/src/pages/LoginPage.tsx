import React, { useContext, useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import '../styles/LoginPage.css';

export default function LoginPage() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  // 이미 로그인 상태면 홈으로
  useEffect(() => {
    if (!auth) return;
    if (auth.isAuthReady && auth.designer) {
      navigate('/home', { replace: true });
    }
  }, [auth, navigate]);

  // 401로 로그아웃된 경우 안내 문구
  useEffect(() => {
    const reason = sessionStorage.getItem('authError');
    if (reason === 'unauthorized') {
      setError('세션이 만료되었습니다. 다시 로그인해주세요.');
      sessionStorage.removeItem('authError');
    }
  }, []);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!auth) {
      setError('AuthProvider가 설정되지 않았습니다. (AuthContext가 null)');
      return;
    }

    const trimmedEmail = email.trim();
    const trimmedPassword = password.trim();

    if (!trimmedEmail || !trimmedPassword) {
      setError('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      await auth.login(trimmedEmail, trimmedPassword);

      // 원래 가려던 페이지가 있으면 거기로, 없으면 /home
      const from = (location.state as any)?.from?.pathname || '/home';
      navigate(from, { replace: true });
    } catch (err: any) {
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        '로그인에 실패했습니다. 이메일/비밀번호를 확인하세요.';
      setError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1 className="login-title">디자이너 로그인</h1>
        <p className="login-subtitle">고객 관리 시스템</p>

        <form className="login-form" onSubmit={onSubmit}>
          <label className="login-label">
            이메일
            <input
              className="login-input"
              type="email"
              placeholder="example@domain.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />
          </label>

          <label className="login-label">
            비밀번호
            <input
              className="login-input"
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>

          {error && <div className="login-error">{error}</div>}

          <button className="login-button" type="submit" disabled={isSubmitting}>
            {isSubmitting ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="login-links">
          <button type="button" className="login-link" onClick={() => navigate('/signup')}>
            회원가입
          </button>

          <span className="login-divider">|</span>

          <button type="button" className="login-link" onClick={() => navigate('/find-account')}>
            아이디/비밀번호 찾기
          </button>
        </div>
      </div>
    </div>
  );
}