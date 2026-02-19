import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { signupApi } from '../api/auth';
import '../styles/SignupPage.css';

export default function SignupPage() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [password2, setPassword2] = useState('');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');

  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [success, setSuccess] = useState('');

  // 이미 로그인 상태면 홈으로
  useEffect(() => {
    if (!auth) return;
    if (auth.isAuthReady && auth.designer) {
      navigate('/home', { replace: true });
    }
  }, [auth, navigate]);

  const validate = () => {
    if (!email.trim() || !password.trim() || !name.trim()) {
      return '이메일, 비밀번호, 이름은 필수입니다.';
    }
    if (password.length < 8) {
      return '비밀번호는 8자 이상을 권장합니다.';
    }
    if (password !== password2) {
      return '비밀번호 확인이 일치하지 않습니다.';
    }
    return '';
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const msg = validate();
    if (msg) {
      setError(msg);
      return;
    }

    try {
      setIsSubmitting(true);

      await signupApi({
        email: email.trim(),
        password: password.trim(),
        name: name.trim(),
        phone: phone.trim() ? phone.trim() : undefined,
      });

      setSuccess('회원가입이 완료되었습니다. 로그인 해주세요.');
      // UX: 0.8초 후 로그인 페이지로 이동
      setTimeout(() => navigate('/login', { replace: true }), 800);
    } catch (err: any) {
      const msg =
        err?.response?.data?.message ||
        err?.message ||
        '회원가입에 실패했습니다.';
      setError(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="signup-page">
      <div className="signup-card">
        <h1 className="signup-title">회원가입</h1>
        <p className="signup-subtitle">디자이너 계정 생성</p>

        <form className="signup-form" onSubmit={onSubmit}>
          <label className="signup-label">
            이메일 *
            <input
              className="signup-input"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              placeholder="example@domain.com"
            />
          </label>

          <label className="signup-label">
            비밀번호 *
            <input
              className="signup-input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="new-password"
              placeholder="비밀번호"
            />
          </label>

          <label className="signup-label">
            비밀번호 확인 *
            <input
              className="signup-input"
              type="password"
              value={password2}
              onChange={(e) => setPassword2(e.target.value)}
              autoComplete="new-password"
              placeholder="비밀번호 확인"
            />
          </label>

          <label className="signup-label">
            이름 *
            <input
              className="signup-input"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              autoComplete="name"
              placeholder="이름"
            />
          </label>

          <label className="signup-label">
            전화번호
            <input
              className="signup-input"
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              autoComplete="tel"
              placeholder="010-1234-5678"
            />
          </label>

          {error && <div className="signup-error">{error}</div>}
          {success && <div className="signup-success">{success}</div>}

          <button className="signup-button" type="submit" disabled={isSubmitting}>
            {isSubmitting ? '가입 중...' : '회원가입'}
          </button>
        </form>

        <div className="signup-links">
          <button type="button" className="signup-link" onClick={() => navigate('/login')}>
            로그인으로 돌아가기
          </button>
        </div>
      </div>
    </div>
  );
}
