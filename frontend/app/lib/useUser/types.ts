export interface AddApplicationRequest {
  id: number;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface ApiState<RESPONSE> {
  data: RESPONSE | null;
  isMutating: boolean;
  isError?: boolean;
}
