import React, { useState, useEffect } from 'react';
import { LogOut, Server, Clock, Users, FileText, BarChart3, Plus, Edit, Trash2, Search, Download, X, Check, AlertCircle, Eye, Filter, ClipboardList, CheckCircle, XCircle } from 'lucide-react';
import './../styles/AdminDashboard.css';

interface User {
  id: string;
  email: string;
  user_metadata: {
    name: string;
    avatar_url: string;
    role?: string;
    login_type?: string;
  };
}

interface AdminDashboardProps {
  user: User;
}

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

interface Schedule {
  id: string;
  course: string;
  professor: string;
  days: string;
  startTime: string;
  endTime: string;
  location: string;
  students: string;
}

interface Usuario {
  id: string;
  code: string;
  nombres: string;
  apellidos: string;
  email: string;
  tipoDocumento: string;
  numeroDocumento: string;
  celular: string;
  facultad: string;
  escuela: string;
  role: string;
  genero: string;
  status: 'Activo' | 'Inactivo';
}

interface AuditLog {
  id: string;
  time: string;
  user: string;
  action: string;
  module: string;
  ip: string;
  status: 'success' | 'failed';
  motivo: string;
}

interface ReservaEspacio {
  id: string;
  type: 'laboratorio' | 'aula';
  resource: string;
  resourceId?: string;
  date: string;
  startTime: string;
  endTime: string;
  status: 'pending' | 'approved' | 'cancelled';
  motivo?: string;
  ciclo?: string;
  curso?: string;
  solicitante?: string;
  solicitanteEmail?: string;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({ user }) => {
  const [activeModule, setActiveModule] = useState<'labs' | 'schedules' | 'users' | 'audit' | 'reports' | 'reservas'>('labs');
  
  // Estados para modales
  const [showLabModal, setShowLabModal] = useState(false);
  const [showUserModal, setShowUserModal] = useState(false);
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [showMotivoModal, setShowMotivoModal] = useState(false);
  
  // Estados para edición
  const [editingEspacio, setEditingEspacio] = useState<Espacio | null>(null);
  const [editingUser, setEditingUser] = useState<Usuario | null>(null);
  const [editingSchedule, setEditingSchedule] = useState<Schedule | null>(null);
  
  // Estado de espacios (antes laboratorios)
  const [espacios, setEspacios] = useState<Espacio[]>([
    { id: '1', name: 'LAB-01', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: 'Edificio A - Piso 2', capacity: '30', resources: 'Proyector, Pizarra Digital', status: 'Disponible' },
    { id: '2', name: 'LAB-02', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: 'Edificio A - Piso 3', capacity: '25', resources: 'Proyector', status: 'Disponible' },
    { id: '3', name: 'Aula A-301', tipo: 'Aula', facultad: 'Ingeniería', escuela: 'Civil', location: 'Edificio A - Piso 3', capacity: '45', resources: 'Proyector', status: 'Disponible' },
    { id: '4', name: 'Aula B-201', tipo: 'Aula', facultad: 'Ingeniería', escuela: 'Industrial', location: 'Edificio B - Piso 2', capacity: '50', resources: 'Proyector, Pizarra', status: 'Disponible' },
    { id: '5', name: 'LAB-03', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Electrónica', location: 'Edificio B - Piso 1', capacity: '40', resources: 'Proyector, Pizarra Digital, Audio', status: 'En Mantenimiento' },
  ]);

  const [espacioForm, setEspacioForm] = useState({
    name: '',
    tipo: 'Laboratorio' as 'Aula' | 'Laboratorio',
    facultad: 'Ingeniería',
    escuela: 'Sistemas',
    location: '',
    capacity: '',
    resources: '',
    status: 'Disponible' as 'Disponible' | 'En Mantenimiento'
  });

  // Estado de horarios
  const [schedules, setSchedules] = useState<Schedule[]>([
    { id: '1', course: 'Matemáticas III', professor: 'Dr. García', days: 'Lun-Mié', startTime: '08:00', endTime: '10:00', location: 'Aula A-301', students: '45' },
    { id: '2', course: 'Programación Avanzada', professor: 'Ing. López', days: 'Mar-Jue', startTime: '10:00', endTime: '12:00', location: 'LAB-01', students: '38' },
    { id: '3', course: 'Base de Datos', professor: 'Ing. Rodríguez', days: 'Mié-Vie', startTime: '14:00', endTime: '16:00', location: 'LAB-02', students: '42' },
    { id: '4', course: 'Cálculo II', professor: 'Dr. Ramírez', days: 'Lun-Mié-Vie', startTime: '14:00', endTime: '16:00', location: 'Aula B-201', students: '50' },
    { id: '5', course: 'Física I', professor: 'Dra. Sánchez', days: 'Mar-Jue', startTime: '08:00', endTime: '10:00', location: 'LAB-03', students: '35' },
  ]);

  const [scheduleForm, setScheduleForm] = useState({
    course: '',
    professor: '',
    days: {
      lunes: false,
      martes: false,
      miercoles: false,
      jueves: false,
      viernes: false,
      sabado: false
    },
    startTime: '',
    endTime: '',
    location: '',
    students: ''
  });

  // Estado de usuarios
  const [usuarios, setUsuarios] = useState<Usuario[]>([
    { 
      id: '1', 
      code: '2023077282', 
      nombres: 'Juan Carlos', 
      apellidos: 'Pérez González',
      email: '2023077282@upt.edu.pe', 
      tipoDocumento: 'DNI',
      numeroDocumento: '74589632',
      celular: '987654321',
      facultad: 'Ingeniería',
      escuela: 'Sistemas',
      role: 'Estudiante',
      genero: 'Masculino',
      status: 'Activo' 
    },
    { 
      id: '2', 
      code: 'DOC001', 
      nombres: 'María Elena', 
      apellidos: 'García Torres',
      email: 'garcia@upt.edu.pe', 
      tipoDocumento: 'DNI',
      numeroDocumento: '45789632',
      celular: '965874123',
      facultad: 'Ingeniería',
      escuela: 'Sistemas',
      role: 'Docente',
      genero: 'Femenino',
      status: 'Activo' 
    },
  ]);

  const [userForm, setUserForm] = useState({
    code: '',
    nombres: '',
    apellidos: '',
    email: '',
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    celular: '',
    facultad: 'Ingeniería',
    escuela: 'Sistemas',
    role: 'Estudiante',
    genero: 'Masculino',
    password: '',
    status: 'Activo' as 'Activo' | 'Inactivo'
  });

  // Estado de auditoría
  const [auditLogs, setAuditLogs] = useState<AuditLog[]>([
    { id: '1', time: '10:23:45', user: 'admin', action: 'Login', module: 'Auth', ip: '192.168.1.100', status: 'success', motivo: 'Acceso al sistema' },
    { id: '2', time: '10:22:12', user: '2023077282', action: 'Crear Reserva', module: 'Reservas', ip: '192.168.1.105', status: 'success', motivo: 'Reserva de LAB-01' },
    { id: '3', time: '10:20:03', user: 'unknown', action: 'Login', module: 'Auth', ip: '192.168.1.200', status: 'failed', motivo: 'Credenciales inválidas' },
    { id: '4', time: '10:18:55', user: 'DOC001', action: 'Actualizar Horario', module: 'Horarios', ip: '192.168.1.110', status: 'success', motivo: 'Cambio de aula' },
    { id: '5', time: '10:15:30', user: 'admin', action: 'Eliminar Usuario', module: 'Usuarios', ip: '192.168.1.100', status: 'success', motivo: 'Usuario duplicado' },
    { id: '6', time: '10:12:18', user: 'admin', action: 'Rechazar Reserva', module: 'Reservas', ip: '192.168.1.100', status: 'success', motivo: 'Conflicto de horarios' },
  ]);

  const [searchTerm, setSearchTerm] = useState('');
  const [filterRole, setFilterRole] = useState('all');
  const [filterStatus, setFilterStatus] = useState('all');
  const [selectedAction, setSelectedAction] = useState<{ type: string; id: string } | null>(null);
  const [motivo, setMotivo] = useState('');

  // Estado de reservas
  const [reservas, setReservas] = useState<ReservaEspacio[]>([]);
  const [showMotivoRechazoModal, setShowMotivoRechazoModal] = useState(false);
  const [reservaToProcess, setReservaToProcess] = useState<ReservaEspacio | null>(null);
  const [motivoRechazo, setMotivoRechazo] = useState('');

  // Cargar reservas desde localStorage
  useEffect(() => {
    const loadReservas = () => {
      const reservasPendientes = JSON.parse(localStorage.getItem('reservas_pendientes') || '[]');
      setReservas(reservasPendientes);
    };
    
    loadReservas();
    
    // Actualizar cada 2 segundos para reflejar nuevas reservas
    const interval = setInterval(loadReservas, 2000);
    return () => clearInterval(interval);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('admin_session');
    window.location.reload();
  };

  const handleAprobarReserva = (reserva: ReservaEspacio) => {
    const updatedReservas = reservas.map(r => 
      r.id === reserva.id ? { ...r, status: 'approved' as const } : r
    );
    setReservas(updatedReservas);
    localStorage.setItem('reservas_pendientes', JSON.stringify(updatedReservas));
    addAuditLog('admin', 'Aprobar Reserva', 'Reservas', 'success', `Reserva de ${reserva.resource} aprobada`);
  };

  const handleIniciarRechazo = (reserva: ReservaEspacio) => {
    setReservaToProcess(reserva);
    setMotivoRechazo('');
    setShowMotivoRechazoModal(true);
  };

  const handleConfirmarRechazo = () => {
    if (!reservaToProcess || !motivoRechazo) return;

    const updatedReservas = reservas.map(r => 
      r.id === reservaToProcess.id ? { ...r, status: 'cancelled' as const, motivo: motivoRechazo } : r
    );
    setReservas(updatedReservas);
    localStorage.setItem('reservas_pendientes', JSON.stringify(updatedReservas));
    addAuditLog('admin', 'Rechazar Reserva', 'Reservas', 'success', motivoRechazo);
    
    setShowMotivoRechazoModal(false);
    setReservaToProcess(null);
    setMotivoRechazo('');
  };

  const handleCreateEspacio = () => {
    if (editingEspacio) {
      setEspacios(espacios.map(e => e.id === editingEspacio.id ? { ...espacioForm, id: editingEspacio.id } : e));
      addAuditLog('admin', 'Actualizar Espacio', 'Espacios', 'success', `Actualización de ${espacioForm.name}`);
    } else {
      setEspacios([...espacios, { ...espacioForm, id: Date.now().toString() }]);
      addAuditLog('admin', 'Crear Espacio', 'Espacios', 'success', `Creación de ${espacioForm.name}`);
    }
    setShowLabModal(false);
    setEditingEspacio(null);
    setEspacioForm({ name: '', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: '', capacity: '', resources: '', status: 'Disponible' });
  };

  const handleEditEspacio = (espacio: Espacio) => {
    setEditingEspacio(espacio);
    setEspacioForm(espacio);
    setShowLabModal(true);
  };

  const handleDeleteEspacio = (id: string) => {
    setSelectedAction({ type: 'delete-espacio', id });
    setShowMotivoModal(true);
  };

  const handleCreateSchedule = () => {
    const selectedDays = Object.entries(scheduleForm.days)
      .filter(([_, checked]) => checked)
      .map(([day]) => {
        const dayNames: Record<string, string> = {
          lunes: 'Lun',
          martes: 'Mar',
          miercoles: 'Mié',
          jueves: 'Jue',
          viernes: 'Vie',
          sabado: 'Sáb'
        };
        return dayNames[day];
      })
      .join('-');

    const newSchedule = {
      ...scheduleForm,
      days: selectedDays,
      id: editingSchedule?.id || Date.now().toString()
    };

    if (editingSchedule) {
      setSchedules(schedules.map(s => s.id === editingSchedule.id ? newSchedule : s));
      addAuditLog('admin', 'Actualizar Horario', 'Horarios', 'success', `Actualización de ${scheduleForm.course}`);
    } else {
      setSchedules([...schedules, newSchedule]);
      addAuditLog('admin', 'Crear Horario', 'Horarios', 'success', `Creación de horario para ${scheduleForm.course}`);
    }
    setShowScheduleModal(false);
    setEditingSchedule(null);
    setScheduleForm({ 
      course: '', 
      professor: '', 
      days: { lunes: false, martes: false, miercoles: false, jueves: false, viernes: false, sabado: false }, 
      startTime: '', 
      endTime: '', 
      location: '', 
      students: '' 
    });
  };

  const handleEditSchedule = (schedule: Schedule) => {
    setEditingSchedule(schedule);
    
    // Convertir string de días a objeto de checkboxes
    const daysMap: Record<string, keyof typeof scheduleForm.days> = {
      'Lun': 'lunes',
      'Mar': 'martes',
      'Mié': 'miercoles',
      'Jue': 'jueves',
      'Vie': 'viernes',
      'Sáb': 'sabado'
    };
    
    const daysObj = {
      lunes: false,
      martes: false,
      miercoles: false,
      jueves: false,
      viernes: false,
      sabado: false
    };
    
    schedule.days.split('-').forEach(day => {
      const key = daysMap[day];
      if (key) daysObj[key] = true;
    });
    
    setScheduleForm({
      course: schedule.course,
      professor: schedule.professor,
      days: daysObj,
      startTime: schedule.startTime,
      endTime: schedule.endTime,
      location: schedule.location,
      students: schedule.students
    });
    setShowScheduleModal(true);
  };

  const handleDeleteSchedule = (id: string) => {
    setSelectedAction({ type: 'delete-schedule', id });
    setShowMotivoModal(true);
  };

  const handleCreateUser = () => {
    if (editingUser) {
      setUsuarios(usuarios.map(u => u.id === editingUser.id 
        ? { 
            id: editingUser.id, 
            code: userForm.code,
            nombres: userForm.nombres,
            apellidos: userForm.apellidos,
            email: userForm.email,
            tipoDocumento: userForm.tipoDocumento,
            numeroDocumento: userForm.numeroDocumento,
            celular: userForm.celular,
            facultad: userForm.facultad,
            escuela: userForm.escuela,
            role: userForm.role,
            genero: userForm.genero,
            status: userForm.status
          }
        : u
      ));
      addAuditLog('admin', 'Actualizar Usuario', 'Usuarios', 'success', `Actualización de ${userForm.nombres} ${userForm.apellidos}`);
    } else {
      setUsuarios([...usuarios, { 
        id: Date.now().toString(), 
        code: userForm.code,
        nombres: userForm.nombres,
        apellidos: userForm.apellidos,
        email: userForm.email,
        tipoDocumento: userForm.tipoDocumento,
        numeroDocumento: userForm.numeroDocumento,
        celular: userForm.celular,
        facultad: userForm.facultad,
        escuela: userForm.escuela,
        role: userForm.role,
        genero: userForm.genero,
        status: userForm.status
      }]);
      addAuditLog('admin', 'Crear Usuario', 'Usuarios', 'success', `Creación de usuario ${userForm.nombres} ${userForm.apellidos}`);
    }
    setShowUserModal(false);
    setEditingUser(null);
    setUserForm({ 
      code: '', 
      nombres: '', 
      apellidos: '',
      email: '', 
      tipoDocumento: 'DNI',
      numeroDocumento: '',
      celular: '',
      facultad: 'Ingeniería',
      escuela: 'Sistemas',
      role: 'Estudiante',
      genero: 'Masculino',
      password: '', 
      status: 'Activo' 
    });
  };

  const handleEditUser = (usuario: Usuario) => {
    setEditingUser(usuario);
    setUserForm({ ...usuario, password: '' });
    setShowUserModal(true);
  };

  const handleDeleteUser = (id: string) => {
    setSelectedAction({ type: 'delete-user', id });
    setShowMotivoModal(true);
  };

  const handleConfirmAction = () => {
    if (!selectedAction || !motivo) return;

    if (selectedAction.type === 'delete-espacio') {
      const espacio = espacios.find(e => e.id === selectedAction.id);
      setEspacios(espacios.filter(e => e.id !== selectedAction.id));
      addAuditLog('admin', 'Eliminar Espacio', 'Espacios', 'success', motivo);
    } else if (selectedAction.type === 'delete-user') {
      const usuario = usuarios.find(u => u.id === selectedAction.id);
      setUsuarios(usuarios.filter(u => u.id !== selectedAction.id));
      addAuditLog('admin', 'Eliminar Usuario', 'Usuarios', 'success', motivo);
    } else if (selectedAction.type === 'delete-schedule') {
      const schedule = schedules.find(s => s.id === selectedAction.id);
      setSchedules(schedules.filter(s => s.id !== selectedAction.id));
      addAuditLog('admin', 'Eliminar Horario', 'Horarios', 'success', motivo);
    }

    setShowMotivoModal(false);
    setSelectedAction(null);
    setMotivo('');
  };

  const addAuditLog = (user: string, action: string, module: string, status: 'success' | 'failed', motivo: string) => {
    const newLog: AuditLog = {
      id: Date.now().toString(),
      time: new Date().toLocaleTimeString('es-ES'),
      user,
      action,
      module,
      ip: '192.168.1.100',
      status,
      motivo
    };
    setAuditLogs([newLog, ...auditLogs]);
  };

  const modules = [
    { id: 'labs' as const, name: 'Gestión de Espacios', icon: Server, color: 'blue' },
    { id: 'schedules' as const, name: 'Gestión de Horarios', icon: Clock, color: 'green' },
    { id: 'reservas' as const, name: 'Gestión de Reservas', icon: ClipboardList, color: 'indigo' },
    { id: 'users' as const, name: 'Gestión de Usuarios', icon: Users, color: 'purple' },
    { id: 'audit' as const, name: 'Auditoría del Sistema', icon: FileText, color: 'orange' },
    { id: 'reports' as const, name: 'Reportes y Estadísticas', icon: BarChart3, color: 'red' },
  ];

  const filteredUsers = usuarios.filter(u => {
    const matchSearch = u.nombres.toLowerCase().includes(searchTerm.toLowerCase()) || 
                       u.apellidos.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       u.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       u.code.toLowerCase().includes(searchTerm.toLowerCase());
    const matchRole = filterRole === 'all' || u.role === filterRole;
    const matchStatus = filterStatus === 'all' || u.status === filterStatus;
    return matchSearch && matchRole && matchStatus;
  });

  return (
    <div className="admin-dashboard">
      {/* Header con diseño mejorado */}
      <header className="admin-header">
        <div className="admin-header-container">
          <div className="admin-header-left">
            <div className="admin-logo">
              <Server className="admin-logo-icon" />
            </div>
            <div>
              <h1 className="admin-title">Panel de Control Administrativo</h1>
              <p className="admin-subtitle">IntegraUPT - Sistema de Gestión</p>
            </div>
          </div>
          <div className="admin-header-right">
            <div className="admin-user-info">
              <p className="admin-user-name">{user.user_metadata.name}</p>
              <p className="admin-user-role">Administrador del Sistema</p>
            </div>
            <div className="admin-user-avatar">
              <span className="admin-avatar-text">
                {user.user_metadata.name?.charAt(0) || 'A'}
              </span>
            </div>
            <button
              onClick={handleLogout}
              className="admin-logout-btn"
            >
              <LogOut className="admin-logout-icon" />
              Cerrar Sesión
            </button>
          </div>
        </div>
      </header>

      <div className="admin-main-container">
        {/* Módulos Grid con diseño mejorado */}
        <div className="admin-modules-grid">
          {modules.map((module) => {
            const Icon = module.icon;
            const isActive = activeModule === module.id;
            return (
              <button
                key={module.id}
                onClick={() => setActiveModule(module.id)}
                className={`admin-module-card ${isActive ? 'admin-module-active' : ''}`}
              >
                <div className={`admin-module-icon admin-module-${module.color}`}>
                  <Icon className="admin-module-icon-svg" />
                </div>
                <h3 className="admin-module-title">{module.name}</h3>
                <p className="admin-module-description">
                  {module.id === 'labs' && 'Administrar aulas y laboratorios'}
                  {module.id === 'schedules' && 'Configurar horarios académicos'}
                  {module.id === 'reservas' && 'Aprobar o rechazar reservas'}
                  {module.id === 'users' && 'Gestionar accesos y permisos'}
                  {module.id === 'audit' && 'Revisar logs del sistema'}
                  {module.id === 'reports' && 'Generar informes y análisis'}
                </p>
              </button>
            );
          })}
        </div>

        {/* Contenido del Módulo Activo */}
        <div className="admin-content">
          {/* Gestión de Espacios */}
          {activeModule === 'labs' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Gestión de Espacios</h2>
                  <p className="admin-content-subtitle">Administra aulas y laboratorios del sistema</p>
                </div>
                <button 
                  onClick={() => {
                    setEditingEspacio(null);
                    setEspacioForm({ name: '', tipo: 'Laboratorio', facultad: 'Ingeniería', escuela: 'Sistemas', location: '', capacity: '', resources: '', status: 'Disponible' });
                    setShowLabModal(true);
                  }}
                  className="admin-primary-btn admin-primary-blue"
                >
                  <Plus className="admin-btn-icon" />
                  Nuevo Espacio
                </button>
              </div>

              <div className="admin-search-container">
                <div className="admin-search-wrapper">
                  <Search className="admin-search-icon" />
                  <input
                    type="text"
                    placeholder="Buscar espacios..."
                    className="admin-search-input"
                  />
                </div>
              </div>

              <div className="admin-table-container">
                <table className="admin-table">
                  <thead className="admin-table-header">
                    <tr>
                      <th className="admin-table-th">Nombre</th>
                      <th className="admin-table-th">Tipo</th>
                      <th className="admin-table-th">Facultad</th>
                      <th className="admin-table-th">Escuela</th>
                      <th className="admin-table-th">Ubicación</th>
                      <th className="admin-table-th">Capacidad</th>
                      <th className="admin-table-th">Estado</th>
                      <th className="admin-table-th">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="admin-table-body">
                    {espacios.map((espacio) => (
                      <tr key={espacio.id} className="admin-table-row">
                        <td className="admin-table-td">{espacio.name}</td>
                        <td className="admin-table-td">
                          <span className={`admin-badge ${espacio.tipo === 'Laboratorio' ? 'admin-badge-blue' : 'admin-badge-purple'}`}>
                            {espacio.tipo}
                          </span>
                        </td>
                        <td className="admin-table-td">{espacio.facultad}</td>
                        <td className="admin-table-td">{espacio.escuela}</td>
                        <td className="admin-table-td">{espacio.location}</td>
                        <td className="admin-table-td">{espacio.capacity}</td>
                        <td className="admin-table-td">
                          <span className={`admin-badge ${espacio.status === 'Disponible' ? 'admin-badge-green' : 'admin-badge-yellow'}`}>
                            {espacio.status}
                          </span>
                        </td>
                        <td className="admin-table-td">
                          <div className="admin-actions">
                            <button 
                              onClick={() => handleEditEspacio(espacio)}
                              className="admin-action-btn admin-action-edit"
                              title="Editar"
                            >
                              <Edit className="admin-action-icon" />
                            </button>
                            <button 
                              onClick={() => handleDeleteEspacio(espacio.id)}
                              className="admin-action-btn admin-action-delete"
                              title="Eliminar"
                            >
                              <Trash2 className="admin-action-icon" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Gestión de Horarios */}
          {activeModule === 'schedules' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Gestión de Horarios</h2>
                  <p className="admin-content-subtitle">Configura y administra los horarios académicos</p>
                </div>
                <button 
                  onClick={() => {
                    setEditingSchedule(null);
                    setScheduleForm({ 
                      course: '', 
                      professor: '', 
                      days: { lunes: false, martes: false, miercoles: false, jueves: false, viernes: false, sabado: false }, 
                      startTime: '', 
                      endTime: '', 
                      location: '', 
                      students: '' 
                    });
                    setShowScheduleModal(true);
                  }}
                  className="admin-primary-btn admin-primary-green"
                >
                  <Plus className="admin-btn-icon" />
                  Nuevo Horario
                </button>
              </div>

              {/* Tabla Semanal de Horarios */}
              <div className="admin-schedule-weekly">
                <h3 className="admin-schedule-title">
                  <Clock className="admin-schedule-icon" />
                  Vista Semanal de Horarios
                </h3>
                <div className="admin-schedule-table-container">
                  <table className="admin-schedule-table">
                    <thead>
                      <tr className="admin-schedule-header">
                        <th className="admin-schedule-th">Hora</th>
                        <th className="admin-schedule-th">Lunes</th>
                        <th className="admin-schedule-th">Martes</th>
                        <th className="admin-schedule-th">Miércoles</th>
                        <th className="admin-schedule-th">Jueves</th>
                        <th className="admin-schedule-th">Viernes</th>
                        <th className="admin-schedule-th">Sábado</th>
                      </tr>
                    </thead>
                    <tbody>
                      {['08:00-10:00', '10:00-12:00', '12:00-14:00', '14:00-16:00', '16:00-18:00', '18:00-20:00'].map((timeSlot) => {
                        const [start] = timeSlot.split('-');
                        return (
                          <tr key={timeSlot}>
                            <td className="admin-schedule-time">{timeSlot}</td>
                            {['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'].map((day) => {
                              const courseInSlot = schedules.find(s => 
                                s.days.includes(day) && 
                                s.startTime === start
                              );
                              return (
                                <td key={day} className={`admin-schedule-cell ${courseInSlot ? 'admin-schedule-occupied' : 'admin-schedule-available'}`}>
                                  {courseInSlot ? (
                                    <div className="admin-schedule-course">
                                      <div className="admin-schedule-course-name">{courseInSlot.course}</div>
                                      <div className="admin-schedule-course-location">{courseInSlot.location}</div>
                                      <div className="admin-schedule-course-professor">{courseInSlot.professor}</div>
                                    </div>
                                  ) : (
                                    <div className="admin-schedule-empty">Disponible</div>
                                  )}
                                </td>
                              );
                            })}
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Lista de Horarios */}
              <div className="admin-schedule-list">
                <h3 className="admin-schedule-list-title">Lista de Horarios Registrados</h3>
                <div className="admin-search-wrapper">
                  <Search className="admin-search-icon" />
                  <input
                    type="text"
                    placeholder="Buscar horarios..."
                    className="admin-search-input"
                  />
                </div>
              </div>

              <div className="admin-table-container">
                <table className="admin-table">
                  <thead className="admin-table-header">
                    <tr>
                      <th className="admin-table-th">Curso</th>
                      <th className="admin-table-th">Docente</th>
                      <th className="admin-table-th">Días</th>
                      <th className="admin-table-th">Horario</th>
                      <th className="admin-table-th">Ubicación</th>
                      <th className="admin-table-th">Estudiantes</th>
                      <th className="admin-table-th">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="admin-table-body">
                    {schedules.map((schedule) => (
                      <tr key={schedule.id} className="admin-table-row">
                        <td className="admin-table-td">{schedule.course}</td>
                        <td className="admin-table-td">{schedule.professor}</td>
                        <td className="admin-table-td">{schedule.days}</td>
                        <td className="admin-table-td">{schedule.startTime} - {schedule.endTime}</td>
                        <td className="admin-table-td">{schedule.location}</td>
                        <td className="admin-table-td">{schedule.students}</td>
                        <td className="admin-table-td">
                          <div className="admin-actions">
                            <button 
                              onClick={() => handleEditSchedule(schedule)}
                              className="admin-action-btn admin-action-edit"
                              title="Editar"
                            >
                              <Edit className="admin-action-icon" />
                            </button>
                            <button 
                              onClick={() => handleDeleteSchedule(schedule.id)}
                              className="admin-action-btn admin-action-delete"
                              title="Eliminar"
                            >
                              <Trash2 className="admin-action-icon" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Gestión de Reservas */}
          {activeModule === 'reservas' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Gestión de Reservas</h2>
                  <p className="admin-content-subtitle">Aprueba o rechaza las solicitudes de reserva de espacios</p>
                </div>
                <div className="admin-reservas-stats">
                  <div className="admin-reserva-stat admin-reserva-pending">
                    <span className="admin-reserva-stat-text"><strong>Pendientes:</strong> {reservas.filter(r => r.status === 'pending').length}</span>
                  </div>
                  <div className="admin-reserva-stat admin-reserva-approved">
                    <span className="admin-reserva-stat-text"><strong>Aprobadas:</strong> {reservas.filter(r => r.status === 'approved').length}</span>
                  </div>
                </div>
              </div>

              {reservas.length === 0 ? (
                <div className="admin-empty-state">
                  <ClipboardList className="admin-empty-icon" />
                  <h3 className="admin-empty-title">No hay reservas registradas</h3>
                  <p className="admin-empty-description">Las solicitudes de reserva aparecerán aquí cuando los usuarios las creen</p>
                </div>
              ) : (
                <>
                  {/* Filtros */}
                  <div className="admin-reservas-filters">
                    <button className="admin-filter-btn admin-filter-pending">
                      Pendientes ({reservas.filter(r => r.status === 'pending').length})
                    </button>
                    <button className="admin-filter-btn admin-filter-approved">
                      Aprobadas ({reservas.filter(r => r.status === 'approved').length})
                    </button>
                    <button className="admin-filter-btn admin-filter-rejected">
                      Rechazadas ({reservas.filter(r => r.status === 'cancelled').length})
                    </button>
                  </div>

                  {/* Tabla de Reservas */}
                  <div className="admin-table-container">
                    <table className="admin-table">
                      <thead className="admin-table-header">
                        <tr>
                          <th className="admin-table-th">Espacio</th>
                          <th className="admin-table-th">Solicitante</th>
                          <th className="admin-table-th">Ciclo/Curso</th>
                          <th className="admin-table-th">Fecha</th>
                          <th className="admin-table-th">Horario</th>
                          <th className="admin-table-th">Estado</th>
                          <th className="admin-table-th">Acciones</th>
                        </tr>
                      </thead>
                      <tbody className="admin-table-body">
                        {reservas.map((reserva) => (
                          <tr key={reserva.id} className="admin-table-row">
                            <td className="admin-table-td">
                              <div className="admin-reserva-resource">
                                <div className={`admin-reserva-icon ${reserva.type === 'laboratorio' ? 'admin-reserva-lab' : 'admin-reserva-aula'}`}>
                                  {reserva.type === 'laboratorio' ? <Server className="admin-reserva-icon-svg" /> : <Clock className="admin-reserva-icon-svg" />}
                                </div>
                                <div>
                                  <div className="admin-reserva-name">{reserva.resource}</div>
                                  <div className="admin-reserva-type">{reserva.type}</div>
                                </div>
                              </div>
                            </td>
                            <td className="admin-table-td">
                              <div className="admin-reserva-solicitante">{reserva.solicitante}</div>
                              <div className="admin-reserva-email">{reserva.solicitanteEmail}</div>
                            </td>
                            <td className="admin-table-td">
                              <div className="admin-reserva-ciclo">Ciclo {reserva.ciclo}</div>
                              <div className="admin-reserva-curso">{reserva.curso}</div>
                            </td>
                            <td className="admin-table-td">
                              {new Date(reserva.date).toLocaleDateString('es-ES', { 
                                day: '2-digit', 
                                month: 'short', 
                                year: 'numeric' 
                              })}
                            </td>
                            <td className="admin-table-td">
                              {reserva.startTime} - {reserva.endTime}
                            </td>
                            <td className="admin-table-td">
                              <span className={`admin-badge ${
                                reserva.status === 'pending' 
                                  ? 'admin-badge-yellow' 
                                  : reserva.status === 'approved'
                                  ? 'admin-badge-green'
                                  : 'admin-badge-red'
                              }`}>
                                {reserva.status === 'pending' && '⏳ Pendiente'}
                                {reserva.status === 'approved' && '✓ Aprobada'}
                                {reserva.status === 'cancelled' && '✗ Rechazada'}
                              </span>
                            </td>
                            <td className="admin-table-td">
                              {reserva.status === 'pending' && (
                                <div className="admin-reserva-actions">
                                  <button
                                    onClick={() => handleAprobarReserva(reserva)}
                                    className="admin-reserva-approve"
                                    title="Aprobar"
                                  >
                                    <CheckCircle className="admin-reserva-action-icon" />
                                    <span className="admin-reserva-action-text">Aprobar</span>
                                  </button>
                                  <button
                                    onClick={() => handleIniciarRechazo(reserva)}
                                    className="admin-reserva-reject"
                                    title="Rechazar"
                                  >
                                    <XCircle className="admin-reserva-action-icon" />
                                    <span className="admin-reserva-action-text">Rechazar</span>
                                  </button>
                                </div>
                              )}
                              {reserva.status === 'approved' && (
                                <span className="admin-reserva-approved-text">Reserva aprobada</span>
                              )}
                              {reserva.status === 'cancelled' && (
                                <div className="admin-reserva-rejected">
                                  <div>Rechazada</div>
                                  {reserva.motivo && (
                                    <div className="admin-reserva-motivo">Motivo: {reserva.motivo}</div>
                                  )}
                                </div>
                              )}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </>
              )}
            </div>
          )}

          {/* Gestión de Usuarios */}
          {activeModule === 'users' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Gestión de Usuarios</h2>
                  <p className="admin-content-subtitle">Administra estudiantes, docentes y personal</p>
                </div>
                <button 
                  onClick={() => {
                    setEditingUser(null);
                    setUserForm({ 
                      code: '', 
                      nombres: '', 
                      apellidos: '',
                      email: '', 
                      tipoDocumento: 'DNI',
                      numeroDocumento: '',
                      celular: '',
                      facultad: 'Ingeniería',
                      escuela: 'Sistemas',
                      role: 'Estudiante',
                      genero: 'Masculino',
                      password: '', 
                      status: 'Activo' 
                    });
                    setShowUserModal(true);
                  }}
                  className="admin-primary-btn admin-primary-purple"
                >
                  <Plus className="admin-btn-icon" />
                  Nuevo Usuario
                </button>
              </div>

              <div className="admin-users-filters">
                <div className="admin-search-wrapper">
                  <Search className="admin-search-icon" />
                  <input
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Buscar usuarios..."
                    className="admin-search-input"
                  />
                </div>
                <select 
                  value={filterRole}
                  onChange={(e) => setFilterRole(e.target.value)}
                  className="admin-filter-select"
                >
                  <option value="all">Todos los roles</option>
                  <option value="Estudiante">Estudiantes</option>
                  <option value="Docente">Docentes</option>
                  <option value="Administrativo">Administrativos</option>
                </select>
                <select 
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  className="admin-filter-select"
                >
                  <option value="all">Todos los estados</option>
                  <option value="Activo">Activos</option>
                  <option value="Inactivo">Inactivos</option>
                </select>
              </div>

              <div className="admin-table-container">
                <table className="admin-table">
                  <thead className="admin-table-header">
                    <tr>
                      <th className="admin-table-th">Código</th>
                      <th className="admin-table-th">Nombres</th>
                      <th className="admin-table-th">Apellidos</th>
                      <th className="admin-table-th">Email</th>
                      <th className="admin-table-th">Rol</th>
                      <th className="admin-table-th">Estado</th>
                      <th className="admin-table-th">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="admin-table-body">
                    {filteredUsers.map((usuario) => (
                      <tr key={usuario.id} className="admin-table-row">
                        <td className="admin-table-td">{usuario.code}</td>
                        <td className="admin-table-td">{usuario.nombres}</td>
                        <td className="admin-table-td">{usuario.apellidos}</td>
                        <td className="admin-table-td">{usuario.email}</td>
                        <td className="admin-table-td">
                          <span className="admin-badge admin-badge-blue">
                            {usuario.role}
                          </span>
                        </td>
                        <td className="admin-table-td">
                          <span className={`admin-badge ${usuario.status === 'Activo' ? 'admin-badge-green' : 'admin-badge-gray'}`}>
                            {usuario.status}
                          </span>
                        </td>
                        <td className="admin-table-td">
                          <div className="admin-actions">
                            <button 
                              onClick={() => handleEditUser(usuario)}
                              className="admin-action-btn admin-action-edit"
                              title="Editar"
                            >
                              <Edit className="admin-action-icon" />
                            </button>
                            <button 
                              onClick={() => handleDeleteUser(usuario.id)}
                              className="admin-action-btn admin-action-delete"
                              title="Eliminar"
                            >
                              <Trash2 className="admin-action-icon" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Auditoría del Sistema */}
          {activeModule === 'audit' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Auditoría del Sistema</h2>
                  <p className="admin-content-subtitle">Registro detallado de todas las acciones del sistema</p>
                </div>
                <button className="admin-primary-btn admin-primary-orange">
                  <Download className="admin-btn-icon" />
                  Exportar Logs
                </button>
              </div>

              <div className="admin-audit-stats">
                <div className="admin-audit-stat admin-audit-blue">
                  <p className="admin-audit-stat-label">Eventos Hoy</p>
                  <p className="admin-audit-stat-value">1,247</p>
                </div>
                <div className="admin-audit-stat admin-audit-green">
                  <p className="admin-audit-stat-label">Logins Exitosos</p>
                  <p className="admin-audit-stat-value">358</p>
                </div>
                <div className="admin-audit-stat admin-audit-red">
                  <p className="admin-audit-stat-label">Intentos Fallidos</p>
                  <p className="admin-audit-stat-value">12</p>
                </div>
              </div>

              <div className="admin-table-container">
                <table className="admin-table">
                  <thead className="admin-table-header">
                    <tr>
                      <th className="admin-table-th">Timestamp</th>
                      <th className="admin-table-th">Usuario</th>
                      <th className="admin-table-th">Acción</th>
                      <th className="admin-table-th">Módulo</th>
                      <th className="admin-table-th">Motivo</th>
                      <th className="admin-table-th">IP</th>
                      <th className="admin-table-th">Estado</th>
                    </tr>
                  </thead>
                  <tbody className="admin-table-body">
                    {auditLogs.map((log) => (
                      <tr key={log.id} className="admin-table-row">
                        <td className="admin-table-td admin-audit-time">{log.time}</td>
                        <td className="admin-table-td">{log.user}</td>
                        <td className="admin-table-td">{log.action}</td>
                        <td className="admin-table-td">{log.module}</td>
                        <td className="admin-table-td admin-audit-motivo">
                          <span className="admin-audit-motivo-badge">
                            {log.motivo}
                          </span>
                        </td>
                        <td className="admin-table-td admin-audit-ip">{log.ip}</td>
                        <td className="admin-table-td">
                          <span className={`admin-badge ${log.status === 'success' ? 'admin-badge-green' : 'admin-badge-red'}`}>
                            {log.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Reportes y Estadísticas */}
          {activeModule === 'reports' && (
            <div>
              <div className="admin-content-header">
                <div>
                  <h2 className="admin-content-title">Reportes y Estadísticas</h2>
                  <p className="admin-content-subtitle">Análisis y métricas del sistema</p>
                </div>
                <button className="admin-primary-btn admin-primary-red">
                  <Download className="admin-btn-icon" />
                  Generar Reporte
                </button>
              </div>

              <div className="admin-reports-stats">
                {[
                  { label: 'Total Estudiantes', value: '2,458', change: '↑ 8%', color: 'blue' },
                  { label: 'Total Docentes', value: '124', change: '→ 0%', color: 'green' },
                  { label: 'Reservas Activas', value: '342', change: '↑ 12%', color: 'purple' },
                  { label: 'Tasa de Uso', value: '87%', change: '↑ 3%', color: 'orange' },
                ].map((stat, idx) => (
                  <div key={idx} className="admin-report-stat">
                    <p className="admin-report-stat-label">{stat.label}</p>
                    <p className="admin-report-stat-value">{stat.value}</p>
                    <p className={`admin-report-stat-change admin-report-${stat.color}`}>{stat.change} vs período anterior</p>
                  </div>
                ))}
              </div>

              <div className="admin-reports-charts">
                <div className="admin-report-chart">
                  <h3 className="admin-report-chart-title">Uso de Laboratorios</h3>
                  <div className="admin-usage-bars">
                    {[
                      { name: 'LAB-01', usage: 92, color: 'green' },
                      { name: 'LAB-02', usage: 78, color: 'blue' },
                      { name: 'LAB-03', usage: 65, color: 'yellow' },
                      { name: 'LAB-04', usage: 45, color: 'gray' },
                    ].map((lab, idx) => (
                      <div key={idx} className="admin-usage-bar">
                        <div className="admin-usage-info">
                          <span className="admin-usage-name">{lab.name}</span>
                          <span className="admin-usage-percent">{lab.usage}%</span>
                        </div>
                        <div className="admin-usage-track">
                          <div
                            className={`admin-usage-progress admin-usage-${lab.color}`}
                            style={{ width: `${lab.usage}%` }}
                          ></div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="admin-report-chart">
                  <h3 className="admin-report-chart-title">Reservas por Mes</h3>
                  <div className="admin-monthly-reservas">
                    {[
                      { month: 'Enero', count: 245 },
                      { month: 'Febrero', count: 289 },
                      { month: 'Marzo', count: 312 },
                      { month: 'Abril', count: 298 },
                      { month: 'Mayo', count: 334 },
                      { month: 'Junio', count: 356 },
                    ].map((item, idx) => (
                      <div key={idx} className="admin-monthly-item">
                        <span className="admin-monthly-name">{item.month}</span>
                        <span className="admin-monthly-count">{item.count} reservas</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Modal Espacio */}
      {showLabModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal">
            <div className="admin-modal-header">
              <h3 className="admin-modal-title">
                {editingEspacio ? 'Editar Espacio' : 'Nuevo Espacio'}
              </h3>
              <button onClick={() => setShowLabModal(false)} className="admin-modal-close">
                <X className="admin-modal-close-icon" />
              </button>
            </div>
            
            <form onSubmit={(e) => { e.preventDefault(); handleCreateEspacio(); }} className="admin-modal-form">
              {/* Información Básica */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Información Básica</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Nombre del Espacio *</label>
                    <input
                      type="text"
                      value={espacioForm.name}
                      onChange={(e) => setEspacioForm({ ...espacioForm, name: e.target.value })}
                      placeholder="Ej: LAB-04 o Aula C-201"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Tipo de Espacio *</label>
                    <select
                      value={espacioForm.tipo}
                      onChange={(e) => setEspacioForm({ ...espacioForm, tipo: e.target.value as 'Aula' | 'Laboratorio' })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Laboratorio">Laboratorio</option>
                      <option value="Aula">Aula</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Facultad *</label>
                    <select
                      value={espacioForm.facultad}
                      onChange={(e) => setEspacioForm({ ...espacioForm, facultad: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Ingeniería">Ingeniería</option>
                      <option value="Ciencias Empresariales">Ciencias Empresariales</option>
                      <option value="Arquitectura">Arquitectura</option>
                      <option value="Ciencias de la Salud">Ciencias de la Salud</option>
                      <option value="Derecho">Derecho</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Escuela *</label>
                    <select
                      value={espacioForm.escuela}
                      onChange={(e) => setEspacioForm({ ...espacioForm, escuela: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Sistemas">Ingeniería de Sistemas</option>
                      <option value="Civil">Ingeniería Civil</option>
                      <option value="Industrial">Ingeniería Industrial</option>
                      <option value="Mecánica">Ingeniería Mecánica</option>
                      <option value="Electrónica">Ingeniería Electrónica</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* Ubicación y Capacidad */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Ubicación y Capacidad</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Ubicación *</label>
                    <input
                      type="text"
                      value={espacioForm.location}
                      onChange={(e) => setEspacioForm({ ...espacioForm, location: e.target.value })}
                      placeholder="Ej: Edificio A - Piso 2"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Capacidad *</label>
                    <input
                      type="text"
                      value={espacioForm.capacity}
                      onChange={(e) => setEspacioForm({ ...espacioForm, capacity: e.target.value })}
                      placeholder="Ej: 30 o 45 estudiantes"
                      className="admin-form-input"
                      required
                    />
                  </div>
                </div>
              </div>

              {/* Recursos y Estado */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Recursos y Estado</h4>
                <div className="admin-form-stack">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Recursos Disponibles *</label>
                    <textarea
                      value={espacioForm.resources}
                      onChange={(e) => setEspacioForm({ ...espacioForm, resources: e.target.value })}
                      placeholder="Ej: Proyector, Pizarra Digital, Audio, Computadoras"
                      rows={3}
                      className="admin-form-textarea"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Estado *</label>
                    <select
                      value={espacioForm.status}
                      onChange={(e) => setEspacioForm({ ...espacioForm, status: e.target.value as 'Disponible' | 'En Mantenimiento' })}
                      className="admin-form-select"
                    >
                      <option value="Disponible">Disponible</option>
                      <option value="En Mantenimiento">En Mantenimiento</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="admin-modal-actions">
                <button
                  type="submit"
                  className="admin-modal-btn admin-modal-primary"
                >
                  <Check className="admin-modal-btn-icon" />
                  {editingEspacio ? 'Guardar Cambios' : 'Crear Espacio'}
                </button>
                <button
                  type="button"
                  onClick={() => setShowLabModal(false)}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Horario */}
      {showScheduleModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal admin-modal-sm">
            <div className="admin-modal-header">
              <h3 className="admin-modal-title">
                {editingSchedule ? 'Editar Horario' : 'Nuevo Horario'}
              </h3>
              <button onClick={() => setShowScheduleModal(false)} className="admin-modal-close">
                <X className="admin-modal-close-icon" />
              </button>
            </div>
            
            <form onSubmit={(e) => { e.preventDefault(); handleCreateSchedule(); }} className="admin-modal-form">
              <div className="admin-form-group">
                <label className="admin-form-label">Nombre del Curso</label>
                <input
                  type="text"
                  value={scheduleForm.course}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, course: e.target.value })}
                  placeholder="Ej: Cálculo Integral"
                  className="admin-form-input"
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label">Docente</label>
                <input
                  type="text"
                  value={scheduleForm.professor}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, professor: e.target.value })}
                  placeholder="Ej: Dr. Juan Pérez"
                  className="admin-form-input"
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label">Días de la semana *</label>
                <div className="admin-days-grid">
                  {[
                    { key: 'lunes', label: 'Lunes' },
                    { key: 'martes', label: 'Martes' },
                    { key: 'miercoles', label: 'Miércoles' },
                    { key: 'jueves', label: 'Jueves' },
                    { key: 'viernes', label: 'Viernes' },
                    { key: 'sabado', label: 'Sábado' }
                  ].map(({ key, label }) => (
                    <label key={key} className="admin-day-checkbox">
                      <input
                        type="checkbox"
                        checked={scheduleForm.days[key as keyof typeof scheduleForm.days]}
                        onChange={(e) => setScheduleForm({ 
                          ...scheduleForm, 
                          days: { ...scheduleForm.days, [key]: e.target.checked } 
                        })}
                        className="admin-checkbox"
                      />
                      <span className="admin-day-label">{label}</span>
                    </label>
                  ))}
                </div>
                <p className="admin-form-help">Selecciona al menos un día</p>
              </div>

              <div className="admin-form-grid">
                <div className="admin-form-group">
                  <label className="admin-form-label">Hora Inicio</label>
                  <input
                    type="time"
                    value={scheduleForm.startTime}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, startTime: e.target.value })}
                    className="admin-form-input"
                    required
                  />
                </div>
                <div className="admin-form-group">
                  <label className="admin-form-label">Hora Fin</label>
                  <input
                    type="time"
                    value={scheduleForm.endTime}
                    onChange={(e) => setScheduleForm({ ...scheduleForm, endTime: e.target.value })}
                    className="admin-form-input"
                    required
                  />
                </div>
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label">Ubicación</label>
                <input
                  type="text"
                  value={scheduleForm.location}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, location: e.target.value })}
                  placeholder="Ej: Aula A-301 o LAB-01"
                  className="admin-form-input"
                  required
                />
              </div>

              <div className="admin-form-group">
                <label className="admin-form-label">Número de Estudiantes</label>
                <input
                  type="number"
                  value={scheduleForm.students}
                  onChange={(e) => setScheduleForm({ ...scheduleForm, students: e.target.value })}
                  placeholder="Ej: 40"
                  className="admin-form-input"
                  required
                />
              </div>

              <div className="admin-modal-actions">
                <button
                  type="submit"
                  className="admin-modal-btn admin-modal-primary"
                >
                  <Check className="admin-modal-btn-icon" />
                  {editingSchedule ? 'Guardar Cambios' : 'Crear Horario'}
                </button>
                <button
                  type="button"
                  onClick={() => setShowScheduleModal(false)}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Usuario */}
      {showUserModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal admin-modal-lg">
            <div className="admin-modal-header">
              <h3 className="admin-modal-title">
                {editingUser ? 'Editar Usuario' : 'Nuevo Usuario'}
              </h3>
              <button onClick={() => setShowUserModal(false)} className="admin-modal-close">
                <X className="admin-modal-close-icon" />
              </button>
            </div>
            
            <form onSubmit={(e) => { e.preventDefault(); handleCreateUser(); }} className="admin-modal-form">
              {/* Información Personal */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Información Personal</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Nombres *</label>
                    <input
                      type="text"
                      value={userForm.nombres}
                      onChange={(e) => setUserForm({ ...userForm, nombres: e.target.value })}
                      placeholder="Ej: Juan Carlos"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Apellidos *</label>
                    <input
                      type="text"
                      value={userForm.apellidos}
                      onChange={(e) => setUserForm({ ...userForm, apellidos: e.target.value })}
                      placeholder="Ej: Pérez González"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Género *</label>
                    <select
                      value={userForm.genero}
                      onChange={(e) => setUserForm({ ...userForm, genero: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Masculino">Masculino</option>
                      <option value="Femenino">Femenino</option>
                      <option value="Otro">Otro</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Tipo de Documento *</label>
                    <select
                      value={userForm.tipoDocumento}
                      onChange={(e) => setUserForm({ ...userForm, tipoDocumento: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="DNI">DNI</option>
                      <option value="Carnet de Extranjería">Carnet de Extranjería</option>
                      <option value="Pasaporte">Pasaporte</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Número de Documento *</label>
                    <input
                      type="text"
                      value={userForm.numeroDocumento}
                      onChange={(e) => setUserForm({ ...userForm, numeroDocumento: e.target.value })}
                      placeholder="Ej: 74589632"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Celular *</label>
                    <input
                      type="tel"
                      value={userForm.celular}
                      onChange={(e) => setUserForm({ ...userForm, celular: e.target.value })}
                      placeholder="Ej: 987654321"
                      className="admin-form-input"
                      required
                    />
                  </div>
                </div>
              </div>

              {/* Información Académica */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Información Académica</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Código/ID *</label>
                    <input
                      type="text"
                      value={userForm.code}
                      onChange={(e) => setUserForm({ ...userForm, code: e.target.value })}
                      placeholder="Ej: 2023077284 o DOC002"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Email *</label>
                    <input
                      type="email"
                      value={userForm.email}
                      onChange={(e) => setUserForm({ ...userForm, email: e.target.value })}
                      placeholder="Ej: carlos.ramirez@upt.edu.pe"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Facultad *</label>
                    <select
                      value={userForm.facultad}
                      onChange={(e) => setUserForm({ ...userForm, facultad: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Ingeniería">Ingeniería</option>
                      <option value="Ciencias Empresariales">Ciencias Empresariales</option>
                      <option value="Arquitectura">Arquitectura</option>
                      <option value="Ciencias de la Salud">Ciencias de la Salud</option>
                      <option value="Derecho">Derecho</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Escuela *</label>
                    <select
                      value={userForm.escuela}
                      onChange={(e) => setUserForm({ ...userForm, escuela: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Sistemas">Ingeniería de Sistemas</option>
                      <option value="Civil">Ingeniería Civil</option>
                      <option value="Industrial">Ingeniería Industrial</option>
                      <option value="Mecánica">Ingeniería Mecánica</option>
                      <option value="Electrónica">Ingeniería Electrónica</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Rol *</label>
                    <select
                      value={userForm.role}
                      onChange={(e) => setUserForm({ ...userForm, role: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Estudiante">Estudiante</option>
                      <option value="Docente">Docente</option>
                      <option value="Administrativo">Administrativo</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Estado *</label>
                    <select
                      value={userForm.status}
                      onChange={(e) => setUserForm({ ...userForm, status: e.target.value as 'Activo' | 'Inactivo' })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Activo">Activo</option>
                      <option value="Inactivo">Inactivo</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* Credenciales */}
              {!editingUser && (
                <div className="admin-form-section">
                  <h4 className="admin-form-section-title">Credenciales de Acceso</h4>
                  <div className="admin-form-stack">
                    <div className="admin-form-group">
                      <label className="admin-form-label">Contraseña *</label>
                      <input
                        type="password"
                        value={userForm.password}
                        onChange={(e) => setUserForm({ ...userForm, password: e.target.value })}
                        placeholder="Contraseña inicial"
                        className="admin-form-input"
                        required
                      />
                      <p className="admin-form-help">La contraseña debe tener al menos 6 caracteres</p>
                    </div>
                  </div>
                </div>
              )}

              <div className="admin-modal-actions">
                <button
                  type="submit"
                  className="admin-modal-btn admin-modal-primary"
                >
                  <Check className="admin-modal-btn-icon" />
                  {editingUser ? 'Guardar Cambios' : 'Crear Usuario'}
                </button>
                <button
                  type="button"
                  onClick={() => setShowUserModal(false)}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Motivo */}
      {showMotivoModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal admin-modal-md">
            <div className="admin-modal-header admin-modal-header-danger">
              <h3 className="admin-modal-title">
                <AlertCircle className="admin-modal-title-icon" />
                Confirmar Acción
              </h3>
            </div>
            
            <div className="admin-modal-form">
              <p className="admin-modal-text">
                Esta acción requiere un motivo. Por favor, proporciona una justificación:
              </p>
              
              <div className="admin-form-group">
                <label className="admin-form-label">Motivo de la acción</label>
                <textarea
                  value={motivo}
                  onChange={(e) => setMotivo(e.target.value)}
                  placeholder="Describe el motivo de esta acción..."
                  rows={4}
                  className="admin-form-textarea"
                  required
                />
              </div>

              <div className="admin-modal-actions">
                <button
                  onClick={handleConfirmAction}
                  disabled={!motivo}
                  className="admin-modal-btn admin-modal-danger"
                >
                  <Check className="admin-modal-btn-icon" />
                  Confirmar
                </button>
                <button
                  onClick={() => {
                    setShowMotivoModal(false);
                    setSelectedAction(null);
                    setMotivo('');
                  }}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal Motivo de Rechazo de Reserva */}
      {showMotivoRechazoModal && reservaToProcess && (
        <div className="admin-modal-overlay">
          <div className="admin-modal admin-modal-md">
            <div className="admin-modal-header admin-modal-header-danger">
              <h3 className="admin-modal-title">
                <XCircle className="admin-modal-title-icon" />
                Rechazar Reserva
              </h3>
            </div>
            
            <div className="admin-modal-form">
              <div className="admin-reserva-info">
                <p className="admin-reserva-info-text">
                  <strong>Espacio:</strong> {reservaToProcess.resource}<br />
                  <strong>Solicitante:</strong> {reservaToProcess.solicitante}<br />
                  <strong>Fecha:</strong> {new Date(reservaToProcess.date).toLocaleDateString('es-ES')} | {reservaToProcess.startTime} - {reservaToProcess.endTime}
                </p>
              </div>

              <p className="admin-modal-text">
                Por favor, proporciona un motivo detallado para rechazar esta reserva:
              </p>
              
              <div className="admin-form-group">
                <label className="admin-form-label">Motivo del rechazo *</label>
                <textarea
                  value={motivoRechazo}
                  onChange={(e) => setMotivoRechazo(e.target.value)}
                  placeholder="Ej: El espacio ya está reservado para ese horario, conflicto con actividad programada, etc."
                  rows={4}
                  className="admin-form-textarea"
                  required
                />
              </div>

              <div className="admin-modal-actions">
                <button
                  onClick={handleConfirmarRechazo}
                  disabled={!motivoRechazo}
                  className="admin-modal-btn admin-modal-danger"
                >
                  <XCircle className="admin-modal-btn-icon" />
                  Confirmar Rechazo
                </button>
                <button
                  onClick={() => {
                    setShowMotivoRechazoModal(false);
                    setReservaToProcess(null);
                    setMotivoRechazo('');
                  }}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};