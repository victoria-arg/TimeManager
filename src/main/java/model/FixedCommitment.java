package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FixedCommitment: compromisos con horario fijo.
 * No permite creación en el pasado.
 */
public class FixedCommitment extends Activity {

    private final LocalDateTime scheduledTime; // hora programada

    private static final Logger LOGGER = Logger.getLogger(FixedCommitment.class.getName());
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    // Constructor: bloquea si la hora ya pasó
    public FixedCommitment(String id, String name, int duration, boolean completed, LocalDateTime scheduledTime) {
        super(id, name, duration, completed);

        if (scheduledTime == null) {
            throw new IllegalArgumentException("La hora programada no puede ser nula.");
        }
        if (scheduledTime.isBefore(LocalDateTime.now(ZONE))) {
            throw new IllegalArgumentException("No se pueden crear compromisos en el pasado.");
        }

        this.scheduledTime = scheduledTime;
        LOGGER.log(Level.INFO, "Nuevo compromiso: {0} a las {1}",
                new Object[]{name, scheduledTime});
    }

    // --- Getters ---
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    // --- Estado ---
    public boolean isUpcoming() {
        LocalDateTime now = LocalDateTime.now(ZONE);
        return !scheduledTime.isBefore(now) &&
                Duration.between(now, scheduledTime).toMinutes() <= 30;
    }

    public boolean isOverdue() {
        return scheduledTime.isBefore(LocalDateTime.now(ZONE)) && !getCompleted();
    }

    // --- Conflicto ---
    public boolean hasConflict(List<FixedCommitment> others) {
        if (others == null || others.isEmpty()) return false;

        LocalDateTime thisStart = getScheduledTime();
        LocalDateTime thisEnd = thisStart.plusMinutes(getDuration());

        return others.stream()
                .filter(other -> other != this)
                .anyMatch(other -> {
                    LocalDateTime otherStart = other.getScheduledTime();
                    LocalDateTime otherEnd = otherStart.plusMinutes(other.getDuration());
                    return thisStart.isBefore(otherEnd) && otherStart.isBefore(thisEnd);
                });
    }

    // --- Reprogramar ---
    public boolean reschedule(LocalDateTime newTime, List<FixedCommitment> others) {
        if (newTime == null || newTime.isBefore(LocalDateTime.now(ZONE))) {
            LOGGER.log(Level.WARNING, "Reprogramación inválida: {0}",
                    newTime == null ? "hora nula" : "hora en el pasado");
            return false;
        }

        FixedCommitment temp = new FixedCommitment(getId(), getName(), getDuration(), getCompleted(), newTime);
        if (temp.hasConflict(others)) {
            LOGGER.log(Level.WARNING, "Conflicto de horario al reprogramar: {0}", getName());
            return false;
        }

        // Aplicar cambio
        try {
            java.lang.reflect.Field field = this.getClass().getDeclaredField("scheduledTime");
            field.setAccessible(true);
            field.set(this, newTime);
            LOGGER.log(Level.INFO, "Compromiso reprogramado a: {0}", newTime);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error crítico al reprogramar", e);
            return false;
        }
    }

    // --- Puntos ---
    @Override
    public int calculatePoints() {
        return getCompleted() ? 100 : 0;
    }

    // --- Formato agenda ---
    @Override
    public String toString() {
        String status = getCompleted() ? "[COMPLETADO]" :
                isOverdue() ? "[VENCIDO]" :
                        isUpcoming() ? "[PRÓXIMO!]" : "";
        return String.format("• %s - %s %s",
                scheduledTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
                getName(), status);
    }
}
