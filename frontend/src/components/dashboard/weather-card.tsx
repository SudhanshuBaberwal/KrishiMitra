
import {
  CloudSun,
  Droplets,
  Wind,
  Sun,
  MapPin,
} from "lucide-react";

export default function WeatherCard() {
  return (
    <section id="weather" className="dashboard-card overflow-hidden">
      <div className="flex items-center justify-between p-5 pb-2">
        <div>
          <h3 className="text-sm font-bold">Weather Overview</h3>
          <p className="mt-1 text-[11px] muted-text">Local weather conditions</p>
        </div>
        <CloudSun size={21} className="text-[var(--primary)]" />
      </div>

      <div className="px-5 pb-5">
        <div className="mt-4 flex items-center justify-between rounded-2xl bg-[var(--primary-soft)] p-4">
          <div>
            <div className="flex items-center gap-1 text-xs muted-text">
              <MapPin size={13} />
              Dharwad, Karnataka
            </div>
            <h2 className="mt-2 text-4xl font-bold">28°</h2>
            <p className="mt-1 text-xs font-medium">Partly Cloudy</p>
          </div>
          <Sun size={65} strokeWidth={1.2} className="text-amber-500" />
        </div>

        <div className="mt-5 grid grid-cols-3 gap-2">
          {[
            { icon: Droplets, label: "Humidity", value: "68%" },
            { icon: Wind, label: "Wind", value: "12 km/h" },
            { icon: CloudSun, label: "Rain", value: "10%" },
          ].map((item) => {
            const Icon = item.icon;
            return (
              <div key={item.label} className="text-center">
                <Icon size={17} className="mx-auto mb-2 muted-text" />
                <p className="text-xs font-bold">{item.value}</p>
                <p className="mt-1 text-[10px] muted-text">{item.label}</p>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
