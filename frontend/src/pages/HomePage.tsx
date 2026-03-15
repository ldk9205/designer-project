import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import "../styles/HomePage.css";

export default function HomePage() {
  const navigate = useNavigate();

  const go = (path: string) => navigate(path);

  return (
    <div className="home-wrapper">

      {/* Header */}
      <Header />

      <main className="home-main">

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => go("/community")}
          onKeyDown={(e) => e.key === "Enter" && go("/community")}
        >
          💬 커뮤니티
        </div>

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => go("/customers")}
          onKeyDown={(e) => e.key === "Enter" && go("/customers")}
        >
          👥 고객 관리
        </div>

        <div
          className="card"
          role="button"
          tabIndex={0}
          onClick={() => go("/reservations")}
          onKeyDown={(e) => e.key === "Enter" && go("/reservations")}
        >
          📅 예약 현황
        </div>

      </main>
    </div>
  );
}