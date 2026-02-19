import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

export default function HomePage() {
  const auth = useContext(AuthContext);

  return (
    <div style={{ padding: 24 }}>
      <h2>Home</h2>
      <div>안녕하세요, {auth?.designer?.name}님</div>

      <button
        style={{ marginTop: 12 }}
        onClick={() => auth?.logout()}
      >
        로그아웃
      </button>
    </div>
  );
}
