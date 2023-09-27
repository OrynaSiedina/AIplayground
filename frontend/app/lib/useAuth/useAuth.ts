import { useState } from "react";
import { API_ENDPOINTS, ApiState, axios, saveTokens } from "@/app/lib";
import { useRouter } from "next/navigation";
import useSWR, { mutate } from "swr";
import { AuthorizationRequest, LoginResponse } from "./types";

type LoginCallback = (response: LoginResponse) => void;

export interface FetchData<RESPONSE, CALLBACK = (response: RESPONSE) => void> {
  successCallback?: CALLBACK;
  fetchKey: string | null;
  data: AuthorizationRequest | null;
  fetchType: string | null;
}

async function fetcher({
  url,
  args,
  type,
}: {
  url: string;
  args: AuthorizationRequest | null;
  type: string;
}) {
  return axios({
    method: type,
    url: url,
    data: args,
  }).then((r) => r.data);
}

const INITIAL_STATE = {
  fetchKey: null,
  fetchType: null,
  data: {
    nickname: "",
    password: "",
    email: "",
  },
};

export function useAuth<RESPONSE>() {
  const router = useRouter();
  const [data, setData] =
    useState<
      FetchData<RESPONSE, LoginCallback | ((response: RESPONSE) => void)>
    >(INITIAL_STATE);

  const { isLoading, error } = useSWR(
    !!data.fetchKey
      ? { url: data.fetchKey, args: data.data, type: data.fetchType }
      : null,
    fetcher,
    {
      onSuccess: (response) => {
        if (data.successCallback) {
          data.successCallback(response);
        }
        setData(INITIAL_STATE);
      },
    },
  );

  const register = (
    newUserData: AuthorizationRequest,
    callback?: (response: RESPONSE) => void,
  ) => {
    setData({
      successCallback: callback,
      fetchKey: API_ENDPOINTS.REGISTER,
      data: newUserData,
      fetchType: "post",
    });
  };

  const login = (loginData: AuthorizationRequest) => {
    setData({
      successCallback: (response: LoginResponse) => {
        const { access_token, refresh_token } = response;
        if (access_token && refresh_token) {
          saveTokens(access_token, refresh_token);
          void router.push("/private");
        }
      },
      fetchKey: API_ENDPOINTS.LOGIN,
      data: loginData,
      fetchType: "post",
    });
  };

  const logout = () => {
    window.localStorage.clear();
    void mutate(() => true, undefined, { revalidate: false });
    void router.push("/authorization");
  };

  const verify = (url: string, successCallback?: () => void) => {
    setData({
      fetchKey: url,
      data: null,
      fetchType: "get",
      successCallback: successCallback,
    });
  };

  const verifyApiState: ApiState<void> = {
    data: null,
    isMutating: isLoading,
    isError: !!error,
  };

  const repeatVerify = (url: string, successCallback: () => void) => {
    setData({
      fetchKey: url,
      data: null,
      fetchType: "patch",
      successCallback: successCallback,
    });
  };

  return {
    register,
    login,
    logout,
    verify,
    repeatVerify,
    verifyApiState,
    isLoggedIn:
      typeof window !== "undefined" &&
      !!window.localStorage.getItem("accessToken") &&
      !!window.localStorage.getItem("refreshToken"),
  };
}
