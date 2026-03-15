import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteCustomerApi, getCustomersApi } from "../api/customer";
import type { CustomerResponseDto } from "../types/customer";
import "../styles/CustomerListPage.css";

export default function CustomerListPage() {
  const [customers, setCustomers] = useState<CustomerResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadCustomers = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getCustomersApi();
      setCustomers(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || "고객 목록 조회 실패");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadCustomers();
  }, []);

  const onDelete = async (customerId: number) => {
    const ok = window.confirm("정말 삭제하시겠습니까?");
    if (!ok) return;

    try {
      await deleteCustomerApi(customerId);
      setCustomers((prev) => prev.filter((c) => c.id !== customerId));
    } catch (err: any) {
      alert(err?.response?.data?.message || err?.message || "고객 삭제 실패");
    }
  };

  return (
    <div className="customer-list-page">
      <div className="customer-list-card">
        <h1 className="customer-list-title">고객 목록</h1>
        <p className="customer-list-subtitle">등록된 고객을 확인하고 관리하세요</p>

        <div className="customer-list-top">
          <Link to="/customers/new" className="customer-list-create-link">
            고객 등록
          </Link>
        </div>

        {loading && <div className="customer-list-loading">불러오는 중...</div>}
        {error && <div className="customer-list-error">{error}</div>}

        {!loading && !error && customers.length === 0 && (
          <div className="customer-list-empty">등록된 고객이 없습니다.</div>
        )}

        {!loading && !error && customers.length > 0 && (
          <div className="customer-list-table-wrap">
            <table className="customer-list-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>이름</th>
                  <th>전화번호</th>
                  <th>관리</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <tr key={customer.id}>
                    <td>{customer.id}</td>
                    <td>{customer.name}</td>
                    <td>{customer.phone || "-"}</td>
                    <td>
                      <div className="customer-list-actions">
                        <Link to={`/customers/${customer.id}`}>상세</Link>
                        <Link to={`/customers/${customer.id}/edit`}>수정</Link>
                        <button onClick={() => onDelete(customer.id)}>삭제</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}