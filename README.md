# 🕐 TIME MANAGER

**Tu organizador semanal inteligente desarrollado en Java**

## 📋 Descripción

Time Manager es una aplicación de consola desarrollada en Java que te ayuda a gestionar tus actividades semanales mediante dos tipos de tareas:

- **🎯 Metas Personales**: Actividades recurrentes como ejercicio, lectura, meditación, etc.
- **📅 Compromisos Fijos**: Citas programadas con fecha y hora específica

## ✨ Características Principales

### 🎯 Metas Personales
- Definir actividades con minutos objetivo semanal
- Seguimiento de progreso en tiempo real
- Barra de progreso visual
- Sistema de puntos basado en porcentaje completado
- Diferentes tipos de actividades (correr, leer, meditar, etc.)

### 📅 Compromisos Fijos
- Programar citas con fecha y hora específica
- Validación contra creación en el pasado
- Detección automática de conflictos de horario
- Alertas de próximos compromisos (30 minutos)
- Seguimiento de compromisos vencidos
- 100 puntos por compromiso completado

### 📊 Estadísticas y Reportes
- Resumen de progreso de metas
- Estadísticas de completación
- Cálculo automático de puntos totales
- Niveles de productividad
- Exportación de reportes a archivos de texto

### 🔧 Funcionalidades Adicionales
- Interfaz de consola intuitiva con emojis
- Datos de ejemplo para demostración
- Sistema de IDs automático (G1, G2... para metas, C1, C2... para compromisos)
- Exportación de agenda diaria
- Ayuda integrada

## 🏗️ Arquitectura del Proyecto

```
src/main/java/
├── Main.java                 # Clase principal con interfaz de usuario
├── model/
│   ├── Activity.java         # Clase abstracta base
│   ├── PersonalGoal.java     # Metas personales
│   ├── FixedCommitment.java  # Compromisos fijos
│   ├── Week.java            # Contenedor de actividades
│   └── WeekStatistics.java  # Cálculo de estadísticas
└── utils/
    ├── TimeUtils.java        # Utilidades de tiempo y formato
    ├── DataExporter.java     # Exportación de reportes
    └── SampleDataLoader.java # Carga de datos de ejemplo
```

## 🚀 Cómo Ejecutar

### Prerequisitos
- Java 24 o superior
- Sistema operativo: Windows/Linux/macOS

### Compilación
```bash
javac -d target/classes src/main/java/model/*.java src/main/java/utils/*.java src/main/java/Main.java
```

### Ejecución
```bash
java -cp target/classes Main
```

## 📱 Manual de Uso

### Menú Principal
1. **Agregar Meta Personal**: Crear nuevas metas con objetivo semanal
2. **Agregar Compromiso Fijo**: Programar citas con fecha y hora
3. **Marcar como Completado**: Completar actividades y ganar puntos
4. **Mostrar Semana**: Ver vista general de la semana
5. **Próximos (30 min)**: Ver compromisos próximos
6. **Vencidos**: Ver compromisos no completados
7. **Eliminar actividad**: Remover actividades por ID
8. **Ver Estadísticas**: Resumen detallado de progreso
9. **Exportar Reporte**: Generar archivos de texto
10. **Cargar Datos Demo**: Agregar datos de ejemplo
11. **Ayuda**: Guía de uso detallada

### Consejos de Uso
- Usa los IDs para identificar actividades (G1, C2, etc.)
- Completa actividades regularmente para maximizar puntos
- Revisa "Próximos" para no perder compromisos
- Formato de fecha: `dd/MM/yyyy HH:mm`

## 🎮 Sistema de Puntos

### Metas Personales
- Puntos = porcentaje de progreso (0-100)
- Meta completada = 100 puntos

### Compromisos Fijos
- Compromiso completado = 100 puntos
- No completado = 0 puntos

### Niveles de Productividad
- 🌟 **EXCEPCIONAL**: 500+ puntos, 80%+ completación
- 🔥 **MUY BUENO**: 300+ puntos, 60%+ completación
- 👍 **BUENO**: 150+ puntos, 40%+ completación
- 📈 **REGULAR**: 50+ puntos, 20%+ completación
- 💪 **INICIANDO**: Menos de 50 puntos

## 📊 Ejemplo de Uso

```
🎯 Meta: Ejercicio matutino (correr)
   - Objetivo: 180 minutos semanales
   - Progreso: 90/180 min (50%)
   - Puntos: 50

📅 Compromiso: Reunión de equipo
   - Fecha: 15/11/2024 14:00
   - Duración: 60 minutos
   - Estado: Completado
   - Puntos: 100
```

## 🛠️ Características Técnicas

- **Lenguaje**: Java 24
- **Paradigma**: Programación Orientada a Objetos
- **Patrones**: Herencia, Polimorfismo, Encapsulación
- **Validaciones**: Fechas, conflictos de horario, datos de entrada
- **Persistencia**: Exportación a archivos de texto
- **Interfaz**: Consola con formato mejorado y emojis

## 👥 Autor

Proyecto desarrollado como parte del curso de Programación Orientada a Objetos.

## 📝 Licencia

Este proyecto es de uso educativo.

---

¡Maximiza tu productividad semanal con Time Manager! 🚀