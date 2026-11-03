"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import {
  ArrowLeft,
  ArrowRight,
  CheckCircle2,
  Leaf,
  Loader2,
  MapPin,
  Sprout,
  
} from "lucide-react";

import {
  createFarmerProfile,
  getFarmerProfile,
  updateFarmerProfile,
} from "@/lib/api/farmer";

import { FarmerProfileInput } from "@/types/farmer";

const profileSchema = z.object({
  name: z.string().trim().min(2, "Enter your full name"),
  state: z.string().trim().min(2, "Enter your state"),
  district: z.string().trim().min(2, "Enter your district"),
  taluka: z.string().trim().min(2, "Enter your taluka"),
  preferredLanguage: z.enum(["kn", "hi", "en"]),
  cropName: z.string(),
  cropVariety: z.string(),
  growthStage: z.string(),
  farmSize: z.number().positive("Farm size must be greater than zero"),
  irrigationType: z.string().trim().min(2, "Enter your irrigation type"),
});

const inputClass =
  "mt-2 h-12 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] px-4 text-sm outline-none transition placeholder:text-[var(--muted)] focus:border-[var(--primary)] focus:ring-2 focus:ring-[var(--primary)]/10";

const initialValues: FarmerProfileInput = {
  name: "",
  state: "Karnataka",
  district: "Dharwad",
  taluka: "",
  preferredLanguage: "kn",
  cropName: "",
  cropVariety: "",
  growthStage: "",
  farmSize: 0,
  irrigationType: "",
};

function Field({
  label,
  error,
  children,
}: {
  label: string;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <label className="text-xs font-semibold">{label}</label>
      {children}
      {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
    </div>
  );
}

export default function FarmerProfileForm() {
  const router = useRouter();

  const [profileExists, setProfileExists] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [pageError, setPageError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FarmerProfileInput>({
    resolver: zodResolver(profileSchema),
    defaultValues: initialValues,
  });

  useEffect(() => {
    let active = true;

    async function loadProfile() {
      try {
        const profile = await getFarmerProfile();

        if (!active) return;

        reset({
          name: profile.name ?? "",
          state: profile.state ?? "Karnataka",
          district: profile.district ?? "Dharwad",
          taluka: profile.taluka ?? "",
          preferredLanguage: (profile.preferredLanguage || "kn") as
            | "kn"
            | "hi"
            | "en",
          cropName: profile.cropName ?? "",
          cropVariety: profile.cropVariety ?? "",
          growthStage: profile.growthStage ?? "",
          farmSize: profile.farmSize ?? 1,
          irrigationType: profile.irrigationType ?? "",
        });

        setProfileExists(true);
      } catch (error) {
        if (!active) return;

        if (axios.isAxiosError(error) && error.response?.status === 404) {
          setProfileExists(false);
        } else if (
          axios.isAxiosError(error) &&
          error.response?.status === 401
        ) {
          router.replace("/login");
        } else {
          setPageError(
            "Unable to load your profile. Please refresh and try again.",
          );
        }
      } finally {
        if (active) setLoadingProfile(false);
      }
    }

    loadProfile();

    return () => {
      active = false;
    };
  }, [reset, router]);

  const onSubmit = async (values: FarmerProfileInput) => {
    setPageError("");
    setSuccessMessage("");

    try {
      const payload: FarmerProfileInput = {
        ...values,
        name: values.name.trim(),
        state: values.state.trim(),
        district: values.district.trim(),
        taluka: values.taluka.trim(),
        cropName: values.cropName.trim(),
        cropVariety: values.cropVariety.trim(),
        growthStage: values.growthStage.trim(),
        irrigationType: values.irrigationType.trim(),
        farmSize: Number(values.farmSize),
      };

      if (profileExists) {
        await updateFarmerProfile(payload);
      } else {
        await createFarmerProfile(payload);
        setProfileExists(true);
      }

      setSuccessMessage("Your farmer profile has been saved successfully.");

      setTimeout(() => {
        router.push("/");
      }, 900);
    } catch (error) {
      const message = axios.isAxiosError(error)
        ? error.response?.data?.message
        : undefined;

      setPageError(
        typeof message === "string"
          ? message
          : "Unable to save your profile. Please try again.",
      );
    }
  };

  if (loadingProfile) {
    return (
      <div className="flex min-h-[70vh] items-center justify-center">
        <Loader2 className="animate-spin text-[var(--primary)]" size={30} />
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-[var(--background)] px-4 py-8 sm:px-6 lg:px-10">
      <div className="mx-auto max-w-4xl">
        <button
          type="button"
          onClick={() => router.push("/")}
          className="mb-7 inline-flex items-center gap-2 text-sm muted-text hover:text-[var(--primary)]"
        >
          <ArrowLeft size={16} />
          Back to dashboard
        </button>

        <div className="mb-8 flex items-start gap-4">
          <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl bg-[var(--primary-soft)] text-[var(--primary)]">
            <Leaf size={28} />
          </div>

          <div>
            <p className="text-xs font-bold uppercase tracking-[2px] text-[var(--primary)]">
              Farmer onboarding
            </p>
            <h1 className="mt-2 text-3xl font-bold tracking-tight">
              {profileExists ? "Your farm profile" : "Tell us about your farm"}
            </h1>
            <p className="mt-2 max-w-2xl text-sm leading-6 muted-text">
              Add your location and farming details to receive more relevant
              weather, crop and market recommendations.
            </p>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <section className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 sm:p-7">
            <div className="mb-6 flex items-center gap-3">
              <MapPin className="text-[var(--primary)]" size={21} />
              <div>
                <h2 className="font-bold">Personal and location details</h2>
                <p className="mt-1 text-xs muted-text">
                  Where is your farm located?
                </p>
              </div>
            </div>

            <div className="grid gap-5 sm:grid-cols-2">
              <Field label="Full name" error={errors.name?.message}>
                <input
                  {...register("name")}
                  className={inputClass}
                  placeholder="Your full name"
                />
              </Field>

              <Field label="State" error={errors.state?.message}>
                <input
                  {...register("state")}
                  className={inputClass}
                  placeholder="State"
                />
              </Field>

              <Field label="District" error={errors.district?.message}>
                <input
                  {...register("district")}
                  className={inputClass}
                  placeholder="District"
                />
              </Field>

              <Field label="Taluka" error={errors.taluka?.message}>
                <input
                  {...register("taluka")}
                  className={inputClass}
                  placeholder="Taluka"
                />
              </Field>

              <Field
                label="Preferred language"
                error={errors.preferredLanguage?.message}
              >
                <select
                  {...register("preferredLanguage")}
                  className={inputClass}
                >
                  <option value="kn">ಕನ್ನಡ - Kannada</option>
                  <option value="hi">हिन्दी - Hindi</option>
                  <option value="en">English</option>
                </select>
              </Field>

              <Field label="Farm size (acres)" error={errors.farmSize?.message}>
                <input
                  {...register("farmSize", { valueAsNumber: true })}
                  type="number"
                  min="0.01"
                  step="0.01"
                  className={inputClass}
                  placeholder="e.g. 2.5"
                />
              </Field>
            </div>
          </section>

          <section className="rounded-2xl border border-[var(--border)] bg-[var(--surface)] p-5 sm:p-7">
            <div className="mb-6 flex items-center gap-3">
              <Sprout className="text-[var(--primary)]" size={21} />
              <div>
                <h2 className="font-bold">Crop details</h2>
                <p className="mt-1 text-xs muted-text">
                  You can update these details as your crop changes.
                </p>
              </div>
            </div>

            <div className="grid gap-5 sm:grid-cols-2">
              <Field label="Primary crop">
                <input
                  {...register("cropName")}
                  className={inputClass}
                  placeholder="e.g. Maize, Cotton, Wheat"
                />
              </Field>

              <Field label="Crop variety">
                <input
                  {...register("cropVariety")}
                  className={inputClass}
                  placeholder="Crop variety"
                />
              </Field>

              <Field label="Current growth stage">
                <input
                  {...register("growthStage")}
                  className={inputClass}
                  placeholder="e.g. Sowing, Flowering"
                />
              </Field>

              <Field
                label="Irrigation type"
                error={errors.irrigationType?.message}
              >
                <input
                  {...register("irrigationType")}
                  className={inputClass}
                  placeholder="e.g. Rainfed, Drip, Borewell"
                />
              </Field>
            </div>
          </section>

          {pageError && (
            <p
              role="alert"
              className="rounded-xl bg-red-50 p-4 text-sm text-red-600"
            >
              {pageError}
            </p>
          )}

          {successMessage && (
            <p
              role="status"
              className="flex items-center gap-2 rounded-xl bg-green-50 p-4 text-sm text-green-700"
            >
              <CheckCircle2 size={18} />
              {successMessage}
            </p>
          )}

          <div className="flex flex-col-reverse justify-between gap-3 sm:flex-row sm:items-center">
            <p className="text-xs muted-text">
              Your profile information helps personalize your farm dashboard.
            </p>

            <button
              type="submit"
              disabled={isSubmitting}
              className="inline-flex h-12 items-center justify-center gap-2 rounded-xl bg-[var(--primary)] px-6 text-sm font-bold text-white transition hover:bg-[var(--primary-dark)] disabled:opacity-60"
            >
              {isSubmitting ? (
                <>
                  <Loader2 size={17} className="animate-spin" />
                  Saving...
                </>
              ) : (
                <>
                  {profileExists ? "Update profile" : "Save profile"}
                  <ArrowRight size={17} />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </main>
  );
}
