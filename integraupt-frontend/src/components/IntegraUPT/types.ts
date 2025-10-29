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

export interface Espacio {
  id: string;
  codigo: string;
  nombre: string;
  ubicacion: string;
  tipo: 'Laboratorio' | 'Aula';
  capacidad: number;
  equipamiento: string;
  facultad: string;
  escuela: string;
  estado: string; // 'Disponible' | 'En Mantenimiento'
}

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