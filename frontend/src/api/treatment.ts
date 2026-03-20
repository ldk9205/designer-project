import api from "./axios";
import type {
  TreatmentCreateRequestDto,
  TreatmentDetailResponseDto,
  TreatmentResponseDto,
} from "../types/treatment";

// 시술 등록
export const createTreatmentApi = async (
  payload: TreatmentCreateRequestDto
): Promise<void> => {
  await api.post("/treatments", payload);
};

// 특정 고객의 시술 이력 목록 조회
export const getTreatmentsByCustomerApi = async (
  customerId: number
): Promise<TreatmentResponseDto[]> => {
  const response = await api.get(`/customers/${customerId}/treatments`);
  return response.data;
};

// 시술 단건 상세 조회
export const getTreatmentDetailApi = async (
  treatmentId: number
): Promise<TreatmentDetailResponseDto> => {
  const response = await api.get(`/treatments/${treatmentId}`);
  return response.data;
};

// 시술 삭제
export const deleteTreatmentApi = async (
  treatmentId: number
): Promise<void> => {
  await api.delete(`/treatments/${treatmentId}`);
};