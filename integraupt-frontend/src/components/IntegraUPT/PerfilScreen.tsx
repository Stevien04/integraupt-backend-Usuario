import React, { useState } from 'react';
import { Navigation } from './Navigation';
import { Edit3, Save, X, User, Mail, GraduationCap, Star, Users, Award } from 'lucide-react';
import './../../styles/PerfilScreen.css';

interface PerfilScreenProps {
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
  onSectionChange?: (section: 'home' | 'servicios' | 'eventos' | 'perfil', servicio?: 'espacios' | 'citas') => void;
  onBackToDashboard?: () => void;
}

export const PerfilScreen: React.FC<PerfilScreenProps> = ({ 
  user, 
  currentSection = 'perfil', 
  onSectionChange,
  onBackToDashboard 
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [profile, setProfile] = useState({
    name: user.user_metadata.name || 'Usuario',
    email: user.email,
    career: 'Ingeniería de Sistemas',
    semester: 8,
    bio: 'Estudiante apasionado por la tecnología y el desarrollo de software.',
    skills: ['Java', 'React', 'Spring Boot', 'MySQL'],
    interests: ['Desarrollo Web', 'Inteligencia Artificial', 'Startup'],
    achievements: ['Primer lugar en Hackathon UPT 2024', 'Proyecto destacado en curso de IA'],
    teams: ['Club de Programación', 'Equipo de Robótica'],
    phone: '+51 999 888 777',
    gpa: 16.8
  });

  const [editedProfile, setEditedProfile] = useState(profile);

  const handleEdit = () => {
    setIsEditing(true);
    setEditedProfile(profile);
  };

  const handleSave = () => {
    setProfile(editedProfile);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setEditedProfile(profile);
    setIsEditing(false);
  };

  const handleSkillAdd = (skill: string) => {
    if (skill.trim() && !editedProfile.skills.includes(skill.trim())) {
      setEditedProfile({
        ...editedProfile,
        skills: [...editedProfile.skills, skill.trim()]
      });
    }
  };

  const handleSkillRemove = (index: number) => {
    setEditedProfile({
      ...editedProfile,
      skills: editedProfile.skills.filter((_, i) => i !== index)
    });
  };

  return (
    <div className="perfil-screen-container">
      <Navigation 
        user={user} 
        currentSection={currentSection} 
        onSectionChange={onSectionChange} 
        onBackToDashboard={onBackToDashboard}
      />
      
      <main className="perfil-screen-main">
        <div className="perfil-screen-card">
          {/* Header del perfil */}
          <div className="perfil-screen-header">
            <div className="perfil-screen-header-content">
              <div className="perfil-screen-profile-info">
                <div className="perfil-screen-avatar-container">
                  {user.user_metadata.avatar_url ? (
                    <img
                      src={user.user_metadata.avatar_url}
                      alt="Profile"
                      className="perfil-screen-avatar"
                    />
                  ) : (
                    <div className="perfil-screen-avatar-placeholder">
                      <User className="perfil-screen-avatar-icon" />
                    </div>
                  )}
                  <div className="perfil-screen-online-indicator"></div>
                </div>
                
                <div className="perfil-screen-user-info">
                  <h1 className="perfil-screen-user-name">{profile.name}</h1>
                  <p className="perfil-screen-career">{profile.career}</p>
                  <p className="perfil-screen-semester">{profile.semester}° Semestre</p>
                </div>
              </div>

              <button
                onClick={isEditing ? handleCancel : handleEdit}
                className="perfil-screen-edit-button"
              >
                {isEditing ? (
                  <>
                    <X className="perfil-screen-button-icon" />
                    <span>Cancelar</span>
                  </>
                ) : (
                  <>
                    <Edit3 className="perfil-screen-button-icon" />
                    <span>Editar</span>
                  </>
                )}
              </button>
            </div>
          </div>

          {/* Contenido del perfil */}
          <div className="perfil-screen-content">
            {/* Información básica */}
            <div className="perfil-screen-grid">
              <div className="perfil-screen-field">
                <label className="perfil-screen-label">
                  Nombre Completo
                </label>
                {isEditing ? (
                  <input
                    type="text"
                    value={editedProfile.name}
                    onChange={(e) => setEditedProfile({...editedProfile, name: e.target.value})}
                    className="perfil-screen-input"
                  />
                ) : (
                  <p className="perfil-screen-text">{profile.name}</p>
                )}
              </div>

              <div className="perfil-screen-field">
                <label className="perfil-screen-label">
                  Email
                </label>
                <p className="perfil-screen-text perfil-screen-email">
                  <Mail className="perfil-screen-icon" />
                  {profile.email}
                </p>
              </div>

              <div className="perfil-screen-field">
                <label className="perfil-screen-label">
                  Carrera
                </label>
                {isEditing ? (
                  <select
                    value={editedProfile.career}
                    onChange={(e) => setEditedProfile({...editedProfile, career: e.target.value})}
                    className="perfil-screen-input"
                  >
                    <option>Ingeniería de Sistemas</option>
                    <option>Ingeniería Civil</option>
                    <option>Ingeniería Industrial</option>
                    <option>Arquitectura</option>
                    <option>Psicología</option>
                  </select>
                ) : (
                  <p className="perfil-screen-text perfil-screen-career-text">
                    <GraduationCap className="perfil-screen-icon" />
                    {profile.career}
                  </p>
                )}
              </div>

              <div className="perfil-screen-field">
                <label className="perfil-screen-label">
                  Promedio Ponderado
                </label>
                <p className="perfil-screen-gpa">{profile.gpa}</p>
              </div>
            </div>

            {/* Biografía */}
            <div className="perfil-screen-field">
              <label className="perfil-screen-label">
                Biografía
              </label>
              {isEditing ? (
                <textarea
                  value={editedProfile.bio}
                  onChange={(e) => setEditedProfile({...editedProfile, bio: e.target.value})}
                  rows={3}
                  className="perfil-screen-textarea"
                />
              ) : (
                <p className="perfil-screen-bio">{profile.bio}</p>
              )}
            </div>

            {/* Habilidades */}
            <div className="perfil-screen-section">
              <h3 className="perfil-screen-section-title">
                <Star className="perfil-screen-section-icon perfil-screen-skills-icon" />
                Habilidades
              </h3>
              <div className="perfil-screen-skills-container">
                {(isEditing ? editedProfile.skills : profile.skills).map((skill, index) => (
                  <span
                    key={index}
                    className={`perfil-screen-skill ${isEditing ? 'perfil-screen-skill-editable' : ''}`}
                  >
                    <span>{skill}</span>
                    {isEditing && (
                      <button
                        onClick={() => handleSkillRemove(index)}
                        className="perfil-screen-skill-remove"
                      >
                        <X className="perfil-screen-skill-remove-icon" />
                      </button>
                    )}
                  </span>
                ))}
                {isEditing && (
                  <input
                    type="text"
                    placeholder="Agregar habilidad"
                    onKeyPress={(e) => {
                      if (e.key === 'Enter') {
                        handleSkillAdd(e.currentTarget.value);
                        e.currentTarget.value = '';
                      }
                    }}
                    className="perfil-screen-skill-input"
                  />
                )}
              </div>
            </div>

            {/* Logros */}
            <div className="perfil-screen-section">
              <h3 className="perfil-screen-section-title">
                <Award className="perfil-screen-section-icon perfil-screen-achievements-icon" />
                Logros Destacados
              </h3>
              <ul className="perfil-screen-achievements-list">
                {profile.achievements.map((achievement, index) => (
                  <li key={index} className="perfil-screen-achievement-item">
                    <span className="perfil-screen-achievement-bullet"></span>
                    <span className="perfil-screen-achievement-text">{achievement}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Equipos/Clubes */}
            <div className="perfil-screen-section">
              <h3 className="perfil-screen-section-title">
                <Users className="perfil-screen-section-icon perfil-screen-teams-icon" />
                Equipos y Clubes
              </h3>
              <div className="perfil-screen-teams-container">
                {profile.teams.map((team, index) => (
                  <span
                    key={index}
                    className="perfil-screen-team"
                  >
                    {team}
                  </span>
                ))}
              </div>
            </div>

            {/* Botón de guardar */}
            {isEditing && (
              <div className="perfil-screen-save-section">
                <button
                  onClick={handleSave}
                  className="perfil-screen-save-button"
                >
                  <Save className="perfil-screen-button-icon" />
                  <span>Guardar Cambios</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
};