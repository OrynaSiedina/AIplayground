// Note: This file is used to define the API endpoints
// Its a good idea to define the API endpoints in a separate file, so that you can easily change the API endpoints in the future
// it also prevents from having to hardcode the API endpoints in multiple places, typos are less likely to happen etc.

export const API_ENDPOINTS = {
  LOGIN: "auth/login",
  REGISTER: "auth/register",
  REFRESH: "auth/refresh",
  APPLICATIONS: "applications",
  SAVED_APPLICATIONS: "user/applications",
  STORE: "store",
  CATEGORIES: "store/categories",
  VERIFY: "auth/verify",
  CHANGE_PASSWORD: "user/password",
} as const;

export const PUBLIC_API_ENDPOINTS = [
  API_ENDPOINTS.LOGIN,
  API_ENDPOINTS.REGISTER,
  API_ENDPOINTS.REFRESH,
  API_ENDPOINTS.VERIFY,
] as string[];
