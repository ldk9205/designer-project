import api from "./axios";
import type {
  TreatmentCreateRequestDto,
  TreatmentCreateResponseDto,
  TreatmentDetailResponseDto,
  TreatmentResponseDto,
  TreatmentUpdateRequestDto,
} from "../types/treatment";

export const createTreatmentApi = async (
  payload: TreatmentCreateRequestDto
): Promise<TreatmentCreateResponseDto> => {
  const response = await api.post("/treatments", payload);
  return response.data;
};

export const getTreatmentsByCustomerApi = async (
  customerId: number
): Promise<TreatmentResponseDto[]> => {
  const response = await api.get(`/customers/${customerId}/treatments`);
  return response.data;
};

export const getTreatmentDetailApi = async (
  treatmentId: number
): Promise<TreatmentDetailResponseDto> => {
  const response = await api.get(`/treatments/${treatmentId}`);
  return response.data;
};

export const updateTreatmentApi = async (
  treatmentId: number,
  payload: TreatmentUpdateRequestDto
): Promise<void> => {
  await api.patch(`/treatments/${treatmentId}`, payload);
};

export const deleteTreatmentApi = async (
  treatmentId: number
): Promise<void> => {
  await api.delete(`/treatments/${treatmentId}`);
};