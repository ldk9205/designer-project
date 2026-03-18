import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { deleteCustomerApi, getCustomersApi } from "../api/customer";
import type { CustomerResponseDto } from "../types/customer";
import "../styles/CustomerListPage.css";

export default function CustomerListPage() {
  const [customers, setCustomers] = useState<CustomerResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchName, setSearchName] = useState("");

  const loadCustomers = async (name?: string) => {
    try {
      setLoading(true);
      setError("");
      const data = await getCustomersApi(name);
      setCustomers(data);
    } catch (err: any) {
      setError(err?.response?.data?.message || err?.message || "고객 목록 조회 실패");
    } finally {
      setLoading(false);
    }
  };

  // 처음 진입 시 전체 목록 조회
  useEffect(() => {
    void loadCustomers();
  }, []);

  const onSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    await loadCustomers(searchName);
  };

  const onReset = async () => {
    setSearchName("");
    await loadCustomers();
  };

  const onDelete = async (customerId: number) => {
    const ok = window.confirm("정말 삭제하시겠습니까?");
    if (!ok) return;

    try {
      await deleteCustomerApi(customerId);

      // 삭제 후 현재 화면 목록에서만 제거
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

        <form className="customer-list-search" onSubmit={onSearch}>
          <input
            type="text"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            placeholder="고객 이름 검색"
            className="customer-list-search-input"
          />
          <button type="submit" className="customer-list-search-button">
            검색
          </button>
          <button
            type="button"
            className="customer-list-reset-button"
            onClick={onReset}
          >
            전체보기
          </button>
        </form>

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

                    <td>
                      <Link
                        to={`/customers/${customer.id}`}
                        className="customer-name-link"
                      >
                        {customer.name}
                      </Link>
                    </td>

                    <td>{customer.phone || "-"}</td>

                    <td>
                      <div className="customer-list-actions">
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