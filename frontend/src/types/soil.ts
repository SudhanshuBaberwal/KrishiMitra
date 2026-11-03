export type SoilSource = "FARMER_ENTERED" | "LAB_REPORT" | "SOIL_HEALTH_CARD";

export type SoilVerificationStatus =
  | "SELF_REPORTED"
  | "PENDING_VERIFICATION"
  | "VERIFIED";

export interface SoilParameters {
  pH?: number;
  electricalConductivity?: number;
  organicCarbon?: number;
  nitrogen?: number;
  phosphorus?: number;
  potassium?: number;
  sulphur?: number;
  zinc?: number;
  iron?: number;
  manganese?: number;
  copper?: number;
  boron?: number;
}

export interface SoilRecord {
  id: string;
  farmerId: string;
  sampleDate: string;
  sampleLocation: string;
  source: SoilSource;
  verificationStatus: SoilVerificationStatus;
  labName?: string;
  reportReference?: string;
  parameters: SoilParameters;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SoilRecordInput {
  sampleDate: string;
  sampleLocation: string;
  source: SoilSource;
  labName?: string;
  reportReference?: string;
  parameters: SoilParameters;
  notes?: string;
}

export interface SoilRecordUpdateInput {
  sampleLocation: string;
  labName?: string;
  reportReference?: string;
  parameters: SoilParameters;
  notes?: string;
}

export interface SoilApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}