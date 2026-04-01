import api from "./axios";
import type {
  CustomerCreateRequestDto,
  CustomerUpdateRequestDto,
  CustomerResponseDto,
  CustomerPageResponseDto,
} from "../types/customer";

// 고객 등록
export const createCustomerApi = async (
  payload: CustomerCreateRequestDto
): Promise<CustomerResponseDto> => {
  const response = await api.post("/customers", payload);
  return response.data;
};

// 고객 목록 조회 + 이름 검색 + Paging Block
export const getCustomersApi = async (params?: {
  name?: string;
  page?: number;
  size?: number;
}): Promise<CustomerPageResponseDto> => {
  const response = await api.get("/customers", {
    params: {
      name: params?.name,
      page: params?.page ?? 0,
      size: params?.size ?? 5,
    },
  });
  return response.data;
};

// 고객 단건 조회
export const getCustomerApi = async (
  customerId: number
): Promise<CustomerResponseDto> => {
  const response = await api.get(`/customers/${customerId}`);
  return response.data;
};

// 고객 수정
export const updateCustomerApi = async (
  customerId: number,
  payload: CustomerUpdateRequestDto
): Promise<CustomerResponseDto> => {
  const response = await api.patch(`/customers/${customerId}`, payload);
  return response.data;
};

// 고객 삭제
export const deleteCustomerApi = async (customerId: number): Promise<void> => {
  await api.delete(`/customers/${customerId}`);
};