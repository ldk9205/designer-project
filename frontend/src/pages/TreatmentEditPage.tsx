import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  getTreatmentDetailApi,
  updateTreatmentApi,
} from "../api/treatment";
import type { TreatmentUpdateRequestDto } from "../types/treatment";
import "../styles/TreatmentEditPage.css";

export default function TreatmentEditPage() {
  const { customerId, treatmentId } = useParams();
  const navigate = useNavigate();

  const numericCustomerId = Number(customerId);
  const numericTreatmentId = Number(treatmentId);

  const [form, setForm] = useState<TreatmentUpdateRequestDto>({
    treatmentDate: "",
    treatmentTime: "",
    category: "",
    styleName: "",
    detail: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadTreatment = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getTreatmentDetailApi(numericTreatmentId);

        setForm({
          treatmentDate: data.treatmentDate || "",
          treatmentTime: data.treatmentTime || "",
          category: data.category || "",
          styleName: data.styleName || "",
          detail: data.detail || "",
        });
      } catch (err: any) {
        setError(
          err?.response?.data?.message ||
            err?.message ||
            "시술 이력 조회 실패"
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

  const onChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const goTreatmentList = () => {
    navigate(`/customers/${numericCustomerId}/treatments`);
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError("");

      await updateTreatmentApi(numericTreatmentId, form);

      alert("시술 이력이 수정되었습니다.");
      goTreatmentList();
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
          err?.message ||
          "시술 이력 수정 실패"
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="treatment-edit-page">불러오는 중...</div>;
  }

  return (
    <div className="treatment-edit-page">
      <div className="treatment-edit-card">
        <div className="treatment-edit-header">
          <h1 className="treatment-edit-title">시술 이력 수정</h1>
        </div>

        <form onSubmit={onSubmit} className="treatment-edit-form">
          <div className="treatment-edit-field">
            <label>시술 날짜</label>
            <input
              type="date"
              name="treatmentDate"
              value={form.treatmentDate || ""}
              onChange={onChange}
              required
            />
          </div>

          <div className="treatment-edit-field">
            <label>시술 시간</label>
            <input
              type="time"
              name="treatmentTime"
              value={form.treatmentTime || ""}
              onChange={onChange}
            />
          </div>

          <div className="treatment-edit-field">
            <label>카테고리</label>
            <input
              type="text"
              name="category"
              value={form.category || ""}
              onChange={onChange}
              placeholder="예: 커트, 펌, 염색"
              required
            />
          </div>

          <div className="treatment-edit-field">
            <label>스타일명</label>
            <input
              type="text"
              name="styleName"
              value={form.styleName || ""}
              onChange={onChange}
              placeholder="예: 레이어드컷"
            />
          </div>

          <div className="treatment-edit-field">
            <label>상세 내용</label>
            <textarea
              name="detail"
              value={form.detail || ""}
              onChange={onChange}
              rows={5}
              placeholder="시술 상세 내용을 입력하세요"
            />
          </div>

          {error && <div className="treatment-edit-error">{error}</div>}

          <div className="treatment-edit-actions">
            <button
              type="button"
              className="treatment-edit-cancel"
              onClick={goTreatmentList}
            >
              취소
            </button>

            <button
              type="submit"
              className="treatment-edit-submit"
              disabled={saving}
            >
              {saving ? "수정 중..." : "수정 저장"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}