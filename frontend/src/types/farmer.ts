
export type FarmerProfile = {
  id: string;
  userId: string;
  name: string;
  state: string;
  district: string;
  taluka: string;
  preferredLanguage: string;
  cropName: string;
  cropVariety: string;
  growthStage: string;
  farmSize: number;
  irrigationType: string;
  createdAt?: string;
  updatedAt?: string;
};

export type PreferredLanguage = "en" | "kn" | "hi";

export interface FarmerProfileInput {
  name: string;
  state: string;
  district: string;
  taluka: string;
  preferredLanguage: PreferredLanguage;
  cropName: string;
  cropVariety: string;
  growthStage: string;
  farmSize: number;
  irrigationType: string;
}

export type FarmerApiResponse = {
  success: boolean;
  message: string;
  data: FarmerProfile;
  timestamp: string;
};
