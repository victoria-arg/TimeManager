package utils;

import model.*;
import java.time.LocalDateTime;

/**
 * Carga datos de ejemplo para demostrar el funcionamiento del sistema
 */
public class SampleDataLoader {
    
    /**
     * Carga datos de ejemplo en la semana
     */
    public static void loadSampleData(Week week) {
        // Metas personales de ejemplo
        PersonalGoal ejercicio = new PersonalGoal("", "Ejercicio matutino", 45, "correr", 180.0);
        PersonalGoal lectura = new PersonalGoal("", "Lectura diaria", 30, "leer", 150.0);
        PersonalGoal meditacion = new PersonalGoal("", "Meditación", 20, "meditar", 100.0);
        
        // Simular progreso en algunas metas
        ejercicio.addMinutes(90.0); // 50% completado
        lectura.addMinutes(75.0);   // 50% completado
        meditacion.addMinutes(100.0); // 100% completado
        meditacion.setCompleted(true);
        
        // Agregar metas
        week.add(ejercicio);
        week.add(lectura);
        week.add(meditacion);
        
        // Compromisos fijos de ejemplo
        LocalDateTime now = LocalDateTime.now();
        
        // Compromiso para hoy
        FixedCommitment reunion = new FixedCommitment("", "Reunión de equipo", 60, false,
            now.plusHours(2).withMinute(0).withSecond(0));
        
        // Compromiso para mañana
        FixedCommitment dentista = new FixedCommitment("", "Cita con dentista", 45, false,
            now.plusDays(1).withHour(10).withMinute(30).withSecond(0));
        
        // Compromiso para pasado mañana
        FixedCommitment presentacion = new FixedCommitment("", "Presentación proyecto", 90, false,
            now.plusDays(2).withHour(14).withMinute(0).withSecond(0));
        
        // Compromiso próximo (dentro de 30 min)
        FixedCommitment llamada = new FixedCommitment("", "Llamada importante", 30, false,
            now.plusMinutes(25));
            
        // Agregar compromisos
        week.add(reunion);
        week.add(dentista);
        week.add(presentacion);
        week.add(llamada);
        
        System.out.println("✅ Datos de ejemplo cargados exitosamente:");
        System.out.println("   • 3 metas personales (1 completada)");
        System.out.println("   • 4 compromisos programados");
        System.out.println("   • 1 compromiso próximo (25 min)");
    }
    
    /**
     * Carga solo metas de ejemplo
     */
    public static void loadSampleGoals(Week week) {
        PersonalGoal yoga = new PersonalGoal("", "Yoga matutino", 30, "yoga", 120.0);
        PersonalGoal estudio = new PersonalGoal("", "Estudio programación", 60, "estudiar", 300.0);
        
        week.add(yoga);
        week.add(estudio);
        
        System.out.println("✅ Metas de ejemplo agregadas.");
    }
    
    /**
     * Carga solo compromisos de ejemplo
     */
    public static void loadSampleCommitments(Week week) {
        LocalDateTime now = LocalDateTime.now();
        
        FixedCommitment gimnasio = new FixedCommitment("", "Ir al gimnasio", 75, false,
            now.plusDays(1).withHour(18).withMinute(0).withSecond(0));
            
        FixedCommitment compras = new FixedCommitment("", "Hacer compras", 60, false,
            now.plusDays(3).withHour(16).withMinute(30).withSecond(0));
        
        week.add(gimnasio);
        week.add(compras);
        
        System.out.println("✅ Compromisos de ejemplo agregados.");
    }
}