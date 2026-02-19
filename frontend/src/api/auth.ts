import api from './axios';

export type Designer = {
  id: number;
  email: string;
  name: string;
  phone: string;
  createdAt: string;
};

export type AuthResponse = {
  accessToken: string;
  designer: Designer;
};

export async function loginApi(email: string, password: string) {
  const res = await api.post<AuthResponse>('/auth/login', { email, password });
  return res.data;
}

export async function meApi() {
  const res = await api.get<Designer>('/auth/me');
  return res.data;
}

export async function logoutApi() {
  await api.post('/auth/logout');
}

export type SignupRequest = {
  email: string;
  password: string;
  name: string;
  phone?: string;
};

export async function signupApi(payload: SignupRequest) {
  await api.post('/auth/signup', payload);
}
