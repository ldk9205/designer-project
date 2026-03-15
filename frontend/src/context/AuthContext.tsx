import React, {createContext, useEffect, useMemo, useRef, useState} from "react";
import api, { setAccessToken } from "../api/axios";
import { loginApi, meApi, logoutApi, type Designer } from "../api/auth";
import { getExpMs } from "../utils/jwt";

type AuthContextType = {
  designer: Designer | null;
  isAuthReady: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  forceRefresh: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [designer, setDesigner] = useState<Designer | null>(null);
  const [isAuthReady, setIsAuthReady] = useState(false);

  const refreshTimerRef = useRef<number | null>(null);

  const clearRefreshTimer = () => {
    if (refreshTimerRef.current) {
      window.clearTimeout(refreshTimerRef.current);
      refreshTimerRef.current = null;
    }
  };

  const login = async (email: string, password: string) => {
    const data = await loginApi(email, password);
    setAccessToken(data.accessToken);
    scheduleProactiveRefresh(data.accessToken);
    setDesigner(data.designer);
  };

  const logout = async () => {
    clearRefreshTimer();
    try {
      await logoutApi();
    } finally {
      setAccessToken(null);
      setDesigner(null);
    }
  };

  const forceRefreshRef = useRef<() => Promise<void>>(async () => {});
  const logoutRef = useRef<() => Promise<void>>(async () => {});

  logoutRef.current = logout;

  const scheduleProactiveRefresh = (token: string) => {
    clearRefreshTimer();

    const expMs = getExpMs(token);
    if (!expMs) return;

    // 만료 90초 전 선제 refresh
    const refreshAt = expMs - 90_000;
    const delay = Math.max(0, refreshAt - Date.now());

    refreshTimerRef.current = window.setTimeout(async () => {
      try {
        await forceRefreshRef.current();
      } catch {
        await logoutRef.current();
      }
    }, delay);
  };

  // ★ 추가: refresh(쿠키) → 토큰 세팅 → me 로드 로직을 한 곳으로 통합
  const refreshAndLoadMe = async () => { // ★
    const res = await api.post("/auth/refresh"); // ★
    const newToken = res.data?.accessToken as string; // ★

    setAccessToken(newToken); // ★
    scheduleProactiveRefresh(newToken); // ★

    const me = await meApi(); // ★
    setDesigner(me); // ★
  }; // ★

  const forceRefresh = async () => { // ★
    await refreshAndLoadMe(); // ★
  }; // 
  
  forceRefreshRef.current = forceRefresh;

  // 앱 시작: refresh로 access 복구 -> me
  useEffect(() => {
    const bootstrap = async () => {
      try {
        await refreshAndLoadMe();
      } catch {
        setAccessToken(null);
        setDesigner(null);
      } finally {
        setIsAuthReady(true);
      }
    };

    bootstrap();
    return () => clearRefreshTimer();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const value = useMemo(
    () => ({ designer, isAuthReady, login, logout, forceRefresh }),
    [designer, isAuthReady],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
