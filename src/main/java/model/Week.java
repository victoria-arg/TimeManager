package model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        // Asignar ID
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
        Activity a = findById(id);
        if (a != null) {
            activities.remove(a);
            return true;
        }
        return false;
    }

    public Activity findById(String id) {  // ← String
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
        StringBuilder sb = new StringBuilder("=== SEMANA ===\n");

        // Metas
        var metas = getPersonalGoals();
        sb.append("METAS PERSONALES:\n");
        if (metas.isEmpty()) sb.append("  Ninguna.\n");
        else metas.forEach(m -> sb.append("  ").append(m).append("\n"));

        // Agenda semana
        sb.append("\nCOMPROMISOS DE LA SEMANA:\n");
        var agenda = getAgendaSemana();
        if (agenda.isEmpty()) {
            sb.append("  Ninguno.\n");
        } else {
            LocalDateTime hoy = LocalDateTime.now(ZONE).toLocalDate().atStartOfDay();
            for (int i = 0; i < 7; i++) {
                LocalDateTime dia = hoy.plusDays(i);
                var delDia = agenda.stream()
                        .filter(fc -> fc.getScheduledTime().toLocalDate().equals(dia.toLocalDate()))
                        .toList();
                if (!delDia.isEmpty()) {
                    sb.append("  ").append(dia.format(DIA).toUpperCase()).append(":\n");
                    delDia.forEach(fc -> sb.append("    ").append(fc).append("\n"));
                }
            }
        }

        sb.append("\nPUNTOS TOTALES: ").append(getTotalPoints()).append("\n");
        return sb.toString();
    }
}