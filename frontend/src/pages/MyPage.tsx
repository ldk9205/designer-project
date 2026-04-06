import React, { useContext, useEffect, useState } from "react";
import { AuthContext } from "../context/AuthContext";
import Header from "../components/Header";
import { updateMeApi } from "../api/auth";
import "../styles/MyPage.css";

export default function MyPage() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("MyPage must be used within AuthProvider");

  const { designer, isAuthReady, logout, forceRefresh } = ctx;

  const [isEditing, setIsEditing] = useState(false);
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (designer) {
      setName(designer.name ?? "");
      setPhone(designer.phone ?? "");
    }
  }, [designer]);

  if (!isAuthReady) return <div style={{ padding: 20 }}>불러오는 중...</div>;

  if (!designer) {
    return (
      <div style={{ padding: 20 }}>
        <h2>내 정보</h2>
        <p>로그인이 필요합니다.</p>
      </div>
    );
  }

  const onEdit = () => {
    setMessage("");
    setName(designer.name ?? "");
    setPhone(designer.phone ?? "");
    setIsEditing(true);
  };

  const onCancel = () => {
    setName(designer.name ?? "");
    setPhone(designer.phone ?? "");
    setMessage("");
    setIsEditing(false);
  };

  const onSave = async () => {
    const trimmedName = name.trim();
    const trimmedPhone = phone.trim();

    if (!trimmedName) {
      setMessage("이름을 입력해주세요.");
      return;
    }

    if (!trimmedPhone) {
      setMessage("전화번호를 입력해주세요.");
      return;
    }

    try {
      setSaving(true);
      setMessage("");

      await updateMeApi({
        name: trimmedName,
        phone: trimmedPhone,
      });

      await forceRefresh();
      setIsEditing(false);
      setMessage("이름과 전화번호가 수정되었습니다.");
    } catch (err: any) {
      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "내 정보 수정에 실패했습니다."
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="mypage-wrapper">
      <Header />

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
            {isEditing ? (
              <input
                className="mypage-input"
                type="text"
                placeholder="이름을 입력하세요"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            ) : (
              <span>{designer.name}</span>
            )}
          </div>

          <div className="mypage-info-item">
            <span className="mypage-label">Phone</span>
            {isEditing ? (
              <input
                className="mypage-input"
                type="text"
                placeholder="전화번호를 입력하세요"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            ) : (
              <span>{designer.phone}</span>
            )}
          </div>

          <div className="mypage-info-item">
            <span className="mypage-label">Created</span>
            <span>{new Date(designer.createdAt).toLocaleString()}</span>
          </div>
        </div>

        {message && <p className="mypage-message">{message}</p>}

        <div className="mypage-actions">
          {!isEditing ? (
            <>
              <button
                className="mypage-btn mypage-btn-edit"
                onClick={onEdit}
              >
                수정하기
              </button>

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
            </>
          ) : (
            <>
              <button
                className="mypage-btn mypage-btn-save"
                onClick={onSave}
                disabled={saving}
              >
                {saving ? "저장 중..." : "저장"}
              </button>

              <button
                className="mypage-btn mypage-btn-cancel"
                onClick={onCancel}
                disabled={saving}
              >
                취소
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}