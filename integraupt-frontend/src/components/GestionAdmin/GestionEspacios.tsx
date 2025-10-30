import React, { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Search, X, Check, Filter } from 'lucide-react';

interface Espacio {
  id: string;
  codigo: string;
  nombre: string;
  tipo: 'Laboratorio' | 'Salon';
  facultad: string;
  escuela: string;
  ubicacion: string;
  capacidad: number;
  equipamiento: string;
  estado: number;
}

interface GestionEspaciosProps {
  onAuditLog: (user: string, action: string, module: string, status: 'success' | 'failed', motivo: string) => void;
}

export const GestionEspacios: React.FC<GestionEspaciosProps> = ({ onAuditLog }) => {
  const [espacios, setEspacios] = useState<Espacio[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingEspacio, setEditingEspacio] = useState<Espacio | null>(null);
  const [saving, setSaving] = useState(false);
  
  // Estados para filtros - AGREGAR ESTOS
  const [searchTerm, setSearchTerm] = useState('');
  const [filterTipo, setFilterTipo] = useState('all');
  const [filterFacultad, setFilterFacultad] = useState('all');
  const [filterEstado, setFilterEstado] = useState('all');
  
  const [formData, setFormData] = useState({
    codigo: '',
    nombre: '',
    tipo: 'Laboratorio' as 'Laboratorio' | 'Salon' | 'Aula',
    facultad: 'FAING',
    escuela: 'Ing. de Sistemas',
    ubicacion: '',
    capacidad: '',
    equipamiento: '',
    estado: '1'
  });

  // Cargar espacios
  const loadEspacios = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/api/espacios');
      if (!response.ok) throw new Error('Error al cargar espacios');
      
      const data = await response.json();
      console.log('📦 Datos de la API:', data);
      
      const espaciosMapeados = data.map((espacio: any) => ({
        id: espacio.id.toString(),
        codigo: espacio.codigo,
        nombre: espacio.nombre,
        tipo: espacio.tipo,
        facultad: espacio.facultadNombre || espacio.facultad || 'FAING',
        escuela: espacio.escuelaNombre || espacio.escuela || 'Ing. de Sistemas',
        ubicacion: espacio.ubicacion,
        capacidad: espacio.capacidad,
        equipamiento: espacio.equipamiento || '',
        estado: espacio.estado
      }));
      
      setEspacios(espaciosMapeados);
    } catch (error) {
      console.error('❌ Error:', error);
      onAuditLog('admin', 'Cargar Espacios', 'Espacios', 'failed', 'Error al cargar espacios');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadEspacios();
  }, []);

  // Función para filtrar espacios - AGREGAR ESTA FUNCIÓN
  const filteredEspacios = espacios.filter(espacio => {
    const matchSearch = espacio.nombre.toLowerCase().includes(searchTerm.toLowerCase()) || 
                       espacio.codigo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                       espacio.ubicacion?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchTipo = filterTipo === 'all' || espacio.tipo.toLowerCase() === filterTipo.toLowerCase();
    
    const matchFacultad = filterFacultad === 'all' || espacio.facultad.toLowerCase().includes(filterFacultad.toLowerCase());
    
    const matchEstado = filterEstado === 'all' || 
                       (filterEstado === 'disponible' && espacio.estado === 1) ||
                       (filterEstado === 'mantenimiento' && espacio.estado === 0);
    
    return matchSearch && matchTipo && matchFacultad && matchEstado;
  });

  const mapTipoToBackend = (tipo: string): 'Laboratorio' | 'Salon' => {
    console.log('🔄 Mapeando tipo:', tipo);
    
    // Convertir "Aula" a "Salon" para el backend
    if (tipo === 'Aula') {
      return 'Salon';
    }
    
    // Asegurar que solo se envíen los valores que el backend acepta
    if (tipo === 'Laboratorio' || tipo === 'Salon') {
      return tipo as 'Laboratorio' | 'Salon';
    }
    
    // Valor por defecto
    console.warn('⚠️ Tipo no reconocido, usando valor por defecto:', tipo);
    return 'Laboratorio';
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    
    console.log('🔄 Iniciando guardado...');
    console.log('📝 Datos del formulario:', formData);
    
    if (saving) {
      console.log('⏳ Ya se está guardando, evitando duplicado...');
      return;
    }

    // Validar campos requeridos
    if (!formData.codigo || !formData.nombre || !formData.ubicacion || !formData.capacidad) {
      alert('Por favor complete todos los campos requeridos (*)');
      return;
    }

    try {
      setSaving(true);
      
      // Preparar datos para el backend - CORREGIDO
      const espacioData = {
        codigo: formData.codigo.trim(),
        nombre: formData.nombre.trim(),
        ubicacion: formData.ubicacion.trim(),
        tipo: mapTipoToBackend(formData.tipo), // ← USA LA FUNCIÓN DE MAPEO
        capacidad: Number(formData.capacidad),
        equipamiento: formData.equipamiento.trim(),
        facultadId: 1,
        escuelaId: 2,
        estado: Number(formData.estado)
      };

      console.log('📤 Datos procesados para enviar:', espacioData);

      let url = 'http://localhost:8080/api/espacios';
      let method = 'POST';

      if (editingEspacio) {
        url = `http://localhost:8080/api/espacios/${editingEspacio.id}`;
        method = 'PUT';
        console.log(`🔄 Modo EDICIÓN - URL: ${url}, ID: ${editingEspacio.id}`);
      }

      const response = await fetch(url, {
        method,
        headers: { 
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(espacioData)
      });

      console.log('📥 Respuesta HTTP:', response.status, response.statusText);

      if (!response.ok) {
        const errorText = await response.text();
        console.error('❌ Error del servidor:', errorText);
        
        try {
          const errorJson = JSON.parse(errorText);
          throw new Error(errorJson.message || `Error ${response.status}`);
        } catch {
          throw new Error(`Error ${response.status}: ${errorText}`);
        }
      }

      const result = await response.json();
      console.log('✅ Respuesta del servidor:', result);
      
      if (result.success) {
        const action = editingEspacio ? 'Actualizar Espacio' : 'Crear Espacio';
        const motivo = `${editingEspacio ? 'Actualización' : 'Creación'} de ${formData.nombre}`;
        
        console.log(`✅ ${action} exitoso:`, motivo);
        
        onAuditLog('admin', action, 'Espacios', 'success', motivo);
        await loadEspacios();
        resetForm();
        
        alert(`${action} exitoso!`);
      } else {
        throw new Error(result.message || 'Error desconocido del servidor');
      }
    } catch (error) {
      console.error('❌ Error al guardar espacio:', error);
      const action = editingEspacio ? 'Actualizar Espacio' : 'Crear Espacio';
      onAuditLog('admin', action, 'Espacios', 'failed', 
        `Error: ${error instanceof Error ? error.message : 'Error desconocido'}`);
      
      alert(`Error al ${editingEspacio ? 'actualizar' : 'crear'} espacio: ${error instanceof Error ? error.message : 'Error desconocido'}`);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('¿Estás seguro de eliminar este espacio?')) return;

    try {
      console.log(`🗑️ Eliminando espacio ID: ${id}`);

      const response = await fetch(`http://localhost:8080/api/espacios/${id}`, {
        method: 'DELETE',
      });

      console.log('📥 Respuesta de eliminación:', response.status);

      if (response.ok) {
        // Si la respuesta es exitosa pero no tiene contenido
        onAuditLog('admin', 'Eliminar Espacio', 'Espacios', 'success', 'Espacio eliminado');
        await loadEspacios();
        alert('Espacio eliminado exitosamente!');
        return;
      }

      // Si hay error, leer el mensaje
      const result = await response.json();
      
      if (!result.success) {
        throw new Error(result.message || 'Error al eliminar espacio');
      }

    } catch (error) {
      console.error('❌ Error al eliminar:', error);
      
      let errorMessage = 'Error al eliminar espacio: ';
      
      if (error instanceof Error) {
        if (error.message.includes('500')) {
          errorMessage += 'No se puede eliminar el espacio porque tiene reservas asociadas o está en uso.';
        } else {
          errorMessage += error.message;
        }
      } else {
        errorMessage += 'Error interno del servidor';
      }
      
      onAuditLog('admin', 'Eliminar Espacio', 'Espacios', 'failed', errorMessage);
      alert(errorMessage);
    }
  };

  const handleEdit = (espacio: Espacio) => {
  console.log('✏️ Editando espacio:', espacio);
  
  // Convertir "Aula" a "Salon" para mostrar en el formulario
  const tipoParaFormulario = espacio.tipo === 'Aula' ? 'Salon' : espacio.tipo;
    
    setEditingEspacio(espacio);
    setFormData({
      codigo: espacio.codigo || '',
      nombre: espacio.nombre || '',
      tipo: tipoParaFormulario as 'Laboratorio' | 'Salon',
      facultad: espacio.facultad || 'FAING',
      escuela: espacio.escuela || 'Ing. de Sistemas',
      ubicacion: espacio.ubicacion || '',
      capacidad: espacio.capacidad?.toString() || '',
      equipamiento: espacio.equipamiento || '',
      estado: espacio.estado?.toString() || '1'
    });
    setShowModal(true);
  };

  const handleNew = () => {
    console.log('🆕 Creando nuevo espacio...');
    setEditingEspacio(null);
    setFormData({
      codigo: '',
      nombre: '',
      tipo: 'Laboratorio',
      facultad: 'FAING',
      escuela: 'Ing. de Sistemas',
      ubicacion: '',
      capacidad: '',
      equipamiento: '',
      estado: '1'
    });
    setShowModal(true);
  };

  const resetForm = () => {
    console.log('🗑️ Reseteando formulario...');
    setFormData({
      codigo: '',
      nombre: '',
      tipo: 'Laboratorio',
      facultad: 'FAING',
      escuela: 'Ing. de Sistemas',
      ubicacion: '',
      capacidad: '',
      equipamiento: '',
      estado: '1'
    });
    setEditingEspacio(null);
    setShowModal(false);
    setSaving(false);
  };

  const handleFormSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('🎯 Formulario enviado - Ejecutando handleSave...');
    handleSave(e);
  };

  return (
    <div>
      {/* Header */}
      <div className="admin-content-header">
        <div>
          <h2 className="admin-content-title">Gestión de Espacios</h2>
          <p className="admin-content-subtitle">Administra aulas y laboratorios del sistema</p>
        </div>
        <button 
          onClick={handleNew}
          className="admin-primary-btn admin-primary-blue"
        >
          <Plus className="admin-btn-icon" />
          Nuevo Espacio
        </button>
      </div>

      {/* Filtros y Búsqueda - NUEVO DISEÑO MEJORADO */}
      <div className="admin-filters-section">
        <div className="admin-filters-header">
          <Filter className="admin-search-icon" />
          <span className="admin-filters-title">Filtros y Búsqueda</span>
        </div>
        
        <div className="admin-filters-grid">
          {/* Búsqueda */}
          <div className="admin-filter-group admin-filter-search">
            <label className="admin-filter-label">Buscar espacios</label>
            <div className="admin-search-wrapper admin-search-expanded">
              <Search className="admin-search-icon" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Buscar por nombre, código o ubicación..."
                className="admin-search-input"
              />
            </div>
          </div>

          {/* Filtro por Tipo */}
          <div className="admin-filter-group">
            <label className="admin-filter-label">Filtrar por tipo</label>
            <select 
              value={filterTipo}
              onChange={(e) => setFilterTipo(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">Todos los tipos</option>
              <option value="laboratorio">Laboratorios</option>
              <option value="salon">Aulas</option>
            </select>
          </div>

          {/* Filtro por Facultad */}
          <div className="admin-filter-group">
            <label className="admin-filter-label">Filtrar por facultad</label>
            <select 
              value={filterFacultad}
              onChange={(e) => setFilterFacultad(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">Todas las facultades</option>
              <option value="FAING">FAING</option>
              <option value="FADE">FADE</option>
              <option value="FACEM">FACEM</option>
              <option value="FAEDCOH">FAEDCOH</option>
              <option value="FACSA">FACSA</option>
              <option value="FAU">FAU</option>
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
              <option value="disponible">Disponible</option>
              <option value="mantenimiento">En Mantenimiento</option>
            </select>
          </div>
        </div>

        {/* Contador de resultados */}
        <div className="admin-results-count">
          <span className="admin-results-text">
            Mostrando {filteredEspacios.length} de {espacios.length} espacios
          </span>
        </div>
      </div>

      {/* Tabla */}
      {loading ? (
        <div className="admin-loading">
          <div className="admin-loading-spinner"></div>
          <p>Cargando espacios...</p>
        </div>
      ) : (
        <div className="admin-table-container">
          <table className="admin-table">
            <thead className="admin-table-header">
              <tr>
                <th className="admin-table-th">Código</th>
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
              {filteredEspacios.map((espacio) => (
                <tr key={espacio.id} className="admin-table-row">
                  <td className="admin-table-td">{espacio.codigo || 'N/A'}</td>
                  <td className="admin-table-td">{espacio.nombre}</td>
                  <td className="admin-table-td">
                    <span className={`admin-badge ${espacio.tipo === 'Laboratorio' ? 'admin-badge-blue' : 'admin-badge-purple'}`}>
                      {espacio.tipo === 'Salon' ? 'Aula' : espacio.tipo}
                    </span>
                  </td>
                  <td className="admin-table-td">{espacio.facultad}</td>
                  <td className="admin-table-td">{espacio.escuela}</td>
                  <td className="admin-table-td">{espacio.ubicacion || 'No especificada'}</td>
                  <td className="admin-table-td">{espacio.capacidad}</td>
                  <td className="admin-table-td">
                    <span className={`admin-badge ${espacio.estado === 1 ? 'admin-badge-green' : 'admin-badge-yellow'}`}>
                      {espacio.estado === 1 ? 'Disponible' : 'En Mantenimiento'}
                    </span>
                  </td>
                  <td className="admin-table-td">
                    <div className="admin-actions">
                      <button 
                        onClick={() => handleEdit(espacio)}
                        className="admin-action-btn admin-action-edit"
                        title="Editar"
                      >
                        <Edit className="admin-action-icon" />
                      </button>
                      <button 
                        onClick={() => handleDelete(espacio.id)}
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

          {/* Mensaje cuando no hay resultados */}
          {filteredEspacios.length === 0 && (
            <div className="admin-empty-state">
              <Search className="admin-empty-icon" />
              <h3 className="admin-empty-title">No se encontraron espacios</h3>
              <p className="admin-empty-description">
                No hay espacios que coincidan con los filtros aplicados. 
                Intenta ajustar los criterios de búsqueda.
              </p>
            </div>
          )}
        </div>
      )}

      {/* Modal - Mantener igual que antes */}
      {showModal && (
        <div className="admin-modal-overlay">
          <div className="admin-modal">
            <div className="admin-modal-header">
              <h3 className="admin-modal-title">
                {editingEspacio ? `Editando: ${editingEspacio.nombre}` : 'Nuevo Espacio'}
              </h3>
              <button onClick={resetForm} className="admin-modal-close">
                <X className="admin-modal-close-icon" />
              </button>
            </div>
            
            <form onSubmit={handleFormSubmit} className="admin-modal-form">
              {/* Información Básica */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Información Básica</h4>
                <div className="admin-form-grid">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Código *</label>
                    <input
                      type="text"
                      value={formData.codigo}
                      onChange={(e) => setFormData({ ...formData, codigo: e.target.value })}
                      placeholder="Ej: ESP-001"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Nombre *</label>
                    <input
                      type="text"
                      value={formData.nombre}
                      onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                      placeholder="Ej: LAB-04 o Aula C-201"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Tipo *</label>
                    <select
                      value={formData.tipo}
                      onChange={(e) => setFormData({ ...formData, tipo: e.target.value as 'Laboratorio' | 'Salon' })}
                      className="admin-form-select"
                      required
                    >
                      <option value="Laboratorio">Laboratorio</option>
                      <option value="Salon">Salón</option>
                    </select>
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
                      <option value="FACEA">FACEA</option>
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
                      <option value="Ing. Industrial">Ing. Industrial</option>
                      <option value="Derecho">Derecho</option>
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
                      value={formData.ubicacion}
                      onChange={(e) => setFormData({ ...formData, ubicacion: e.target.value })}
                      placeholder="Ej: Edificio A - Piso 2"
                      className="admin-form-input"
                      required
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Capacidad *</label>
                    <input
                      type="number"
                      value={formData.capacidad}
                      onChange={(e) => setFormData({ ...formData, capacidad: e.target.value })}
                      placeholder="Ej: 30"
                      className="admin-form-input"
                      required
                      min="1"
                    />
                  </div>
                </div>
              </div>

              {/* Equipamiento y Estado */}
              <div className="admin-form-section">
                <h4 className="admin-form-section-title">Equipamiento y Estado</h4>
                <div className="admin-form-stack">
                  <div className="admin-form-group">
                    <label className="admin-form-label">Equipamiento</label>
                    <textarea
                      value={formData.equipamiento}
                      onChange={(e) => setFormData({ ...formData, equipamiento: e.target.value })}
                      placeholder="Ej: Proyector, Pizarra Digital, Audio, Computadoras"
                      rows={3}
                      className="admin-form-textarea"
                    />
                  </div>

                  <div className="admin-form-group">
                    <label className="admin-form-label">Estado *</label>
                    <select
                      value={formData.estado}
                      onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
                      className="admin-form-select"
                      required
                    >
                      <option value="1">Disponible</option>
                      <option value="0">En Mantenimiento</option>
                    </select>
                    <p className="admin-form-help">Estado actual: {formData.estado === '1' ? 'Disponible' : 'En Mantenimiento'}</p>
                  </div>
                </div>
              </div>

              <div className="admin-modal-actions">
                <button
                  type="submit"
                  className="admin-modal-btn admin-modal-primary"
                  disabled={saving}
                >
                  <Check className="admin-modal-btn-icon" />
                  {saving ? 'Guardando...' : (editingEspacio ? 'Guardar Cambios' : 'Crear Espacio')}
                </button>
                <button
                  type="button"
                  onClick={resetForm}
                  className="admin-modal-btn admin-modal-secondary"
                  disabled={saving}
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