import React, { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { deleteTreatmentApi, getTreatmentDetailApi } from "../api/treatment";
import type { TreatmentDetailResponseDto } from "../types/treatment";
import "../styles/TreatmentDetailPage.css";

export default function TreatmentDetailPage() {
  const { treatmentId } = useParams();
  const navigate = useNavigate();
  const numericTreatmentId = Number(treatmentId);

  const [treatment, setTreatment] =
    useState<TreatmentDetailResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [deleting, setDeleting] = useState(false);

  const loadTreatment = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await getTreatmentDetailApi(numericTreatmentId);
      setTreatment(data);
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "시술 상세 조회 실패"
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!numericTreatmentId) {
      setError("잘못된 시술 ID입니다.");
      setLoading(false);
      return;
    }

    void loadTreatment();
  }, [numericTreatmentId]);

  const onDelete = async () => {
    if (!treatment) return;

    const ok = window.confirm("정말 이 시술 이력을 삭제하시겠습니까?");
    if (!ok) return;

    try {
      setDeleting(true);
      await deleteTreatmentApi(treatment.id);
      alert("시술 이력이 삭제되었습니다.");
      navigate(`/customers/${treatment.customerId}/treatments`);
    } catch (err: any) {
      alert(
        err?.response?.data?.message ||
          err?.message ||
          "시술 삭제 실패"
      );
    } finally {
      setDeleting(false);
    }
  };

  return (
    <div className="treatment-detail-page">
      <div className="treatment-detail-card">
        <div className="treatment-detail-header">
          <h1 className="treatment-detail-title">시술 상세</h1>
          {treatment && (
            <Link
              to={`/customers/${treatment.customerId}/treatments`}
              className="treatment-detail-back-link"
            >
              고객 이력으로
            </Link>
          )}
        </div>

        {loading && <div className="treatment-detail-loading">불러오는 중...</div>}
        {error && <div className="treatment-detail-error">{error}</div>}

        {!loading && !error && treatment && (
          <>
            <div className="treatment-detail-content">
              <div className="treatment-detail-row">
                <span className="label">ID</span>
                <span>{treatment.id}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">고객 ID</span>
                <span>{treatment.customerId}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">시술 날짜</span>
                <span>{treatment.treatmentDate}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">시술 시간</span>
                <span>{treatment.treatmentTime || "-"}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">카테고리</span>
                <span>{treatment.category}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">스타일명</span>
                <span>{treatment.styleName || "-"}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">상세 내용</span>
                <span>{treatment.detail || "-"}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">등록일</span>
                <span>{treatment.createdAt}</span>
              </div>
              <div className="treatment-detail-row">
                <span className="label">수정일</span>
                <span>{treatment.updatedAt || "-"}</span>
              </div>
            </div>

            <div className="treatment-detail-images">
              <h2>이미지</h2>

              {treatment.images.length === 0 ? (
                <p>등록된 이미지가 없습니다.</p>
              ) : (
                <div className="treatment-image-grid">
                  {treatment.images.map((image) => (
                    <div key={image.id} className="treatment-image-item">
                      <img
                        src={image.imageUrl}
                        alt={image.originalName || "treatment image"}
                      />
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="treatment-detail-actions">
              <button onClick={onDelete} disabled={deleting}>
                {deleting ? "삭제 중..." : "삭제"}
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}