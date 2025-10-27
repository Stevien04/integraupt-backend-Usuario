import React, { useState } from 'react';
import { Navigation } from './Navigation';
import { Calendar, Clock, MapPin, Search, Plus, Edit, Trash2, X, Check, Server, Monitor, MessageCircle, ArrowLeft, Heart, Brain, Users as UsersIcon, BookOpen, Eye, Building2 } from 'lucide-react';
import './../../styles/ServiciosScreen.css';

interface Espacio {
  id: string;
  name: string;
  tipo: 'Aula' | 'Laboratorio';
  facultad: string;
  escuela: string;
  location: string;
  capacity: string;
  resources: string;
  status: 'Disponible' | 'En Mantenimiento';
}

interface Reservation {
  id: string;
  type: 'laboratorio' | 'aula' | 'psicologia';
  resource: string;
  resourceId?: string;
  date: string;
  startTime: string;
  endTime: string;
  status: 'active' | 'pending' | 'cancelled' | 'approved';
  motivo?: string;
  ciclo?: string;
  curso?: string;
  solicitante?: string;
  solicitanteEmail?: string;
}

interface HorarioCurso {
  id: string;
  curso: string;
  profesor: string;
  startTime: string;
  endTime: string;
  days: string;
  students: string;
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
  const [selectedReservation, setSelectedReservation] = useState<Reservation | null>(null);
  const [showReservaModal, setShowReservaModal] = useState(false);
  const [espacioToReserve, setEspacioToReserve] = useState<Espacio | null>(null);

  // Espacios disponibles
  const [espacios] = useState<Espacio[]>([
    { id: '1', name: 'LAB-01', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: 'Edificio A - Piso 2', capacity: '30', resources: 'Proyector, Pizarra Digital', status: 'Disponible' },
    { id: '2', name: 'LAB-02', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: 'Edificio A - Piso 3', capacity: '25', resources: 'Proyector', status: 'Disponible' },
    { id: '3', name: 'Aula A-301', tipo: 'Aula', facultad: 'Ingeniería', escuela: 'Civil', location: 'Edificio A - Piso 3', capacity: '45', resources: 'Proyector', status: 'Disponible' },
    { id: '4', name: 'Aula B-201', tipo: 'Aula', facultad: 'Ingeniería', escuela: 'Industrial', location: 'Edificio B - Piso 2', capacity: '50', resources: 'Proyector, Pizarra', status: 'Disponible' },
    { id: '5', name: 'LAB-03', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Electrónica', location: 'Edificio B - Piso 1', capacity: '40', resources: 'Proyector, Pizarra Digital, Audio', status: 'En Mantenimiento' },
  ]);

  // Horarios de cursos por espacio (para la vista semanal)
  const [horariosEspacios] = useState<Record<string, HorarioCurso[]>>({
    '1': [ // LAB-01
      { id: '1', curso: 'Programación Avanzada', profesor: 'Ing. López', startTime: '10:00', endTime: '11:40', days: 'Mar-Jue', students: '38' },
      { id: '2', curso: 'Base de Datos II', profesor: 'Ing. Torres', startTime: '14:00', endTime: '15:40', days: 'Lun-Mié', students: '32' },
    ],
    '2': [ // LAB-02
      { id: '3', curso: 'Redes y Comunicaciones', profesor: 'Ing. Pérez', startTime: '08:00', endTime: '09:40', days: 'Mar-Jue', students: '28' },
      { id: '4', curso: 'Inteligencia Artificial', profesor: 'Dr. Ramírez', startTime: '16:00', endTime: '17:40', days: 'Lun-Mié-Vie', students: '25' },
    ],
    '3': [ // Aula A-301
      { id: '5', curso: 'Matemáticas III', profesor: 'Dr. García', startTime: '08:00', endTime: '09:40', days: 'Lun-Mié-Vie', students: '45' },
      { id: '6', curso: 'Física II', profesor: 'Dra. Sánchez', startTime: '12:00', endTime: '13:40', days: 'Mar-Jue', students: '42' },
    ],
    '4': [ // Aula B-201
      { id: '7', curso: 'Cálculo II', profesor: 'Dr. Ramírez', startTime: '14:00', endTime: '15:40', days: 'Lun-Mié-Vie', students: '50' },
      { id: '8', curso: 'Estadística', profesor: 'Ing. Castro', startTime: '10:00', endTime: '11:40', days: 'Mar-Jue-Sáb', students: '48' },
    ],
  });

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

  // Reservas de espacios
  const [reservationsEspacios, setReservationsEspacios] = useState<Reservation[]>([
    {
      id: '1',
      type: 'laboratorio',
      resource: 'LAB-01',
      date: '2025-10-10',
      startTime: '10:00',
      endTime: '12:00',
      status: 'active',
      ciclo: 'VI',
      curso: 'Programación Avanzada'
    },
    {
      id: '2',
      type: 'aula',
      resource: 'Aula A-301',
      date: '2025-10-11',
      startTime: '14:00',
      endTime: '16:00',
      status: 'active',
      ciclo: 'IV',
      curso: 'Cálculo II'
    }
  ]);

  // Citas de psicología
  const [citasPsicologia, setCitasPsicologia] = useState<Reservation[]>([
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

  const handleCreateEspacio = () => {
    const newReservation: Reservation = {
      id: Date.now().toString(),
      type: espaciosForm.type,
      resource: espaciosForm.resource,
      date: espaciosForm.date,
      startTime: espaciosForm.startTime,
      endTime: espaciosForm.endTime,
      ciclo: espaciosForm.ciclo,
      curso: espaciosForm.curso,
      status: 'pending',
      solicitante: user.user_metadata.name,
      solicitanteEmail: user.email
    };
    
    // Guardar en localStorage para que el admin pueda verla
    const reservasPendientes = JSON.parse(localStorage.getItem('reservas_pendientes') || '[]');
    localStorage.setItem('reservas_pendientes', JSON.stringify([...reservasPendientes, newReservation]));
    
    setReservationsEspacios([...reservationsEspacios, newReservation]);
    setEspaciosForm({ type: 'laboratorio', resource: '', date: '', startTime: '', endTime: '', ciclo: '', curso: '' });
    setView('list');
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

  const handleSubmitReservaRapida = () => {
    if (!espacioToReserve) return;

    const newReservation: Reservation = {
      id: Date.now().toString(),
      type: espacioToReserve.tipo === 'Laboratorio' ? 'laboratorio' : 'aula',
      resource: espacioToReserve.name,
      resourceId: espacioToReserve.id,
      date: reservaRapidaForm.date,
      startTime: reservaRapidaForm.startTime,
      endTime: reservaRapidaForm.endTime,
      ciclo: reservaRapidaForm.ciclo,
      curso: reservaRapidaForm.curso,
      motivo: reservaRapidaForm.motivo,
      status: 'pending',
      solicitante: user.user_metadata.name,
      solicitanteEmail: user.email
    };
    
    // Guardar en localStorage para que el admin pueda verla
    const reservasPendientes = JSON.parse(localStorage.getItem('reservas_pendientes') || '[]');
    localStorage.setItem('reservas_pendientes', JSON.stringify([...reservasPendientes, newReservation]));
    
    setReservationsEspacios([...reservationsEspacios, newReservation]);
    setShowReservaModal(false);
    setEspacioToReserve(null);
    
    // Mostrar mensaje de éxito
    alert('¡Reserva enviada! Tu solicitud está pendiente de aprobación por el administrador.');
  };

  const handleCreateCita = () => {
    // Calcular hora de fin (40 minutos después)
    const [hours, minutes] = citasForm.startTime.split(':').map(Number);
    const endMinutes = minutes + 40;
    const endHours = hours + Math.floor(endMinutes / 60);
    const endTime = `${endHours.toString().padStart(2, '0')}:${(endMinutes % 60).toString().padStart(2, '0')}`;

    const newCita: Reservation = {
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

  const handleCancelReservation = (id: string, type: 'espacios' | 'citas') => {
    if (window.confirm('¿Estás seguro de cancelar esta reserva?')) {
      if (type === 'espacios') {
        setReservationsEspacios(reservationsEspacios.map(r => 
          r.id === id ? { ...r, status: 'cancelled' as const } : r
        ));
      } else {
        setCitasPsicologia(citasPsicologia.map(r => 
          r.id === id ? { ...r, status: 'cancelled' as const } : r
        ));
      }
    }
  };

  const handleDeleteReservation = (id: string, type: 'espacios' | 'citas') => {
    if (window.confirm('¿Estás seguro de eliminar esta reserva?')) {
      if (type === 'espacios') {
        setReservationsEspacios(reservationsEspacios.filter(r => r.id !== id));
      } else {
        setCitasPsicologia(citasPsicologia.filter(r => r.id !== id));
      }
    }
  };

  const handleVerHorario = (espacio: Espacio) => {
    setSelectedEspacio(espacio);
    setView('horario-semanal');
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return 'servicios-status-active';
      case 'pending': return 'servicios-status-pending';
      case 'cancelled': return 'servicios-status-cancelled';
      default: return 'servicios-status-default';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'active': return 'Activa';
      case 'pending': return 'Pendiente';
      case 'cancelled': return 'Cancelada';
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

  // Generar bloques de tiempo de 50 minutos desde 08:00 hasta 22:00
  const generateTimeSlots = () => {
    const slots = [];
    let hour = 8;
    let minute = 0;

    while (hour < 22) {
      const startTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
      minute += 50;
      if (minute >= 60) {
        hour += Math.floor(minute / 60);
        minute = minute % 60;
      }
      const endTime = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
      slots.push(`${startTime}-${endTime}`);
      
      minute += 10; // Descanso de 10 minutos
      if (minute >= 60) {
        hour += Math.floor(minute / 60);
        minute = minute % 60;
      }
    }
    return slots;
  };

  const timeSlots = generateTimeSlots();

  // Función para verificar si un curso ocupa un bloque de tiempo en un día específico
  const getCursoInSlot = (day: string, timeSlot: string) => {
    if (!selectedEspacio) return null;
    
    const horarios = horariosEspacios[selectedEspacio.id] || [];
    const [slotStart] = timeSlot.split('-');
    
    for (const horario of horarios) {
      if (horario.days.includes(day)) {
        // Verificar si el slot está dentro del rango del curso
        if (slotStart >= horario.startTime && slotStart < horario.endTime) {
          return horario;
        }
      }
    }
    return null;
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
        {/* Menú Principal de Servicios */}
        {selectedService === 'menu' && (
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
        {selectedService === 'espacios' && view === 'espacios-grid' && (
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
                    espacio.status === 'Disponible' 
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
                        <h3 className="servicios-espacio-name">{espacio.name}</h3>
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
                      <span>{espacio.location}</span>
                    </div>
                    <div className="servicios-espacio-detail">
                      <UsersIcon className="servicios-espacio-detail-icon" />
                      <span>Capacidad: {espacio.capacity}</span>
                    </div>
                  </div>

                  <div className="servicios-espacio-recursos">
                    <p className="servicios-espacio-recursos-label">Recursos:</p>
                    <p className="servicios-espacio-recursos-text">{espacio.resources}</p>
                  </div>

                  <div className="servicios-espacio-actions">
                    <div className="servicios-espacio-status-container">
                      <span className={`servicios-espacio-status ${
                        espacio.status === 'Disponible' 
                          ? 'servicios-espacio-status-disponible' 
                          : 'servicios-espacio-status-mantenimiento'
                      }`}>
                        {espacio.status}
                      </span>

                      <button
                        onClick={() => handleVerHorario(espacio)}
                        className="servicios-espacio-btn servicios-espacio-btn-horario"
                      >
                        <Eye className="servicios-espacio-btn-icon" />
                        Ver Horario
                      </button>
                    </div>
                    
                    {espacio.status === 'Disponible' && (
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
                  <h1 className="servicios-espacio-info-title">{selectedEspacio.name}</h1>
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
                      Capacidad: {selectedEspacio.capacity}
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
              </h3>
              
              <div className="servicios-horario-table-container">
                <table className="servicios-horario-table">
                  <thead>
                    <tr className="servicios-horario-header">
                      <th className="servicios-horario-th servicios-horario-time-header">
                        Horario
                      </th>
                      {['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'].map((day) => (
                        <th key={day} className="servicios-horario-th">
                          {day}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {timeSlots.map((timeSlot) => (
                      <tr key={timeSlot} className="servicios-horario-row">
                        <td className="servicios-horario-time">
                          {timeSlot}
                        </td>
                        {['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'].map((day) => {
                          const curso = getCursoInSlot(day, timeSlot);
                          return (
                            <td
                              key={day}
                              className={`servicios-horario-cell ${
                                curso 
                                  ? 'servicios-horario-cell-ocupado' 
                                  : 'servicios-horario-cell-disponible'
                              }`}
                            >
                              {curso ? (
                                <div className="servicios-horario-curso">
                                  <div className="servicios-horario-curso-nombre">{curso.curso}</div>
                                  <div className="servicios-horario-curso-profesor">{curso.profesor}</div>
                                  <div className="servicios-horario-curso-estudiantes">{curso.students} estudiantes</div>
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

            {reservationsEspacios.length === 0 ? (
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
                {reservationsEspacios.map((reservation) => (
                  <div
                    key={reservation.id}
                    className={`servicios-reserva-card ${
                      reservation.status === 'active' ? 'servicios-reserva-active' :
                      reservation.status === 'pending' ? 'servicios-reserva-pending' : 'servicios-reserva-cancelled'
                    }`}
                  >
                    <div className="servicios-reserva-header">
                      <div className="servicios-reserva-type">
                        {getTypeIcon(reservation.type)}
                        <h3 className="servicios-reserva-type-label">{getTypeLabel(reservation.type)}</h3>
                      </div>
                      <span className={`servicios-reserva-status ${getStatusColor(reservation.status)}`}>
                        {getStatusLabel(reservation.status)}
                      </span>
                    </div>
                    
                    <div className="servicios-reserva-details">
                      <div className="servicios-reserva-detail">
                        <MapPin className="servicios-reserva-detail-icon" />
                        {reservation.resource}
                      </div>
                      <div className="servicios-reserva-detail">
                        <Calendar className="servicios-reserva-detail-icon" />
                        {new Date(reservation.date).toLocaleDateString('es-ES')}
                      </div>
                      <div className="servicios-reserva-detail">
                        <Clock className="servicios-reserva-detail-icon" />
                        {reservation.startTime} - {reservation.endTime}
                      </div>
                      {reservation.ciclo && (
                        <div className="servicios-reserva-info servicios-reserva-info-ciclo">
                          <strong>Ciclo:</strong> {reservation.ciclo}
                        </div>
                      )}
                      {reservation.curso && (
                        <div className="servicios-reserva-info servicios-reserva-info-curso">
                          <strong>Curso:</strong> {reservation.curso}
                        </div>
                      )}
                    </div>

                    {reservation.status !== 'cancelled' && (
                      <button
                        onClick={() => handleCancelReservation(reservation.id, 'espacios')}
                        className="servicios-reserva-action servicios-reserva-action-cancel"
                      >
                        <Trash2 className="servicios-reserva-action-icon" />
                        Cancelar
                      </button>
                    )}

                    {reservation.status === 'cancelled' && (
                      <button
                        onClick={() => handleDeleteReservation(reservation.id, 'espacios')}
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
                      <option key={e.id} value={e.name}>{e.name} - {e.location}</option>
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
                >
                  <Check className="servicios-form-btn-icon" />
                  Crear Reserva
                </button>
                <button
                  type="button"
                  onClick={() => setView('list')}
                  className="servicios-form-btn servicios-form-btn-secondary"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Vista de Citas y Asesorías */}
        {selectedService === 'citas' && (
          <div>
            <button
              onClick={() => setSelectedService('menu')}
              className="servicios-back-btn servicios-back-btn-citas"
            >
              <ArrowLeft className="servicios-back-icon" />
              Volver a Servicios
            </button>

            {view === 'list' && (
              <div>
                {/* Header con información del servicio */}
                <div className="servicios-citas-header">
                  <div className="servicios-citas-header-content">
                    <div className="servicios-citas-header-icon">
                      <MessageCircle className="servicios-citas-header-icon-svg" />
                    </div>
                    <div>
                      <h1 className="servicios-citas-title">Citas y Asesorías</h1>
                      <p className="servicios-citas-subtitle">Orientación Psicológica</p>
                    </div>
                  </div>

                  <div className="servicios-citas-info-grid">
                    <div className="servicios-citas-info-card">
                      <h3 className="servicios-citas-info-title">
                        <Heart className="servicios-citas-info-icon" />
                        Te aconsejamos en:
                      </h3>
                      <ul className="servicios-citas-info-list">
                        <li className="servicios-citas-info-item">
                          <span className="servicios-citas-info-bullet">•</span>
                          <span>Aspectos familiares</span>
                        </li>
                        <li className="servicios-citas-info-item">
                          <span className="servicios-citas-info-bullet">•</span>
                          <span>Habilidades sociales</span>
                        </li>
                        <li className="servicios-citas-info-item">
                          <span className="servicios-citas-info-bullet">•</span>
                          <span>Rendimiento académico</span>
                        </li>
                        <li className="servicios-citas-info-item">
                          <span className="servicios-citas-info-bullet">•</span>
                          <span>Temas personales a tratar</span>
                        </li>
                      </ul>
                    </div>

                    <div className="servicios-citas-info-card">
                      <h3 className="servicios-citas-info-title">
                        <Clock className="servicios-citas-info-icon" />
                        Información de la cita
                      </h3>
                      <div className="servicios-citas-info-details">
                        <p><strong className="servicios-citas-info-strong">Duración:</strong> 40 minutos</p>
                        <p><strong className="servicios-citas-info-strong">Modalidad:</strong> Presencial</p>
                        <p><strong className="servicios-citas-info-strong">Ubicación:</strong> Centro de Bienestar Estudiantil</p>
                      </div>
                    </div>
                  </div>

                  <div className="servicios-citas-header-actions">
                    <button
                      onClick={() => setView('new')}
                      className="servicios-citas-agendar-btn"
                    >
                      <Plus className="servicios-citas-agendar-icon" />
                      Agendar Cita
                    </button>
                  </div>
                </div>

                {/* Mis Citas */}
                <div className="servicios-citas-section">
                  <h2 className="servicios-citas-section-title">Mis Citas Agendadas</h2>
                  {citasPsicologia.length === 0 ? (
                    <div className="servicios-citas-empty">
                      <MessageCircle className="servicios-citas-empty-icon" />
                      <h3 className="servicios-citas-empty-title">No hay citas agendadas</h3>
                      <p className="servicios-citas-empty-description">Agenda tu primera cita de orientación psicológica</p>
                    </div>
                  ) : (
                    <div className="servicios-citas-grid">
                      {citasPsicologia.map((cita) => (
                        <div
                          key={cita.id}
                          className={`servicios-cita-card ${
                            cita.status === 'active' ? 'servicios-cita-active' :
                            cita.status === 'pending' ? 'servicios-cita-pending' : 'servicios-cita-cancelled'
                          }`}
                        >
                          <div className="servicios-cita-header">
                            <div className="servicios-cita-type">
                              <MessageCircle className="servicios-cita-type-icon" />
                              <h3 className="servicios-cita-type-label">Orientación Psicológica</h3>
                            </div>
                            <span className={`servicios-cita-status ${getStatusColor(cita.status)}`}>
                              {getStatusLabel(cita.status)}
                            </span>
                          </div>
                          
                          <div className="servicios-cita-details">
                            <div className="servicios-cita-detail">
                              <Calendar className="servicios-cita-detail-icon" />
                              {new Date(cita.date).toLocaleDateString('es-ES')}
                            </div>
                            <div className="servicios-cita-detail">
                              <Clock className="servicios-cita-detail-icon" />
                              {cita.startTime} - {cita.endTime} (40 min)
                            </div>
                            {cita.motivo && (
                              <div className="servicios-cita-info">
                                <strong>Motivo:</strong> {cita.motivo}
                              </div>
                            )}
                          </div>

                          {cita.status !== 'cancelled' && (
                            <button
                              onClick={() => handleCancelReservation(cita.id, 'citas')}
                              className="servicios-cita-action servicios-cita-action-cancel"
                            >
                              <Trash2 className="servicios-cita-action-icon" />
                              Cancelar
                            </button>
                          )}

                          {cita.status === 'cancelled' && (
                            <button
                              onClick={() => handleDeleteReservation(cita.id, 'citas')}
                              className="servicios-cita-action servicios-cita-action-delete"
                            >
                              <Trash2 className="servicios-cita-action-icon" />
                              Eliminar
                            </button>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Formulario Nueva Cita Psicológica */}
            {view === 'new' && (
              <div className="servicios-form-container">
                <div className="servicios-form-header">
                  <h2 className="servicios-form-title">Agendar Cita de Orientación</h2>
                  <button
                    onClick={() => setView('list')}
                    className="servicios-form-close"
                  >
                    <X className="servicios-form-close-icon" />
                  </button>
                </div>

                <div className="servicios-citas-info-note">
                  <p className="servicios-citas-info-note-text">
                    <strong>Duración de la cita:</strong> 40 minutos
                  </p>
                </div>

                <form onSubmit={(e) => { e.preventDefault(); handleCreateCita(); }} className="servicios-form">
                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Fecha</label>
                    <input
                      type="date"
                      value={citasForm.date}
                      onChange={(e) => setCitasForm({ ...citasForm, date: e.target.value })}
                      min={new Date().toISOString().split('T')[0]}
                      className="servicios-form-input"
                      required
                    />
                  </div>

                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Hora de Inicio</label>
                    <input
                      type="time"
                      value={citasForm.startTime}
                      onChange={(e) => setCitasForm({ ...citasForm, startTime: e.target.value })}
                      className="servicios-form-input"
                      required
                    />
                    <p className="servicios-form-help">La cita finalizará automáticamente 40 minutos después</p>
                  </div>

                  <div className="servicios-form-group">
                    <label className="servicios-form-label">Motivo de la Consulta</label>
                    <textarea
                      value={citasForm.motivo}
                      onChange={(e) => setCitasForm({ ...citasForm, motivo: e.target.value })}
                      placeholder="Describe brevemente el motivo de tu consulta..."
                      rows={4}
                      className="servicios-form-textarea"
                      required
                    />
                  </div>

                  <div className="servicios-form-actions">
                    <button
                      type="submit"
                      className="servicios-form-btn servicios-form-btn-citas"
                    >
                      <Check className="servicios-form-btn-icon" />
                      Agendar Cita
                    </button>
                    <button
                      type="button"
                      onClick={() => setView('list')}
                      className="servicios-form-btn servicios-form-btn-secondary"
                    >
                      Cancelar
                    </button>
                  </div>
                </form>
              </div>
            )}
          </div>
        )}

        {/* Modal de Reserva Rápida desde Card */}
        {showReservaModal && espacioToReserve && (
          <div className="servicios-modal-overlay">
            <div className="servicios-modal">
              <div className="servicios-modal-header servicios-modal-header-success">
                <div>
                  <h3 className="servicios-modal-title">Reservar {espacioToReserve.name}</h3>
                  <p className="servicios-modal-subtitle">{espacioToReserve.tipo} - {espacioToReserve.facultad}</p>
                </div>
                <button 
                  onClick={() => setShowReservaModal(false)} 
                  className="servicios-modal-close"
                >
                  <X className="servicios-modal-close-icon" />
                </button>
              </div>
              
              <form onSubmit={(e) => { e.preventDefault(); handleSubmitReservaRapida(); }} className="servicios-modal-form">
                <div className="servicios-modal-info">
                  <p className="servicios-modal-info-text">
                    <strong>Información del espacio:</strong> {espacioToReserve.location} | Capacidad: {espacioToReserve.capacity}
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
                  >
                    <Check className="servicios-modal-btn-icon" />
                    Enviar Solicitud
                  </button>
                  <button
                    type="button"
                    onClick={() => setShowReservaModal(false)}
                    className="servicios-modal-btn servicios-modal-btn-secondary"
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