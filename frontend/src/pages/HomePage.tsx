import { useNavigate } from "react-router-dom";
import Header from "../components/Header"; // ★ 추가
import "../styles/HomePage.css";

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="home-wrapper">

      {/* ★ Header 컴포넌트로 분리 */}
      <Header />

      <main className="home-main">

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/community")}
          onKeyDown={(e) => e.key === "Enter" && navigate("/community")}
        >
          💬 커뮤니티
        </div>

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/reservations")}
          onKeyDown={(e) => e.key === "Enter" && navigate("/reservations")}
        >
          📅 예약 현황
        </div>

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/sales")}
          onKeyDown={(e) => e.key === "Enter" && navigate("/sales")}
        >
          💰 매출 요약
        </div>

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => navigate("/notifications")}
          onKeyDown={(e) => e.key === "Enter" && navigate("/notifications")}
        >
          🔔 알림
        </div>

      </main>
    </div>
  );
}