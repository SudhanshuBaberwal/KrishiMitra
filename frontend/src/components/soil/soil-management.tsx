"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import {
  Activity,
  Beaker,
  CalendarDays,
  CheckCircle2,
  ClipboardList,
  FlaskConical,
  Loader2,
  MapPin,
  Pencil,
  Plus,
  RefreshCw,
  ShieldAlert,
  Trash2,
  X,
} from "lucide-react";

import {
  createSoilRecord,
  deleteSoilRecord,
  getSoilRecords,
  updateSoilRecord,
} from "@/lib/api/soil";

import type {
  SoilParameters,
  SoilRecord,
  SoilRecordInput,
  SoilRecordUpdateInput,
} from "@/types/soil";

const parameterFields: {
  key: keyof SoilParameters;
  label: string;
  unit: string;
  step: string;
  min: number;
}[] = [
  { key: "pH", label: "Soil pH", unit: "pH", step: "0.1", min: 0 },
  {
    key: "electricalConductivity",
    label: "Electrical Conductivity",
    unit: "dS/m",
    step: "0.01",
    min: 0,
  },
  {
    key: "organicCarbon",
    label: "Organic Carbon",
    unit: "%",
    step: "0.01",
    min: 0,
  },
  { key: "nitrogen", label: "Nitrogen (N)", unit: "kg/ha", step: "0.1", min: 0 },
  {
    key: "phosphorus",
    label: "Phosphorus (P)",
    unit: "kg/ha",
    step: "0.1",
    min: 0,
  },
  {
    key: "potassium",
    label: "Potassium (K)",
    unit: "kg/ha",
    step: "0.1",
    min: 0,
  },
  { key: "sulphur", label: "Sulphur", unit: "ppm", step: "0.1", min: 0 },
  { key: "zinc", label: "Zinc", unit: "ppm", step: "0.01", min: 0 },
  { key: "iron", label: "Iron", unit: "ppm", step: "0.1", min: 0 },
  { key: "manganese", label: "Manganese", unit: "ppm", step: "0.1", min: 0 },
  { key: "copper", label: "Copper", unit: "ppm", step: "0.01", min: 0 },
  { key: "boron", label: "Boron", unit: "ppm", step: "0.01", min: 0 },
];

const emptyParameters: SoilParameters = {
  pH: undefined,
  electricalConductivity: undefined,
  organicCarbon: undefined,
  nitrogen: undefined,
  phosphorus: undefined,
  potassium: undefined,
  sulphur: undefined,
  zinc: undefined,
  iron: undefined,
  manganese: undefined,
  copper: undefined,
  boron: undefined,
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

function formatDate(date: string) {
  if (!date) return "Not available";

  return new Date(`${date.slice(0, 10)}T00:00:00`).toLocaleDateString(
    "en-IN",
    {
      day: "numeric",
      month: "short",
      year: "numeric",
    },
  );
}

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

function SoilForm({
  initialData,
  onSubmit,
  onCancel,
  saving,
}: {
  initialData?: SoilRecord | null;
  onSubmit: (
    data: SoilRecordInput | SoilRecordUpdateInput,
  ) => Promise<void>;
  onCancel: () => void;
  saving: boolean;
}) {
  const [sampleDate, setSampleDate] = useState(
    initialData?.sampleDate?.slice(0, 10) ||
      new Date().toISOString().slice(0, 10),
  );
  const [sampleLocation, setSampleLocation] = useState(
    initialData?.sampleLocation || "",
  );
  const [labName, setLabName] = useState(initialData?.labName || "");
  const [reportReference, setReportReference] = useState(
    initialData?.reportReference || "",
  );
  const [notes, setNotes] = useState(initialData?.notes || "");
  const [parameters, setParameters] = useState<SoilParameters>(() => ({
    ...emptyParameters,
    ...(initialData?.parameters || {}),
  }));
  const [formError, setFormError] = useState("");

  function updateParameter(key: keyof SoilParameters, value: string) {
    setParameters((previous) => ({
      ...previous,
      [key]: value === "" ? undefined : Number(value),
    }));
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError("");

    if (!initialData && !sampleDate) {
      setFormError("Please select the soil sample date.");
      return;
    }

    if (!sampleLocation.trim()) {
      setFormError("Please enter the sample location.");
      return;
    }

    const hasParameter = Object.values(parameters).some(
      (value) => value !== undefined && Number.isFinite(value),
    );

    if (!hasParameter) {
      setFormError("Please enter at least one soil parameter.");
      return;
    }

    const cleanedParameters = Object.fromEntries(
      Object.entries(parameters).filter(
        ([, value]) => value !== undefined && Number.isFinite(value),
      ),
    ) as SoilParameters;

    try {
      if (initialData) {
        await onSubmit({
          sampleLocation: sampleLocation.trim(),
          labName: labName.trim() || undefined,
          reportReference: reportReference.trim() || undefined,
          parameters: cleanedParameters,
          notes: notes.trim() || undefined,
        });
      } else {
        await onSubmit({
          sampleDate,
          sampleLocation: sampleLocation.trim(),
          source: "FARMER_ENTERED",
          labName: labName.trim() || undefined,
          reportReference: reportReference.trim() || undefined,
          parameters: cleanedParameters,
          notes: notes.trim() || undefined,
        });
      }
    } catch (error) {
      setFormError(getErrorMessage(error));
    }
  }

  const inputClass =
    "mt-2 w-full rounded-xl border border-[var(--border)] bg-[var(--surface)] px-4 py-3 text-sm text-[var(--foreground)] outline-none transition focus:border-[var(--primary)]";

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="grid gap-4 sm:grid-cols-2">
        {!initialData && (
          <label className="text-xs font-semibold">
            Sample date *
            <input
              type="date"
              className={inputClass}
              value={sampleDate}
              max={new Date().toISOString().slice(0, 10)}
              onChange={(event) => setSampleDate(event.target.value)}
              required
            />
          </label>
        )}

        <label className="text-xs font-semibold">
          Sample location *
          <input
            className={inputClass}
            value={sampleLocation}
            onChange={(event) => setSampleLocation(event.target.value)}
            placeholder="e.g. North side of field"
            required
          />
        </label>

        <label className="text-xs font-semibold">
          Laboratory name
          <input
            className={inputClass}
            value={labName}
            onChange={(event) => setLabName(event.target.value)}
            placeholder="Optional"
          />
        </label>

        <label className="text-xs font-semibold">
          Report reference
          <input
            className={inputClass}
            value={reportReference}
            onChange={(event) => setReportReference(event.target.value)}
            placeholder="Optional report ID"
          />
        </label>
      </div>

      <div>
        <div className="mb-4">
          <h3 className="font-semibold">Soil test parameters</h3>
          <p className="mt-1 text-xs muted-text">
            Enter the values available in your soil test report. Leave
            unavailable values blank.
          </p>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {parameterFields.map((field) => (
            <label key={field.key} className="text-xs font-semibold">
              {field.label}
              <div className="relative">
                <input
                  type="number"
                  min={field.min}
                  step={field.step}
                  className={`${inputClass} pr-16`}
                  value={parameters[field.key] ?? ""}
                  onChange={(event) =>
                    updateParameter(field.key, event.target.value)
                  }
                  placeholder="Enter value"
                />
                <span className="pointer-events-none absolute right-3 top-1/2 mt-1 -translate-y-1/2 text-[10px] muted-text">
                  {field.unit}
                </span>
              </div>
            </label>
          ))}
        </div>
      </div>

      <label className="block text-xs font-semibold">
        Additional notes
        <textarea
          className={`${inputClass} min-h-24 resize-y`}
          value={notes}
          onChange={(event) => setNotes(event.target.value)}
          placeholder="Add observations about your soil..."
        />
      </label>

      {!initialData && (
        <div className="flex gap-3 rounded-xl bg-[var(--primary-soft)] p-4 text-xs">
          <ShieldAlert
            size={18}
            className="mt-0.5 shrink-0 text-[var(--primary)]"
          />
          <p className="muted-text">
            This record will be saved as farmer-entered and self-reported.
            Enter values from a soil test report whenever possible. Do not
            guess test values.
          </p>
        </div>
      )}

      {formError && (
        <p className="rounded-xl bg-red-500/10 px-4 py-3 text-xs text-red-600">
          {formError}
        </p>
      )}

      <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
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
          {initialData ? "Update Soil Record" : "Save Soil Record"}
        </button>
      </div>
    </form>
  );
}

function SoilRecordCard({
  record,
  onEdit,
  onDelete,
  deleting,
}: {
  record: SoilRecord;
  onEdit: (record: SoilRecord) => void;
  onDelete: (record: SoilRecord) => void;
  deleting: boolean;
}) {
  const parameters = parameterFields.filter(
    (field) => record.parameters?.[field.key] !== undefined,
  );

  return (
    <article className="dashboard-card space-y-5 p-5 sm:p-6">
      <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start">
        <div className="flex items-start gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <FlaskConical size={21} />
          </div>
          <div>
            <h3 className="font-bold">Soil Sample</h3>
            <p className="mt-1 flex items-center gap-1 text-xs muted-text">
              <MapPin size={13} />
              {record.sampleLocation || "Location not provided"}
            </p>
            <p className="mt-1 flex items-center gap-1 text-xs muted-text">
              <CalendarDays size={13} />
              {formatDate(record.sampleDate)}
            </p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded-full bg-amber-500/10 px-3 py-1.5 text-[10px] font-semibold text-amber-700 dark:text-amber-300">
            {formatLabel(record.verificationStatus || "SELF_REPORTED")}
          </span>

          <button
            type="button"
            onClick={() => onEdit(record)}
            aria-label="Edit soil record"
            className="rounded-lg border border-[var(--border)] p-2 hover:bg-[var(--surface-muted)]"
          >
            <Pencil size={15} />
          </button>

          <button
            type="button"
            onClick={() => onDelete(record)}
            disabled={deleting}
            aria-label="Delete soil record"
            className="rounded-lg border border-red-500/20 p-2 text-red-600 hover:bg-red-500/10 disabled:opacity-50"
          >
            {deleting ? (
              <Loader2 size={15} className="animate-spin" />
            ) : (
              <Trash2 size={15} />
            )}
          </button>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4">
        {parameters.map((field) => (
          <div
            key={field.key}
            className="rounded-xl bg-[var(--surface-muted)] p-3"
          >
            <p className="text-[10px] muted-text">{field.label}</p>
            <p className="mt-1 text-lg font-bold">
              {record.parameters[field.key]}
            </p>
            <p className="text-[10px] muted-text">{field.unit}</p>
          </div>
        ))}
      </div>

      {(record.labName || record.reportReference || record.notes) && (
        <div className="space-y-2 border-t border-[var(--border)] pt-4 text-xs">
          {record.labName && (
            <p>
              <span className="muted-text">Laboratory:</span> {record.labName}
            </p>
          )}
          {record.reportReference && (
            <p>
              <span className="muted-text">Report reference:</span>{" "}
              {record.reportReference}
            </p>
          )}
          {record.notes && (
            <p>
              <span className="muted-text">Notes:</span> {record.notes}
            </p>
          )}
        </div>
      )}

      <p className="text-[10px] muted-text">
        Source: {formatLabel(record.source || "FARMER_ENTERED")}
      </p>
    </article>
  );
}

export default function SoilManagement() {
  const [records, setRecords] = useState<SoilRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingRecord, setEditingRecord] = useState<SoilRecord | null>(null);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function fetchRecords() {
      try {
        const data = await getSoilRecords();

        if (!cancelled) {
          setRecords(data);
          setError("");
        }
      } catch (err) {
        if (!cancelled) {
          setError(getErrorMessage(err));
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    void fetchRecords();

    return () => {
      cancelled = true;
    };
  }, []);

  async function refreshRecords() {
    setRefreshing(true);
    setError("");

    try {
      const data = await getSoilRecords();
      setRecords(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setRefreshing(false);
    }
  }

  async function handleSave(
    data: SoilRecordInput | SoilRecordUpdateInput,
  ) {
    setSaving(true);

    try {
      if (editingRecord) {
        await updateSoilRecord(
          editingRecord.id,
          data as SoilRecordUpdateInput,
        );
      } else {
        await createSoilRecord(data as SoilRecordInput);
      }

      setShowForm(false);
      setEditingRecord(null);
      await refreshRecords();
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(record: SoilRecord) {
    const confirmed = window.confirm(
      "Are you sure you want to delete this soil record?",
    );

    if (!confirmed) return;

    setDeletingId(record.id);
    setError("");

    try {
      await deleteSoilRecord(record.id);
      setRecords((previous) =>
        previous.filter((item) => item.id !== record.id),
      );
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setDeletingId(null);
    }
  }

  function openAddForm() {
    setEditingRecord(null);
    setShowForm(true);
  }

  function openEditForm(record: SoilRecord) {
    setEditingRecord(record);
    setShowForm(true);
  }

  const latestRecord = [...records].sort(
    (a, b) =>
      new Date(b.sampleDate).getTime() - new Date(a.sampleDate).getTime(),
  )[0];

  return (
    <div className="space-y-7">
      <section className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <p className="text-xs font-medium muted-text">
            Farm management / Soil
          </p>
          <h1 className="mt-2 text-2xl font-bold tracking-tight sm:text-3xl">
            Soil Health <span>🌱</span>
          </h1>
          <p className="mt-2 text-sm muted-text">
            Store and review your farm soil test records and nutrient values.
          </p>
        </div>

        <div className="flex flex-wrap gap-2">
          <button
            type="button"
            onClick={() => void refreshRecords()}
            disabled={refreshing}
            className="flex items-center gap-2 rounded-xl border border-[var(--border)] px-4 py-3 text-xs font-semibold hover:bg-[var(--surface-muted)] disabled:opacity-60"
          >
            <RefreshCw
              size={15}
              className={refreshing ? "animate-spin" : ""}
            />
            Refresh
          </button>

          <button
            type="button"
            onClick={openAddForm}
            className="flex items-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-xs font-semibold text-white shadow-lg shadow-green-900/10 transition hover:bg-[var(--primary-dark)]"
          >
            <Plus size={17} />
            Add Soil Record
          </button>
        </div>
      </section>

      <section className="grid gap-4 sm:grid-cols-3">
        <div className="dashboard-card flex items-center gap-4 p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <ClipboardList size={22} />
          </div>
          <div>
            <p className="text-xs muted-text">Total records</p>
            <p className="mt-1 text-2xl font-bold">{records.length}</p>
          </div>
        </div>

        <div className="dashboard-card flex items-center gap-4 p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <Activity size={22} />
          </div>
          <div>
            <p className="text-xs muted-text">Latest sample</p>
            <p className="mt-1 text-base font-bold">
              {latestRecord ? formatDate(latestRecord.sampleDate) : "—"}
            </p>
          </div>
        </div>

        <div className="dashboard-card flex items-center gap-4 p-5">
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <Beaker size={22} />
          </div>
          <div>
            <p className="text-xs muted-text">Latest record source</p>
            <p className="mt-1 text-sm font-bold">
              {latestRecord
                ? formatLabel(latestRecord.source)
                : "No records"}
            </p>
          </div>
        </div>
      </section>

      {error && (
        <div className="flex flex-col justify-between gap-3 rounded-xl bg-red-500/10 p-4 text-sm text-red-600 sm:flex-row sm:items-center">
          <p>{error}</p>
          <button
            type="button"
            onClick={() => void refreshRecords()}
            className="flex w-fit items-center gap-2 font-semibold"
          >
            <RefreshCw size={14} /> Retry
          </button>
        </div>
      )}

      {showForm && (
        <section className="dashboard-card p-5 sm:p-7">
          <div className="mb-6 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-bold">
                {editingRecord ? "Update soil record" : "Add soil test record"}
              </h2>
              <p className="mt-1 text-xs muted-text">
                Enter the values available from your soil test.
              </p>
            </div>

            <button
              type="button"
              onClick={() => {
                setShowForm(false);
                setEditingRecord(null);
              }}
              aria-label="Close soil form"
              className="rounded-lg p-2 hover:bg-[var(--surface-muted)]"
            >
              <X size={19} />
            </button>
          </div>

          <SoilForm
            key={editingRecord?.id ?? "new-soil-record"}
            initialData={editingRecord}
            onSubmit={handleSave}
            onCancel={() => {
              setShowForm(false);
              setEditingRecord(null);
            }}
            saving={saving}
          />
        </section>
      )}

      <section className="space-y-4">
        <div>
          <h2 className="text-lg font-bold">Soil test history</h2>
          <p className="mt-1 text-xs muted-text">
            Your saved soil records, sorted by the latest sample date.
          </p>
        </div>

        {loading ? (
          <div className="dashboard-card flex min-h-48 items-center justify-center gap-3 p-8">
            <Loader2
              size={22}
              className="animate-spin text-[var(--primary)]"
            />
            <span className="text-sm muted-text">
              Loading soil records...
            </span>
          </div>
        ) : records.length === 0 ? (
          <div className="dashboard-card flex flex-col items-center justify-center px-6 py-14 text-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-[var(--primary-soft)] text-[var(--primary)]">
              <FlaskConical size={30} />
            </div>
            <h3 className="mt-5 font-bold">No soil records yet</h3>
            <p className="mt-2 max-w-sm text-sm muted-text">
              Add your first soil test record to keep your farm soil
              information in one place.
            </p>
            <button
              type="button"
              onClick={openAddForm}
              className="mt-5 flex items-center gap-2 rounded-xl bg-[var(--primary)] px-5 py-3 text-xs font-semibold text-white"
            >
              <Plus size={16} />
              Add First Record
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {[...records]
              .sort(
                (a, b) =>
                  new Date(b.sampleDate).getTime() -
                  new Date(a.sampleDate).getTime(),
              )
              .map((record) => (
                <SoilRecordCard
                  key={record.id}
                  record={record}
                  onEdit={openEditForm}
                  onDelete={handleDelete}
                  deleting={deletingId === record.id}
                />
              ))}
          </div>
        )}
      </section>

      <div className="flex items-start gap-3 rounded-xl border border-[var(--border)] p-4 text-xs muted-text">
        <CheckCircle2
          size={17}
          className="mt-0.5 shrink-0 text-[var(--primary)]"
        />
        <p>
          Soil values are displayed as entered. Nutrient interpretation and
          fertilizer recommendations should be based on reliable test
          results and appropriate local agricultural guidance.
        </p>
      </div>
    </div>
  );
}