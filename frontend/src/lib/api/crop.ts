import api from "../client";
import type { CropApiResponse, CropInput, FarmerCrop } from "@/types/crop";

export async function getCrops(): Promise<FarmerCrop[]> {
  const response = await api.get<CropApiResponse<FarmerCrop[]>>(
    "/api/farmers/me/crops",
  );

  return response.data.data;
}

export async function getCropById(cropId: string): Promise<FarmerCrop> {
  const response = await api.get<CropApiResponse<FarmerCrop>>(
    `/api/farmers/me/crops/${cropId}`,
  );

  return response.data.data;
}

export async function createCrop(payload: CropInput): Promise<FarmerCrop> {
  const response = await api.post<CropApiResponse<FarmerCrop>>(
    "/api/farmers/me/crops",
    payload,
  );

  return response.data.data;
}
export async function updateCrop(
  cropId: string,
  payload: CropInput,
): Promise<FarmerCrop> {
  const response = await api.put<CropApiResponse<FarmerCrop>>(
    `/api/farmers/me/crops/${cropId}`,
    { ...payload, active: true },
  );

  return response.data.data;
}

export async function deleteCrop(cropId: string): Promise<void> {
  await api.delete(`/api/farmers/me/crops/${cropId}`);
}
