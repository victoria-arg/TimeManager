package utils;

import model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para exportar datos de la semana a archivos
 */
public class DataExporter {
    
    private static final DateTimeFormatter EXPORT_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    
    /**
     * Exporta el resumen de la semana a un archivo de texto
     */
    public static boolean exportWeekSummary(Week week, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("TIME MANAGER - REPORTE SEMANAL\n");
            writer.write("Generado: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n");
            writer.write("=".repeat(50) + "\n\n");
            
            // Metas
            writer.write("METAS PERSONALES:\n");
            var metas = week.getPersonalGoals();
            if (metas.isEmpty()) {
                writer.write("  No hay metas definidas.\n");
            } else {
                for (PersonalGoal meta : metas) {
                    writer.write(String.format("  - %s (%s): %.1f/%.1f min (%.1f%%) - %s\n",
                        meta.getName(), meta.getExerciseType(), 
                        meta.getAchievedMinutes(), meta.getTargetMinutes(),
                        meta.getProgressPercentage(),
                        meta.getCompleted() ? "COMPLETADA" : "PENDIENTE"));
                }
            }
            
            // Compromisos
            writer.write("\nCOMPROMISOS FIJOS:\n");
            var compromisos = week.getFixedCommitments();
            if (compromisos.isEmpty()) {
                writer.write("  No hay compromisos programados.\n");
            } else {
                for (FixedCommitment comp : compromisos) {
                    String status = comp.getCompleted() ? "COMPLETADO" :
                                  comp.isOverdue() ? "VENCIDO" : "PENDIENTE";
                    writer.write(String.format("  - %s - %s (%d min) - %s\n",
                        comp.getScheduledTime().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")),
                        comp.getName(), comp.getDuration(), status));
                }
            }
            
            // Estadísticas
            WeekStatistics stats = new WeekStatistics(week);
            writer.write("\nESTADÍSTICAS:\n");
            writer.write(String.format("  - Total de puntos: %d\n", week.getTotalPoints()));
            writer.write(String.format("  - Metas completadas: %.1f%%\n", stats.getGoalsCompletionRate()));
            writer.write(String.format("  - Compromisos completados: %.1f%%\n", stats.getCommitmentsCompletionRate()));
            writer.write(String.format("  - Minutos de ejercicio: %.1f\n", stats.getTotalExerciseMinutes()));
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Genera un nombre de archivo automático basado en la fecha
     */
    public static String generateFilename() {
        return "time_manager_" + LocalDateTime.now().format(EXPORT_FORMAT) + ".txt";
    }
    
    /**
     * Exporta solo los compromisos del día actual
     */
    public static boolean exportTodaySchedule(Week week, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("AGENDA DE HOY\n");
            writer.write("Fecha: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            writer.write("=".repeat(30) + "\n\n");
            
            var hoy = LocalDateTime.now().toLocalDate();
            var compromisosPorHoy = week.getFixedCommitments().stream()
                .filter(fc -> fc.getScheduledTime().toLocalDate().equals(hoy))
                .sorted((a, b) -> a.getScheduledTime().compareTo(b.getScheduledTime()))
                .toList();
            
            if (compromisosPorHoy.isEmpty()) {
                writer.write("No hay compromisos para hoy.\n");
            } else {
                for (FixedCommitment comp : compromisosPorHoy) {
                    writer.write(String.format("%s - %s (%d min)\n",
                        comp.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        comp.getName(), comp.getDuration()));
                }
            }
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error al exportar agenda: " + e.getMessage());
            return false;
        }
    }
}