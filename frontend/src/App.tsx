import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import HomePage from "./pages/HomePage";
import ProtectedRoute from "./routes/ProtectedRoute";
import SignupPage from "./pages/SignupPage";
import CommunityPage from "./pages/CommunityPage";
<<<<<<< HEAD
import UploadPage from "./pages/UploadPage";

=======
import MyPage from "./pages/MyPage";
>>>>>>> origin/main

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* 비로그인 페이지 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* 로그인 필요 페이지 */}
          <Route element={<ProtectedRoute />}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/mypage" element={<MyPage />} />
          </Route>

          {/* 기본 진입 */}
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="*" element={<Navigate to="/home" replace />} />

          <Route path="/community" element={<CommunityPage />} />

          {/* 이미지 업로드 페이지 */}
          <Route path="/upload" element={<UploadPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
