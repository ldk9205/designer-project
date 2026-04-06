import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getTreatmentDetailApi, updateTreatmentApi } from "../api/treatment";
import {
  deleteTreatmentImageApi,
  uploadTreatmentImageApi,
} from "../api/fileApi";
import type {
  ImageResponseDto,
  TreatmentUpdateRequestDto,
} from "../types/treatment";
import Header from "../components/Header";
import "../styles/TreatmentEditPage.css";

type PreviewFile = {
  id: string;
  file: File;
  previewUrl: string;
};

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

  const [existingImages, setExistingImages] = useState<ImageResponseDto[]>([]);
  const [previewFiles, setPreviewFiles] = useState<PreviewFile[]>([]);
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

        setExistingImages(data.images || []);
      } catch (err: any) {
        setError(
          err?.response?.data?.message || err?.message || "시술 이력 조회 실패",
        );
      } finally {
        setLoading(false);
      }
    };

    if (!numericTreatmentId || Number.isNaN(numericTreatmentId)) {
      setError("잘못된 접근입니다.");
      setLoading(false);
      return;
    }

    void loadTreatment();
  }, [numericTreatmentId]);

  useEffect(() => {
    return () => {
      previewFiles.forEach((item) => URL.revokeObjectURL(item.previewUrl));
    };
  }, [previewFiles]);

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

  const removeExistingImage = async (imageId: number) => {
    const ok = window.confirm("이 이미지를 삭제하시겠습니까?");
    if (!ok) return;

    try {
      await deleteTreatmentImageApi(imageId);
      setExistingImages((prev) => prev.filter((img) => img.id !== imageId));
    } catch (err: any) {
      alert(err?.response?.data?.message || err?.message || "이미지 삭제 실패");
    }
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

      for (const item of previewFiles) {
        await uploadTreatmentImageApi(numericTreatmentId, item.file);
      }

      alert("시술 이력이 수정되었습니다.");
      navigate(
        `/customers/${numericCustomerId}/treatments/${numericTreatmentId}`,
      );
    } catch (err: any) {
      setError(
        err?.response?.data?.message || err?.message || "시술 이력 수정 실패",
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
      <Header />
      
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

          <div className="treatment-edit-field">
            <label>기존 업로드 이미지</label>
            {existingImages.length === 0 ? (
              <div className="empty-image-text">등록된 사진이 없습니다.</div>
            ) : (
              <div className="image-preview-grid">
                {existingImages.map((image) => (
                  <div key={image.id} className="image-preview-card">
                    <button
                      type="button"
                      className="image-remove-button"
                      onClick={() => removeExistingImage(image.id)}
                    >
                      ×
                    </button>
                    <img
                      src={image.imageUrl}
                      alt={image.originalName || `image-${image.id}`}
                    />
                    <div className="image-preview-name">
                      {image.originalName || `image-${image.id}`}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="treatment-edit-field">
            <label>추가 사진 업로드</label>
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
