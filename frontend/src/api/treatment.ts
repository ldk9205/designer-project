import api from "./axios";
import type {
  TreatmentCreateRequestDto,
  TreatmentCreateResponseDto,
  TreatmentDetailResponseDto,
  TreatmentPageResponseDto,
  TreatmentUpdateRequestDto,
} from "../types/treatment";

export const createTreatmentApi = async (
  payload: TreatmentCreateRequestDto
): Promise<TreatmentCreateResponseDto> => {
  const response = await api.post("/treatments", payload);
  return response.data;
};

export const getTreatmentsByCustomerApi = async (
  customerId: number,
  page: number,
  size: number,
  category?: string,
  sortDirection: "desc" | "asc" = "desc"
): Promise<TreatmentPageResponseDto> => {
  const response = await api.get(`/customers/${customerId}/treatments`, {
    params: {
      page,
      size,
      category: category?.trim() ? category.trim() : undefined,
      sortDirection,
    },
  });
  
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