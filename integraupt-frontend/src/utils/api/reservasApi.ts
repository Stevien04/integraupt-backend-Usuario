const rawBaseUrl = import.meta.env.VITE_BACKEND_URL;
const API_BASE_URL = (rawBaseUrl ? rawBaseUrl.replace(/\/+$/, '') : 'http://localhost:8082');

const ADMIN_RESERVAS_ENDPOINT = `${API_BASE_URL}/api/admin/reservas`;
const PUBLIC_RESERVAS_ENDPOINT = `${API_BASE_URL}/api/reservas`;

export type EstadoReserva = 'Pendiente' | 'Aprobado' | 'Rechazado';

interface ReservaDto {
  id: string;
  usuarioId?: string | null;
  aprobadoPor?: string | null;
  espacioId?: string | null;
  tipo?: string | null;
  estado?: string | null;
  curso?: string | null;
  ciclo?: string | null;
  motivo?: string | null;
  motivoRechazo?: string | null;
  fecha?: string | null;
  horaInicio?: string | null;
  horaFin?: string | null;
  creadoEn?: string | null;
  actualizadoEn?: string | null;
}

export interface Reserva {
  id: string;
  usuarioId?: string;
  aprobadoPor?: string;
  espacioId?: string;
  tipo?: string;
  estado: EstadoReserva;
  curso?: string;
  ciclo?: string;
  motivo?: string;
  motivoRechazo?: string;
  fecha?: string;
  horaInicio?: string;
  horaFin?: string;
  creadoEn?: string;
  actualizadoEn?: string;
}

export interface CrearReservaPayload {
  usuarioId: string;
  espacioId?: string;
  tipo: string;
  curso?: string;
  ciclo?: string;
  motivo?: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
}

export type ResumenReservas = Record<EstadoReserva, number>;

type ActualizarEstadoPayload = {
  estado: EstadoReserva;
  motivo?: string;
  aprobadoPor?: string;
};

const normalizarEstado = (estado?: string | null): EstadoReserva => {
  const valor = (estado ?? '').toLowerCase();
  if (valor.startsWith('apro')) {
    return 'Aprobado';
  }
  if (valor.startsWith('rech')) {
    return 'Rechazado';
  }
  return 'Pendiente';
};

const sanitizarTexto = (valor?: string | null): string | undefined => {
  if (!valor) return undefined;
  const limpio = valor.trim();
  return limpio.length > 0 ? limpio : undefined;
};

const mapearReserva = (dto: ReservaDto): Reserva => ({
  id: dto.id,
  usuarioId: sanitizarTexto(dto.usuarioId),
  aprobadoPor: sanitizarTexto(dto.aprobadoPor),
  espacioId: sanitizarTexto(dto.espacioId),
  tipo: sanitizarTexto(dto.tipo),
  estado: normalizarEstado(dto.estado),
  curso: sanitizarTexto(dto.curso),
  ciclo: sanitizarTexto(dto.ciclo),
  motivo: sanitizarTexto(dto.motivo),
  motivoRechazo: sanitizarTexto(dto.motivoRechazo),
  fecha: sanitizarTexto(dto.fecha),
  horaInicio: sanitizarTexto(dto.horaInicio),
  horaFin: sanitizarTexto(dto.horaFin),
  creadoEn: sanitizarTexto(dto.creadoEn),
  actualizadoEn: sanitizarTexto(dto.actualizadoEn),
});

const manejarRespuesta = async <T>(respuesta: Response): Promise<T> => {
  if (!respuesta.ok) {
    const texto = await respuesta.text();
    throw new Error(texto || `Error en la solicitud (${respuesta.status})`);
  }
  return respuesta.json() as Promise<T>;
};

const asegurarHoraConSegundos = (hora: string): string => {
  if (!hora) return hora;
  return hora.length === 5 ? `${hora}:00` : hora;
};

export const fetchReservasAdmin = async (): Promise<Reserva[]> => {
  const respuesta = await fetch(ADMIN_RESERVAS_ENDPOINT);
  const datos = await manejarRespuesta<ReservaDto[]>(respuesta);
  return datos.map(mapearReserva);
};

export const fetchResumenReservas = async (): Promise<ResumenReservas> => {
  const respuesta = await fetch(`${ADMIN_RESERVAS_ENDPOINT}/resumen`);
  const datos = await manejarRespuesta<Record<string, number | string>>(respuesta);
  const resumen: ResumenReservas = {
    Pendiente: 0,
    Aprobado: 0,
    Rechazado: 0,
  };

  Object.entries(datos).forEach(([estado, conteo]) => {
    const clave = normalizarEstado(estado);
    const valor = typeof conteo === 'number' ? conteo : Number(conteo);
    resumen[clave] = Number.isNaN(valor) ? 0 : valor;
  });

  return resumen;
};

export const actualizarEstadoReserva = async (id: string, payload: ActualizarEstadoPayload): Promise<Reserva> => {
  const body: Record<string, unknown> = {
    estado: payload.estado,
  };
  if (payload.motivo) {
    body.motivo = payload.motivo;
  }
  if (payload.aprobadoPor) {
    body.aprobadoPor = payload.aprobadoPor;
  }

  const respuesta = await fetch(`${ADMIN_RESERVAS_ENDPOINT}/${encodeURIComponent(id)}/estado`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });

  const datos = await manejarRespuesta<ReservaDto>(respuesta);
  return mapearReserva(datos);
};

export const crearReserva = async (payload: CrearReservaPayload): Promise<Reserva> => {
  const respuesta = await fetch(PUBLIC_RESERVAS_ENDPOINT, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      ...payload,
      horaInicio: asegurarHoraConSegundos(payload.horaInicio),
      horaFin: asegurarHoraConSegundos(payload.horaFin),
    }),
  });

  const datos = await manejarRespuesta<ReservaDto>(respuesta);
  return mapearReserva(datos);
};

export const fetchReservasPorUsuario = async (usuarioId: string): Promise<Reserva[]> => {
  const respuesta = await fetch(`${PUBLIC_RESERVAS_ENDPOINT}/usuario/${encodeURIComponent(usuarioId)}`);
  const datos = await manejarRespuesta<ReservaDto[]>(respuesta);
  return datos.map(mapearReserva);
};