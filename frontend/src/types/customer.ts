export type CustomerCreateRequestDto = {
  name: string;
  phone: string;
  memo?: string;
};

export type CustomerUpdateRequestDto = {
  name?: string;
  phone?: string;
  memo?: string;
};

export type CustomerResponseDto = {
  id: number;
  designerId: number;
  name: string;
  phone: string | null;
  memo: string | null;
  createdAt: string;
  updatedAt: string;
};

export type CustomerPageResponseDto = {
  content: CustomerResponseDto[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};