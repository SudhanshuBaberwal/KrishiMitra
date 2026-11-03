
import ProtectedRoute from "@/components/auth/protected-route";
import FarmerProfileForm from "@/components/farmer/farmer-profile-form";

export default function ProfilePage() {
  return (
    <ProtectedRoute>
      <FarmerProfileForm />
    </ProtectedRoute>
  );
}
