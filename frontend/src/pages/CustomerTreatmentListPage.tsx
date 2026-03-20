import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getCustomerApi } from "../api/customer";
import { getTreatmentsByCustomerApi } from "../api/treatment";
import type { CustomerResponseDto } from "../types/customer";
import type { TreatmentResponseDto } from "../types/treatment";
import "../styles/CustomerTreatmentListPage.css";

export default function CustomerTreatmentListPage() {
  const { customerId } = useParams();
  const numericCustomerId = Number(customerId);

  const [customer, setCustomer] = useState<CustomerResponseDto | null>(null);
  const [treatments, setTreatments] = useState<TreatmentResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [customerData, treatmentData] = await Promise.all([
        getCustomerApi(numericCustomerId),
        getTreatmentsByCustomerApi(numericCustomerId),
      ]);

      setCustomer(customerData);
      setTreatments(treatmentData);
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "고객 이력 조회 실패"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!numericCustomerId) {
      setError("잘못된 고객 ID입니다.");
      setLoading(false);
      return;
    }

    void loadData();
  }, [numericCustomerId]);

  return (
    <div className="customer-treatment-page">
      <div className="customer-treatment-card">
        <div className="customer-treatment-header">
          <h1 className="customer-treatment-title">고객 이력</h1>
          <Link to="/customers" className="customer-treatment-back-link">
            고객 목록으로
          </Link>
        </div>

        {loading && (
          <div className="customer-treatment-loading">불러오는 중...</div>
        )}

        {error && <div className="customer-treatment-error">{error}</div>}

        {!loading && !error && customer && (
          <>
            <div className="customer-treatment-customer-info">
              <h2>{customer.name}</h2>
              <p>전화번호: {customer.phone || "-"}</p>
              <p>메모: {customer.memo || "-"}</p>
            </div>

            <div className="customer-treatment-top">
              <Link
                to={`/customers/${numericCustomerId}/treatments/new`}
                className="customer-treatment-create-link"
              >
                시술 이력 등록
              </Link>
            </div>

            {treatments.length === 0 ? (
              <div className="customer-treatment-empty">
                등록된 시술 이력이 없습니다.
              </div>
            ) : (
              <div className="customer-treatment-table-wrap">
                <table className="customer-treatment-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>날짜</th>
                      <th>시간</th>
                      <th>카테고리</th>
                      <th>스타일명</th>
                    </tr>
                  </thead>
                  <tbody>
                    {treatments.map((treatment) => (
                      <tr key={treatment.id}>
                        <td>{treatment.id}</td>
                        <td>
                          <Link
                            to={`/treatments/${treatment.id}`}
                            className="customer-treatment-detail-link"
                          >
                            {treatment.treatmentDate}
                          </Link>
                        </td>
                        <td>{treatment.treatmentTime || "-"}</td>
                        <td>{treatment.category}</td>
                        <td>{treatment.styleName || "-"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}