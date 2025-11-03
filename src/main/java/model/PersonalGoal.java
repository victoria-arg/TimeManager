package model;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonalGoal extends Activity {

    private static final Logger LOGGER = Logger.getLogger(PersonalGoal.class.getName());

    private final String exerciseType;     // ej. "correr", "leer"
    private double achievedMinutes;        // minutos acumulados
    private final double targetMinutes;    // meta semanal

    //constructor
    public PersonalGoal(String id, String name, int sessionDuration,
                        String exerciseType, double targetMinutes) {
        super(id, name, sessionDuration, false);
        this.achievedMinutes = 0.0;
        this.targetMinutes = Math.max(targetMinutes, 1.0);
        this.exerciseType = exerciseType != null ? exerciseType : "actividad";
        LOGGER.log(Level.INFO, "Nueva meta: {0} ({1} min objetivo)",
                new Object[]{name, this.targetMinutes});
    }

    // --- Getters ---
    public String getExerciseType() {
        return exerciseType; }
    public double getAchievedMinutes() {
        return achievedMinutes; }
    public double getTargetMinutes() {
        return targetMinutes; }

    // --- Progreso ---
    public void addMinutes(double minutes) {
        if (minutes < 0) {
            LOGGER.log(Level.WARNING, "Minutos negativos ignorados: {0}", minutes);
            return;
        }
        this.achievedMinutes = Math.min(this.achievedMinutes + minutes, this.targetMinutes);
        if (this.achievedMinutes >= this.targetMinutes) {
            setCompleted(true);
            LOGGER.log(Level.INFO, "¡Meta alcanzada! {0}", getName());
        }
    }

    public double getProgressPercentage() {
        return (achievedMinutes / targetMinutes) * 100.0;
    }

    // --- Puntos ---
    @Override
    public int calculatePoints() {
        return getCompleted() ? (int) getProgressPercentage() : 0;
    }

    // --- toString() para agenda ---
    @Override
    public String toString() {
        String check = getCompleted() ? "Checkmark" : "Cross";
        return String.format("• %s (%s) → %.1f/%.1f min (%.1f%%) %s",
                getName(), exerciseType, achievedMinutes, targetMinutes,
                getProgressPercentage(), check);
    }
}


