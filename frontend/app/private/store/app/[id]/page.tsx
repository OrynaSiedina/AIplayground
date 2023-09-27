"use client";
import { API_ENDPOINTS, ApiState, ApplicationData, axios } from "@/app/lib";
import { ApplicationCard, CenteredCircularProgress } from "@/app/components";
import useSWR from "swr";

const fetcher = (url: string) => axios.get(url).then((res) => res.data);

export default function AppPage({ params }: { params: { id: string } }) {

  const { data, isLoading } = useSWR(`${API_ENDPOINTS.APPLICATIONS}/${params.id}`, fetcher)

  const appApiState:ApiState<ApplicationData> = {
    data: data,
    isMutating: isLoading,
  }

  return (
    <>
      {appApiState.isMutating && <CenteredCircularProgress />}
      {appApiState.data && !appApiState.isMutating && (
        <ApplicationCard appData={appApiState.data} variant="page" />
      )}
    </>
  );
}
