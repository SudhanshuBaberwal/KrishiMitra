
import { ReactNode } from "react";
import { ArrowUpRight, ArrowDownRight } from "lucide-react";

type StatCardProps = {
  title: string;
  value: string;
  subtitle: string;
  icon: ReactNode;
  trend?: string;
  positive?: boolean;
};

export default function StatCard({
  title,
  value,
  subtitle,
  icon,
  trend,
  positive = true,
}: StatCardProps) {
  return (
    <div className="dashboard-card p-5 transition duration-200 hover:-translate-y-1 hover:shadow-lg hover:shadow-black/[0.03]">
      <div className="flex items-center justify-between">
        <p className="text-xs font-medium muted-text">{title}</p>
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
          {icon}
        </div>
      </div>

      <div className="mt-5 flex items-end justify-between gap-2">
        <div>
          <h3 className="text-2xl font-bold tracking-tight">{value}</h3>
          <p className="mt-1 text-[11px] muted-text">{subtitle}</p>
        </div>

        {trend && (
          <span
            className={`mb-1 flex items-center gap-1 rounded-lg px-2 py-1 text-[10px] font-bold ${
              positive
                ? "bg-[var(--primary-soft)] text-[var(--primary)]"
                : "bg-red-50 text-red-600"
            }`}
          >
            {positive ? (
              <ArrowUpRight size={13} />
            ) : (
              <ArrowDownRight size={13} />
            )}
            {trend}
          </span>
        )}
      </div>
    </div>
  );
}
