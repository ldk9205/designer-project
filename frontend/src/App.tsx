import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";

import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import HomePage from "./pages/HomePage";
import MyPage from "./pages/MyPage";
import CommunityPage from "./pages/CommunityPage";
import UploadPage from "./pages/UploadPage";
import WritePostPage from "./pages/WritePostPage";
import PostDetailPage from "./pages/PostDetailPage";

import CustomerListPage from "./pages/CustomerListPage";
import CustomerCreatePage from "./pages/CustomerCreatePage";
import CustomerDetailPage from "./pages/CustomerDetailPage";
import CustomerEditPage from "./pages/CustomerEditPage";

import CustomerTreatmentListPage from "./pages/CustomerTreatmentListPage";
import TreatmentCreatePage from "./pages/TreatmentCreatePage";
import TreatmentDetailPage from "./pages/TreatmentDetailPage";

import ProtectedRoute from "./routes/ProtectedRoute";

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* 비로그인 페이지 */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />

          {/* 공개 페이지 */}
          <Route path="/community" element={<CommunityPage />} />
          <Route path="/community/:id" element={<PostDetailPage />} />

          {/* 로그인 필요 페이지 */}
          <Route element={<ProtectedRoute />}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/mypage" element={<MyPage />} />

            {/* 고객 관리 */}
            <Route path="/customers" element={<CustomerListPage />} />
            <Route path="/customers/new" element={<CustomerCreatePage />} />
            <Route
              path="/customers/:customerId"
              element={<CustomerDetailPage />}
            />
            <Route
              path="/customers/:customerId/edit"
              element={<CustomerEditPage />}
            />
            <Route
              path="/customers/:customerId/treatments"
              element={<CustomerTreatmentListPage />}
            />
            <Route
              path="/customers/:customerId/treatments/new"
              element={<TreatmentCreatePage />}
            />
            <Route
              path="/treatments/:treatmentId"
              element={<TreatmentDetailPage />}
            />

            {/* 커뮤니티 */}
            <Route path="/community/write" element={<WritePostPage />} />

            {/* 이미지 업로드 */}
            <Route path="/upload" element={<UploadPage />} />
          </Route>

          {/* 기본 진입 */}
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="*" element={<Navigate to="/home" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}