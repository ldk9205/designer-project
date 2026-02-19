import React, { useContext } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function ProtectedRoute() {
  const auth = useContext(AuthContext);
  const location = useLocation();

  if (!auth) return <Navigate to="/login" replace />;

  if (!auth.isAuthReady) {
    return <div style={{ padding: 24 }}>인증 확인 중...</div>;
  }

  if (!auth.designer) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}
