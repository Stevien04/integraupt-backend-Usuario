import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Navigation } from './Navigation';
import {
  Calendar,
  Clock,
  MapPin,
  Search,
  Plus,
  Edit,
  Trash2,
  Eye,
  X,
  Check,
  AlertCircle,
  Loader2,
} from 'lucide-react';
import './../../styles/ReservasScreen.css';
import {
  reservasService,
  type ReservaUsuario,
  type EspacioActivo,
} from './services/reservasService';
import { facultadNombre, escuelaNombre } from './services/reservasService';

interface ReservasScreenProps {
  user: {
    id: string;
    email: string;
    user_metadata: {
      name: string;
      avatar_url: string;
      role?: string;
      login_type?: string;
      codigo?: string;
      [key: string]: unknown;
    };
  };
  currentSection?: 'home' | 'reservas' | 'eventos' | 'perfil';
  onSectionChange?: (section: 'home' | 'reservas' | 'eventos' | 'perfil') => void;
  onBackToDashboard?: () => void;
}

type ViewMode = 'list' | 'new' | 'edit' | 'availability';

interface ReservationFormState {
  locationId: string;
  espacioId: string;
  fechaReserva: string;
  horaInicio: string;
  horaFin: string;
  descripcion: string;
  motivo: string;
}

interface AvailabilityResource {
  id: number;
  name: string;
  available: boolean;
  details: string;
  facultadId: number;
  escuelaId: number;
  tipo: string;
  capacidad: number;
  equipamiento?: string | null;
}

interface AvailabilityLocation {
  locationId: string;
  locationName: string;
  resources: AvailabilityResource[];
}

const BASE_FORM_STATE: ReservationFormState = {
  locationId: '',
  espacioId: '',
  fechaReserva: '',
  horaInicio: '',
  horaFin: '',
  descripcion: '',
  motivo: '',
};

const createFormState = (overrides?: Partial<ReservationFormState>): ReservationFormState => ({
  ...BASE_FORM_STATE,
  ...overrides,
});

const normalizeStatus = (status: string | null | undefined) =>
  (status ?? '').toLowerCase();

const statusToCardClass = (status: string) => {
  const normalized = normalizeStatus(status);
  if (normalized === 'aprobada' || normalized === 'activa') {
    return 'reservas-card reservas-card-active';
  }
  if (normalized === 'pendiente') {
    return 'reservas-card reservas-card-pending';
  }
  if (normalized === 'cancelada' || normalized === 'rechazada') {
    return 'reservas-card reservas-card-cancelled';
  }
  return 'reservas-card';
};

const statusToBadgeClass = (status: string) => {
  const normalized = normalizeStatus(status);
  if (normalized === 'aprobada' || normalized === 'activa') {
    return 'reservas-status reservas-status-active';
  }
  if (normalized === 'pendiente') {
    return 'reservas-status reservas-status-pending';
  }
  if (normalized === 'cancelada' || normalized === 'rechazada') {
    return 'reservas-status reservas-status-cancelled';
  }
  return 'reservas-status reservas-status-default';
};

const statusToLabel = (status: string) => {
  const normalized = normalizeStatus(status);
  switch (normalized) {
    case 'aprobada':
      return 'Aprobada';
    case 'cancelada':
      return 'Cancelada';
    case 'rechazada':
      return 'Rechazada';
    case 'pendiente':
      return 'Pendiente';
    case 'activa':
      return 'Activa';
    default:
      return status;
  }
};

const canEditReservation = (status: string) => normalizeStatus(status) === 'pendiente';
const canCancelReservation = (status: string) => {
  const normalized = normalizeStatus(status);
  return normalized === 'pendiente' || normalized === 'aprobada';
};
const canDeleteReservation = (status: string) => {
  const normalized = normalizeStatus(status);
  return normalized === 'cancelada' || normalized === 'rechazada';
};

const formatDate = (dateIso: string) => {
  if (!dateIso) return '';
  const date = new Date(`${dateIso}T00:00:00`);
  if (Number.isNaN(date.getTime())) {
    return dateIso;
  }
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
};

const formatTime = (value?: string | null) => {
  if (!value) return '';
  return value.length >= 5 ? value.slice(0, 5) : value;
};

const resolveUsuarioId = (user: ReservasScreenProps['user']): number | null => {
  const metadata = user?.user_metadata ?? {};
  const possibleKeys = [
    'usuarioId',
    'usuario_id',
    'idUsuario',
    'id_usuario',
    'codigoUsuario',
    'codigo_usuario',
    'codigo',
  ];

  for (const key of possibleKeys) {
    const value = metadata[key];
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value;
    }
    if (typeof value === 'string') {
      const parsed = Number(value);
      if (!Number.isNaN(parsed) && Number.isFinite(parsed)) {
        return parsed;
      }
    }
  }

  const parsedId = Number(user?.id ?? '');
  if (!Number.isNaN(parsedId) && Number.isFinite(parsedId)) {
    return parsedId;
  }

  return null;
};

export const ReservasScreen: React.FC<ReservasScreenProps> = ({
  user,
  currentSection = 'reservas',
  onSectionChange,
  onBackToDashboard,
}) => {
  const [view, setView] = useState<ViewMode>('list');
  const [reservations, setReservations] = useState<ReservaUsuario[]>([]);
  const [reservationsLoading, setReservationsLoading] = useState<boolean>(false);
  const [reservationsError, setReservationsError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [espacios, setEspacios] = useState<EspacioActivo[]>([]);
  const [espaciosLoading, setEspaciosLoading] = useState<boolean>(false);
  const [espaciosError, setEspaciosError] = useState<string | null>(null);
  const [selectedReservation, setSelectedReservation] = useState<ReservaUsuario | null>(null);
  const [formData, setFormData] = useState<ReservationFormState>(createFormState());
  const [formError, setFormError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [actioning, setActioning] = useState<{ id: number; type: 'cancel' | 'delete' } | null>(null);

  const usuarioId = useMemo(() => resolveUsuarioId(user), [user]);
  const navigationSection = useMemo(
    () => (currentSection === 'reservas' ? 'servicios' : currentSection) as
      | 'home'
      | 'servicios'
      | 'eventos'
      | 'perfil',
    [currentSection],
  );

  const handleNavigationChange = useCallback(
    (section: 'home' | 'servicios' | 'eventos' | 'perfil') => {
      if (section === 'servicios') {
        onSectionChange?.('reservas');
      } else {
        onSectionChange?.(section);
      }
    },
    [onSectionChange],
  );

  const loadReservations = useCallback(async () => {
    if (!usuarioId) {
      setReservations([]);
      setReservationsError('No se pudo determinar el identificador del usuario.');
      return;
    }

    setReservationsLoading(true);
    setReservationsError(null);
    try {
      const data = await reservasService.getReservasPorUsuario(usuarioId);
      setReservations(data);
    } catch (error) {
      setReservationsError(
        error instanceof Error
          ? error.message
          : 'No se pudieron cargar las reservas. Inténtalo nuevamente.',
      );
    } finally {
      setReservationsLoading(false);
    }
  }, [usuarioId]);

  const loadEspacios = useCallback(async () => {
    setEspaciosLoading(true);
    setEspaciosError(null);
    try {
      const data = await reservasService.getEspaciosActivos();
      setEspacios(data);
    } catch (error) {
      setEspaciosError(
        error instanceof Error
          ? error.message
          : 'No se pudieron cargar los espacios disponibles.',
      );
    } finally {
      setEspaciosLoading(false);
    }
  }, []);

  useEffect(() => {
    loadReservations();
  }, [loadReservations]);

  useEffect(() => {
    loadEspacios();
  }, [loadEspacios]);

  useEffect(() => {
    if (selectedReservation && !formData.locationId && espacios.length > 0) {
      const espacio = espacios.find((item) => item.id === selectedReservation.espacioId);
      if (espacio) {
        setFormData((prev) => ({
          ...prev,
          locationId: String(espacio.facultadId),
        }));
      }
    }
  }, [espacios, selectedReservation, formData.locationId]);

  const sortedReservations = useMemo(() => {
    const reservationsClone = [...reservations];
    reservationsClone.sort((a, b) => {
      const dateA = new Date(`${a.fechaReserva}T${formatTime(a.horaInicio) || '00:00'}:00`);
      const dateB = new Date(`${b.fechaReserva}T${formatTime(b.horaInicio) || '00:00'}:00`);
      return dateB.getTime() - dateA.getTime();
    });
    return reservationsClone;
  }, [reservations]);

  const filteredReservations = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) {
      return sortedReservations;
    }

    return sortedReservations.filter((reservation) => {
      const values = [
        reservation.espacioNombre,
        reservation.espacioCodigo,
        reservation.descripcion ?? '',
        reservation.motivo ?? '',
        reservation.bloqueNombre ?? '',
        statusToLabel(reservation.estado),
        formatDate(reservation.fechaReserva),
      ];
      return values.some((value) => value.toLowerCase().includes(term));
    });
  }, [sortedReservations, searchTerm]);

  const availabilityData = useMemo<AvailabilityLocation[]>(() => {
    if (espacios.length === 0) {
      return [];
    }

    const grouped = new Map<string, AvailabilityLocation>();
    espacios.forEach((espacio) => {
      const locationId = String(espacio.facultadId);
      if (!grouped.has(locationId)) {
        grouped.set(locationId, {
          locationId,
          locationName: facultadNombre(espacio.facultadId),
          resources: [],
        });
      }

      grouped.get(locationId)!.resources.push({
        id: espacio.id,
        name: `${espacio.nombre} (${espacio.codigo})`,
        available: espacio.estado === 1,
        details: `${espacio.tipo} • Capacidad: ${espacio.capacidad}`,
        facultadId: espacio.facultadId,
        escuelaId: espacio.escuelaId,
        tipo: espacio.tipo,
        capacidad: espacio.capacidad,
        equipamiento: espacio.equipamiento,
      });
    });

    return Array.from(grouped.values())
      .map((group) => ({
        ...group,
        resources: group.resources.sort((a, b) => a.name.localeCompare(b.name, 'es')),
      }))
      .sort((a, b) => a.locationName.localeCompare(b.locationName, 'es'));
  }, [espacios]);

  const locationOptions = useMemo(() => {
    const uniqueLocations = new Map<string, string>();
    espacios.forEach((espacio) => {
      uniqueLocations.set(String(espacio.facultadId), facultadNombre(espacio.facultadId));
    });
    return Array.from(uniqueLocations.entries())
      .map(([id, name]) => ({ id, name }))
      .sort((a, b) => a.name.localeCompare(b.name, 'es'));
  }, [espacios]);

  const resourceOptions = useMemo(() => {
    return espacios
      .filter((espacio) => !formData.locationId || String(espacio.facultadId) === formData.locationId)
      .map((espacio) => ({
        id: String(espacio.id),
        label: `${espacio.nombre} (${espacio.codigo})`,
        subtitle: `${espacio.tipo} • ${escuelaNombre(espacio.escuelaId)} • Capacidad: ${espacio.capacidad}`,
      }))
      .sort((a, b) => a.label.localeCompare(b.label, 'es'));
  }, [espacios, formData.locationId]);

  const handleShowNewForm = (overrides?: Partial<ReservationFormState>) => {
    setSelectedReservation(null);
    setFormError(null);
    setActionError(null);
    setFormData(createFormState(overrides));
    setView('new');
  };

  const handleBackToList = () => {
    setView('list');
    setSelectedReservation(null);
    setFormError(null);
    setActionError(null);
  };

  const handleEditReservation = (reservation: ReservaUsuario) => {
    const espacio = espacios.find((item) => item.id === reservation.espacioId);
    setSelectedReservation(reservation);
    setFormError(null);
    setActionError(null);
    setFormData(
      createFormState({
        locationId: espacio ? String(espacio.facultadId) : '',
        espacioId: String(reservation.espacioId),
        fechaReserva: reservation.fechaReserva,
        horaInicio: formatTime(reservation.horaInicio),
        horaFin: formatTime(reservation.horaFin),
        descripcion: reservation.descripcion ?? `Reserva de ${reservation.espacioNombre}`,
        motivo: reservation.motivo ?? '',
      }),
    );
    setView('edit');
  };

  const validateForm = () => {
    if (!formData.locationId) {
      setFormError('Selecciona una ubicación.');
      return false;
    }
    if (!formData.espacioId) {
      setFormError('Selecciona un recurso para la reserva.');
      return false;
    }
    if (!formData.fechaReserva) {
      setFormError('Selecciona la fecha de la reserva.');
      return false;
    }
    if (!formData.horaInicio || !formData.horaFin) {
      setFormError('Ingresa la hora de inicio y fin.');
      return false;
    }
    return true;
  };

  const handleSubmitReservation = async () => {
    if (!usuarioId) {
      setFormError('No se pudo identificar al usuario.');
      return;
    }

    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    setFormError(null);

    const espacio = espacios.find((item) => String(item.id) === formData.espacioId);
    const descripcion = formData.descripcion.trim() || `Reserva de ${espacio?.nombre ?? 'espacio'}`;
    const motivo = formData.motivo.trim();

    try {
      if (view === 'edit' && selectedReservation) {
        const updated = await reservasService.actualizarReserva(selectedReservation.id, {
          reservaId: selectedReservation.id,
          usuarioId,
          espacioId: Number(formData.espacioId),
          fechaReserva: formData.fechaReserva,
          horaInicio: formData.horaInicio,
          horaFin: formData.horaFin,
          descripcion,
          motivo: motivo || undefined,
        });

        setReservations((prev) => prev.map((item) => (item.id === updated.id ? updated : item)));
      } else {
        const created = await reservasService.crearReserva({
          usuarioId,
          espacioId: Number(formData.espacioId),
          fechaReserva: formData.fechaReserva,
          horaInicio: formData.horaInicio,
          horaFin: formData.horaFin,
          descripcion,
          motivo: motivo || undefined,
        });

        setReservations((prev) => [...prev, created]);
      }

      setFormData(createFormState());
      setSelectedReservation(null);
      setView('list');
    } catch (error) {
      setFormError(
        error instanceof Error ? error.message : 'No se pudo guardar la reserva. Inténtalo nuevamente.',
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancelReservation = async (reservation: ReservaUsuario) => {
    if (!usuarioId) {
      setActionError('No se pudo identificar al usuario.');
      return;
    }

    const confirmCancel = window.confirm('¿Estás seguro de cancelar esta reserva?');
    if (!confirmCancel) {
      return;
    }

    setActioning({ id: reservation.id, type: 'cancel' });
    setActionError(null);

    try {
      const updated = await reservasService.actualizarEstado(reservation.id, 'Cancelada', undefined, usuarioId);
      setReservations((prev) => prev.map((item) => (item.id === updated.id ? updated : item)));
    } catch (error) {
      setActionError(
        error instanceof Error ? error.message : 'No se pudo cancelar la reserva. Inténtalo nuevamente.',
      );
    } finally {
      setActioning(null);
    }
  };

  const handleDeleteReservation = async (reservation: ReservaUsuario) => {
    if (!usuarioId) {
      setActionError('No se pudo identificar al usuario.');
      return;
    }

    const confirmDelete = window.confirm('¿Estás seguro de eliminar esta reserva? Esta acción es irreversible.');
    if (!confirmDelete) {
      return;
    }

    setActioning({ id: reservation.id, type: 'delete' });
    setActionError(null);

    try {
      await reservasService.eliminarReserva(reservation.id, usuarioId);
      setReservations((prev) => prev.filter((item) => item.id !== reservation.id));
    } catch (error) {
      setActionError(
        error instanceof Error ? error.message : 'No se pudo eliminar la reserva. Inténtalo nuevamente.',
      );
    } finally {
      setActioning(null);
    }
  };

  const today = useMemo(() => new Date().toISOString().split('T')[0], []);

  return (
    <div className="reservas-container">
      <Navigation
        user={user}
        currentSection={navigationSection}
        onSectionChange={handleNavigationChange}
        onBackToDashboard={onBackToDashboard}
      />

      <main className="reservas-main">
        <div className="reservas-header">
          <h1 className="reservas-title">Gestión de Reservas</h1>
          <p className="reservas-subtitle">
            Administra tus reservas de espacios y recursos académicos
          </p>
        </div>

        <div className="reservas-actions">
          <button
            onClick={handleBackToList}
            className={`reservas-action-btn ${
              view === 'list' ? 'reservas-action-active' : 'reservas-action-inactive'
            }`}
          >
            Mis Reservas
          </button>
          <button
            onClick={() => handleShowNewForm()}
            className={`reservas-action-btn reservas-action-new ${
              view === 'new' ? 'reservas-action-active' : 'reservas-action-inactive'
            }`}
          >
            <Plus className="reservas-action-icon" />
            Nueva Reserva
          </button>
          <button
            onClick={() => {
              setView('availability');
              setFormError(null);
              setActionError(null);
            }}
            className={`reservas-action-btn reservas-action-availability ${
              view === 'availability' ? 'reservas-action-active' : 'reservas-action-inactive'
            }`}
          >
            <Eye className="reservas-action-icon" />
            Ver Disponibilidad
          </button>
        </div>

        {view === 'list' && (
          <div className="reservas-list-container">
            <div className="reservas-search-container">
              <div className="reservas-search-wrapper">
                <Search className="reservas-search-icon" />
                <input
                  type="text"
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Buscar por ubicación, recurso o estado..."
                  className="reservas-search-input"
                />
              </div>
            </div>

            {actionError && (
              <div className="reservas-form-error">{actionError}</div>
            )}

            {reservationsLoading ? (
              <div className="reservas-loading">
                <Loader2 className="reservas-loading-icon" />
                Cargando tus reservas...
              </div>
            ) : reservationsError ? (
              <div className="reservas-empty">
                <AlertCircle className="reservas-empty-icon" />
                <h3 className="reservas-empty-title">No pudimos cargar tus reservas</h3>
                <p className="reservas-empty-description">{reservationsError}</p>
                <button onClick={loadReservations} className="reservas-empty-btn">
                  Reintentar
                </button>
              </div>
            ) : filteredReservations.length === 0 ? (
              <div className="reservas-empty">
                <Calendar className="reservas-empty-icon" />
                <h3 className="reservas-empty-title">No hay reservas</h3>
                <p className="reservas-empty-description">
                  {searchTerm
                    ? 'No se encontraron reservas con ese criterio.'
                    : 'Comienza creando tu primera reserva.'}
                </p>
                <button
                  onClick={() => handleShowNewForm()}
                  className="reservas-empty-btn"
                >
                  Crear Reserva
                </button>
              </div>
            ) : (
              <div className="reservas-grid">
                {filteredReservations.map((reservation) => (
                  <div key={reservation.id} className={statusToCardClass(reservation.estado)}>
                    <div className="reservas-card-header">
                      <div>
                        <h3 className="reservas-card-title">{reservation.espacioNombre}</h3>
                        {reservation.bloqueNombre && (
                          <p className="reservas-card-subtitle">{reservation.bloqueNombre}</p>
                        )}
                      </div>
                      <span className={statusToBadgeClass(reservation.estado)}>
                        {statusToLabel(reservation.estado)}
                      </span>
                    </div>

                    <div className="reservas-card-details">
                      <div className="reservas-detail">
                        <MapPin className="reservas-detail-icon" />
                        {reservation.espacioCodigo}
                      </div>
                      <div className="reservas-detail">
                        <Calendar className="reservas-detail-icon" />
                        {formatDate(reservation.fechaReserva)}
                      </div>
                      <div className="reservas-detail">
                        <Clock className="reservas-detail-icon" />
                        {`${formatTime(reservation.horaInicio)} - ${formatTime(reservation.horaFin)}`}
                      </div>
                      {reservation.descripcion && (
                        <div className="reservas-detail reservas-detail-description">
                          {reservation.descripcion}
                        </div>
                      )}
                      {reservation.motivo && (
                        <div className="reservas-detail reservas-detail-motivo">
                          Motivo: {reservation.motivo}
                        </div>
                      )}
                    </div>

                    <div className="reservas-card-actions">
                      {canEditReservation(reservation.estado) && (
                        <button
                          onClick={() => handleEditReservation(reservation)}
                          className="reservas-btn reservas-btn-edit"
                        >
                          <Edit className="reservas-btn-icon" />
                          Editar
                        </button>
                      )}
                      {canCancelReservation(reservation.estado) && (
                        <button
                          onClick={() => handleCancelReservation(reservation)}
                          className="reservas-btn reservas-btn-cancel"
                          disabled={actioning?.id === reservation.id && actioning.type === 'cancel'}
                        >
                          <Trash2 className="reservas-btn-icon" />
                          {actioning?.id === reservation.id && actioning.type === 'cancel'
                            ? 'Cancelando...'
                            : 'Cancelar'}
                        </button>
                      )}
                    </div>

                    {canDeleteReservation(reservation.estado) && (
                      <button
                        onClick={() => handleDeleteReservation(reservation)}
                        className="reservas-btn reservas-btn-delete"
                        disabled={actioning?.id === reservation.id && actioning.type === 'delete'}
                      >
                        <Trash2 className="reservas-btn-icon" />
                        {actioning?.id === reservation.id && actioning.type === 'delete'
                          ? 'Eliminando...'
                          : 'Eliminar'}
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {(view === 'new' || view === 'edit') && (
          <div className="reservas-form-container">
            <div className="reservas-form-header">
              <h2 className="reservas-form-title">
                {view === 'edit' ? 'Editar Reserva' : 'Nueva Reserva'}
              </h2>
              <button onClick={handleBackToList} className="reservas-form-close">
                <X className="reservas-form-close-icon" />
              </button>
            </div>

            <form
              onSubmit={(event) => {
                event.preventDefault();
                handleSubmitReservation();
              }}
              className="reservas-form"
            >
              {formError && <div className="reservas-form-error">{formError}</div>}

              <div className="reservas-form-group">
                <label className="reservas-form-label">Ubicación</label>
                <select
                  value={formData.locationId}
                  onChange={(event) =>
                    setFormData((prev) => ({
                      ...prev,
                      locationId: event.target.value,
                      espacioId: '',
                    }))
                  }
                  className="reservas-form-select"
                  required
                >
                  <option value="">Selecciona una ubicación</option>
                  {locationOptions.map((option) => (
                    <option key={option.id} value={option.id}>
                      {option.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="reservas-form-group">
                <label className="reservas-form-label">Recurso</label>
                <select
                  value={formData.espacioId}
                  onChange={(event) => {
                    const value = event.target.value;
                    const espacio = espacios.find((item) => String(item.id) === value);
                    setFormData((prev) => ({
                      ...prev,
                      espacioId: value,
                      descripcion: prev.descripcion || (espacio ? `Reserva de ${espacio.nombre}` : prev.descripcion),
                    }));
                  }}
                  className="reservas-form-select"
                  required
                  disabled={resourceOptions.length === 0}
                >
                  <option value="">Selecciona un recurso</option>
                  {resourceOptions.map((option) => (
                    <option key={option.id} value={option.id}>
                      {option.label}
                    </option>
                  ))}
                </select>
                {formData.espacioId && (
                  <p className="reservas-form-helper">
                    {resourceOptions.find((item) => item.id === formData.espacioId)?.subtitle}
                  </p>
                )}
              </div>

              <div className="reservas-form-group">
                <label className="reservas-form-label">Fecha</label>
                <input
                  type="date"
                  value={formData.fechaReserva}
                  onChange={(event) =>
                    setFormData((prev) => ({
                      ...prev,
                      fechaReserva: event.target.value,
                    }))
                  }
                  min={today}
                  className="reservas-form-input"
                  required
                />
              </div>

              <div className="reservas-form-grid">
                <div className="reservas-form-group">
                  <label className="reservas-form-label">Hora Inicio</label>
                  <input
                    type="time"
                    value={formData.horaInicio}
                    onChange={(event) =>
                      setFormData((prev) => ({
                        ...prev,
                        horaInicio: event.target.value,
                      }))
                    }
                    className="reservas-form-input"
                    required
                  />
                </div>
                <div className="reservas-form-group">
                  <label className="reservas-form-label">Hora Fin</label>
                  <input
                    type="time"
                    value={formData.horaFin}
                    onChange={(event) =>
                      setFormData((prev) => ({
                        ...prev,
                        horaFin: event.target.value,
                      }))
                    }
                    className="reservas-form-input"
                    required
                  />
                </div>
              </div>

              <div className="reservas-form-actions">
                <button
                  type="submit"
                  className={`reservas-form-btn ${
                    view === 'edit' ? 'reservas-form-btn-edit' : 'reservas-form-btn-primary'
                  }`}
                  disabled={submitting}
                >
                  <Check className="reservas-form-btn-icon" />
                  {submitting
                    ? view === 'edit'
                      ? 'Guardando...'
                      : 'Creando...'
                    : view === 'edit'
                      ? 'Guardar Cambios'
                      : 'Crear Reserva'}
                </button>
                <button
                  type="button"
                  onClick={handleBackToList}
                  className="reservas-form-btn reservas-form-btn-secondary"
                  disabled={submitting}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {view === 'availability' && (
          <div className="reservas-availability-container">
            <div className="reservas-availability-info">
              <p className="reservas-availability-info-text">
                Consulta la disponibilidad en tiempo real de todos los espacios y recursos
              </p>
            </div>

            {espaciosLoading ? (
              <div className="reservas-loading">
                <Loader2 className="reservas-loading-icon" />
                Cargando espacios disponibles...
              </div>
            ) : espaciosError ? (
              <div className="reservas-empty">
                <AlertCircle className="reservas-empty-icon" />
                <h3 className="reservas-empty-title">No pudimos cargar los espacios</h3>
                <p className="reservas-empty-description">{espaciosError}</p>
                <button onClick={loadEspacios} className="reservas-empty-btn">
                  Reintentar
                </button>
              </div>
            ) : availabilityData.length === 0 ? (
              <div className="reservas-empty">
                <Calendar className="reservas-empty-icon" />
                <h3 className="reservas-empty-title">No hay espacios registrados</h3>
                <p className="reservas-empty-description">
                  Aún no existen espacios disponibles para mostrar.
                </p>
              </div>
            ) : (
              availabilityData.map((location) => (
                <div key={location.locationId} className="reservas-availability-location">
                  <h3 className="reservas-availability-location-title">{location.locationName}</h3>
                  <div className="reservas-availability-grid">
                    {location.resources.map((resource) => (
                      <div
                        key={resource.id}
                        className={`reservas-availability-card ${
                          resource.available
                            ? 'reservas-availability-available'
                            : 'reservas-availability-unavailable'
                        }`}
                      >
                        <div className="reservas-availability-card-header">
                          <h4 className="reservas-availability-resource-name">{resource.name}</h4>
                          <span
                            className={`reservas-availability-status ${
                              resource.available
                                ? 'reservas-availability-status-available'
                                : 'reservas-availability-status-unavailable'
                            }`}
                          >
                            {resource.available ? 'Disponible' : 'No disponible'}
                          </span>
                        </div>
                        <p className="reservas-availability-info">
                          {resource.details} • {escuelaNombre(resource.escuelaId)}
                        </p>
                        {resource.equipamiento && (
                          <p className="reservas-availability-info">
                            Equipamiento: {resource.equipamiento}
                          </p>
                        )}
                        {resource.available && (
                          <button
                            onClick={() =>
                              handleShowNewForm({
                                locationId: String(resource.facultadId),
                                espacioId: String(resource.id),
                                fechaReserva: today,
                                descripcion: `Reserva de ${resource.name}`,
                              })
                            }
                            className="reservas-availability-btn"
                          >
                            Reservar Ahora
                          </button>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </main>
    </div>
  );
};
