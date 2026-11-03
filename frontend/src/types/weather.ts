export interface WeatherLocation {
  name: string;
  district: string | null;
  state: string | null;
  country: string;
  latitude: number;
  longitude: number;
  timezone: string;
}

export interface WeatherCurrent {
  time?: string;
  interval?: number;
  temperature_2m?: number;
  relative_humidity_2m?: number;
  apparent_temperature?: number;
  is_day?: number;
  precipitation?: number;
  rain?: number;
  showers?: number;
  weather_code?: number;
  cloud_cover?: number;
  wind_speed_10m?: number;
  wind_direction_10m?: number;
  wind_gusts_10m?: number;
}

export interface WeatherHourly {
  time?: string[];
  temperature_2m?: number[];
  relative_humidity_2m?: number[];
  apparent_temperature?: number[];
  precipitation_probability?: number[];
  precipitation?: number[];
  rain?: number[];
  weather_code?: number[];
  wind_speed_10m?: number[];
}

export interface WeatherDaily {
  time?: string[];
  weather_code?: number[];
  temperature_2m_max?: number[];
  temperature_2m_min?: number[];
  rain_sum?: number[];
  precipitation_sum?: number[];
  precipitation_probability_max?: number[];
  wind_speed_10m_max?: number[];
  sunrise?: string[];
  sunset?: string[];
}

export interface WeatherResponse {
  source: string;
  attribution: string;
  location: WeatherLocation;
  current: WeatherCurrent | null;
  hourly: WeatherHourly | null;
  daily: WeatherDaily | null;
  fetchedAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface WeatherCoordinates {
  latitude: number;
  longitude: number;
}