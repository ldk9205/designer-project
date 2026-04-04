import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  deleteTreatmentApi,
  getTreatmentsByCustomerApi,
} from "../api/treatment";
import type { TreatmentResponseDto } from "../types/treatment";
import "../styles/CustomerTreatmentListPage.css";

const PAGE_SIZE = 5;
const PAGE_BLOCK_SIZE = 5;

export default function CustomerTreatmentListPage() {
  const { customerId } = useParams();
  const navigate = useNavigate();

  const numericCustomerId = Number(customerId);

  const [treatments, setTreatments] = useState<TreatmentResponseDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const loadTreatments = async (targetPage: number) => {
    try {
      setLoading(true);
      setError("");

      const data = await getTreatmentsByCustomerApi(
        numericCustomerId,
        targetPage,
        PAGE_SIZE
      );

      setTreatments(data.content);
      setPage(data.page);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "시술 이력 목록 조회 실패"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!numericCustomerId || Number.isNaN(numericCustomerId)) {
      setError("잘못된 고객 ID입니다.");
      setLoading(false);
      return;
    }

    void loadTreatments(0);
  }, [numericCustomerId]);

  const onDelete = async (treatmentId: number) => {
    const ok = window.confirm("정말 이 시술 이력을 삭제하시겠습니까?");
    if (!ok) return;

    try {
      await deleteTreatmentApi(treatmentId);
      alert("시술 이력이 삭제되었습니다.");

      const isLastItemOnPage = treatments.length === 1;
      const nextPage =
        isLastItemOnPage && page > 0 ? page - 1 : page;

      await loadTreatments(nextPage);
    } catch (err: any) {
      alert(
        err?.response?.data?.message ||
          err?.message ||
          "시술 이력 삭제 실패"
      );
    }
  };

  if (loading) {
    return <div className="customer-treatment-page">불러오는 중...</div>;
  }

  if (error) {
    return <div className="customer-treatment-page">{error}</div>;
  }

  const currentBlock = Math.floor(page / PAGE_BLOCK_SIZE);
  const startPage = currentBlock * PAGE_BLOCK_SIZE;
  const endPage = Math.min(startPage + PAGE_BLOCK_SIZE, totalPages);

  const pageNumbers = Array.from(
    { length: endPage - startPage },
    (_, index) => startPage + index
  );

  const hasTreatments = treatments.length > 0;

  return (
    <div className="customer-treatment-page">
      <div className="customer-treatment-card">
        <div className="customer-treatment-header">
          <div className="customer-treatment-title-wrap">
            <h1 className="customer-treatment-title">고객 시술 이력</h1>
            <p className="customer-treatment-subtitle">
              총 {totalElements}건
            </p>
          </div>

          <div className="customer-treatment-header-actions">
            <Link to="/home" className="customer-treatment-home-link">
              홈으로
            </Link>
            <Link to="/customers" className="customer-treatment-back-link">
              고객 목록으로
            </Link>
            <button
              type="button"
              className="customer-treatment-create-button"
              onClick={() =>
                navigate(`/customers/${numericCustomerId}/treatments/new`)
              }
            >
              시술 등록
            </button>
          </div>
        </div>

        {!hasTreatments ? (
          <div className="customer-treatment-empty">
            등록된 시술 이력이 없습니다.
          </div>
        ) : (
          <>
            <div className="customer-treatment-table-wrap">
              <table className="customer-treatment-table">
                <thead>
                  <tr>
                    <th>날짜</th>
                    <th>시간</th>
                    <th>카테고리</th>
                    <th>스타일명</th>
                    <th>관리</th>
                  </tr>
                </thead>
                <tbody>
                  {treatments.map((treatment) => (
                    <tr key={treatment.id}>
                      <td>{treatment.treatmentDate}</td>
                      <td>{treatment.treatmentTime || "-"}</td>
                      <td>{treatment.category}</td>
                      <td>{treatment.styleName || "-"}</td>
                      <td>
                        <div className="customer-treatment-row-actions">
                          <button
                            type="button"
                            className="customer-treatment-detail-button"
                            onClick={() =>
                              navigate(
                                `/customers/${numericCustomerId}/treatments/${treatment.id}`
                              )
                            }
                          >
                            상세 이력
                          </button>
                          <button
                            type="button"
                            className="customer-treatment-delete-button"
                            onClick={() => onDelete(treatment.id)}
                          >
                            삭제
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {totalPages > 0 && (
              <div className="customer-treatment-pagination">
                <button
                  type="button"
                  className="customer-treatment-page-nav-button"
                  onClick={() => loadTreatments(0)}
                  disabled={page === 0}
                >
                  처음
                </button>

                <button
                  type="button"
                  className="customer-treatment-page-nav-button"
                  onClick={() => loadTreatments(startPage - 1)}
                  disabled={startPage === 0}
                >
                  이전
                </button>

                <div className="customer-treatment-page-number-group">
                  {pageNumbers.map((pageNumber) => (
                    <button
                      key={pageNumber}
                      type="button"
                      className={`customer-treatment-page-number-button ${
                        pageNumber === page ? "active" : ""
                      }`}
                      onClick={() => loadTreatments(pageNumber)}
                    >
                      {pageNumber + 1}
                    </button>
                  ))}
                </div>

                <button
                  type="button"
                  className="customer-treatment-page-nav-button"
                  onClick={() => loadTreatments(endPage)}
                  disabled={endPage >= totalPages}
                >
                  다음
                </button>

                <button
                  type="button"
                  className="customer-treatment-page-nav-button"
                  onClick={() => loadTreatments(totalPages - 1)}
                  disabled={page >= totalPages - 1}
                >
                  마지막
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}