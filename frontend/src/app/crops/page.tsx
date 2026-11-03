
import DashboardShell from "@/components/layout/dashboard-shell";
import CropManagement from "@/components/crops/crop-management";

export default function CropsPage() {
  return (
    <DashboardShell>
      <CropManagement />
    </DashboardShell>
  );
}
