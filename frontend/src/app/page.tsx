
import {
  Sprout,
  Droplets,
  LandPlot,
  TrendingUp,
  ArrowUpRight,
  CalendarDays,
  Bot,
  ChevronRight,
  Leaf,
} from "lucide-react";

import DashboardShell from "@/components/layout/dashboard-shell";
import StatCard from "@/components/dashboard/stat-card";
import WeatherCard from "@/components/dashboard/weather-card";
import CropCard from "@/components/dashboard/crop-card";

export default function Home() {
  return (
    <DashboardShell>
      <div className="space-y-7">
        {/* Welcome */}
        <section className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
          <div>
            <p className="text-xs font-medium muted-text">
              Monday, November 3, 2026
            </p>
            <h1 className="mt-2 text-2xl font-bold tracking-tight sm:text-3xl">
              Good morning, Farmer <span>🌱</span>
            </h1>
            <p className="mt-2 text-sm muted-text">
              Here&apos;s what&apos;s happening with your farm today.
            </p>
          </div>

          <button className="flex w-fit items-center gap-2 rounded-xl bg-[var(--primary)] px-4 py-3 text-xs font-semibold text-white shadow-lg shadow-green-900/10 transition hover:bg-[var(--primary-dark)]">
            <CalendarDays size={16} />
            View farming schedule
          </button>
        </section>

        {/* Location */}
        <div className="flex items-center gap-2 text-xs muted-text">
          <LandPlot size={15} className="text-[var(--primary)]" />
          Dharwad District, Karnataka
          <span className="rounded-md bg-[var(--primary-soft)] px-2 py-1 text-[10px] font-semibold text-[var(--primary)]">
            Farmer dashboard
          </span>
        </div>

        {/* Stats */}
        <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard
            title="Active Crops"
            value="03"
            subtitle="Across your farmland"
            icon={<Sprout size={20} />}
          />
          <StatCard
            title="Farm Area"
            value="5.5 Acres"
            subtitle="Total registered area"
            icon={<LandPlot size={20} />}
          />
          <StatCard
            title="Soil Health"
            value="Good"
            subtitle="Based on latest record"
            icon={<Droplets size={20} />}
            trend="Healthy"
          />
          <StatCard
            title="Market Trend"
            value="Positive"
            subtitle="Sample market indicator"
            icon={<TrendingUp size={20} />}
            trend="2.4%"
          />
        </section>

        {/* Main content */}
        <section className="grid gap-5 xl:grid-cols-[1.15fr_0.85fr]">
          <CropCard />
          <WeatherCard />
        </section>

        {/* Advisory */}
        <section
          id="advisories"
          className="dashboard-card overflow-hidden p-5 sm:p-6"
        >
          <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
            <div>
              <div className="flex items-center gap-2">
                <Leaf size={19} className="text-[var(--primary)]" />
                <h3 className="text-sm font-bold">Farming Insights</h3>
              </div>
              <p className="mt-1 text-xs muted-text">
                Recommendations and updates for your farm
              </p>
            </div>
            <span className="w-fit rounded-lg bg-amber-50 px-3 py-1.5 text-[10px] font-semibold text-amber-700">
              Sample advisory
            </span>
          </div>

          <div className="mt-5 flex flex-col gap-4 rounded-2xl bg-[var(--surface-muted)] p-4 sm:flex-row sm:items-center sm:p-5">
            <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
              <Sprout size={24} />
            </div>
            <div className="flex-1">
              <h4 className="text-sm font-bold">
                Keep monitoring your crop growth
              </h4>
              <p className="mt-1 text-xs leading-5 muted-text">
                Check soil moisture and local weather conditions before
                planning your next irrigation activity.
              </p>
            </div>
            <button className="flex items-center gap-1 text-xs font-semibold text-[var(--primary)]">
              Read more <ChevronRight size={15} />
            </button>
          </div>
        </section>

        {/* AI Assistant */}
        <section
          id="assistant"
          className="relative overflow-hidden rounded-2xl bg-[#174f31] p-6 text-white sm:p-8"
        >
          <div className="pointer-events-none absolute -right-10 -top-20 h-64 w-64 rounded-full border-[35px] border-white/[0.06]" />
          <div className="relative flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
            <div className="max-w-xl">
              <div className="mb-3 flex items-center gap-2 text-xs font-semibold text-green-200">
                <Bot size={17} />
                KRISHIMITRA AI ASSISTANT
              </div>
              <h3 className="text-xl font-bold sm:text-2xl">
                Have a question about your farm?
              </h3>
              <p className="mt-2 text-sm leading-6 text-green-100/75">
                Get agricultural guidance in Kannada, Hindi or English with
                your AI farming assistant.
              </p>
            </div>

            <button className="flex w-fit shrink-0 items-center gap-2 rounded-xl bg-white px-5 py-3 text-xs font-bold text-[#174f31] transition hover:bg-green-50">
              Ask KrishiMitra <ArrowUpRight size={16} />
            </button>
          </div>
        </section>

        <p className="pb-2 text-center text-[10px] muted-text">
          Demo dashboard · Sample values only · Live backend integration coming next
        </p>
      </div>
    </DashboardShell>
  );
}
