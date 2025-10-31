import React, { useState, useEffect, useMemo } from 'react';
import { Navigation } from './Navigation';
import { Calendar, Clock, MapPin, Search, Plus, Edit, Trash2, X, Check, Server, Monitor, MessageCircle, ArrowLeft, Eye, Building2, BookOpen, Users as UsersIcon } from 'lucide-react';
import './../../styles/ServiciosScreen.css';
import { espaciosService } from './services/espaciosService';
import { reservasService } from './services/reservasService';
import { serviciosScreenService, type HorarioCompleto } from './services/serviciosScreenService';
import { horariosService, type BloqueHorarioCatalogoMap } from './services/horariosService';

import type { Espacio, Reservacion } from './types';

interface BloqueHorario {
  id: number;
  label: string;
  horaInicio: string;
  horaFinal: string;
  orden: number;
}

interface ServiciosScreenProps {
  user: {
    id: string;
    email: string;
    user_metadata: {
      name: string;
      avatar_url: string;
      role?: string;
      login_type?: string;
      codigo?: string;
    };
  };
  currentSection?: 'home' | 'servicios' | 'eventos' | 'perfil';
  onSectionChange?: (section: 'home' | 'servicios' | 'eventos' | 'perfil') => void;
  onBackToDashboard?: () => void;
}

export const ServiciosScreen: React.FC<ServiciosScreenProps> = ({
  user,
  currentSection = 'servicios',
  onSectionChange,
  onBackToDashboard
}) => {
  const [selectedService, setSelectedService] = useState<'menu' | 'espacios' | 'citas'>('menu');
  const [view, setView] = useState<'list' | 'new' | 'edit' | 'espacios-grid' | 'horario-semanal' | 'reservar-espacio'>('list');
  const [selectedEspacio, setSelectedEspacio] = useState<Espacio | null>(null);
  const [selectedReservacion, setSelectedReservacion] = useState<Reservacion | null>(null);
  const [showReservaModal, setShowReservaModal] = useState(false);
  const [espacioToReserve, setEspacioToReserve] = useState<Espacio | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Estados para datos reales
  const [espacios, setEspacios] = useState<Espacio[]>([]);
  const [reservacionesEspacios, setReservacionesEspacios] = useState<Reservacion[]>([]);
  const [horariosEspacios, setHorariosEspacios] = useState<Record<string, HorarioCompleto[]>>({});
  const [horariosCargando, setHorariosCargando] = useState(false);

 const [bloquesCatalogo, setBloquesCatalogo] = useState<BloqueHorarioCatalogoMap>(horariosService.getBloquesHorarios());
   const [bloquesCargando, setBloquesCargando] = useState(true);
   const [bloquesError, setBloquesError] = useState<string | null>(null);

   useEffect(() => {
     let activo = true;

     const cargarBloques = async () => {
       try {
         const bloques = await horariosService.fetchBloquesHorarios();
         if (activo) {
           setBloquesCatalogo({ ...bloques });
           setBloquesError(null);
         }
       } catch (error) {
         console.error('Error al cargar bloques horarios:', error);
         if (activo) {
           setBloquesError('No se pudieron cargar los bloques horarios desde la base de datos.');
         }
       } finally {
         if (activo) {
           setBloquesCargando(false);
         }
       }
     };

     cargarBloques();

     return () => {
       activo = false;
     };
   }, []);

   const ordenBloques = useMemo(() => {
     const mapaOrden = new Map<number, number>();
     Object.entries(bloquesCatalogo)
       .sort(([, a], [, b]) => (a.orden ?? Number.MAX_SAFE_INTEGER) - (b.orden ?? Number.MAX_SAFE_INTEGER))
       .forEach(([id], index) => {
        mapaOrden.set(Number(id), index);
      });
         return mapaOrden;
        }, [bloquesCatalogo]);

    const formatearHora = (hora?: string | null) => {
      if (!hora || hora === 'N/A') {
        return 'N/A';
      }

      const match = hora.match(/^(\d{1,2}):(\d{2})/);
      if (match) {
        const horas = match[1].padStart(2, '0');
        const minutos = match[2];
        return `${horas}:${minutos}`;
      }

      return hora;
    };

    const obtenerOrdenBloque = (bloqueId: number) => {
      const bloqueCatalogo = bloquesCatalogo[bloqueId];
     if (bloqueCatalogo?.orden != null) {
            return bloqueCatalogo.orden;
      }
      const ordenCatalogo = ordenBloques.get(bloqueId);
      return ordenCatalogo !== undefined ? ordenCatalogo + 1 : bloqueId;
    };


  // Estado para formulario de reserva de espacios
  const [espaciosForm, setEspaciosForm] = useState({
    type: 'laboratorio' as 'laboratorio' | 'aula',
    resource: '',
    date: '',
    startTime: '',
    endTime: '',
    ciclo: '',
    curso: ''
  });

  // Estado para formulario de reserva rápida desde card
  const [reservaRapidaForm, setReservaRapidaForm] = useState({
    ciclo: '',
    curso: '',
    date: '',
    startTime: '',
    endTime: '',
    motivo: ''
  });

  // Estado para formulario de citas psicológicas
  const [citasForm, setCitasForm] = useState({
    date: '',
    startTime: '',
    motivo: ''
  });

  // Citas de psicología (mantenemos datos mock por ahora)
  const [citasPsicologia, setCitasPsicologia] = useState<Reservacion[]>([
    {
      id: '3',
      type: 'psicologia',
      resource: 'Orientación Psicológica',
      date: '2025-10-12',
      startTime: '15:00',
      endTime: '15:40',
      status: 'active',
      motivo: 'Orientación académica'
    }
  ]);

  // Cargar datos reales al montar el componente
  useEffect(() => {
    cargarEspacios();
    cargarReservaciones();
  }, []);

  const cargarEspacios = async () => {
  try {
    setLoading(true);
    setError(null);
    const espaciosBD = await espaciosService.getAllEspacios();

    // Mapear datos de BD a formato del frontend
    const espaciosMapeados: Espacio[] = espaciosBD.map(espacioBD => ({
      id: espacioBD.id.toString(),
      codigo: espacioBD.codigo,
      nombre: espacioBD.nombre,
      ubicacion: espacioBD.ubicacion || 'Ubicación no especificada',
      tipo: espaciosService.getTipoFrontend(espacioBD.tipo),
      capacidad: espacioBD.capacidad,
      equipamiento: espacioBD.equipamiento || 'Equipamiento no especificado',
      facultad: espacioBD.facultad,
      escuela: espacioBD.escuela,
      estado: espaciosService.getEstadoTexto(espacioBD.estado)
    }));

    setEspacios(espaciosMapeados);
  } catch (err) {
    setError('Error al cargar los espacios');
    console.error('Error cargando espacios:', err);
  } finally {
    setLoading(false);
  }
};

  const cargarReservaciones = async () => {
    try {
      const reservasBD = await reservasService.getReservasPorEstado();

      // Mapear reservas de BD a formato del frontend
      const reservasMapeadas = reservasBD.map(reservaBD =>
        reservasService.mapearReservaFrontend(reservaBD)
      );

      setReservacionesEspacios(reservasMapeadas);
    } catch (err) {
      console.error('Error cargando reservaciones:', err);
    }
  };

  const handleCreateEspacio = async () => {
    try {
      setLoading(true);

      // Crear la descripción con ciclo y curso
      const descripcion = `Ciclo: ${espaciosForm.ciclo} - Curso: ${espaciosForm.curso}`;

      // Buscar el espacio seleccionado para obtener su ID
      const espacioSeleccionado = espacios.find(e => e.nombre === espaciosForm.resource);

      if (!espacioSeleccionado) {
        throw new Error('Espacio no encontrado');
      }

      // Preparar datos para la API
      const reservaData = {
        usuario: parseInt(user.id),
        espacio: parseInt(espacioSeleccionado.id),
        fechaReserva: espaciosForm.date,
        bloque: 10, // Por defecto, necesitarías mapear el horario
        descripcion: descripcion
      };

      // Enviar a la API
      await reservasService.crearReserva(reservaData);

      // Recargar reservaciones
      await cargarReservaciones();

      setEspaciosForm({ type: 'laboratorio', resource: '', date: '', startTime: '', endTime: '', ciclo: '', curso: '' });
      setView('list');

      alert('¡Reserva enviada! Tu solicitud está pendiente de aprobación por el administrador.');

    } catch (err) {
      setError('Error al crear la reserva');
      console.error('Error creando reserva:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleReservarEspacio = (espacio: Espacio) => {
    setEspacioToReserve(espacio);
    setReservaRapidaForm({
      ciclo: '',
      curso: '',
      date: '',
      startTime: '',
      endTime: '',
      motivo: ''
    });
    setShowReservaModal(true);
  };

  const handleSubmitReservaRapida = async () => {
    if (!espacioToReserve) return;

    try {
      setLoading(true);

      // Crear la descripción con ciclo y curso
      const descripcion = `Ciclo: ${reservaRapidaForm.ciclo} - Curso: ${reservaRapidaForm.curso} - ${reservaRapidaForm.motivo || 'Sin descripción adicional'}`;

      // Preparar datos para la API
      const reservaData = {
        usuario: parseInt(user.id),
        espacio: parseInt(espacioToReserve.id),
        fechaReserva: reservaRapidaForm.date,
        bloque: 10, // Necesitarías mapear el horario a un bloque ID
        descripcion: descripcion,
        motivo: reservaRapidaForm.motivo
      };

      // Enviar a la API
      await reservasService.crearReserva(reservaData);

      // Recargar reservaciones
      await cargarReservaciones();

      setShowReservaModal(false);
      setEspacioToReserve(null);

      // Mostrar mensaje de éxito
      alert('¡Reserva enviada! Tu solicitud está pendiente de aprobación por el administrador.');

    } catch (err) {
      setError('Error al crear la reserva');
      console.error('Error creando reserva:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateCita = () => {
    // Calcular hora de fin (40 minutos después)
    const [hours, minutes] = citasForm.startTime.split(':').map(Number);
    const endMinutes = minutes + 40;
    const endHours = hours + Math.floor(endMinutes / 60);
    const endTime = `${endHours.toString().padStart(2, '0')}:${(endMinutes % 60).toString().padStart(2, '0')}`;

    const newCita: Reservacion = {
      id: Date.now().toString(),
      type: 'psicologia',
      resource: 'Orientación Psicológica',
      date: citasForm.date,
      startTime: citasForm.startTime,
      endTime: endTime,
      status: 'pending',
      motivo: citasForm.motivo
    };
    setCitasPsicologia([...citasPsicologia, newCita]);
    setCitasForm({ date: '', startTime: '', motivo: '' });
    setView('list');
    setSelectedService('menu');
  };

  const handleCancelReservacion = (id: string, type: 'espacios' | 'citas') => {
    if (window.confirm('¿Estás seguro de cancelar esta reserva?')) {
      if (type === 'espacios') {
        setReservacionesEspacios(reservacionesEspacios.map(r =>
          r.id === id ? { ...r, status: 'cancelled' as const } : r
        ));
      } else {
        setCitasPsicologia(citasPsicologia.map(r =>
          r.id === id ? { ...r, status: 'cancelled' as const } : r
        ));
      }
    }
  };

  const handleDeleteReservacion = (id: string, type: 'espacios' | 'citas') => {
    if (window.confirm('¿Estás seguro de eliminar esta reserva?')) {
      if (type === 'espacios') {
        setReservacionesEspacios(reservacionesEspacios.filter(r => r.id !== id));
      } else {
        setCitasPsicologia(citasPsicologia.filter(r => r.id !== id));
      }
    }
  };

  const handleVerHorario = async (espacio: Espacio) => {
    try {
      setHorariosCargando(true);
      setSelectedEspacio(espacio);

      console.log('Cargando horarios para espacio:', espacio);

      // Usar el NUEVO servicio especializado
      const horariosCompletos = await serviciosScreenService.getHorariosCompletosPorEspacio(parseInt(espacio.id));

      console.log('Horarios completos recibidos:', horariosCompletos);


      console.log('Horarios finales para tabla:', horariosCompletos);
      setHorariosEspacios({ [espacio.id]: horariosCompletos });
      setView('horario-semanal');

    } catch (error) {
      console.error('Error cargando horarios:', error);
      setHorariosEspacios({});
      setView('horario-semanal');
    } finally {
      setHorariosCargando(false);
    }
  };




  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return 'servicios-status-active';
      case 'pending': return 'servicios-status-pending';
      case 'cancelled': return 'servicios-status-cancelled';
      case 'approved': return 'servicios-status-approved';
      default: return 'servicios-status-default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'active': return 'Activa';
      case 'pending': return 'Pendiente';
      case 'cancelled': return 'Cancelada';
      case 'approved': return 'Aprobada';
      default: return status;
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'laboratorio': return <Server className="servicios-type-icon" />;
      case 'aula': return <Monitor className="servicios-type-icon" />;
      case 'psicologia': return <MessageCircle className="servicios-type-icon" />;
      default: return <MapPin className="servicios-type-icon" />;
    }
  };

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'laboratorio': return 'Laboratorio';
      case 'aula': return 'Aula';
      case 'psicologia': return 'Psicología';
      default: return type;
    }
  };

    const normalizarDia = (dia: string) =>
        dia
          .normalize('NFD')
          .replace(/[\u0300-\u036f]/g, '')
          .toLowerCase()
          .trim();

      const diasTabla = [
        { clave: 'Lunes', etiqueta: 'Lunes' },
        { clave: 'Martes', etiqueta: 'Martes' },
        { clave: 'Miercoles', etiqueta: 'Miércoles' },
        { clave: 'Jueves', etiqueta: 'Jueves' },
        { clave: 'Viernes', etiqueta: 'Viernes' },
        { clave: 'Sabado', etiqueta: 'Sábado' }
      ];

      const horariosSeleccionados = selectedEspacio
        ? horariosEspacios[selectedEspacio.id] || []
        : [];

      const bloquesDisponibles: BloqueHorario[] = useMemo(() => {
        if (!horariosSeleccionados.length) {
          return [];
    }
const mapaBloques = new Map<number, BloqueHorario>();

  horariosSeleccionados.forEach(horario => {
           if (!mapaBloques.has(horario.bloqueId)) {
             const bloqueCatalogo = bloquesCatalogo[horario.bloqueId];
             const horaInicioCatalogo = formatearHora(bloqueCatalogo?.horaInicio || horario.horaInicio);
             const horaFinalCatalogo = formatearHora(bloqueCatalogo?.horaFinal || horario.horaFinal);

    mapaBloques.set(horario.bloqueId, {
              id: horario.bloqueId,
             label: `${horaInicioCatalogo} a ${horaFinalCatalogo}`,
                           horaInicio: horaInicioCatalogo,
                           horaFinal: horaFinalCatalogo,
                           orden: obtenerOrdenBloque(horario.bloqueId)
            });
          }
        });

   return Array.from(mapaBloques.values()).sort((a, b) => {
             if (a.orden !== b.orden) {
               return a.orden - b.orden;
             }
             if (a.horaInicio !== 'N/A' && b.horaInicio !== 'N/A') {
               return a.horaInicio.localeCompare(b.horaInicio);
             }
             return a.id - b.id;
           });
         }, [horariosSeleccionados, bloquesCatalogo, ordenBloques]);


       const obtenerHorarioParaCelda = (dia: string, bloqueId: number) => {
         if (!horariosSeleccionados.length) {
           return null;
    }

    const diaNormalizado = normalizarDia(dia);

  return (
        horariosSeleccionados.find(horario =>
          horario.bloqueId === bloqueId &&
          normalizarDia(horario.diaSemana) === diaNormalizado
        ) || null
      );
  };

  return (
    <div className="servicios-container">
      <Navigation
        user={user}
        currentSection={currentSection}
        onSectionChange={onSectionChange}
        onBackToDashboard={onBackToDashboard}
      />

      <main className="servicios-main">
        {/* Mostrar estado de carga o error */}
        {loading && (
          <div className="servicios-loading">
            Cargando...
          </div>
        )}

        {error && (
          <div className="servicios-error">
            {error}
            <button onClick={cargarEspacios} className="servicios-retry-btn">
              Reintentar
            </button>
          </div>
        )}

        {/* Menú Principal de Servicios */}
        {selectedService === 'menu' && !loading && !error && (
          <div>
            <div className="servicios-header">
              <h1 className="servicios-title">Servicios</h1>
              <p className="servicios-subtitle">Selecciona el servicio que necesitas</p>
            </div>

            <div className="servicios-menu-grid">
              {/* Card: Reserva de Espacios */}
              <button
                onClick={() => {
                  setSelectedService('espacios');
                  setView('espacios-grid');
                }}
                className="servicios-menu-card servicios-menu-card-espacios"
              >
                <div className="servicios-menu-icon-container">
                  <div className="servicios-menu-icon servicios-menu-icon-espacios">
                    <Server className="servicios-menu-icon-svg" />
                  </div>
                </div>
                <h3 className="servicios-menu-card-title">Reserva de Espacios</h3>
                <p className="servicios-menu-card-description">
                  Reserva laboratorios y aulas para tus actividades académicas
                </p>
                <div className="servicios-menu-card-badge">
                  Laboratorios y Aulas
                </div>
              </button>

              {/* Card: Citas y Asesorías */}
              <button
                onClick={() => {
                  setSelectedService('citas');
                  setView('list');
                }}
                className="servicios-menu-card servicios-menu-card-citas"
              >
                <div className="servicios-menu-icon-container">
                  <div className="servicios-menu-icon servicios-menu-icon-citas">
                    <MessageCircle className="servicios-menu-icon-svg" />
                  </div>
                </div>
                <h3 className="servicios-menu-card-title">Citas y Asesorías</h3>
                <p className="servicios-menu-card-description">
                  Agenda tu cita de orientación psicológica personalizada
                </p>
                <div className="servicios-menu-card-badge servicios-menu-card-badge-citas">
                  Orientación Psicológica
                </div>
              </button>
            </div>
          </div>
        )}

        {/* Vista de Grid de Espacios (para usuarios académicos) */}
        {selectedService === 'espacios' && view === 'espacios-grid' && !loading && !error && (
          <div>
            <button
              onClick={() => setSelectedService('menu')}
              className="servicios-back-btn"
            >
              <ArrowLeft className="servicios-back-icon" />
              Volver a Servicios
            </button>

            <div className="servicios-header">
              <h1 className="servicios-title">Espacios Disponibles</h1>
              <p className="servicios-subtitle">Selecciona un espacio para ver su horario semanal</p>
            </div>

            <div className="servicios-espacios-grid">
              {espacios.map((espacio) => (
                <div
                  key={espacio.id}
                  className={`servicios-espacio-card ${
                    espacio.estado === 'Disponible'
                      ? 'servicios-espacio-disponible'
                      : 'servicios-espacio-mantenimiento'
                  }`}
                >
                  <div className="servicios-espacio-header">
                    <div className="servicios-espacio-info">
                      <div className={`servicios-espacio-icon ${
                        espacio.tipo === 'Laboratorio'
                          ? 'servicios-espacio-lab'
                          : 'servicios-espacio-aula'
                      }`}>
                        {espacio.tipo === 'Laboratorio' ? <Server className="servicios-espacio-icon-svg" /> : <Monitor className="servicios-espacio-icon-svg" />}
                      </div>
                      <div>
                        <h3 className="servicios-espacio-name">{espacio.nombre}</h3>
                        <span className={`servicios-espacio-tipo ${
                          espacio.tipo === 'Laboratorio'
                            ? 'servicios-espacio-tipo-lab'
                            : 'servicios-espacio-tipo-aula'
                        }`}>
                          {espacio.tipo}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div className="servicios-espacio-details">
                    <div className="servicios-espacio-detail">
                      <Building2 className="servicios-espacio-detail-icon" />
                      <span>{espacio.facultad}</span>
                    </div>
                    <div className="servicios-espacio-detail">
                      <BookOpen className="servicios-espacio-detail-icon" />
                      <span>{espacio.escuela}</span>
                    </div>
                    <div className="servicios-espacio-detail">
                      <MapPin className="servicios-espacio-detail-icon" />
                      <span>{espacio.ubicacion}</span>
                    </div>
                    <div className="servicios-espacio-detail">
                      <UsersIcon className="servicios-espacio-detail-icon" />
                      <span>Capacidad: {espacio.capacidad}</span>
                    </div>
                  </div>

                  <div className="servicios-espacio-recursos">
                    <p className="servicios-espacio-recursos-label">Recursos:</p>
                    <p className="servicios-espacio-recursos-text">{espacio.equipamiento}</p>
                  </div>

                  <div className="servicios-espacio-actions">
                    <div className="servicios-espacio-status-container">
                      <span className={`servicios-espacio-status ${
                        espacio.estado === 'Disponible'
                          ? 'servicios-espacio-status-disponible'
                          : 'servicios-espacio-status-mantenimiento'
                      }`}>
                        {espacio.estado}
                      </span>

                      <button
                        onClick={() => handleVerHorario(espacio)}
                        className="servicios-espacio-btn servicios-espacio-btn-horario"
                      >
                        <Eye className="servicios-espacio-btn-icon" />
                        Ver Horario
                      </button>
                    </div>

                    {espacio.estado === 'Disponible' && (
                      <button
                        onClick={() => handleReservarEspacio(espacio)}
                        className="servicios-espacio-btn servicios-espacio-btn-reservar"
                      >
                        <Calendar className="servicios-espacio-btn-icon" />
                        Reservar Espacio
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Vista Semanal de Horarios por Espacio */}
        {selectedService === 'espacios' && view === 'horario-semanal' && selectedEspacio && (
          <div>
            <button
              onClick={() => setView('espacios-grid')}
              className="servicios-back-btn"
            >
              <ArrowLeft className="servicios-back-icon" />
              Volver a Espacios
            </button>

            <div className="servicios-espacio-info-card">
              <div className="servicios-espacio-info-header">
                <div>
                  <h1 className="servicios-espacio-info-title">{selectedEspacio.nombre}</h1>
                  <div className="servicios-espacio-info-badges">
                    <span className={`servicios-espacio-info-badge ${
                      selectedEspacio.tipo === 'Laboratorio'
                        ? 'servicios-espacio-info-badge-lab'
                        : 'servicios-espacio-info-badge-aula'
                    }`}>
                      {selectedEspacio.tipo}
                    </span>
                    <span className="servicios-espacio-info-badge servicios-espacio-info-badge-facultad">
                      {selectedEspacio.facultad} - {selectedEspacio.escuela}
                    </span>
                    <span className="servicios-espacio-info-badge servicios-espacio-info-badge-capacidad">
                      Capacidad: {selectedEspacio.capacidad}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Tabla Semanal con bloques de 50 minutos */}
            <div className="servicios-horario-container">
              <h3 className="servicios-horario-title">
                <Clock className="servicios-horario-title-icon" />
                Horario Semanal (Bloques de 50 minutos)
                {horariosCargando && <span className="servicios-loading-text">Cargando horarios...</span>}
              </h3>

              {horariosCargando ? (
              <div className="servicios-horario-loading">
                <div className="servicios-loading-spinner"></div>
                <p>Cargando horarios del espacio...</p>
              </div>
              ) : (
              <>
               {bloquesError && (
                                <div className="servicios-horario-error">{bloquesError}</div>
                              )}
                <div className="servicios-horario-table-container">
                 {bloquesDisponibles.length === 0 ? (
                                     <div className="servicios-horario-empty">
                                       No se encontraron bloques de horarios configurados para este espacio.
                                     </div>
                                   ) : (
                                     <table className="servicios-horario-table">
                                       <thead>
                                         <tr className="servicios-horario-header">
                                           <th className="servicios-horario-th servicios-horario-time-header">
                                             Horario
                          </th>
                       {diasTabla.map(dia => (
                                                  <th key={dia.clave} className="servicios-horario-th">
                                                    {dia.etiqueta}
                                                  </th>
                                                ))}
                        </tr>
                       </thead>
                                            <tbody>
                                              {bloquesDisponibles.map(bloque => (
                                                <tr key={bloque.id} className="servicios-horario-row">
                                                  <td className="servicios-horario-time">
                                                    {bloque.label}
                                                  </td>
                                                  {diasTabla.map(dia => {
                                                    const horarioCelda = obtenerHorarioParaCelda(dia.clave, bloque.id);
                                                       const estaOcupado = Boolean(horarioCelda?.ocupado);

                                                       const horaInicioCelda = formatearHora(horarioCelda?.horaInicio);
                                                       const horaFinalCelda = formatearHora(horarioCelda?.horaFinal);

                                                    return (
                                                      <td
                                                        key={`${dia.clave}-${bloque.id}`}
                                                        className={`servicios-horario-cell ${
                                                          estaOcupado
                                                            ? 'servicios-horario-cell-ocupado'
                                                            : 'servicios-horario-cell-disponible'
                                                        }`}
                                                      >
                                                        {estaOcupado && horarioCelda ? (
                                                          <div className="servicios-horario-curso">
                                                          <div className="servicios-horario-curso-nombre">
                                                          {horarioCelda.curso || 'Bloque reservado'}
                                                            </div>
                                                            {horarioCelda.curso && (
                                                                                                                          <div className="servicios-horario-curso-profesor">
                                                                                                                            {horarioCelda.docente || 'Docente no asignado'}
                                                                                                                          </div>
                                                                                                                        )}
                                                                                                                        {(horaInicioCelda !== 'N/A' || horaFinalCelda !== 'N/A') && (
                                                                                                                          <div className="servicios-horario-curso-estudiantes">
                                                                                                                            {horaInicioCelda} - {horaFinalCelda}
                                                                                                                          </div>
                                                                                                                        )}
                                                          </div>
                                                        ) : (
                                                          <div className="servicios-horario-vacio">Disponible</div>
                                                        )}
                                                      </td>
                                                    );
                                                  })}
                                                </tr>
                                              ))}
                                            </tbody>
                                          </table>
                                        )}
                </div>

                <div className="servicios-horario-leyenda">
                  <div className="servicios-horario-leyenda-items">
                    <div className="servicios-horario-leyenda-item">
                      <div className="servicios-horario-leyenda-color servicios-horario-leyenda-ocupado"></div>
                      <span className="servicios-horario-leyenda-text">Ocupado</span>
                    </div>
                    <div className="servicios-horario-leyenda-item">
                      <div className="servicios-horario-leyenda-color servicios-horario-leyenda-disponible"></div>
                      <span className="servicios-horario-leyenda-text">Disponible</span>
                    </div>
                  </div>
                </div>
                </>
              )}
            </div>
          </div>
        )}

        {/* Vista de Reserva de Espacios (formulario) */}
        {selectedService === 'espacios' && view === 'list' && (
          <div>
            <button
              onClick={() => setSelectedService('menu')}
              className="servicios-back-btn"
            >
              <ArrowLeft className="servicios-back-icon" />
              Volver a Servicios
            </button>

            <div className="servicios-header">
              <h1 className="servicios-title">Reserva de Espacios</h1>
              <p className="servicios-subtitle">Reserva de laboratorios y aulas para tus actividades académicas</p>
            </div>

            <div className="servicios-actions">
              <button
                onClick={() => setView('espacios-grid')}
                className="servicios-action-btn servicios-action-btn-primary"
              >
                <Eye className="servicios-action-btn-icon" />
                Ver Horarios de Espacios
              </button>
              <button
                onClick={() => setView('new')}
                className="servicios-action-btn servicios-action-btn-success"
              >
                <Plus className="servicios-action-btn-icon" />
                Nueva Reserva de Espacio
              </button>
            </div>

            {reservacionesEspacios.length === 0 ? (
              <div className="servicios-empty">
                <Server className="servicios-empty-icon" />
                <h3 className="servicios-empty-title">No hay reservas de espacios</h3>
                <p className="servicios-empty-description">Comienza reservando un laboratorio o aula</p>
                <button
                  onClick={() => setView('new')}
                  className="servicios-empty-btn"
                >
                  Crear Reserva
                </button>
              </div>
            ) : (
              <div className="servicios-reservas-grid">
                {reservacionesEspacios.map((reservacion) => (
                  <div
                    key={reservacion.id}
                    className={`servicios-reserva-card ${
                      reservacion.status === 'active' ? 'servicios-reserva-active' :
                      reservacion.status === 'pending' ? 'servicios-reserva-pending' :
                      reservacion.status === 'approved' ? 'servicios-reserva-approved' : 'servicios-reserva-cancelled'
                    }`}
                  >
                    <div className="servicios-reserva-header">
                      <div className="servicios-reserva-type">
                        {getTypeIcon(reservacion.type)}
                        <h3 className="servicios-reserva-type-label">{getTypeLabel(reservacion.type)}</h3>
                      </div>
                      <span className={`servicios-reserva-status ${getStatusColor(reservacion.status)}`}>
                        {getStatusLabel(reservacion.status)}
                      </span>
                    </div>

                    <div className="servicios-reserva-details">
                      <div className="servicios-reserva-detail">
                        <MapPin className="servicios-reserva-detail-icon" />
                        {reservacion.resource}
                      </div>
                      <div className="servicios-reserva-detail">
                        <Calendar className="servicios-reserva-detail-icon" />
                        {new Date(reservacion.date).toLocaleDateString('es-ES')}
                      </div>
                      <div className="servicios-reserva-detail">
                        <Clock className="servicios-reserva-detail-icon" />
                        {reservacion.startTime} - {reservacion.endTime}
                      </div>
                      {reservacion.ciclo && (
                        <div className="servicios-reserva-info servicios-reserva-info-ciclo">
                          <strong>Ciclo:</strong> {reservacion.ciclo}
                        </div>
                      )}
                      {reservacion.curso && (
                        <div className="servicios-reserva-info servicios-reserva-info-curso">
                          <strong>Curso:</strong> {reservacion.curso}
                        </div>
                      )}
                    </div>

                    {reservacion.status !== 'cancelled' && (
                      <button
                        onClick={() => handleCancelReservacion(reservacion.id, 'espacios')}
                        className="servicios-reserva-action servicios-reserva-action-cancel"
                      >
                        <Trash2 className="servicios-reserva-action-icon" />
                        Cancelar
                      </button>
                    )}

                    {reservacion.status === 'cancelled' && (
                      <button
                        onClick={() => handleDeleteReservacion(reservacion.id, 'espacios')}
                        className="servicios-reserva-action servicios-reserva-action-delete"
                      >
                        <Trash2 className="servicios-reserva-action-icon" />
                        Eliminar
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Formulario Nueva Reserva de Espacio */}
        {selectedService === 'espacios' && view === 'new' && (
          <div className="servicios-form-container">
            <div className="servicios-form-header">
              <h2 className="servicios-form-title">Nueva Reserva de Espacio</h2>
              <button
                onClick={() => setView('list')}
                className="servicios-form-close"
              >
                <X className="servicios-form-close-icon" />
              </button>
            </div>

            <form onSubmit={(e) => { e.preventDefault(); handleCreateEspacio(); }} className="servicios-form">
              <div className="servicios-form-group">
                <label className="servicios-form-label">Tipo de Espacio</label>
                <select
                  value={espaciosForm.type}
                  onChange={(e) => setEspaciosForm({ ...espaciosForm, type: e.target.value as 'laboratorio' | 'aula' })}
                  className="servicios-form-select"
                  required
                >
                  <option value="laboratorio">Laboratorio</option>
                  <option value="aula">Aula</option>
                </select>
              </div>

              <div className="servicios-form-group">
                <label className="servicios-form-label">
                  {espaciosForm.type === 'laboratorio' ? 'Laboratorio' : 'Aula'}
                </label>
                <select
                  value={espaciosForm.resource}
                  onChange={(e) => setEspaciosForm({ ...espaciosForm, resource: e.target.value })}
                  className="servicios-form-select"
                  required
                >
                  <option value="">Selecciona {espaciosForm.type === 'laboratorio' ? 'un laboratorio' : 'un aula'}</option>
                  {espacios
                    .filter(e => e.tipo === (espaciosForm.type === 'laboratorio' ? 'Laboratorio' : 'Aula'))
                    .map(e => (
                      <option key={e.id} value={e.nombre}>{e.nombre} - {e.ubicacion}</option>
                    ))}
                </select>
              </div>

              <div className="servicios-form-grid">
                <div className="servicios-form-group">
                  <label className="servicios-form-label">Ciclo *</label>
                  <select
                    value={espaciosForm.ciclo}
                    onChange={(e) => setEspaciosForm({ ...espaciosForm, ciclo: e.target.value })}
                    className="servicios-form-select"
                    required
                  >
                    <option value="">Selecciona ciclo</option>
                    {['I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X'].map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>

                <div className="servicios-form-group">
                  <label className="servicios-form-label">Curso / Tema *</label>
                  <input
                    type="text"
                    value={espaciosForm.curso}
                    onChange={(e) => setEspaciosForm({ ...espaciosForm, curso: e.target.value })}
                    placeholder="Ej: Programación Avanzada"
                    className="servicios-form-input"
                    required
                  />
                </div>
              </div>

              <div className="servicios-form-group">
                <label className="servicios-form-label">Fecha</label>
                <input
                  type="date"
                  value={espaciosForm.date}
                  onChange={(e) => setEspaciosForm({ ...espaciosForm, date: e.target.value })}
                  min={new Date().toISOString().split('T')[0]}
                  className="servicios-form-input"
                  required
                />
              </div>

              <div className="servicios-form-grid">
                <div className="servicios-form-group">
                  <label className="servicios-form-label">Hora Inicio</label>
                  <input
                    type="time"
                    value={espaciosForm.startTime}
                    onChange={(e) => setEspaciosForm({ ...espaciosForm, startTime: e.target.value })}
                    className="servicios-form-input"
                    required
                  />
                </div>
                <div className="servicios-form-group">
                  <label className="servicios-form-label">Hora Fin</label>
                  <input
                    type="time"
                    value={espaciosForm.endTime}
                    onChange={(e) => setEspaciosForm({ ...espaciosForm, endTime: e.target.value })}
                    className="servicios-form-input"
                    required
                  />
                </div>
              </div>

              <div className="servicios-form-actions">
                <button
                  type="submit"
                  className="servicios-form-btn servicios-form-btn-primary"
                  disabled={loading}
                >
                  <Check className="servicios-form-btn-icon" />
                  {loading ? 'Creando...' : 'Crear Reserva'}
                </button>
                <button
                  type="button"
                  onClick={() => setView('list')}
                  className="servicios-form-btn servicios-form-btn-secondary"
                  disabled={loading}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Modal de Reserva Rápida desde Card */}
        {showReservaModal && espacioToReserve && (
          <div className="servicios-modal-overlay">
            <div className="servicios-modal">
              <div className="servicios-modal-header servicios-modal-header-success">
                <div>
                  <h3 className="servicios-modal-title">Reservar {espacioToReserve.nombre}</h3>
                  <p className="servicios-modal-subtitle">{espacioToReserve.tipo} - {espacioToReserve.facultad}</p>
                </div>
                <button
                  onClick={() => setShowReservaModal(false)}
                  className="servicios-modal-close"
                  disabled={loading}
                >
                  <X className="servicios-modal-close-icon" />
                </button>
              </div>

              <form onSubmit={(e) => { e.preventDefault(); handleSubmitReservaRapida(); }} className="servicios-modal-form">
                <div className="servicios-modal-info">
                  <p className="servicios-modal-info-text">
                    <strong>Información del espacio:</strong> {espacioToReserve.ubicacion} | Capacidad: {espacioToReserve.capacidad}
                  </p>
                </div>

                <div className="servicios-form-grid">
                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Ciclo *</label>
                    <select
                      value={reservaRapidaForm.ciclo}
                      onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, ciclo: e.target.value })}
                      className="servicios-form-input"
                      required
                      disabled={loading}
                    >
                      <option value="">Selecciona ciclo</option>
                      {['I', 'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X'].map(c => (
                        <option key={c} value={c}>{c}</option>
                      ))}
                    </select>
                  </div>

                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Curso / Tema *</label>
                    <input
                      type="text"
                      value={reservaRapidaForm.curso}
                      onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, curso: e.target.value })}
                      placeholder="Ej: Programación Web"
                      className="servicios-form-input"
                      required
                      disabled={loading}
                    />
                  </div>
                </div>

                <div className="servicios-form-group">
                  <label className="servicios-form-label">Fecha *</label>
                  <input
                    type="date"
                    value={reservaRapidaForm.date}
                    onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, date: e.target.value })}
                    min={new Date().toISOString().split('T')[0]}
                    className="servicios-form-input"
                    required
                    disabled={loading}
                  />
                </div>

                <div className="servicios-form-grid">
                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Hora Inicio *</label>
                    <input
                      type="time"
                      value={reservaRapidaForm.startTime}
                      onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, startTime: e.target.value })}
                      className="servicios-form-input"
                      required
                      disabled={loading}
                    />
                  </div>
                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Hora Fin *</label>
                    <input
                      type="time"
                      value={reservaRapidaForm.endTime}
                      onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, endTime: e.target.value })}
                      className="servicios-form-input"
                      required
                      disabled={loading}
                    />
                  </div>
                </div>

                <div className="servicios-form-group">
                  <label className="servicios-form-label">Motivo o Descripción (Opcional)</label>
                  <textarea
                    value={reservaRapidaForm.motivo}
                    onChange={(e) => setReservaRapidaForm({ ...reservaRapidaForm, motivo: e.target.value })}
                    placeholder="Describe el motivo de tu reserva..."
                    rows={3}
                    className="servicios-form-textarea"
                    disabled={loading}
                  />
                </div>

                <div className="servicios-modal-note">
                  <p className="servicios-modal-note-text">
                    <strong>Nota:</strong> Tu reserva quedará en estado "Pendiente" hasta que sea aprobada por el administrador.
                  </p>
                </div>

                <div className="servicios-modal-actions">
                  <button
                    type="submit"
                    className="servicios-modal-btn servicios-modal-btn-primary"
                    disabled={loading}
                  >
                    <Check className="servicios-modal-btn-icon" />
                    {loading ? 'Enviando...' : 'Enviar Solicitud'}
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowReservaModal(false)}
                    className="servicios-modal-btn servicios-modal-btn-secondary"
                    disabled={loading}
                  >
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};