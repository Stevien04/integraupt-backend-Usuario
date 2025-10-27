<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>IntegraUPT Backend - API REST</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            max-width: 800px;
            width: 100%;
        }
        
        h1 {
            color: #667eea;
            text-align: center;
            margin-bottom: 10px;
            font-size: 2.5em;
        }
        
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
            font-size: 1.1em;
        }
        
        .status {
            background: #4ade80;
            color: white;
            padding: 15px 30px;
            border-radius: 10px;
            text-align: center;
            margin-bottom: 30px;
            font-weight: bold;
            font-size: 1.2em;
        }
        
        .info-section {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .info-section h2 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.3em;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }
        
        .endpoints {
            list-style: none;
        }
        
        .endpoints li {
            background: white;
            margin: 10px 0;
            padding: 12px 15px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
            font-family: 'Courier New', monospace;
        }
        
        .method {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 4px;
            font-weight: bold;
            margin-right: 10px;
            font-size: 0.85em;
        }
        
        .get { background: #3b82f6; color: white; }
        .post { background: #10b981; color: white; }
        .put { background: #f59e0b; color: white; }
        .delete { background: #ef4444; color: white; }
        
        .config-item {
            display: flex;
            justify-content: space-between;
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }
        
        .config-item:last-child {
            border-bottom: none;
        }
        
        .config-label {
            font-weight: bold;
            color: #667eea;
        }
        
        .config-value {
            color: #555;
            font-family: 'Courier New', monospace;
        }
        
        .footer {
            text-align: center;
            margin-top: 30px;
            color: #999;
            font-size: 0.9em;
        }
        
        .badge {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 0.85em;
            margin: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 IntegraUPT Backend</h1>
        <p class="subtitle">Sistema de Gestión Universitaria - API REST</p>
        
        <div class="status">
            ✓ Backend Activo y Funcionando
        </div>
        
        <div class="info-section">
            <h2>📋 Configuración del Sistema</h2>
            <div class="config-item">
                <span class="config-label">Puerto:</span>
                <span class="config-value">8080</span>
            </div>
            <div class="config-item">
                <span class="config-label">Base URL:</span>
                <span class="config-value">http://localhost:8080/api</span>
            </div>
            <div class="config-item">
                <span class="config-label">Base de Datos:</span>
                <span class="config-value">MySQL - dbIntegraUPT</span>
            </div>
            <div class="config-item">
                <span class="config-label">Framework:</span>
                <span class="config-value">Spring Boot 3.2.0 + Java 17</span>
            </div>
        </div>
        
        <div class="info-section">
            <h2>🔗 Endpoints Principales</h2>
            <ul class="endpoints">
                <li>
                    <span class="method post">POST</span>
                    <span>/api/auth/login</span>
                </li>
                <li>
                    <span class="method get">GET</span>
                    <span>/api/perfiles</span>
                </li>
                <li>
                    <span class="method get">GET</span>
                    <span>/api/espacios</span>
                </li>
                <li>
                    <span class="method get">GET</span>
                    <span>/api/eventos</span>
                </li>
                <li>
                    <span class="method get">GET</span>
                    <span>/api/reservas</span>
                </li>
                <li>
                    <span class="method post">POST</span>
                    <span>/api/reservas/crear</span>
                </li>
            </ul>
        </div>
        
        <div class="info-section">
            <h2>🛠️ Módulos Disponibles</h2>
            <div>
                <span class="badge">Autenticación</span>
                <span class="badge">Gestión de Perfiles</span>
                <span class="badge">Gestión de Espacios</span>
                <span class="badge">Gestión de Reservas</span>
                <span class="badge">Gestión de Eventos</span>
                <span class="badge">Auditoría</span>
            </div>
        </div>
        
        <div class="info-section">
            <h2>📱 Frontend</h2>
            <div class="config-item">
                <span class="config-label">Framework:</span>
                <span class="config-value">React + TypeScript + Vite</span>
            </div>
            <div class="config-item">
                <span class="config-label">URL Frontend:</span>
                <span class="config-value">http://localhost:5173</span>
            </div>
            <div class="config-item">
                <span class="config-label">CORS:</span>
                <span class="config-value">✓ Habilitado</span>
            </div>
        </div>
        
        <div class="footer">
            <p>IntegraUPT Backend v1.0.0</p>
            <p>Universidad Privada de Tacna © 2025</p>
        </div>
    </div>
</body>
</html>
