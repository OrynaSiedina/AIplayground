export type ApplicationData = {
  id: number;
  name: string;
  description: string;
  html: string;
  css: string;
  javaScript: string;
  owner?: string;
  category?: string;
  public: boolean;
};

export interface CreateFormData {
  name: string;
  description: string;
  prompt: string;
}

export interface GeneratedAppData {
  data: ApplicationData | null;
  showSuccess: boolean;
}
