import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL, // 예: http://localhost:8080
  headers: { "Content-Type": "application/json" },
  withCredentials: true, // refreshToken 쿠키 전송/수신
});

// accessToken: 메모리 변수
let accessToken: string | null = null;

// ✅ "null/undefined/빈값" 방어까지 포함
export function setAccessToken(token: string | null) {
  const t = (token ?? "").trim();
  if (!t || t === "null" || t === "undefined") accessToken = null;
  else accessToken = t;
}

// ★ 변경: "토큰을 절대 붙이지 말아야 하는 auth endpoint"만 명시적으로 제외
const NO_AUTH_ENDPOINTS = [ // ★
  "/auth/login",  // ★
  "/auth/signup", // ★
  "/auth/refresh",// ★
  "/auth/logout", // ★
]; // ★

function isNoAuthEndpoint(url?: string) { // ★
  if (!url) return false; // ★
  return NO_AUTH_ENDPOINTS.some((p) => url.startsWith(p)); // ★
} // ★

// ✅ Authorization 제거 헬퍼
function removeAuthHeader(config: InternalAxiosRequestConfig) {
  if (!config.headers) return;
  // AxiosHeaders 타입일 수도 있어서 any로 처리
  delete (config.headers as any).Authorization;
  delete (config.headers as any).authorization;
}

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const url = config.url ?? "";

  // ✅ 1) /auth/** 요청은 토큰을 절대 붙이지 않는다 (회원가입/로그인/refresh/logout 등)
  if (isNoAuthEndpoint(url)) {
    removeAuthHeader(config);
    return config;
  }

  // ✅ 2) 그 외 요청은 accessToken이 "정상 값"일 때만 Authorization 붙임
  //     (★중요) 이제 /auth/me 는 여기로 들어와서 토큰이 붙음
  const t = (accessToken ?? "").trim();
  if (t && t !== "null" && t !== "undefined") {
    config.headers = config.headers ?? ({} as any);
    (config.headers as any).Authorization = `Bearer ${t}`;
  } else {
    removeAuthHeader(config);
  }

  return config;
});

// ---- 401 -> refresh -> 재시도 (동시성 큐잉) ----
let isRefreshing = false;
let queue: Array<(token: string | null) => void> = [];

function pushQueue(cb: (token: string | null) => void) {
  queue.push(cb);
}
function flushQueue(token: string | null) {
  queue.forEach((cb) => cb(token));
  queue = [];
}

async function refreshAccessToken(): Promise<string> {
  // refreshToken은 HttpOnly 쿠키로 자동 전송됨
  const res = await api.post("/auth/refresh");
  const newToken = (res.data as any)?.accessToken as string | undefined;
  const t = (newToken ?? "").trim();
  if (!t) throw new Error("No accessToken from /auth/refresh");
  return t;
}

api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const status = error.response?.status;
    const original = error.config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined;

    if (!original) return Promise.reject(error);

    // ✅ 0) /auth/** 자체는 refresh 로직 절대 타지 않음
    if (isNoAuthEndpoint(original.url)) { // ★
      return Promise.reject(error);
    }

    // 1) 401 아니거나 이미 재시도했으면 종료
    if (status !== 401 || original._retry) {
      return Promise.reject(error);
    }
    original._retry = true;

    // 2) refresh 진행 중이면 큐에 넣기
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pushQueue((token) => {
          if (!token) return reject(error);
          original.headers = original.headers ?? ({} as any);
          (original.headers as any).Authorization = `Bearer ${token}`;
          resolve(api(original));
        });
      });
    }

    isRefreshing = true;

    try {
      const newToken = await refreshAccessToken();
      setAccessToken(newToken);
      flushQueue(newToken);

      original.headers = original.headers ?? ({} as any);
      (original.headers as any).Authorization = `Bearer ${newToken}`;
      return api(original);
    } catch (e) {
      setAccessToken(null);
      flushQueue(null);
      return Promise.reject(e);
    } finally {
      isRefreshing = false;
    }
  }
);

export default api;