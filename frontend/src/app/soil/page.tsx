import DashboardShell from "@/components/layout/dashboard-shell";
import SoilManagement from "@/components/soil/soil-management";

export default function SoilPage() {
  return (
    <DashboardShell>
      <SoilManagement />
    </DashboardShell>
  );
}