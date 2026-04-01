// src/api/fileApi.ts

import api from "./axios";

/**
 * ===============================
 * 타입 정의
 * ===============================
 */

export interface PresignUploadResponse {
  presignedUrl: string;
  objectKey: string;
}

export interface CommunityItem {
  imageId: number;
  customerName: string;
  treatmentDate: string;
  category: string;
  styleName: string;
  presignedUrl: string;
}

/**
 * ===============================
 * 업로드용 Presigned URL 요청
 * ===============================
 * POST /treatments/{id}/images/presign
 *
 * 🔥 axios 사용 → 자동 Authorization 포함
 * 🔥 refresh 자동 처리됨
 */
export const requestUploadPresignedUrl = async ({
  treatmentId,
  fileName,
  contentType,
}: {
  treatmentId: number;
  fileName: string;
  contentType: string;
}): Promise<PresignUploadResponse> => {
  const res = await api.post<PresignUploadResponse>(
    `/treatments/${treatmentId}/images/presign`,
    {
      fileName,
      contentType,
    }
  );

  return res.data;
};

/**
 * ===============================
 * Presigned URL로 S3 업로드
 * ===============================
 * ⚠️ 이건 S3 직접 호출이므로 axios 말고 fetch 사용
 * (S3는 Spring 인증 필요 없음)
 */
export const uploadFileToS3 = async (
  presignedUrl: string,
  file: File
): Promise<void> => {
  const response = await fetch(presignedUrl, {
    method: "PUT",
    headers: {
      "Content-Type": file.type,
    },
    body: file,
  });

  if (!response.ok) {
    throw new Error("Failed to upload file to S3");
  }
};

/**
 * ===============================
 * 커뮤니티 이미지 목록 조회
 * ===============================
 * GET /api/community
 *
 * 🔥 axios 사용 → Authorization 자동 포함
 * 🔥 401 발생 시 refresh 자동 실행
 */
export const fetchCommunityList = async (): Promise<CommunityItem[]> => {
  const res = await api.get<CommunityItem[]>("/api/community");
  return res.data;
};

// =============================== 2026-04-01
export interface TreatmentImageItem {
  id: number;
  imageUrl: string;
  originalName: string | null;
}

export interface CommunityItem {
  imageId: number;
  customerName: string;
  treatmentDate: string;
  category: string;
  styleName: string;
  presignedUrl: string;
}

export const uploadTreatmentImageApi = async (
  treatmentId: number,
  file: File
): Promise<void> => {
  const formData = new FormData();
  formData.append("file", file);

  await api.post(`/api/treatments/${treatmentId}/images`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

export const getTreatmentImagesApi = async (
  treatmentId: number
): Promise<TreatmentImageItem[]> => {
  const res = await api.get(`/api/treatments/${treatmentId}/images`);
  return res.data;
};

export const deleteTreatmentImageApi = async (imageId: number): Promise<void> => {
  await api.delete(`/api/images/${imageId}`);
};

// export const fetchCommunityList = async (): Promise<CommunityItem[]> => {
//   const res = await api.get<CommunityItem[]>("/api/community");
//   return res.data;
// };
// =============================== 2026-04-01 end