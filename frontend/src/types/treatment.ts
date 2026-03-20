export type TreatmentCreateRequestDto = {
  customerId: number;
  treatmentDate: string;   // yyyy-MM-dd
  treatmentTime?: string;  // HH:mm
  category: string;
  styleName?: string;
  detail?: string;
};

export type TreatmentResponseDto = {
  id: number;
  treatmentDate: string;
  treatmentTime: string | null;
  category: string;
  styleName: string | null;
};

export type ImageResponseDto = {
  id: number;
  imageUrl: string;
  originalName: string | null;
};

export type TreatmentDetailResponseDto = {
  id: number;
  customerId: number;
  treatmentDate: string;
  treatmentTime: string | null;
  category: string;
  styleName: string | null;
  detail: string | null;
  createdAt: string;
  updatedAt: string;
  images: ImageResponseDto[];
};