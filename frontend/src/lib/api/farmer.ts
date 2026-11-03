
import { FarmerApiResponse, FarmerProfile, FarmerProfileInput } from "@/types/farmer";
import api from "../client";

export async function getFarmerProfile(): Promise<FarmerProfile> {
  const response = await api.get<FarmerApiResponse>("/api/farmers/me");
  return response.data.data;
}

export async function createFarmerProfile(
  payload: FarmerProfileInput
): Promise<FarmerProfile> {
  const response = await api.post<FarmerApiResponse>(
    "/api/farmers",
    payload
  );

  return response.data.data;
}

export async function updateFarmerProfile(
  payload: FarmerProfileInput
): Promise<FarmerProfile> {
  const response = await api.put<FarmerApiResponse>(
    "/api/farmers/me",
    payload
  );

  return response.data.data;
}
