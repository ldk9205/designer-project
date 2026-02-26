import { useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "../styles/HomePage.css";

export default function HomePage() {
  // 🔐 전역 인증 상태(AuthContext) 접근
  // 로그인한 디자이너 정보 및 logout 함수 포함
  const auth = useContext(AuthContext);

  // 🔀 페이지 이동을 위한 React Router 훅
  const navigate = useNavigate();

  /**
   * 로그아웃 처리 함수
   * 1. 서버에 로그아웃 요청 (refresh token 제거 등)
   * 2. 로그인 페이지로 이동
   */
  const handleLogout = async () => {
    await auth?.logout(); // AuthContext에 정의된 logout 실행
    navigate("/login"); // 로그인 페이지로 이동
  };

  return (
    <div className="home-wrapper">
      {/* =========================
           Header 영역
         ========================= */}
      <header className="home-header">
        {/* 좌측 로고 영역 */}
        <div className="logo">Designer CRM</div>

        {/* 우측 사용자 메뉴 영역 */}
        <div className="header-right">
          {/* 마이페이지 이동 */}
          <span onClick={() => navigate("/mypage")}>MyPage</span>

          {/* 로그아웃 버튼 */}
          <span onClick={handleLogout}>Logout</span>
        </div>
      </header>

      {/* =========================
           메인 대시보드 영역
           2x2 카드형 UI
         ========================= */}
      <main className="home-main">
        {/* ================= 커뮤니티 카드 =================
           - 클릭 시 /community 페이지로 이동
           - Enter 키 입력 시에도 이동 (접근성 고려)
        */}
        <div
          className="card"
          role="button" // 스크린리더용 버튼 역할 지정
          tabIndex={0} // 키보드 포커스 가능하게 설정
          onClick={() => navigate("/community")}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              navigate("/community");
            }
          }}
        >
          💬 커뮤니티
        </div>

        {/* ================= 예약 현황 카드 =================
           - 오늘 및 미래 예약 목록 페이지 이동
        */}
        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/reservations")}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              navigate("/reservations");
            }
          }}
        >
          📅 예약 현황
        </div>

        {/* ================= 매출 요약 카드 =================
           - 일/월/연 매출 통계 페이지 이동
        */}
        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/sales")}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              navigate("/sales");
            }
          }}
        >
          💰 매출 요약
        </div>

        {/* ================= 알림 카드 =================
           - 공지사항, 시스템 메시지, 고객 알림 확인 페이지 이동
        */}
        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/notifications")}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              navigate("/notifications");
            }
          }}
        >
          🔔 알림
        </div>
      </main>
    </div>
  );
}
