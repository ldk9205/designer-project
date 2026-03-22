import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  deleteTreatmentApi,
  getTreatmentDetailApi,
} from "../api/treatment";
import type { TreatmentDetailResponseDto } from "../types/treatment";
import "../styles/TreatmentDetailPage.css";

export default function TreatmentDetailPage() {
  const { customerId, treatmentId } = useParams();
  const navigate = useNavigate();

  const numericCustomerId = Number(customerId);
  const numericTreatmentId = Number(treatmentId);

  const [treatment, setTreatment] = useState<TreatmentDetailResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
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

    if (
      !numericCustomerId ||
      Number.isNaN(numericCustomerId) ||
      !numericTreatmentId ||
      Number.isNaN(numericTreatmentId)
    ) {
      setError("잘못된 접근입니다.");
      setLoading(false);
      return;
    }

    void loadTreatment();
  }, [numericCustomerId, numericTreatmentId]);

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
          "시술 이력 삭제 실패"
      );
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return <div className="treatment-detail-page">불러오는 중...</div>;
  }

  if (error) {
    return <div className="treatment-detail-page">{error}</div>;
  }

  if (!treatment) {
    return <div className="treatment-detail-page">시술 정보가 없습니다.</div>;
  }

  return (
    <div className="treatment-detail-page">
      <div className="treatment-detail-card">
        <div className="treatment-detail-header">
          <h1 className="treatment-detail-title">시술 상세 이력</h1>
        </div>

        <div className="treatment-detail-body">
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

          <div className="treatment-detail-row detail-column">
            <span className="label">상세 내용</span>
            <div className="treatment-detail-text">
              {treatment.detail || "입력된 상세 내용이 없습니다."}
            </div>
          </div>

          <div className="treatment-detail-row detail-column">
            <span className="label">이미지</span>
            {treatment.images.length === 0 ? (
              <div className="treatment-detail-empty-image">
                등록된 이미지가 없습니다.
              </div>
            ) : (
              <div className="treatment-detail-image-list">
                {treatment.images.map((image) => (
                  <div key={image.id} className="treatment-detail-image-item">
                    <img
                      src={image.imageUrl}
                      alt={image.originalName || "시술 이미지"}
                    />
                    <div className="treatment-detail-image-name">
                      {image.originalName || "이미지"}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="treatment-detail-actions">
          <button
            type="button"
            className="treatment-detail-back"
            onClick={() => navigate(`/customers/${treatment.customerId}/treatments`)}
          >
            목록으로
          </button>

          <button
            type="button"
            className="treatment-detail-edit"
            onClick={() =>
              navigate(`/customers/${treatment.customerId}/treatments/${numericTreatmentId}/edit`)
            }
          >
            수정
          </button>

          <button
            type="button"
            className="treatment-detail-delete"
            onClick={onDelete}
            disabled={deleting}
          >
            {deleting ? "삭제 중..." : "삭제"}
          </button>
        </div>
      </div>
    </div>
  );
}