// src/components/Header.tsx

import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "../styles/HomePage.css"; // 기존 스타일 재사용

export default function Header() {
  const auth = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = async () => {
    await auth?.logout();
    navigate("/login");
  };

  return (
    <header className="home-header">
      {/* 좌측 로고 */}
      <div
        className="logo"
        style={{ cursor: "pointer" }}
        onClick={() => navigate("/")}
      >
        Designer CRM
      </div>

      {/* 우측 메뉴 */}
      <div className="header-right">
        {/* ✅ MyPage 이동 */}
        <span onClick={() => navigate("/mypage")}>
          MyPage
        </span>

        <span onClick={handleLogout}>
          Logout
        </span>
      </div>
    </header>
  );
}