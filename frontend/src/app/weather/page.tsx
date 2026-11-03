import DashboardShell from "@/components/layout/dashboard-shell";
import WeatherDashboard from "@/components/weather/weather-dashboard";

export default function WeatherPage() {
  return (
    <DashboardShell>
      <WeatherDashboard />
    </DashboardShell>
  );
}