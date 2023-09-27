import axios from "axios";
import { API_ENDPOINTS, PUBLIC_API_ENDPOINTS, saveTokens } from "@/app/lib";

axios.defaults.baseURL = process.env.NEXT_PUBLIC_API_URL;

axios.interceptors.request.use(
  (config) => {
    const accessToken = localStorage.getItem("accessToken");
    // we do not want to add the token to requests for:
    // - login / register endpoints which are public
    // - refresh token endpoint which is private and requires a refresh token which is already preset by the response interceptor
    if (
      accessToken &&
      config.url &&
      !PUBLIC_API_ENDPOINTS.includes(config.url)
    ) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    error.message = error.response.data.message;

    // if the error is 401 or 403 and the original request was not a refresh token request (to avoid an infinite loop)
    // we try to refresh the access token
    if (
      (error.response.status === 401 || error.response.status === 403) &&
      !originalRequest._retry &&
      !PUBLIC_API_ENDPOINTS.includes(originalRequest.url)
    ) {
      originalRequest._retry = true;

      const response = await axios.get(API_ENDPOINTS.REFRESH, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("refreshToken")}`,
        },
      });
      if (response.status === 200) {
        saveTokens(response.data.access_token);

        originalRequest.headers["Authorization"] =
          "Bearer " + response.data.access_token;

        return axios(originalRequest);
      } else {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/authorization";
      }
    }
    throw error;
  },
);

export { axios };
