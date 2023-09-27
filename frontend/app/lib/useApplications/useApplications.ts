import {
  API_ENDPOINTS,
  ApiState,
  ApplicationData,
  axios,
  CreateFormData,
} from "@/app/lib";
import useSWRMutation from "swr/mutation";
import useSWR from "swr";
import { useState } from "react";

interface FetcherParam<T> {
  arg?: { method?: string; data?: T };
}

interface FetchData {
  successCallback?: () => void;
  fetchKey: string | null;
  data: ApplicationData | null;
  fetchType: string | null;
}

async function fetcher<T>(url: string, param?: FetcherParam<T>) {
  if (param?.arg?.method) {
    return await axios({
      method: param?.arg.method,
      url,
      data: param?.arg.data,
    }).then((r) => r.data);
  } else {
    return await axios.get(url).then((r) => r.data);
  }
}

async function fetcherWithId({
  url,
  fetchType,
  data,
}: {
  url: string;
  fetchType: string;
  data: ApplicationData;
}) {
  return await axios({
    method: fetchType,
    url,
    data,
  }).then((r) => r.data);
}

export function useApplications() {
  const [fetchData, setFetchData] = useState<FetchData>({
    fetchKey: null,
    fetchType: null,
    data: null,
  });

  const {
    data: appData,
    trigger,
    isMutating,
    error,
  } = useSWRMutation(API_ENDPOINTS.APPLICATIONS, fetcher);

  const { data: createdApps, isLoading: isLoadingCreatedApps } = useSWR(
    API_ENDPOINTS.APPLICATIONS,
    fetcher,
  );

  const createApplication = (
    newApplicationData: CreateFormData,
    callback?: () => void,
  ) => {
    trigger(
      { method: "post", data: newApplicationData },
      { onSuccess: callback },
    );
  };

  const createAppResponse: ApiState<ApplicationData> = {
    data: appData,
    isMutating,
    isError: !!error,
  };

  const { mutate } = useSWR(API_ENDPOINTS.APPLICATIONS);
  useSWR(
    fetchData.fetchKey
      ? {
          url: fetchData.fetchKey,
          fetchType: fetchData.fetchType,
          data: fetchData.data,
        }
      : null,
    fetcherWithId,
      {
        onSuccess: () => {
          if (fetchData.successCallback) {
            fetchData.successCallback();
          }
          setFetchData({
            fetchKey: null,
            fetchType: null,
            data: null,
          });
        }
      }
  );

  const update = (appToUpdateData: ApplicationData, onSuccess: () => void) => {
    setFetchData({
      fetchKey: `${API_ENDPOINTS.APPLICATIONS}/${appToUpdateData.id}`,
      fetchType: "put",
      data: appToUpdateData,
      successCallback: () => {
        void mutate((apps: ApplicationData[]) => {
          const filteredApps = apps.filter((app) => app.id !== appToUpdateData.id);
          return [...filteredApps, appToUpdateData];
        });
        if (onSuccess) {
          onSuccess();
        }
      },
    });
  };

  const getAppById = (id: number) => {
    const app = createdApps?.find((app: ApplicationData) => app.id === id);
    const appApiState: ApiState<ApplicationData> = {
      data: app,
      isMutating: isLoadingCreatedApps,
    };
    return appApiState;
  };

  const getAllApps = () => {
    const createdAppsApiState: ApiState<ApplicationData[]> = {
      data: createdApps,
      isMutating: isLoadingCreatedApps,
    };
    return createdAppsApiState;
  };

  const deleteApp = (id: number, onSuccess?: () => void) => {
    setFetchData({
      fetchKey: `${API_ENDPOINTS.APPLICATIONS}/${id}`,
      fetchType: "delete",
      data: null,
      successCallback: () => {
        if (onSuccess) {
            onSuccess();
        }
        void mutate((apps: ApplicationData[]) => {
          const filteredApps = apps.filter((app) => app.id !== id);
          return [...filteredApps];
        });
      },
    });
  }

  return {
    update,
    createApplication,
    createAppResponse,
    getAllApps,
    getAppById,
    deleteApp
  };
}
