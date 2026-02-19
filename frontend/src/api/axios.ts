import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL, // ✅ CRA 환경변수
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true, // ✅ refreshToken 쿠키 주고받기 위해 필수
});

// accessToken은 localStorage가 아니라 “메모리 변수”로 관리(권장)
let accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
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
  const res = await api.post('/auth/refresh');
  const newToken = res.data?.accessToken as string;
  if (!newToken) throw new Error('No accessToken from /auth/refresh');
  return newToken;
}

api.interceptors.response.use(
  (res) => res,
  async (error: AxiosError) => {
    const status = error.response?.status;
    const original: any = error.config;

    // 401이 아니거나, 이미 재시도 했으면 종료
    if (status !== 401 || original?._retry) {
      return Promise.reject(error);
    }
    original._retry = true;

    // refresh 진행 중이면 큐에 넣었다가 완료 후 재시도
    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        pushQueue((token) => {
          if (!token) return reject(error);
          original.headers.Authorization = `Bearer ${token}`;
          resolve(api(original));
        });
      });
    }

    isRefreshing = true;

    try {
      const newToken = await refreshAccessToken();
      setAccessToken(newToken);
      flushQueue(newToken);

      original.headers.Authorization = `Bearer ${newToken}`;
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
