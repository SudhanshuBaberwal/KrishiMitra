
export type CropSeason = "KHARIF" | "RABI" | "ZAID" | "PERENNIAL";

export type CropGrowthStage =
  | "LAND_PREPARATION"
  | "SOWING"
  | "GERMINATION"
  | "SEEDLING"
  | "VEGETATIVE"
  | "FLOWERING"
  | "FRUITING"
  | "MATURITY"
  | "HARVESTED";

export type AreaUnit = "ACRE" | "HECTARE";

export interface FarmerCrop {
  id: string;
  farmerId: string;
  cropCode: string;
  cropName: string;
  variety: string;
  season: CropSeason;
  sowingDate: string;
  growthStage: CropGrowthStage;
  area: number;
  areaUnit: AreaUnit;
  expectedHarvestDate: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CropInput {
  cropCode: string;
  cropName: string;
  variety: string;
  season: CropSeason;
  sowingDate: string;
  growthStage: CropGrowthStage;
  area: number;
  areaUnit: AreaUnit;
  expectedHarvestDate: string;
  active?: boolean;
}

export interface CropApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
