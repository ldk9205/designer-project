import { createContext, ReactNode, useState } from 'react';

// 1. 컨텍스트가 가질 데이터의 타입 정의
interface AuthContextType {
  user: any; // 나중에 구체적인 User 타입으로 변경 권장
  login: () => void;
  logout: () => void;
}

// 2. 초기값을 undefined나 null로 설정하되, 타입을 명시
export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  // 실제 공유할 데이터와 함수들
  const [user, setUser] = useState(null);

  const login = () => { /* 로그인 로직 */ };
  const logout = () => { /* 로그아웃 로직 */ };

  // 3. value에 정의한 인터페이스 형태의 객체를 전달
  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}