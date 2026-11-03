import api from "@/lib/client";
import type {
  ApiResponse,
  WeatherCoordinates,
  WeatherResponse,
} from "@/types/weather";

const getParams = (coordinates?: WeatherCoordinates) => {
  if (!coordinates) return undefined;

  return {
    latitude: coordinates.latitude,
    longitude: coordinates.longitude,
  };
};

export async function getCurrentWeather(
  coordinates?: WeatherCoordinates
): Promise<WeatherResponse> {
  const response = await api.get<ApiResponse<WeatherResponse>>(
    "/api/weather/current",
    { params: getParams(coordinates) }
  );

  return response.data.data;
}

export async function getWeatherForecast(
  coordinates?: WeatherCoordinates
): Promise<WeatherResponse> {
  const response = await api.get<ApiResponse<WeatherResponse>>(
    "/api/weather/forecast",
    { params: getParams(coordinates) }
  );

  return response.data.data;
}

export async function getHourlyWeather(
  coordinates?: WeatherCoordinates
): Promise<WeatherResponse> {
  const response = await api.get<ApiResponse<WeatherResponse>>(
    "/api/weather/hourly",
    { params: getParams(coordinates) }
  );

  return response.data.data;
}

export async function getWeatherLocation(
  coordinates?: WeatherCoordinates
): Promise<WeatherResponse> {
  const response = await api.get<ApiResponse<WeatherResponse>>(
    "/api/weather/location",
    { params: getParams(coordinates) }
  );

  return response.data.data;
}