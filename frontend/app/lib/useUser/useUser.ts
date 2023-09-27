import useSWR from "swr";
import axios from "axios";
import useSWRMutation from "swr/mutation";
import {
  AddApplicationRequest,
  API_ENDPOINTS,
  ApiState,
  ApplicationData,
  ChangePasswordRequest,
} from "@/app/lib";

async function putFetcher(
  url: string,
  { arg }: { arg: AddApplicationRequest | ChangePasswordRequest },
) {
  return axios.put(url, arg).then((r) => r.data);
}

async function patchFetcher(
  url: string,
  { arg }: { arg: AddApplicationRequest | ChangePasswordRequest },
) {
  return axios.patch(url, arg).then((r) => r.data);
}

async function basicFetcher(url: string) {
  return await axios.get(url).then((r) => r.data);
}

export function useUser() {
  const {
    data: savedAppsData,
    isLoading: isLoadingSavedApps,
    mutate,
  } = useSWR(API_ENDPOINTS.SAVED_APPLICATIONS, basicFetcher);

  const savedAppsApiState: ApiState<ApplicationData[]> = {
    data: savedAppsData,
    isMutating: isLoadingSavedApps,
  };

  const { trigger: usedAppsTrigger } = useSWRMutation(
    API_ENDPOINTS.SAVED_APPLICATIONS,
    putFetcher,
  );

  const { trigger: changePasswordTrigger } = useSWRMutation(
    API_ENDPOINTS.CHANGE_PASSWORD,
    patchFetcher,
  );

  const addToUsedApplications = (
    addApp: AddApplicationRequest,
    callback?: () => void,
  ) => {
    void usedAppsTrigger(addApp, {
      onSuccess: () => {
        callback?.();
        void mutate();
      },
    });
  };

  const changePassword = (
    changePasswordData: ChangePasswordRequest,
    callback?: () => void,
  ) => {
    void changePasswordTrigger(changePasswordData, { onSuccess: callback });
  };

  return {
    addToUsedApplications,
    changePassword,
    savedAppsApiState,
  };
}
