export type CustomerCreateRequestDto = {
  name: string;
  phone?: string;
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