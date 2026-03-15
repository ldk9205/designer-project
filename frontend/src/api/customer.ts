import api from "./axios";
import type {
  CustomerCreateRequestDto,
  CustomerResponseDto,
  CustomerUpdateRequestDto,
} from "../types/customer";

export async function createCustomerApi(
  payload: CustomerCreateRequestDto
): Promise<CustomerResponseDto> {
  const res = await api.post<CustomerResponseDto>("/customers", payload);
  return res.data;
}

export async function getCustomersApi(): Promise<CustomerResponseDto[]> {
  const res = await api.get<CustomerResponseDto[]>("/customers");
  return res.data;
}

export async function getCustomerApi(
  customerId: number
): Promise<CustomerResponseDto> {
  const res = await api.get<CustomerResponseDto>(`/customers/${customerId}`);
  return res.data;
}

export async function updateCustomerApi(
  customerId: number,
  payload: CustomerUpdateRequestDto
): Promise<CustomerResponseDto> {
  const res = await api.patch<CustomerResponseDto>(
    `/customers/${customerId}`,
    payload
  );
  return res.data;
}

export async function deleteCustomerApi(customerId: number): Promise<void> {
  await api.delete(`/customers/${customerId}`);
}