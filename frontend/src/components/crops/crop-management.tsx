"use client";

import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import {
  Sprout,
  Plus,
  Pencil,
  Trash2,
  Loader2,
  RefreshCw,
  X,
  CalendarDays,
  MapPin,
  Leaf,
} from "lucide-react";

import { createCrop, deleteCrop, getCrops, updateCrop } from "@/lib/api/crop";

import type {
  AreaUnit,
  CropGrowthStage,
  CropInput,
  CropSeason,
  FarmerCrop,
} from "@/types/crop";

const seasons: { label: string; value: CropSeason }[] = [
  { label: "Kharif", value: "KHARIF" },
  { label: "Rabi", value: "RABI" },
  { label: "Zaid", value: "ZAID" },
  { label: "Perennial", value: "PERENNIAL" },
];

const growthStages: { label: string; value: CropGrowthStage }[] = [
  { label: "Land Preparation", value: "LAND_PREPARATION" },
  { label: "Sowing", value: "SOWING" },
  { label: "Germination", value: "GERMINATION" },
  { label: "Seedling", value: "SEEDLING" },
  { label: "Vegetative", value: "VEGETATIVE" },
  { label: "Flowering", value: "FLOWERING" },
  { label: "Fruiting", value: "FRUITING" },
  { label: "Maturity", value: "MATURITY" },
  { label: "Harvested", value: "HARVESTED" },
];

const initialForm: CropInput = {
  cropCode: "",
  cropName: "",
  variety: "",
  season: "KHARIF",
  sowingDate: "",
  growthStage: "LAND_PREPARATION",
  area: 1,
  areaUnit: "ACRE",
  expectedHarvestDate: "",
};

function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    return (
      error.response?.data?.message ||
      error.message ||
      "Something went wrong. Please try again."
    );
  }

  return "Something went wrong. Please try again.";
}

function formatStage(stage: string) {
  return stage
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

function CropForm({
  initialData,
  onSubmit,
  onCancel,
  saving,
}: {
  initialData?: FarmerCrop | null;
  onSubmit: (data: CropInput) => Promise<void>;
  onCancel: () => void;
  saving: boolean;
}) {
  const [form, setForm] = useState<CropInput>(() =>
    initialData
      ? {
          cropCode: initialData.cropCode,
          cropName: initialData.cropName,
          variety: initialData.variety,
          season: initialData.season,
          sowingDate: initialData.sowingDate,
          growthStage: initialData.growthStage,
          area: initialData.area,
          areaUnit: initialData.areaUnit,
          expectedHarvestDate: initialData.expectedHarvestDate,
        }
      : initialForm,
  );

  const [formError, setFormError] = useState("");

  function updateField<K extends keyof CropInput>(
    field: K,
    value: CropInput[K],
  ) {
    setForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError("");

    if (!form.cropCode.trim() || !form.cropName.trim()) {
      setFormError("Crop code and crop name are required.");
      return;
    }

    if (!form.variety.trim()) {
      setFormError("Please enter the crop variety.");
      return;
    }

    if (!form.sowingDate || !form.expectedHarvestDate) {
      setFormError("Please select both sowing and expected harvest dates.");
      return;
    }

    if (form.area <= 0 || !Number.isFinite(form.area)) {
      setFormError("Please enter a valid farm area.");
      return;
    }

    if (form.expectedHarvestDate < form.sowingDate) {
      setFormError("Harvest date cannot be before sowing date.");
      return;
    }

    try {
      await onSubmit({
        ...form,
        cropCode: form.cropCode.trim(),
        cropName: form.cropName.trim(),
        variety: form.variety.trim(),
        area: Number(form.area),
      });
    } catch (error) {
      setFormError(getErrorMessage(error));
    }
  }

  const inputClass =
    "mt-2 w-full rounded-xl border border-[var(--border)] bg-[var(--surface)] px-4 py-3 text-sm text-[var(--foreground)] outline-none transition focus:border-[var(--primary)]";

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="grid gap-4 sm:grid-cols-2">
        <label className="text-xs font-semibold">
          Crop name *
          <input
            className={inputClass}
            value={form.cropName}
            onChange={(e) => updateField("cropName", e.target.value)}
            placeholder="e.g. Maize"
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Crop code *
          <input
            className={inputClass}
            value={form.cropCode}
            onChange={(e) => updateField("cropCode", e.target.value)}
            placeholder="e.g. MAIZE"
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Variety *
          <input
            className={inputClass}
            value={form.variety}
            onChange={(e) => updateField("variety", e.target.value)}
            placeholder="e.g. Hybrid"
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Season *
          <select
            className={inputClass}
            value={form.season}
            onChange={(e) =>
              updateField("season", e.target.value as CropSeason)
            }
          >
            {seasons.map((season) => (
              <option key={season.value} value={season.value}>
                {season.label}
              </option>
            ))}
          </select>
        </label>

        <label className="text-xs font-semibold">
          Sowing date *
          <input
            type="date"
            className={inputClass}
            value={form.sowingDate}
            onChange={(e) => updateField("sowingDate", e.target.value)}
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Expected harvest date *
          <input
            type="date"
            className={inputClass}
            value={form.expectedHarvestDate}
            onChange={(e) => updateField("expectedHarvestDate", e.target.value)}
            min={form.sowingDate || undefined}
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Growth stage *
          <select
            className={inputClass}
            value={form.growthStage}
            onChange={(e) =>
              updateField("growthStage", e.target.value as CropGrowthStage)
            }
          >
            {growthStages.map((stage) => (
              <option key={stage.value} value={stage.value}>
                {stage.label}
              </option>
            ))}
          </select>
        </label>

        <div className="grid grid-cols-[1fr_120px] gap-3">
          <label className="text-xs font-semibold">
            Farm area *
            <input
              type="number"
              min="0.01"
              step="0.01"
              className={inputClass}
              value={form.area}
              onChange={(e) => updateField("area", Number(e.target.value))}
              required
            />
          </label>

          <label className="text-xs font-semibold">
            Unit
            <select
              className={inputClass}
              value={form.areaUnit}
              onChange={(e) =>
                updateField("areaUnit", e.target.value as AreaUnit)
              }
            >
              <option value="ACRE">Acre</option>
              <option value="HECTARE">Hectare</option>
            </select>
          </label>
        </div>
      </div>

      {formError && (
        <p className="rounded-xl bg-red-500/10 px-4 py-3 text-xs text-red-600">
          {formError}
        </p>
      )}

      <div className="flex flex-col-reverse gap-3 pt-2 sm:flex-row sm:justify-end">
        <button
          type="button"
          onClick={onCancel}
          disabled={saving}
          className="rounded-xl border border-[var(--border)] px-5 py-3 text-xs font-semibold hover:bg-[var(--surface-muted)]"
        >
          Cancel
        </button>

        <button
          type="submit"
          disabled={saving}
          className="flex items-center justify-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-xs font-semibold text-white transition hover:bg-[var(--primary-dark)] disabled:opacity-60"
        >
          {saving && <Loader2 size={15} className="animate-spin" />}
          {initialData ? "Update Crop" : "Add Crop"}
        </button>
      </div>
    </form>
  );
}

export default function CropManagement() {
  const [crops, setCrops] = useState<FarmerCrop[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingCrop, setEditingCrop] = useState<FarmerCrop | null>(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const loadCrops = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const data = await getCrops();
      setCrops(data.filter((crop) => crop.active));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let cancelled = false;

    const fetchCrops = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getCrops();

        if (!cancelled) {
          setCrops(data);
        }
      } catch (error) {
        if (!cancelled) {
          setError(
            error instanceof Error ? error.message : "Failed to load crops.",
          );
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void fetchCrops();

    return () => {
      cancelled = true;
    };
  }, []);

  async function handleSave(data: CropInput) {
    setSaving(true);

    try {
      if (editingCrop) {
        await updateCrop(editingCrop.id, data);
      } else {
        await createCrop(data);
      }

      setShowForm(false);
      setEditingCrop(null);
      await loadCrops();
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(crop: FarmerCrop) {
    const confirmed = window.confirm(
      `Are you sure you want to delete ${crop.cropName}?`,
    );

    if (!confirmed) return;

    setDeletingId(crop.id);
    setError("");

    try {
      await deleteCrop(crop.id);
      setCrops((previous) => previous.filter((item) => item.id !== crop.id));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setDeletingId(null);
    }
  }

  function openAddForm() {
    setEditingCrop(null);
    setShowForm(true);
  }

  function openEditForm(crop: FarmerCrop) {
    setEditingCrop(crop);
    setShowForm(true);
  }

  const totalArea = crops.reduce((sum, crop) => {
    return sum + crop.area;
  }, 0);

  return (
    <div className="space-y-7">
      <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="text-xs font-medium muted-text">
            Farm management / Crops
          </p>
          <h1 className="mt-2 text-2xl font-bold tracking-tight sm:text-3xl">
            My Crops <span>🌱</span>
          </h1>
          <p className="mt-2 text-sm muted-text">
            Manage your crops, varieties, growing stages and farm area.
          </p>
        </div>

        <button
          onClick={openAddForm}
          className="flex w-fit items-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-xs font-semibold text-white shadow-lg shadow-green-900/10 transition hover:bg-[var(--primary-dark)]"
        >
          <Plus size={17} />
          Add New Crop
        </button>
      </section>

      <section className="grid gap-4 sm:grid-cols-2">
        <div className="dashboard-card flex items-center gap-4 p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <Sprout size={23} />
          </div>
          <div>
            <p className="text-xs muted-text">Active crops</p>
            <p className="mt-1 text-2xl font-bold">{crops.length}</p>
          </div>
        </div>

        <div className="dashboard-card flex items-center gap-4 p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <MapPin size={22} />
          </div>
          <div>
            <p className="text-xs muted-text">Combined registered area</p>
            <p className="mt-1 text-2xl font-bold">
              {totalArea.toFixed(2)}
              <span className="ml-1 text-xs font-medium muted-text">
                (mixed units)
              </span>
            </p>
          </div>
        </div>
      </section>

      {error && (
        <div className="flex flex-col justify-between gap-3 rounded-xl bg-red-500/10 p-4 text-sm text-red-600 sm:flex-row sm:items-center">
          <p>{error}</p>
          <button
            onClick={loadCrops}
            className="flex w-fit items-center gap-2 font-semibold"
          >
            <RefreshCw size={14} /> Retry
          </button>
        </div>
      )}

      {showForm && (
        <div className="dashboard-card p-5 sm:p-7">
          <div className="mb-6 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold">
                {editingCrop ? "Update crop details" : "Register a new crop"}
              </h2>
              <p className="mt-1 text-xs muted-text">
                Enter the details of your crop below.
              </p>
            </div>

            <button
              onClick={() => {
                setShowForm(false);
                setEditingCrop(null);
              }}
              aria-label="Close crop form"
              className="rounded-lg p-2 hover:bg-[var(--surface-muted)]"
            >
              <X size={19} />
            </button>
          </div>

          <CropForm
            key={editingCrop?.id ?? "new-crop"}
            initialData={editingCrop}
            onSubmit={handleSave}
            onCancel={() => {
              setShowForm(false);
              setEditingCrop(null);
            }}
            saving={saving}
          />
        </div>
      )}

      <section>
        <div className="mb-5 flex items-center justify-between">
          <div>
            <h2 className="text-lg font-bold">Registered crops</h2>
            <p className="mt-1 text-xs muted-text">
              Your crop records from KrishiMitra.
            </p>
          </div>

          <button
            onClick={loadCrops}
            disabled={loading}
            aria-label="Refresh crops"
            className="rounded-xl border border-[var(--border)] p-3 hover:bg-[var(--surface-muted)] disabled:opacity-50"
          >
            <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
          </button>
        </div>

        {loading ? (
          <div className="dashboard-card flex min-h-56 items-center justify-center">
            <div className="text-center">
              <Loader2
                size={28}
                className="mx-auto animate-spin text-[var(--primary)]"
              />
              <p className="mt-3 text-sm muted-text">Loading your crops...</p>
            </div>
          </div>
        ) : crops.length === 0 ? (
          <div className="dashboard-card flex min-h-64 flex-col items-center justify-center px-5 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-[var(--primary-soft)] text-[var(--primary)]">
              <Leaf size={30} />
            </div>
            <h3 className="mt-5 text-base font-bold">
              No crops registered yet
            </h3>
            <p className="mt-2 max-w-sm text-xs leading-5 muted-text">
              Add your first crop to start managing its growing stage, season
              and harvest information.
            </p>
            <button
              onClick={openAddForm}
              className="mt-5 flex items-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-xs font-semibold text-white"
            >
              <Plus size={16} /> Register your first crop
            </button>
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {crops.map((crop) => (
              <article
                key={crop.id}
                className="dashboard-card p-5 transition hover:-translate-y-0.5 hover:shadow-lg"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-3">
                    <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
                      <Sprout size={23} />
                    </div>
                    <div>
                      <h3 className="text-base font-bold">{crop.cropName}</h3>
                      <p className="mt-1 text-xs muted-text">{crop.variety}</p>
                    </div>
                  </div>

                  <span className="rounded-lg bg-[var(--primary-soft)] px-2.5 py-1 text-[10px] font-bold text-[var(--primary)]">
                    {crop.season}
                  </span>
                </div>

                <div className="mt-5 rounded-xl bg-[var(--surface-muted)] p-4">
                  <p className="text-[10px] font-semibold uppercase tracking-wider muted-text">
                    Growth stage
                  </p>
                  <p className="mt-1 text-sm font-bold">
                    {formatStage(crop.growthStage)}
                  </p>
                </div>

                <div className="mt-4 grid grid-cols-2 gap-3">
                  <div>
                    <p className="text-[10px] muted-text">Farm area</p>
                    <p className="mt-1 text-sm font-bold">
                      {crop.area} {crop.areaUnit.toLowerCase()}
                    </p>
                  </div>

                  <div>
                    <p className="text-[10px] muted-text">Crop code</p>
                    <p className="mt-1 text-sm font-bold">{crop.cropCode}</p>
                  </div>
                </div>

                <div className="mt-4 space-y-3 border-t border-[var(--border)] pt-4">
                  <div className="flex items-center gap-2 text-xs muted-text">
                    <CalendarDays size={15} />
                    <span>Sowing: {crop.sowingDate || "Not specified"}</span>
                  </div>
                  <div className="flex items-center gap-2 text-xs muted-text">
                    <CalendarDays size={15} />
                    <span>
                      Expected harvest:{" "}
                      {crop.expectedHarvestDate || "Not specified"}
                    </span>
                  </div>
                </div>

                <div className="mt-5 flex gap-2">
                  <button
                    onClick={() => openEditForm(crop)}
                    className="flex flex-1 items-center justify-center gap-2 rounded-xl border border-[var(--border)] px-3 py-2.5 text-xs font-semibold hover:bg-[var(--surface-muted)]"
                  >
                    <Pencil size={14} /> Edit
                  </button>

                  <button
                    onClick={() => handleDelete(crop)}
                    disabled={deletingId === crop.id}
                    aria-label={`Delete ${crop.cropName}`}
                    className="flex items-center justify-center gap-2 rounded-xl border border-red-500/20 px-3 py-2.5 text-xs font-semibold text-red-600 hover:bg-red-500/10 disabled:opacity-50"
                  >
                    {deletingId === crop.id ? (
                      <Loader2 size={14} className="animate-spin" />
                    ) : (
                      <Trash2 size={14} />
                    )}
                    Delete
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
