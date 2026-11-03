"use client";

import { useEffect, useState } from "react";
import {
  Cloud,
  CloudRain,
  CloudSun,
  Droplets,
  MapPin,
  Sun,
  Wind,
} from "lucide-react";
import Link from "next/link";

import { getCurrentWeather } from "@/lib/api/weather";
import type { WeatherResponse } from "@/types/weather";

function getCondition(code?: number) {
  if (code === 0 || code === 1) return "Clear sky";
  if (code === 2) return "Partly cloudy";
  if (code === 3) return "Overcast";
  if (code === 45 || code === 48) return "Foggy";
  if (code !== undefined && code >= 51 && code <= 67) return "Drizzle / Rain";
  if (code !== undefined && code >= 80 && code <= 82) return "Rain showers";
  if (code !== undefined && code >= 95) return "Thunderstorm";
  return "Cloudy";
}

export default function WeatherCard() {
  const [weather, setWeather] = useState<WeatherResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let active = true;

    getCurrentWeather()
      .then((data) => {
        if (active) setWeather(data);
      })
      .catch((error) => {
        console.error("Dashboard weather error:", error);
        if (active) setFailed(true);
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  const current = weather?.current;
  const location = weather?.location;

  const condition = getCondition(current?.weather_code);

  const WeatherIcon =
    current?.weather_code === 0 || current?.weather_code === 1
      ? Sun
      : current?.weather_code === 2
        ? CloudSun
        : current?.weather_code !== undefined &&
            current.weather_code >= 51
          ? CloudRain
          : Cloud;

  return (
    <section id="weather" className="dashboard-card overflow-hidden">
      <div className="flex items-center justify-between p-5 pb-2">
        <div>
          <h3 className="text-sm font-bold">Weather Overview</h3>
          <p className="mt-1 text-[11px] muted-text">
            Local weather conditions
          </p>
        </div>

        <CloudSun size={21} className="text-[var(--primary)]" />
      </div>

      <div className="px-5 pb-5">
        <div className="mt-4 flex items-center justify-between rounded-2xl bg-[var(--primary-soft)] p-4">
          <div>
            <div className="flex items-center gap-1 text-xs muted-text">
              <MapPin size={13} />
              {location?.name
                ? `${location.name}, ${location.state ?? "India"}`
                : "Farm location"}
            </div>

            {loading ? (
              <div className="mt-3 h-10 w-24 animate-pulse rounded-lg bg-[var(--border)]" />
            ) : failed ? (
              <p className="mt-3 text-sm font-semibold">
                Weather unavailable
              </p>
            ) : (
              <>
                <h2 className="mt-2 text-4xl font-bold">
                  {current?.temperature_2m != null
                    ? `${Math.round(current.temperature_2m)}°`
                    : "--"}
                </h2>
                <p className="mt-1 text-xs font-medium">{condition}</p>
              </>
            )}
          </div>

          <WeatherIcon
            size={65}
            strokeWidth={1.2}
            className="text-amber-500"
          />
        </div>

        <div className="mt-5 grid grid-cols-3 gap-2">
          {[
            {
              icon: Droplets,
              label: "Humidity",
              value:
                current?.relative_humidity_2m != null
                  ? `${current.relative_humidity_2m}%`
                  : "--",
            },
            {
              icon: Wind,
              label: "Wind",
              value:
                current?.wind_speed_10m != null
                  ? `${Math.round(current.wind_speed_10m)} km/h`
                  : "--",
            },
            {
              icon: CloudRain,
              label: "Rain",
              value:
                current?.precipitation != null
                  ? `${current.precipitation} mm`
                  : "--",
            },
          ].map((item) => {
            const Icon = item.icon;

            return (
              <div key={item.label} className="text-center">
                <Icon size={17} className="mx-auto mb-2 muted-text" />
                <p className="text-xs font-bold">
                  {loading ? "--" : item.value}
                </p>
                <p className="mt-1 text-[10px] muted-text">{item.label}</p>
              </div>
            );
          })}
        </div>

        <Link
          href="/weather"
          className="mt-5 flex items-center justify-center rounded-xl border border-[var(--border)] px-4 py-3 text-xs font-semibold text-[var(--primary)] transition hover:bg-[var(--primary-soft)]"
        >
          View detailed forecast →
        </Link>
      </div>
    </section>
  );
}