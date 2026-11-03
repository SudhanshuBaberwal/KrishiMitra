
import { Sprout, ArrowUpRight } from "lucide-react";

const crops = [
  { name: "Maize", variety: "Hybrid", stage: "Vegetative", progress: 68 },
  { name: "Cotton", variety: "BT Cotton", stage: "Flowering", progress: 82 },
  { name: "Paddy", variety: "Basmati", stage: "Seedling", progress: 32 },
];

export default function CropCard() {
  return (
    <section id="crops" className="dashboard-card p-5">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-sm font-bold">My Crops</h3>
          <p className="mt-1 text-[11px] muted-text">
            Your crop growth overview
          </p>
        </div>
        <a
          href="#all-crops"
          className="flex items-center gap-1 text-xs font-semibold text-[var(--primary)]"
        >
          View all <ArrowUpRight size={14} />
        </a>
      </div>

      <div className="mt-5 space-y-5">
        {crops.map((crop) => (
          <div key={crop.name}>
            <div className="mb-2 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
                  <Sprout size={18} />
                </div>
                <div>
                  <p className="text-xs font-bold">{crop.name}</p>
                  <p className="mt-1 text-[10px] muted-text">
                    {crop.variety} · {crop.stage}
                  </p>
                </div>
              </div>
              <span className="text-xs font-bold">{crop.progress}%</span>
            </div>

            <div className="h-2 overflow-hidden rounded-full bg-[var(--surface-muted)]">
              <div
                className="h-full rounded-full bg-[var(--primary)]"
                style={{ width: `${crop.progress}%` }}
              />
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
