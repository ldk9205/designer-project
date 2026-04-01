import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { createTreatmentApi } from "../api/treatment";
import { uploadTreatmentImageApi } from "../api/fileApi";
import type { TreatmentCreateRequestDto } from "../types/treatment";
import "../styles/TreatmentCreatePage.css";

type PreviewFile = {
  id: string;
  file: File;
  previewUrl: string;
};

export default function TreatmentCreatePage() {
  const { customerId } = useParams();
  const navigate = useNavigate();

  const numericCustomerId = Number(customerId);

  const [form, setForm] = useState<TreatmentCreateRequestDto>({
    customerId: numericCustomerId,
    treatmentDate: "",
    treatmentTime: "",
    category: "",
    styleName: "",
    detail: "",
  });

  const [previewFiles, setPreviewFiles] = useState<PreviewFile[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const onChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files ?? []);

    const newPreviewFiles = files.map((file) => ({
      id: `${file.name}-${file.size}-${file.lastModified}-${crypto.randomUUID()}`,
      file,
      previewUrl: URL.createObjectURL(file),
    }));

    setPreviewFiles((prev) => [...prev, ...newPreviewFiles]);

    e.target.value = "";
  };

  const removeSelectedFile = (id: string) => {
    setPreviewFiles((prev) => {
      const target = prev.find((item) => item.id === id);
      if (target) {
        URL.revokeObjectURL(target.previewUrl);
      }
      return prev.filter((item) => item.id !== id);
    });
  };

  useEffect(() => {
    return () => {
      previewFiles.forEach((item) => URL.revokeObjectURL(item.previewUrl));
    };
  }, [previewFiles]);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setLoading(true);
      setError("");

      const created = await createTreatmentApi({
        ...form,
        customerId: numericCustomerId,
      });

      for (const item of previewFiles) {
        await uploadTreatmentImageApi(created.treatmentId, item.file);
      }

      alert("시술 이력이 등록되었습니다.");
      navigate(
        `/customers/${numericCustomerId}/treatments/${created.treatmentId}`,
      );
    } catch (err: any) {
      setError(
        err?.response?.data?.message || err?.message || "시술 이력 등록 실패",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="treatment-create-page">
      <div className="treatment-create-card">
        <div className="treatment-create-header">
          <h1 className="treatment-create-title">시술 이력 등록</h1>
        </div>

        <form onSubmit={onSubmit} className="treatment-create-form">
          <div className="treatment-create-field">
            <label>시술 날짜</label>
            <input
              type="date"
              name="treatmentDate"
              value={form.treatmentDate}
              onChange={onChange}
              required
            />
          </div>

          <div className="treatment-create-field">
            <label>시술 시간</label>
            <input
              type="time"
              name="treatmentTime"
              value={form.treatmentTime || ""}
              onChange={onChange}
            />
          </div>

          <div className="treatment-create-field">
            <label>카테고리</label>
            <input
              type="text"
              name="category"
              value={form.category}
              onChange={onChange}
              placeholder="예: 커트, 펌, 염색"
              required
            />
          </div>

          <div className="treatment-create-field">
            <label>스타일명</label>
            <input
              type="text"
              name="styleName"
              value={form.styleName || ""}
              onChange={onChange}
              placeholder="예: 레이어드컷"
            />
          </div>

          <div className="treatment-create-field">
            <label>상세 내용</label>
            <textarea
              name="detail"
              value={form.detail || ""}
              onChange={onChange}
              rows={5}
              placeholder="시술 상세 내용을 입력하세요"
            />
          </div>

          <div className="treatment-create-field">
            <label>시술 사진 업로드</label>
            <input
              type="file"
              multiple
              accept="image/*"
              onChange={onFileChange}
            />
          </div>

          {previewFiles.length > 0 && (
            <div className="image-preview-grid">
              {previewFiles.map((item) => (
                <div key={item.id} className="image-preview-card">
                  <button
                    type="button"
                    className="image-remove-button"
                    onClick={() => removeSelectedFile(item.id)}
                  >
                    ×
                  </button>
                  <img src={item.previewUrl} alt={item.file.name} />
                  <div className="image-preview-name">{item.file.name}</div>
                </div>
              ))}
            </div>
          )}

          {error && <div className="treatment-create-error">{error}</div>}

          <div className="treatment-create-actions">
            <button
              type="button"
              className="treatment-create-cancel"
              onClick={() =>
                navigate(`/customers/${numericCustomerId}/treatments`)
              }
            >
              취소
            </button>
            <button
              type="submit"
              className="treatment-create-submit"
              disabled={loading}
            >
              {loading ? "등록 중..." : "등록"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
