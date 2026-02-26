import React, { useState } from "react";
import {
  requestUploadPresignedUrl,
  uploadFileToS3,
} from "../api/fileApi";

export default function UploadPage() {
  const [treatmentId, setTreatmentId] = useState<string>("");
  const [file, setFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [message, setMessage] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  /**
   * 파일 선택
   */
  const handleFileChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    const selected = e.target.files?.[0];
    if (!selected) return;

    setFile(selected);
    setPreviewUrl(URL.createObjectURL(selected));
  };

  /**
   * 업로드 실행
   */
  const handleUpload = async () => {
    if (!file || !treatmentId) {
      setMessage("시술 ID와 파일을 선택해주세요.");
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      // 1️⃣ Presign 요청
      const { presignedUrl } =
        await requestUploadPresignedUrl({
          treatmentId: Number(treatmentId),
          fileName: file.name,
          contentType: file.type,
        });

      // 2️⃣ S3 업로드
      await uploadFileToS3(presignedUrl, file);

      setMessage("업로드 성공 🎉");
      setFile(null);
      setPreviewUrl(null);
      setTreatmentId("");
    } catch (err: any) {
      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "업로드 중 오류가 발생했습니다."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: "40px" }}>
      <h2>시술 이미지 업로드</h2>

      {/* Treatment ID 입력 */}
      <div style={{ marginBottom: "20px" }}>
        <input
          type="number"
          placeholder="Treatment ID 입력"
          value={treatmentId}
          onChange={(e) => setTreatmentId(e.target.value)}
          style={{ padding: "8px", width: "200px" }}
        />
      </div>

      {/* 파일 선택 */}
      <div style={{ marginBottom: "20px" }}>
        <input
          type="file"
          accept="image/*"
          onChange={handleFileChange}
        />
      </div>

      {/* 미리보기 */}
      {previewUrl && (
        <div style={{ marginBottom: "20px" }}>
          <img
            src={previewUrl}
            alt="preview"
            style={{
              width: "300px",
              borderRadius: "10px",
              border: "1px solid #ddd",
            }}
          />
        </div>
      )}

      {/* 업로드 버튼 */}
      <button
        onClick={handleUpload}
        disabled={loading}
        style={{
          padding: "10px 20px",
          cursor: "pointer",
        }}
      >
        {loading ? "업로드 중..." : "업로드"}
      </button>

      {/* 메시지 */}
      {message && (
        <p style={{ marginTop: "20px" }}>{message}</p>
      )}
    </div>
  );
}