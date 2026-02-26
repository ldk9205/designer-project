import React, { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import "../styles/MyPage.css";


export default function MyPage() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("MyPage must be used within AuthProvider");

  const { designer, isAuthReady, logout, forceRefresh } = ctx;

  if (!isAuthReady) return <div style={{ padding: 20 }}>불러오는 중...</div>;

  if (!designer) {
    return (
      <div style={{ padding: 20 }}>
        <h2>내 정보</h2>
        <p>로그인이 필요합니다.</p>
      </div>
    );
  }

  return (
  <div className="mypage-wrapper">
    <div className="mypage-card">

      <h2 className="mypage-title">내 정보</h2>

      <div className="mypage-info">
        <div className="mypage-info-item">
          <span className="mypage-label">ID</span>
          <span>{designer.id}</span>
        </div>

        <div className="mypage-info-item">
          <span className="mypage-label">Email</span>
          <span>{designer.email}</span>
        </div>

        <div className="mypage-info-item">
          <span className="mypage-label">Name</span>
          <span>{designer.name}</span>
        </div>

        <div className="mypage-info-item">
          <span className="mypage-label">Phone</span>
          <span>{designer.phone}</span>
        </div>

        <div className="mypage-info-item">
          <span className="mypage-label">Created</span>
          <span>{new Date(designer.createdAt).toLocaleString()}</span>
        </div>
      </div>

      <div className="mypage-actions">
        <button
          className="mypage-btn mypage-btn-refresh"
          onClick={forceRefresh}
        >
          새로고침
        </button>

        <button
          className="mypage-btn mypage-btn-logout"
          onClick={logout}
        >
          로그아웃
        </button>
      </div>

    </div>
  </div>
);
}