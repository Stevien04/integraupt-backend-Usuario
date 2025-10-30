import { espaciosService } from './espaciosService';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

export interface ReservaUsuario {
  id: number;
  usuarioId: number;
  espacioId: number;
  espacioNombre: string;
  espacioCodigo: string;
  bloqueId?: number | null;
  bloqueNombre?: string | null;
  horaInicio?: string | null;
  horaFin?: string | null;
  estado: string;
  fechaReserva: string;
  fechaSolicitud?: string | null;
  descripcion?: string | null;
  motivo?: string | null;
}

export interface CrearReservaPayload {
  usuarioId: number;
  espacioId: number;
  fechaReserva: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  motivo?: string;
}

export interface ActualizarReservaPayload extends CrearReservaPayload {
  reservaId: number;
}

export interface EspacioActivo {
  id: number;
  codigo: string;
  nombre: string;
  tipo: string;
  capacidad: number;
  equipamiento?: string | null;
  facultadId: number;
  escuelaId: number;
  estado: number;
}

type ReservaUsuarioResponse = {
  idReserva: number;
  usuarioId: number;
  espacioId: number;
  espacioNombre: string;
  espacioCodigo: string;
  bloqueId?: number | null;
  bloqueNombre?: string | null;
  horaInicio?: string | null;
  horaFin?: string | null;
  estado: string;
  fechaReserva: string;
  fechaSolicitud?: string | null;
  descripcion?: string | null;
  motivo?: string | null;
};

type EspacioResponse = {
  idEspacio: number;
  codigo: string;
  nombre: string;
  tipo: string;
  capacidad: number;
  equipamiento?: string | null;
  facultadId: number;
  escuelaId: number;
  estado: number;
};

const mapTime = (value?: string | null) => {
  if (!value) return null;
  return value.length >= 5 ? value.slice(0, 5) : value;
};

const mapReserva = (reserva: ReservaUsuarioResponse): ReservaUsuario => ({
  id: reserva.idReserva,
  usuarioId: reserva.usuarioId,
  espacioId: reserva.espacioId,
  espacioNombre: reserva.espacioNombre,
  espacioCodigo: reserva.espacioCodigo,
  bloqueId: reserva.bloqueId ?? null,
  bloqueNombre: reserva.bloqueNombre ?? null,
  horaInicio: mapTime(reserva.horaInicio),
  horaFin: mapTime(reserva.horaFin),
  estado: reserva.estado,
  fechaReserva: reserva.fechaReserva,
  fechaSolicitud: reserva.fechaSolicitud ?? null,
  descripcion: reserva.descripcion ?? null,
  motivo: reserva.motivo ?? null,
});

const mapEspacio = (espacio: EspacioResponse): EspacioActivo => ({
  id: espacio.idEspacio,
  codigo: espacio.codigo,
  nombre: espacio.nombre,
  tipo: espacio.tipo,
  capacidad: espacio.capacidad,
  equipamiento: espacio.equipamiento ?? null,
  facultadId: espacio.facultadId,
  escuelaId: espacio.escuelaId,
  estado: espacio.estado,
});

const buildUrl = (path: string, params?: Record<string, string | number | undefined | null>) => {
  const url = new URL(path, API_BASE_URL);
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.append(key, String(value));
      }
    });
  }
  return url.toString();
};

const handleResponse = async <T>(response: Response): Promise<T> => {
  if (!response.ok) {
    let message = `Error ${response.status}`;
    try {
      const data = await response.json();
      if (typeof data === 'string') {
        message = data;
      } else if (data?.message) {
        message = data.message;
      } else if (data?.error) {
        message = data.error;
      }
    } catch (error) {
      const text = await response.text();
      if (text) {
        message = text;
      }
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
};

class ReservasService {
  async getReservasPorUsuario(usuarioId: number): Promise<ReservaUsuario[]> {
    const url = buildUrl('/api/reservas/usuario/' + usuarioId);
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const data = await handleResponse<ReservaUsuarioResponse[]>(response);
    return data.map(mapReserva);
  }

  async getEspaciosActivos(): Promise<EspacioActivo[]> {
    const url = buildUrl('/api/reservas/espacios');
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    const data = await handleResponse<EspacioResponse[]>(response);
    return data.map(mapEspacio);
  }

  async crearReserva(payload: CrearReservaPayload): Promise<ReservaUsuario> {
    const url = buildUrl('/api/reservas');
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const data = await handleResponse<ReservaUsuarioResponse>(response);
    return mapReserva(data);
  }

  async actualizarReserva(reservaId: number, payload: ActualizarReservaPayload): Promise<ReservaUsuario> {
    const url = buildUrl(`/api/reservas/${reservaId}`);
    const response = await fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    const data = await handleResponse<ReservaUsuarioResponse>(response);
    return mapReserva(data);
  }

  async actualizarEstado(reservaId: number, estado: string, motivo?: string, usuarioId?: number): Promise<ReservaUsuario> {
    const url = buildUrl(`/api/reservas/${reservaId}/estado`, { usuarioId });
    const response = await fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ estado, motivo }),
    });

    const data = await handleResponse<ReservaUsuarioResponse>(response);
    return mapReserva(data);
  }

  async eliminarReserva(reservaId: number, usuarioId?: number): Promise<void> {
    const url = buildUrl(`/api/reservas/${reservaId}`, { usuarioId });
    const response = await fetch(url, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    await handleResponse<void>(response);
  }
}

export const reservasService = new ReservasService();

export const facultadNombre = (facultadId?: number) => espaciosService.getFacultadNameById(facultadId ?? 0);
export const escuelaNombre = (escuelaId?: number) => espaciosService.getEscuelaNameById(escuelaId ?? 0);