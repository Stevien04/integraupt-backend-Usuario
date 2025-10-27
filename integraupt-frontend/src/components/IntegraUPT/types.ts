export interface Profile {
  id: string;
  name: string;
  email: string;
  career: string;
  semester: number;
  profileImage: string;
  skills: string[];
  interests: string[];
  achievements: string[];
  teams: string[];
  isOnline: boolean;
  bio?: string;
  gpa?: number;
  phone?: string;
  socialLinks?: {
    linkedin?: string;
    github?: string;
    portfolio?: string;
  };
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}