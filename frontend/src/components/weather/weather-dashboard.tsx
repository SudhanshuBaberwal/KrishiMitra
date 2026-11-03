"use client";

import { useCallback, useEffect, useState } from "react";
import {
  ArrowLeft,
  Cloud,
  CloudDrizzle,
  CloudFog,
  CloudLightning,
  CloudRain,
  CloudSnow,
  CloudSun,
  Droplets,
  LocateFixed,
  MapPin,
  RefreshCw,
  Sun,
  Sunrise,
  Sunset,
  Thermometer,
  Wind,
  AlertCircle,
} from "lucide-react";
import Link from "next/link";

import {
  getCurrentWeather,
  getHourlyWeather,
  getWeatherForecast,
} from "@/lib/api/weather";

import type { WeatherCoordinates, WeatherResponse } from "@/types/weather";

type WeatherBundle = {
  current: WeatherResponse;
  hourly: WeatherResponse;
  forecast: WeatherResponse;
};

function getWeatherDescription(code?: number) {
  if (code === undefined) {
    return { label: "Unknown", Icon: Cloud };
  }

  if (code === 0) return { label: "Clear sky", Icon: Sun };
  if (code === 1) return { label: "Mainly clear", Icon: Sun };
  if (code === 2) return { label: "Partly cloudy", Icon: CloudSun };
  if (code === 3) return { label: "Overcast", Icon: Cloud };
  if (code === 45 || code === 48) {
    return { label: "Foggy", Icon: CloudFog };
  }

  if ([51, 53, 55, 56, 57].includes(code)) {
    return { label: "Drizzle", Icon: CloudDrizzle };
  }

  if ([61, 63, 65, 66, 67, 80, 81, 82].includes(code)) {
    return { label: "Rain", Icon: CloudRain };
  }

  if ([71, 73, 75, 77, 85, 86].includes(code)) {
    return { label: "Snow", Icon: CloudSnow };
  }

  if ([95, 96, 99].includes(code)) {
    return { label: "Thunderstorm", Icon: CloudLightning };
  }

  return { label: "Cloudy", Icon: Cloud };
}

function formatHour(value?: string) {
  if (!value) return "--";

  const date = new Date(value);

  return date.toLocaleTimeString("en-IN", {
    hour: "numeric",
    hour12: true,
  });
}

function formatDay(value?: string, index = 0) {
  if (!value) return index === 0 ? "Today" : "--";

  if (index === 0) return "Today";

  return new Date(`${value}T12:00:00`).toLocaleDateString("en-IN", {
    weekday: "short",
  });
}

function formatTime(value?: string) {
  if (!value) return "--";

  return new Date(value).toLocaleTimeString("en-IN", {
    hour: "numeric",
    minute: "2-digit",
    hour12: true,
  });
}

function MetricCard({
  icon: Icon,
  label,
  value,
  description,
}: {
  icon: React.ElementType;
  label: string;
  value: string;
  description: string;
}) {
  return (
    <div className="dashboard-card p-4">
      <div className="flex items-center justify-between">
        <span className="text-xs muted-text">{label}</span>
        <Icon size={19} className="text-[var(--primary)]" />
      </div>

      <p className="mt-3 text-xl font-bold">{value}</p>
      <p className="mt-1 text-[10px] muted-text">{description}</p>
    </div>
  );
}

function WeatherLoading() {
  return (
    <div className="space-y-5 animate-pulse">
      <div className="h-64 rounded-3xl bg-[var(--surface-muted)]" />

      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <div
            key={index}
            className="h-28 rounded-2xl bg-[var(--surface-muted)]"
          />
        ))}
      </div>

      <div className="h-56 rounded-3xl bg-[var(--surface-muted)]" />
    </div>
  );
}

export default function WeatherDashboard() {
  const [weather, setWeather] = useState<WeatherBundle | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [gpsLoading, setGpsLoading] = useState(false);
  const [coordinates, setCoordinates] = useState<
    WeatherCoordinates | undefined
  >(undefined);

  const loadWeather = useCallback(async (coords?: WeatherCoordinates) => {
    setLoading(true);
    setError("");

    try {
      const [current, hourly, forecast] = await Promise.all([
        getCurrentWeather(coords),
        getHourlyWeather(coords),
        getWeatherForecast(coords),
      ]);

      setWeather({ current, hourly, forecast });
    } catch (err: unknown) {
      console.error("Weather loading error:", err);

      setError(
        err instanceof Error
          ? err.message
          : "Unable to fetch weather information. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      void loadWeather();
    }, 0);

    return () => clearTimeout(timer);
  }, [loadWeather]);

  const handleCurrentLocation = () => {
    if (!navigator.geolocation) {
      setError("Your browser does not support location services.");
      return;
    }

    setGpsLoading(true);
    setError("");

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const coords = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        };

        setCoordinates(coords);
        setGpsLoading(false);
        void loadWeather(coords);
      },
      (positionError) => {
        setGpsLoading(false);

        if (positionError.code === positionError.PERMISSION_DENIED) {
          setError("Location permission denied. Please allow location access.");
        } else if (positionError.code === positionError.POSITION_UNAVAILABLE) {
          setError("Your current location is unavailable.");
        } else {
          setError("Location request timed out. Please try again.");
        }
      },
      {
        enableHighAccuracy: false,
        timeout: 10000,
        maximumAge: 300000,
      },
    );
  };

  const handleProfileLocation = () => {
    setCoordinates(undefined);
    void loadWeather();
  };

  if (loading && !weather) {
    return <WeatherLoading />;
  }

  if (error && !weather) {
    return (
      <div className="dashboard-card flex flex-col items-center justify-center p-8 text-center">
        <AlertCircle size={35} className="text-red-500" />

        <h2 className="mt-4 text-lg font-bold">Unable to load weather</h2>

        <p className="mt-2 max-w-md text-sm muted-text">{error}</p>

        <button
          onClick={() => void loadWeather(coordinates)}
          className="mt-5 flex items-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-sm font-semibold text-white"
        >
          <RefreshCw size={16} />
          Try again
        </button>
      </div>
    );
  }

  if (!weather) return null;

  const current = weather.current.current;
  const hourly = weather.hourly.hourly;
  const daily = weather.forecast.daily;
  const location = weather.forecast.location;

  const currentCondition = getWeatherDescription(current?.weather_code);
  const CurrentIcon = currentCondition.Icon;

  const hourlyTimes = hourly?.time ?? [];
  const hourlyTemperatures = hourly?.temperature_2m ?? [];
  const hourlyProbabilities = hourly?.precipitation_probability ?? [];
  const hourlyCodes = hourly?.weather_code ?? [];

  const currentHourIndex = hourlyTimes.findIndex(
    (time) => time >= new Date().toISOString().slice(0, 13),
  );

  const startHour = currentHourIndex >= 0 ? currentHourIndex : 0;

  const hourlyItems = hourlyTimes
    .slice(startHour, startHour + 12)
    .map((time, index) => {
      const actualIndex = startHour + index;

      return {
        time,
        temperature: hourlyTemperatures[actualIndex],
        probability: hourlyProbabilities[actualIndex],
        code: hourlyCodes[actualIndex],
      };
    });

  const dailyItems = (daily?.time ?? []).map((date, index) => ({
    date,
    index,
    min: daily?.temperature_2m_min?.[index],
    max: daily?.temperature_2m_max?.[index],
    rain: daily?.precipitation_probability_max?.[index],
    rainfall: daily?.precipitation_sum?.[index],
    code: daily?.weather_code?.[index],
    sunrise: daily?.sunrise?.[index],
    sunset: daily?.sunset?.[index],
  }));

  return (
    <div className="space-y-6">
      {/* Header */}
      <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <Link
            href="/"
            className="mb-3 inline-flex items-center gap-2 text-xs font-semibold muted-text transition hover:text-[var(--primary)]"
          >
            <ArrowLeft size={15} />
            Back to dashboard
          </Link>

          <h1 className="text-2xl font-bold tracking-tight sm:text-3xl">
            Weather Forecast
          </h1>

          <p className="mt-2 text-sm muted-text">
            Weather insights to help you plan your farming activities.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            onClick={handleProfileLocation}
            disabled={loading}
            className={`flex items-center gap-2 rounded-xl border border-[var(--border)] px-3 py-2.5 text-xs font-semibold transition hover:bg-[var(--surface-muted)] ${
              !coordinates ? "text-[var(--primary)]" : "muted-text"
            }`}
          >
            <MapPin size={15} />
            Farm location
          </button>

          <button
            onClick={handleCurrentLocation}
            disabled={gpsLoading || loading}
            className="flex items-center gap-2 rounded-xl bg-[var(--primary)] px-3 py-2.5 text-xs font-semibold text-white transition hover:bg-[var(--primary-dark)] disabled:opacity-60"
          >
            <LocateFixed size={15} />
            {gpsLoading ? "Locating..." : "Use my location"}
          </button>

          <button
            onClick={() => void loadWeather(coordinates)}
            disabled={loading}
            aria-label="Refresh weather"
            className="flex h-10 w-10 items-center justify-center rounded-xl border border-[var(--border)] transition hover:bg-[var(--surface-muted)] disabled:opacity-50"
          >
            <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
          </button>
        </div>
      </section>

      {error && (
        <div className="flex items-center justify-between gap-3 rounded-xl border border-red-200 bg-red-50 p-3 text-xs text-red-700 dark:border-red-900 dark:bg-red-950/30 dark:text-red-300">
          <span>{error}</span>
          <button
            onClick={() => void loadWeather(coordinates)}
            className="shrink-0 font-bold underline"
          >
            Retry
          </button>
        </div>
      )}

      {/* Current weather */}
      <section className="relative overflow-hidden rounded-3xl bg-[#174f31] p-5 text-white shadow-lg shadow-green-950/10 sm:p-8">
        <div className="pointer-events-none absolute -right-12 -top-16 h-64 w-64 rounded-full border-[35px] border-white/[0.06]" />
        <div className="pointer-events-none absolute -bottom-32 right-32 h-64 w-64 rounded-full border-[30px] border-white/[0.04]" />

        <div className="relative">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-2 text-xs text-green-100/80">
              <MapPin size={15} />
              <span>
                {location?.name}
                {location?.district ? `, ${location.district}` : ""}
                {location?.state ? `, ${location.state}` : ""}
              </span>
            </div>

            <span className="rounded-full bg-white/10 px-3 py-1.5 text-[10px] font-semibold text-green-100">
              {coordinates ? "GPS location" : "Farm profile location"}
            </span>
          </div>

          <div className="mt-8 flex flex-col justify-between gap-6 sm:flex-row sm:items-center">
            <div>
              <p className="text-sm font-medium text-green-100/80">
                Current temperature
              </p>

              <div className="mt-2 flex items-start">
                <span className="text-6xl font-bold tracking-tighter sm:text-7xl">
                  {current?.temperature_2m != null
                    ? Math.round(current.temperature_2m)
                    : "--"}
                </span>
                <span className="mt-1 text-3xl font-light">°C</span>
              </div>

              <p className="mt-3 text-base font-semibold">
                {currentCondition.label}
              </p>

              <p className="mt-1 text-xs text-green-100/70">
                Feels like{" "}
                {current?.apparent_temperature != null
                  ? `${Math.round(current.apparent_temperature)}°C`
                  : "--"}
              </p>
            </div>

            <div className="flex items-center justify-center sm:pr-8">
              <CurrentIcon
                size={115}
                strokeWidth={1.1}
                className="text-amber-300 drop-shadow-lg"
              />
            </div>
          </div>

          <div className="mt-8 flex flex-wrap items-center justify-between gap-3 border-t border-white/15 pt-4 text-[10px] text-green-100/70">
            <span>Local time: {formatTime(current?.time)}</span>
            <span>Weather data by Open-Meteo</span>
          </div>
        </div>
      </section>

      {/* Weather metrics */}
      <section className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        <MetricCard
          icon={Thermometer}
          label="Feels Like"
          value={
            current?.apparent_temperature != null
              ? `${Math.round(current.apparent_temperature)}°C`
              : "--"
          }
          description="Perceived temperature"
        />

        <MetricCard
          icon={Droplets}
          label="Humidity"
          value={
            current?.relative_humidity_2m != null
              ? `${current.relative_humidity_2m}%`
              : "--"
          }
          description="Relative humidity"
        />

        <MetricCard
          icon={Wind}
          label="Wind Speed"
          value={
            current?.wind_speed_10m != null
              ? `${Math.round(current.wind_speed_10m)} km/h`
              : "--"
          }
          description="At 10 metres"
        />

        <MetricCard
          icon={CloudRain}
          label="Rainfall"
          value={
            current?.precipitation != null
              ? `${current.precipitation} mm`
              : "--"
          }
          description="Current precipitation"
        />
      </section>

      {/* Hourly forecast */}
      <section className="dashboard-card overflow-hidden p-5 sm:p-6">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <h2 className="text-base font-bold">Hourly Forecast</h2>
            <p className="mt-1 text-xs muted-text">
              Temperature and rain probability over the coming hours
            </p>
          </div>

          <span className="rounded-lg bg-[var(--primary-soft)] px-3 py-1.5 text-[10px] font-semibold text-[var(--primary)]">
            Next 12 hours
          </span>
        </div>

        {hourlyItems.length > 0 ? (
          <div className="custom-scrollbar mt-6 flex gap-3 overflow-x-auto pb-3">
            {hourlyItems.map((item, index) => {
              const condition = getWeatherDescription(item.code);
              const Icon = condition.Icon;

              return (
                <div
                  key={item.time}
                  className={`flex min-w-[92px] flex-col items-center rounded-2xl p-4 ${
                    index === 0
                      ? "bg-[var(--primary-soft)]"
                      : "bg-[var(--surface-muted)]"
                  }`}
                >
                  <p className="text-[11px] font-semibold muted-text">
                    {formatHour(item.time)}
                  </p>

                  <Icon size={25} className="my-4 text-[var(--primary)]" />

                  <p className="text-lg font-bold">
                    {item.temperature != null
                      ? `${Math.round(item.temperature)}°`
                      : "--"}
                  </p>

                  <p className="mt-2 text-[10px] muted-text">
                    {item.probability != null
                      ? `${item.probability}% rain`
                      : "--"}
                  </p>
                </div>
              );
            })}
          </div>
        ) : (
          <p className="py-8 text-center text-sm muted-text">
            Hourly forecast is currently unavailable.
          </p>
        )}
      </section>

      {/* Seven day forecast */}
      <section className="dashboard-card overflow-hidden p-5 sm:p-6">
        <div>
          <h2 className="text-base font-bold">7-Day Forecast</h2>
          <p className="mt-1 text-xs muted-text">
            Plan irrigation and farm activities using the upcoming forecast.
          </p>
        </div>

        {dailyItems.length > 0 ? (
          <div className="mt-5 divide-y divide-[var(--border)]">
            {dailyItems.map((day) => {
              const condition = getWeatherDescription(day.code);
              const Icon = condition.Icon;

              return (
                <div
                  key={day.date}
                  className="grid grid-cols-[0.8fr_0.7fr_1fr] items-center gap-2 py-4 sm:grid-cols-[1fr_0.7fr_1.2fr_1fr]"
                >
                  <div>
                    <p className="text-sm font-semibold">
                      {formatDay(day.date, day.index)}
                    </p>
                    <p className="mt-1 text-[10px] muted-text">
                      {new Date(`${day.date}T12:00:00`).toLocaleDateString(
                        "en-IN",
                        {
                          day: "numeric",
                          month: "short",
                        },
                      )}
                    </p>
                  </div>

                  <div className="flex items-center gap-2">
                    <Icon
                      size={22}
                      className="shrink-0 text-[var(--primary)]"
                    />
                    <span className="hidden text-xs muted-text sm:inline">
                      {condition.label}
                    </span>
                  </div>

                  <div className="flex items-center justify-end gap-2 text-xs font-semibold">
                    <span className="muted-text">
                      {day.min != null ? `${Math.round(day.min)}°` : "--"}
                    </span>

                    <div className="h-1.5 w-12 overflow-hidden rounded-full bg-[var(--surface-muted)] sm:w-20">
                      <div className="h-full w-2/3 rounded-full bg-[var(--primary)]" />
                    </div>

                    <span>
                      {day.max != null ? `${Math.round(day.max)}°` : "--"}
                    </span>
                  </div>

                  <div className="hidden items-center justify-end gap-2 text-xs muted-text sm:flex">
                    <Droplets size={14} className="text-blue-500" />
                    {day.rain != null ? `${day.rain}%` : "--"}
                    <span className="text-[10px]">rain</span>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          <p className="py-8 text-center text-sm muted-text">
            Daily forecast is currently unavailable.
          </p>
        )}
      </section>

      {/* Sunrise and sunset */}
      {dailyItems[0] && (
        <section className="grid gap-4 sm:grid-cols-2">
          <div className="dashboard-card flex items-center gap-4 p-5">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-amber-100 text-amber-600 dark:bg-amber-950/40 dark:text-amber-300">
              <Sunrise size={25} />
            </div>

            <div>
              <p className="text-xs muted-text">Sunrise</p>
              <p className="mt-1 text-lg font-bold">
                {formatTime(dailyItems[0].sunrise)}
              </p>
            </div>
          </div>

          <div className="dashboard-card flex items-center gap-4 p-5">
            <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-orange-100 text-orange-600 dark:bg-orange-950/40 dark:text-orange-300">
              <Sunset size={25} />
            </div>

            <div>
              <p className="text-xs muted-text">Sunset</p>
              <p className="mt-1 text-lg font-bold">
                {formatTime(dailyItems[0].sunset)}
              </p>
            </div>
          </div>
        </section>
      )}

      {/* Attribution */}
      <footer className="rounded-2xl bg-[var(--surface-muted)] p-4">
        <div className="flex items-start gap-3">
          <CloudSun
            size={18}
            className="mt-0.5 shrink-0 text-[var(--primary)]"
          />

          <div>
            <p className="text-xs font-semibold">Weather data source</p>
            <p className="mt-1 text-[10px] leading-5 muted-text">
              {weather.forecast.attribution ||
                "Weather data provided by Open-Meteo (CC BY 4.0)."}
            </p>

            <p className="mt-1 text-[10px] muted-text">
              Last fetched: {formatTime(weather.forecast.fetchedAt)}
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
