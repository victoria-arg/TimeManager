package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Clase para calcular y mostrar estadísticas de la semana
 */
public class WeekStatistics {
    
    private final Week week;
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");
    
    public WeekStatistics(Week week) {
        this.week = week;
    }
    
    /**
     * Calcula el porcentaje de metas completadas
     */
    public double getGoalsCompletionRate() {
        List<PersonalGoal> goals = week.getPersonalGoals();
        if (goals.isEmpty()) return 0.0;
        
        long completedGoals = goals.stream()
                .mapToLong(goal -> goal.getCompleted() ? 1 : 0)
                .sum();
        
        return (double) completedGoals / goals.size() * 100;
    }
    
    /**
     * Calcula el porcentaje de compromisos completados
     */
    public double getCommitmentsCompletionRate() {
        List<FixedCommitment> commitments = week.getFixedCommitments();
        if (commitments.isEmpty()) return 0.0;
        
        long completedCommitments = commitments.stream()
                .mapToLong(commitment -> commitment.getCompleted() ? 1 : 0)
                .sum();
        
        return (double) completedCommitments / commitments.size() * 100;
    }
    
    /**
     * Calcula el total de minutos de ejercicio realizados
     */
    public double getTotalExerciseMinutes() {
        return week.getPersonalGoals().stream()
                .mapToDouble(PersonalGoal::getAchievedMinutes)
                .sum();
    }
    
    /**
     * Calcula el promedio de progreso de las metas
     */
    public double getAverageGoalProgress() {
        List<PersonalGoal> goals = week.getPersonalGoals();
        if (goals.isEmpty()) return 0.0;
        
        return goals.stream()
                .mapToDouble(PersonalGoal::getProgressPercentage)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Cuenta compromisos vencidos
     */
    public long getOverdueCount() {
        return week.getOverdue().size();
    }
    
    /**
     * Cuenta compromisos próximos (30 min)
     */
    public long getUpcomingCount() {
        return week.getUpcoming().size();
    }
    
    /**
     * Genera un resumen de estadísticas
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== ESTADÍSTICAS DE LA SEMANA ===\n\n");
        
        // Metas
        report.append("📊 METAS PERSONALES:\n");
        report.append(String.format("  • Completadas: %.1f%%\n", getGoalsCompletionRate()));
        report.append(String.format("  • Progreso promedio: %.1f%%\n", getAverageGoalProgress()));
        report.append(String.format("  • Total ejercicio: %.1f min\n", getTotalExerciseMinutes()));
        
        // Compromisos
        report.append("\n📅 COMPROMISOS:\n");
        report.append(String.format("  • Completados: %.1f%%\n", getCommitmentsCompletionRate()));
        report.append(String.format("  • Vencidos: %d\n", getOverdueCount()));
        report.append(String.format("  • Próximos (30min): %d\n", getUpcomingCount()));
        
        // Puntos
        report.append(String.format("\n🏆 PUNTOS TOTALES: %d\n", week.getTotalPoints()));
        
        // Nivel de productividad
        String productivityLevel = getProductivityLevel();
        report.append(String.format("📈 NIVEL: %s\n", productivityLevel));
        
        return report.toString();
    }
    
    /**
     * Determina el nivel de productividad basado en puntos y completación
     */
    private String getProductivityLevel() {
        int totalPoints = week.getTotalPoints();
        double avgCompletion = (getGoalsCompletionRate() + getCommitmentsCompletionRate()) / 2;
        
        if (totalPoints >= 500 && avgCompletion >= 80) {
            return "🌟 EXCEPCIONAL";
        } else if (totalPoints >= 300 && avgCompletion >= 60) {
            return "🔥 MUY BUENO";
        } else if (totalPoints >= 150 && avgCompletion >= 40) {
            return "👍 BUENO";
        } else if (totalPoints >= 50 && avgCompletion >= 20) {
            return "📈 REGULAR";
        } else {
            return "💪 INICIANDO";
        }
    }
}