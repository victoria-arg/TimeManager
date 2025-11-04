package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Utilidades para manejo de tiempo y formato de fechas
 */
public class TimeUtils {
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Formatea una fecha y hora para mostrar en la interfaz
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }
    
    /**
     * Formatea solo la hora
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMAT);
    }
    
    /**
     * Formatea solo la fecha
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMAT);
    }
    
    /**
     * Obtiene el nombre del día de la semana en español
     */
    public static String getDayName(LocalDateTime dateTime) {
        return dateTime.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase();
    }
    
    /**
     * Convierte minutos a formato hora:minuto
     */
    public static String minutesToHourFormat(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        if (hours > 0) {
            return String.format("%dh %dm", hours, mins);
        } else {
            return String.format("%dm", mins);
        }
    }
    
    /**
     * Calcula el tiempo restante hasta una fecha
     */
    public static String getTimeUntil(LocalDateTime target) {
        LocalDateTime now = LocalDateTime.now();
        if (target.isBefore(now)) {
            return "VENCIDO";
        }
        
        long totalMinutes = java.time.Duration.between(now, target).toMinutes();
        if (totalMinutes < 60) {
            return totalMinutes + " min";
        } else if (totalMinutes < 1440) { // menos de 24 horas
            long hours = totalMinutes / 60;
            long mins = totalMinutes % 60;
            return hours + "h " + mins + "m";
        } else {
            long days = totalMinutes / 1440;
            return days + " día" + (days > 1 ? "s" : "");
        }
    }
}