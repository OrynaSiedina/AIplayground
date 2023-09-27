import useSWR from "swr";
import { API_ENDPOINTS, ApiState, axios } from "@/app/lib";
import { ApplicationsResponse } from "./types";

async function fetcher(url: string) {
  return axios.get(url).then((res) => res.data);
}

export function useStore(category?: string, page?: number) {
  const url =
    API_ENDPOINTS.STORE +
    (category
      ? "?category=" + category + "&page=" + (page ?? 1) + "&size=9"
      : "?page=" + (page ?? 1) + "&size=9");

  const { data: categories } = useSWR(API_ENDPOINTS.CATEGORIES, fetcher);
  const { data: applications, isLoading: loaded } = useSWR(url, fetcher);

  const applicationsApiState: ApiState<ApplicationsResponse> = {
    data: applications,
    isMutating: loaded,
  };

  return {
    categories,
    applicationsApiState,
  };
}
