// moved out from components to lib folder, because its not a component

export const saveTokens = (access_token: string, refresh_token?: string) => {
  try {
    localStorage.setItem("accessToken", access_token);

    if (refresh_token) {
      localStorage.setItem("refreshToken", refresh_token);
    }
  } catch (error) {
    console.error(error);
  }
};
