import { ApplicationData } from "@/app/lib";

export interface ApplicationsResponse {
  totalPages: number;
  apps: ApplicationData[];
}
