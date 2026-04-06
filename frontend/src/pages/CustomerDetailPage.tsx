import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getCustomerApi } from "../api/customer";
import type { CustomerResponseDto } from "../types/customer";
import Header from "../components/Header";
import "../styles/CustomerDetailPage.css";

export default function CustomerDetailPage() {
  const { customerId } = useParams();
  const [customer, setCustomer] = useState<CustomerResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const formatDateTime = (dateTime: string) => {
    if (!dateTime) return "-";

    const date = new Date(dateTime);

    if (Number.isNaN(date.getTime())) {
      return dateTime;
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} ${hours}:${minutes}`;
  };

  useEffect(() => {
    const loadCustomer = async () => {
      try {
        setLoading(true);
        setError("");

        if (!customerId) {
          throw new Error("customerId가 없습니다.");
        }

        const data = await getCustomerApi(Number(customerId));
        setCustomer(data);
      } catch (err: any) {
        setError(err?.response?.data?.message || err?.message || "고객 조회 실패");
      } finally {
        setLoading(false);
      }
    };

    void loadCustomer();
  }, [customerId]);

  if (loading) {
    return (
      <div className="customer-detail-page">
        <Header />
        <div className="customer-detail-card">
          <div className="customer-detail-loading">불러오는 중...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="customer-detail-page">
        <Header />
        <div className="customer-detail-card">
          <div className="customer-detail-error">{error}</div>
        </div>
      </div>
    );
  }

  if (!customer) {
    return (
      <div className="customer-detail-page">
        <Header />
        <div className="customer-detail-card">
          <div className="customer-detail-empty">고객 정보가 없습니다.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="customer-detail-page">
      <Header />
      
      <div className="customer-detail-card">
        <h1 className="customer-detail-title">고객 상세</h1>
        <p className="customer-detail-subtitle">고객 정보를 확인하세요</p>

        <div className="customer-detail-info">
          <div className="customer-detail-row">
            <span>ID</span>
            <strong>{customer.id}</strong>
          </div>
          <div className="customer-detail-row">
            <span>이름</span>
            <strong>{customer.name}</strong>
          </div>
          <div className="customer-detail-row">
            <span>전화번호</span>
            <strong>{customer.phone || "-"}</strong>
          </div>
          <div className="customer-detail-row">
            <span>메모</span>
            <strong>{customer.memo || "-"}</strong>
          </div>
          <div className="customer-detail-row">
            <span>생성일</span>
            <strong>{formatDateTime(customer.createdAt)}</strong>
          </div>
          <div className="customer-detail-row">
            <span>수정일</span>
            <strong>{formatDateTime(customer.updatedAt)}</strong>
          </div>
        </div>

        <div className="customer-detail-actions">
          <Link to={`/customers/${customer.id}/edit`} className="detail-primary-link">
            수정
          </Link>
          <Link to="/customers" className="detail-secondary-link">
            목록
          </Link>
        </div>
      </div>
    </div>
  );
}