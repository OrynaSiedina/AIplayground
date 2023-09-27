"use client";
import { useAuth } from "@/app/lib";

export default function Logout() {
  const { logout } = useAuth();
  logout();
}
