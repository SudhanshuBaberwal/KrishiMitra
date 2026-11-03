import api from "../client";

import type {
  SoilApiResponse,
  SoilRecord,
  SoilRecordInput,
  SoilRecordUpdateInput,
} from "@/types/soil";

export async function getSoilRecords(): Promise<SoilRecord[]> {
  const response = await api.get<SoilApiResponse<SoilRecord[]>>(
    "/api/farmers/me/soil-records",
  );

  return response.data.data;
}

export async function getSoilRecordById(
  recordId: string,
): Promise<SoilRecord> {
  const response = await api.get<SoilApiResponse<SoilRecord>>(
    `/api/farmers/me/soil-records/${recordId}`,
  );

  return response.data.data;
}

export async function createSoilRecord(
  payload: SoilRecordInput,
): Promise<SoilRecord> {
  const response = await api.post<SoilApiResponse<SoilRecord>>(
    "/api/farmers/me/soil-records",
    payload,
  );

  return response.data.data;
}

export async function updateSoilRecord(
  recordId: string,
  payload: SoilRecordUpdateInput,
): Promise<SoilRecord> {
  const response = await api.put<SoilApiResponse<SoilRecord>>(
    `/api/farmers/me/soil-records/${recordId}`,
    payload,
  );

  return response.data.data;
}

export async function deleteSoilRecord(recordId: string): Promise<void> {
  await api.delete(`/api/farmers/me/soil-records/${recordId}`);
}