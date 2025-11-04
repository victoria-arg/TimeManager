package model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Week {
    private final List<Activity> activities = new ArrayList<>();

    private int nextGoalId = 1;
    private int nextCommitmentId = 1;

    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEEE dd/MM");

    private String generateGoalId() {
        return "G" + nextGoalId++; }

    private String generateCommitmentId() {
        return "C" + nextCommitmentId++; }


    public String add(Activity activity) {
        if (activity == null) return "Actividad nula.";

        String newId;
        if (activity instanceof PersonalGoal) {
            newId = generateGoalId();
        } else if (activity instanceof FixedCommitment) {
            newId = generateCommitmentId();
        } else {
            return "Tipo desconocido.";
        }

        // asignar ID
        try {
            Field idField = Activity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(activity, newId);
        } catch (Exception e) {
            return "Error asignando ID.";
        }

        if (findById(newId) != null) return "ID duplicado.";

        if (activity instanceof FixedCommitment fc) {
            if (fc.getScheduledTime().isBefore(LocalDateTime.now(ZONE))) {
                return "No se pueden crear compromisos en el pasado.";
            }
            if (fc.hasConflict(getFixedCommitments())) {
                return "Conflicto de horario.";
            }
        }

        activities.add(activity);
        return null;
    }

    public boolean remove(String id) {
        if (id == null) return false;

        String normalizedId = id.trim().toUpperCase();
        Activity a = findById(normalizedId);
        if (a != null) {
            activities.remove(a);
            return true;
        }
        return false;
    }

    public Activity findById(String id) {
        if (id == null) return null;
        String normalized = id.trim().toUpperCase();// ← String
        return activities.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<FixedCommitment> getUpcoming() {
        return getFixedCommitments().stream().filter(FixedCommitment::isUpcoming).toList();
    }

    public List<FixedCommitment> getOverdue() {
        return getFixedCommitments().stream().filter(FixedCommitment::isOverdue).toList();
    }

    public List<FixedCommitment> getAgendaSemana() {
        LocalDateTime inicio = LocalDateTime.now(ZONE).toLocalDate().atStartOfDay();
        LocalDateTime fin = inicio.plusDays(7);
        return getFixedCommitments().stream()
                .filter(fc -> !fc.getScheduledTime().isBefore(inicio) && fc.getScheduledTime().isBefore(fin))
                .sorted((a, b) -> a.getScheduledTime().compareTo(b.getScheduledTime()))
                .toList();
    }

    public int getTotalPoints() {
        return activities.stream().mapToInt(Activity::calculatePoints).sum();
    }

    public List<Activity> getAll() { return new ArrayList<>(activities); }
    public List<PersonalGoal> getPersonalGoals() {
        return activities.stream().filter(a -> a instanceof PersonalGoal).map(a -> (PersonalGoal)a).toList();
    }
    public List<FixedCommitment> getFixedCommitments() {
        return activities.stream().filter(a -> a instanceof FixedCommitment).map(a -> (FixedCommitment)a).toList();
    }

        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(50)).append("\n");
        sb.append("           📅 VISTA SEMANAL 📅\n");
        sb.append("=".repeat(50)).append("\n\n");

        // Metas
        var metas = getPersonalGoals();
        sb.append("🎯 METAS PERSONALES:\n");
        if (metas.isEmpty()) {
            sb.append("  💭 Ninguna meta definida aún.\n");
        } else {
            metas.forEach(m -> sb.append("  ").append(m).append("\n"));
        }

        // Agenda semana
        sb.append("\n📅 AGENDA DE LA SEMANA:\n");
        var agenda = getAgendaSemana();
        if (agenda.isEmpty()) {
            sb.append("  💭 Sin compromisos programados.\n");
        } else {
            LocalDateTime hoy = LocalDateTime.now(ZONE).toLocalDate().atStartOfDay();
            String currentDay = "";
            
            for (FixedCommitment fc : agenda) {
                String day = fc.getScheduledTime().format(DIA);
                if (!day.equals(currentDay)) {
                    currentDay = day;
                    sb.append("\n  🗓️ ").append(day.toUpperCase()).append(":\n");
                }
                sb.append("    ").append(fc).append("\n");
            }
        }

        // resumen
        sb.append("\n").append("-".repeat(50)).append("\n");
        sb.append("🎯 Metas: ").append(metas.size());
        sb.append(" | 📅 Compromisos: ").append(getFixedCommitments().size());
        sb.append(" | 🏆 Puntos: ").append(getTotalPoints()).append("\n");
        sb.append("-".repeat(50)).append("\n");
        
        return sb.toString();
    }
}