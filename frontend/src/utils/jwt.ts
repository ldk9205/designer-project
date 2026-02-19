import { jwtDecode } from 'jwt-decode';

type JwtPayload = { exp?: number }; // exp는 seconds

export function getExpMs(token: string): number | null {
  try {
    const payload = jwtDecode<JwtPayload>(token);
    if (!payload.exp) return null;
    return payload.exp * 1000;
  } catch {
    return null;
  }
}
