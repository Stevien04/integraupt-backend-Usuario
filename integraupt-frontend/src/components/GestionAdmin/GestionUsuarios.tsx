// components/GestionUsuarios.tsx
import React, { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search, X, Check, Filter } from 'lucide-react';

interface Usuario {
  id: string;
  codigo: string;
  nombres: string;
  apellidos: string;
  email: string;
  tipoDocumento: string;
  numeroDocumento: string;
  celular: string;
  facultad: string;
  escuela: string;
  rol: string;
  genero: string;
  estado: number;
  fechaCreacion?: string;
}

interface GestionUsuariosProps {
  onAuditLog: (user: string, action: string, module: string, status: 'success' | 'failed', motivo: string) => void;
}

export const GestionUsuarios: React.FC<GestionUsuariosProps> = ({ onAuditLog }) => {
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingUsuario, setEditingUsuario] = useState<Usuario | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRol, setFilterRol] = useState('all');
  const [filterEstado, setFilterEstado] = useState('all');
  
  const [formData, setFormData] = useState({
    codigo: '',
    nombres: '',
    apellidos: '',
    email: '',
    tipoDocumento: 'DNI',
    numeroDocumento: '',
    celular: '',
    facultad: 'FAING',
    escuela: 'Ing. de Sistemas',
    rol: 'ESTUDIANTE',
    genero: 'MASCULINO',
    password: '',
    estado: 1
  });

  // Cargar usuarios
  const loadUsuarios = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/usuarios');
      if (!response.ok) throw new Error('Error al cargar usuarios');
      
      const data = await response.json();
      const usuariosMapeados = Array.isArray(data) ? data.map((usuario: any) => ({
        id: String(usuario.id),
        codigo: usuario.codigo || '',
        nombres: usuario.nombres || '',
        apellidos: usuario.apellidos || '',
        email: usuario.email || '',
        tipoDocumento: usuario.tipoDocumento || 'DNI',
        numeroDocumento: usuario.numeroDocumento || '',
        celular: usuario.celular || '',
        facultad: usuario.facultadNombre || 'FAING',
        escuela: usuario.escuelaNombre || 'Ing. de Sistemas',
        rol: (usuario.rol || 'ESTUDIANTE').toUpperCase(),
        genero: (usuario.genero || 'OTRO').toUpperCase(),
        estado: typeof usuario.estado === 'number' ? usuario.estado : 0,
        fechaCreacion: usuario.fechaCreacion || ''
      })) : [];
      
      setUsuarios(usuariosMapeados);
    } catch (error) {
      console.error('Error:', error);
      onAuditLog('admin', 'Cargar Usuarios', 'Usuarios', 'failed', 'Error al cargar usuarios');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsuarios();
  }, []);

  const handleCreate = async () => {
    try {
      const usuarioData = {
        codigo: formData.codigo,
        nombres: formData.nombres,
        apellidos: formData.apellidos,
        email: formData.email,
        tipoDocumento: formData.tipoDocumento,
        numeroDocumento: formData.numeroDocumento,
        celular: formData.celular,
        facultadId: getFacultadId(formData.facultad),
        escuelaId: getEscuelaId(formData.escuela),
        rol: formData.rol,
        genero: formData.genero,
        password: formData.password,
        estado: formData.estado
      };

      const url = editingUsuario 
        ? `http://localhost:8080/api/usuarios/${editingUsuario.id}`
        : 'http://localhost:8080/api/usuarios';
      
      const method = editingUsuario ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuarioData)
      });

      if (!response.ok) throw new Error('Error al guardar usuario');

      const result = await response.json();
      
      if (result.success) {
        onAuditLog('admin', editingUsuario ? 'Actualizar Usuario' : 'Crear Usuario', 'Usuarios', 'success', 
          `${editingUsuario ? 'Actualización' : 'Creación'} de ${formData.nombres} ${formData.apellidos}`);
        await loadUsuarios();
        resetForm();
      }
    } catch (error) {
      console.error('Error:', error);
      onAuditLog('admin', editingUsuario ? 'Actualizar Usuario' : 'Crear Usuario', 'Usuarios', 'failed', 'Error al guardar usuario');
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;

    try {
      const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) throw new Error('Error al eliminar usuario');

      const result = await response.json();
      if (result.success) {
        onAuditLog('admin', 'Eliminar Usuario', 'Usuarios', 'success', 'Usuario eliminado');
        await loadUsuarios();
      }
    } catch (error) {
      console.error('Error:', error);
      onAuditLog('admin', 'Eliminar Usuario', 'Usuarios', 'failed', 'Error al eliminar usuario');
    }
  };

  const handleEdit = (usuario: Usuario) => {
    setEditingUsuario(usuario);
    setFormData({
      codigo: usuario.codigo,
      nombres: usuario.nombres,
      apellidos: usuario.apellidos,
      email: usuario.email,
      tipoDocumento: usuario.tipoDocumento,
      numeroDocumento: usuario.numeroDocumento,
      celular: usuario.celular,
      facultad: usuario.facultad,
      escuela: usuario.escuela,
      rol: usuario.rol,
      genero: usuario.genero,
      password: '', // No mostrar password actual por seguridad
      estado: usuario.estado
    });
    setShowModal(true);
  };

  const resetForm = () => {
    setFormData({
      codigo: '',
      nombres: '',
      apellidos: '',
      email: '',
      tipoDocumento: 'DNI',
      numeroDocumento: '',
      celular: '',
      facultad: 'FAING',
      escuela: 'Ing. de Sistemas',
      rol: 'ESTUDIANTE',
      genero: 'MASCULINO',
      password: '',
      estado: 1
    });
    setEditingUsuario(null);
    setShowModal(false);
  };

  // Helper functions para mapear IDs (deberías ajustar según tu backend)
  const getFacultadId = (facultad: string): number => {
    const clave = (facultad || '').trim().toUpperCase();
    const facultades: { [key: string]: number } = {
      'FAING': 1,
      'FADE': 2,
      'FACEM': 3,
      'FACEA': 3,
      'FAEDCOH': 4,
      'FACSA': 5,
      'FAU': 6
    };
    return facultades[clave] || 1;
  };

  const getEscuelaId = (escuela: string): number => {
    const clave = (escuela || '').trim().toUpperCase();
    const escuelas: { [key: string]: number } = {
      'ING. CIVIL': 1,
      'ING. DE SISTEMAS': 2,
      'ING. ELECTRONICA': 3,
      'ING. AGROINDUSTRIAL': 4,
      'ING. AMBIENTAL': 5,
      'ING. INDUSTRIAL': 6,
      'DERECHO': 7,
      'CIENCIAS CONTABLES Y FINANCIERAS': 8,
      'ECONOMIA Y MICROFINANZAS': 9,
      'ADMINISTRACION': 10,
      'ADMINISTRACION TURISTICO-HOTEL': 11,
      'ADMINISTRACION DE NEGOCIOS INTERNACIONALES': 12,
      'EDUCACION': 13,
      'CIENCIAS DE LA COMUNICACION': 14,
      'HUMANIDADES - PSICOLOGIA': 15,
      'MEDICINA HUMANA': 16,
      'ODONTOLOGIA': 17,
      'TECNOLOGIA MEDICA': 18,
      'ARQUITECTURA': 19
    };
    return escuelas[clave] || 1;
  };

  // Filtrar usuarios
  const filteredUsers = usuarios.filter(usuario => {
    const criterio = searchTerm.toLowerCase();
    const matchSearch =
      (usuario.nombres || '').toLowerCase().includes(criterio) ||
      (usuario.apellidos || '').toLowerCase().includes(criterio) ||
      (usuario.email || '').toLowerCase().includes(criterio) ||
      (usuario.codigo || '').toLowerCase().includes(criterio);
    const matchRol = filterRol === 'all' || (usuario.rol || '').toUpperCase() === filterRol;
    const matchEstado = filterEstado === 'all' || usuario.estado.toString() === filterEstado;
    return matchSearch && matchRol && matchEstado;
  });

  const getRolDisplay = (rol: string) => {
    const roles: { [key: string]: string } = {
      'ESTUDIANTE': 'Estudiante',
      'DOCENTE': 'Docente',
      'ADMINISTRATIVO': 'Administrativo',
      'ADMIN': 'Administrador'
    };
    return roles[rol] || rol;
  };

  const getGeneroDisplay = (genero: string) => {
    const generos: { [key: string]: string } = {
      'MASCULINO': 'Masculino',
      'FEMENINO': 'Femenino',
      'OTRO': 'Otro'
    };
    return generos[genero] || genero;
  };

  return (
    <div>
      {/* Header */}
      <div className="admin-content-header">
        <div>
          <h2 className="admin-content-title">Gestión de Usuarios</h2>
          <p className="admin-content-subtitle">Administra estudiantes, docentes y personal</p>
        </div>
        <button 
          onClick={() => setShowModal(true)}
          className="admin-primary-btn admin-primary-purple"
        >
          <Plus className="admin-btn-icon" />
          Nuevo Usuario
        </button>
      </div>

      {/* Filtros y Búsqueda - DISEÑO MEJORADO */}
      <div className="admin-filters-section">
        <div className="admin-filters-header">
          <Filter className="admin-search-icon" />
          <span className="admin-filters-title">Filtros y Búsqueda</span>
        </div>
        
        <div className="admin-filters-grid">
        {/* Búsqueda */}
        <div className="admin-filter-group admin-filter-search">
            <label className="admin-filter-label">Buscar usuarios</label>
            <div className="admin-search-wrapper admin-search-expanded">
            <Search className="admin-search-icon" />
            <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Buscar por nombre, apellido, email o código..."
                className="admin-search-input"
            />
            </div>
        </div>

          {/* Filtro por Rol */}
          <div className="admin-filter-group">
            <label className="admin-filter-label">Filtrar por rol</label>
            <select 
              value={filterRol}
              onChange={(e) => setFilterRol(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">Todos los roles</option>
              <option value="ESTUDIANTE">Estudiantes</option>
              <option value="DOCENTE">Docentes</option>
              <option value="ADMINISTRATIVO">Administrativos</option>
            </select>
          </div>

          {/* Filtro por Estado */}
          <div className="admin-filter-group">
            <label className="admin-filter-label">Filtrar por estado</label>
            <select 
              value={filterEstado}
              onChange={(e) => setFilterEstado(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">Todos los estados</option>
              <option value="1">Activos</option>
              <option value="0">Inactivos</option>
            </select>
          </div>
        </div>

        {/* Contador de resultados */}
        <div className="admin-results-count">
          <span className="admin-results-text">
            {filteredUsers.length} de {usuarios.length} usuarios encontrados
          </span>
        </div>
      </div>

      {/* Tabla */}
      {loading ? (
        <div className="admin-loading">
          <div className="admin-loading-spinner"></div>
          <p>Cargando usuarios...</p>
        </div>
      ) : (
        <div className="admin-table-container">
          <table className="admin-table">
            <thead className="admin-table-header">
              <tr>
                <th className="admin-table-th">Código</th>
                <th className="admin-table-th">Nombres</th>
                <th className="admin-table-th">Apellidos</th>
                <th className="admin-table-th">Email</th>
                <th className="admin-table-th">Rol</th>
                <th className="admin-table-th">Facultad</th>
                <th className="admin-table-th">Estado</th>
                <th className="admin-table-th">Acciones</th>
              </tr>
            </thead>
            <tbody className="admin-table-body">
              {filteredUsers.map((usuario) => (
                <tr key={usuario.id} className="admin-table-row">
                  <td className="admin-table-td">{usuario.codigo}</td>
                  <td className="admin-table-td">{usuario.nombres}</td>
                  <td className="admin-table-td">{usuario.apellidos}</td>
                  <td className="admin-table-td">{usuario.email}</td>
                  <td className="admin-table-td">
                    <span className={`admin-badge ${
                      usuario.rol === 'ESTUDIANTE' ? 'admin-badge-blue' :
                      usuario.rol === 'DOCENTE' ? 'admin-badge-green' :
                      usuario.rol === 'ADMINISTRATIVO' ? 'admin-badge-purple' :
                      'admin-badge-red'
                    }`}>
                      {getRolDisplay(usuario.rol)}
                    </span>
                  </td>
                  <td className="admin-table-td">{usuario.facultad}</td>
                  <td className="admin-table-td">
                    <span className={`admin-badge ${usuario.estado === 1 ? 'admin-badge-green' : 'admin-badge-gray'}`}>
                      {usuario.estado === 1 ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="admin-table-td">
                    <div className="admin-actions">
                      <button 
                        onClick={() => handleEdit(usuario)}
                        className="admin-action-btn admin-action-edit"
                        title="Editar"
                      >
                        <Edit className="admin-action-icon" />
                      </button>
                      <button 
                        onClick={() => handleDelete(usuario.id)}
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
      )}

      {/* Modal (se mantiene igual) */}
      {showModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal admin-modal-lg">
            <div className="admin-modal-header">
              <h3 className="admin-modal-title">
                {editingUsuario ? 'Editar Usuario' : 'Nuevo Usuario'}
              </h3>
              <button onClick={resetForm} className="admin-modal-close">
                <X className="admin-modal-close-icon" />
              </button>
            </div>
            
            <form onSubmit={(e) => { e.preventDefault(); handleCreate(); }} className="admin-modal-form">
              {/* Información Personal */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Información Personal</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Nombres *</label>
                    <input
                      type="text"
                      value={formData.nombres}
                      onChange={(e) => setFormData({ ...formData, nombres: e.target.value })}
                      placeholder="Ej: Juan Carlos"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Apellidos *</label>
                    <input
                      type="text"
                      value={formData.apellidos}
                      onChange={(e) => setFormData({ ...formData, apellidos: e.target.value })}
                      placeholder="Ej: Pérez González"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Género *</label>
                    <select
                      value={formData.genero}
                      onChange={(e) => setFormData({ ...formData, genero: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="MASCULINO">Masculino</option>
                      <option value="FEMENINO">Femenino</option>
                      <option value="OTRO">Otro</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Tipo de Documento *</label>
                    <select
                      value={formData.tipoDocumento}
                      onChange={(e) => setFormData({ ...formData, tipoDocumento: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="DNI">DNI</option>
                      <option value="CARNET_EXTRANJERIA">Carnet de Extranjería</option>
                      <option value="PASAPORTE">Pasaporte</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Número de Documento *</label>
                    <input
                      type="text"
                      value={formData.numeroDocumento}
                      onChange={(e) => setFormData({ ...formData, numeroDocumento: e.target.value })}
                      placeholder="Ej: 74589632"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Celular *</label>
                    <input
                      type="tel"
                      value={formData.celular}
                      onChange={(e) => setFormData({ ...formData, celular: e.target.value })}
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
                      value={formData.codigo}
                      onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
                      placeholder="Ej: 2023077284 o DOC002"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Email *</label>
                    <input
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      placeholder="Ej: carlos.ramirez@upt.edu.pe"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Facultad *</label>
                    <select
                      value={formData.facultad}
                      onChange={(e) => setFormData({ ...formData, facultad: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="FAING">FAING</option>
                      <option value="FADE">FADE</option>
                      <option value="FACEM">FACEM</option>
                      <option value="FAEDCOH">FAEDCOH</option>
                      <option value="FACSA">FACSA</option>
                      <option value="FAU">FAU</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Escuela *</label>
                    <select
                      value={formData.escuela}
                      onChange={(e) => setFormData({ ...formData, escuela: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Ing. de Sistemas">Ing. de Sistemas</option>
                      <option value="Ing. Civil">Ing. Civil</option>
                      <option value="Ing. Electronica">Ing. Electronica</option>
                      <option value="Ing. Agroindustrial">Ing. Agroindustrial</option>
                      <option value="Ing. Ambiental">Ing. Ambiental</option>
                      <option value="Ing. Industrial">Ing. Industrial</option>
                      <option value="Derecho">Derecho</option>
                      <option value="Ciencias Contables y Financieras">Ciencias Contables y Financieras</option>
                      <option value="Economia y Microfinanzas">Economia y Microfinanzas</option>
                      <option value="Administracion">Administracion</option>
                      <option value="Administracion Turistico-Hotel">Administracion Turistico-Hotel</option>
                      <option value="Administracion de Negocios Internacionales">Administracion de Negocios Internacionales</option>
                      <option value="Educacion">Educacion</option>
                      <option value="Ciencias de la Comunicacion">Ciencias de la Comunicacion</option>
                      <option value="Humanidades - Psicologia">Humanidades - Psicologia</option>
                      <option value="Medicina Humana">Medicina Humana</option>
                      <option value="Odontologia">Odontologia</option>
                      <option value="Tecnologia Medica">Tecnologia Medica</option>
                      <option value="Arquitectura">Arquitectura</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Rol *</label>
                    <select
                      value={formData.rol}
                      onChange={(e) => setFormData({ ...formData, rol: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="ESTUDIANTE">Estudiante</option>
                      <option value="DOCENTE">Docente</option>
                      <option value="ADMINISTRATIVO">Administrativo</option>
                    </select>
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Estado *</label>
                    <select
                      value={formData.estado}
                      onChange={(e) => setFormData({ ...formData, estado: parseInt(e.target.value) })}
                      className="admin-form-select"
                      required
                    >
                      <option value={1}>Activo</option>
                      <option value={0}>Inactivo</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* Credenciales */}
              {!editingUsuario && (
                <div className="admin-form-section">
                  <h4 className="admin-form-section-title">Credenciales de Acceso</h4>
                  <div className="admin-form-stack">
                    <div className="admin-form-group">
                      <label className="admin-form-label">Contraseña *</label>
                      <input
                        type="password"
                        value={formData.password}
                        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        placeholder="Contraseña inicial"
                        className="admin-form-input"
                        required={!editingUsuario}
                        minLength={6}
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
                  {editingUsuario ? 'Guardar Cambios' : 'Crear Usuario'}
                </button>
                <button
                  type="button"
                  onClick={resetForm}
                  className="admin-modal-btn admin-modal-secondary"
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
