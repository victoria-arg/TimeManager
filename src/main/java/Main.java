import model.*;
import utils.DataExporter;
import utils.SampleDataLoader;
import java.time.*;
import java.time.format.*;
import java.util.Scanner;
import java.util.List;

public class Main {
    private static final Week WEEK = new Week();
    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Buenos_Aires");

    public static void main(String[] args) {
                java.util.logging.LogManager.getLogManager().reset();
        showWelcomeMessage();
        int option;
        do {
            showMenu();
            option = readInt("Opción: ");
            executeOption(option);
        } while (option != 0);
        SC.close();
    }

    private static void showMenu() {
        System.out.println("-".repeat(55));
        System.out.println(" ".repeat(14)+ "✨✨ · · · MENU · · · ✨✨");
        System.out.println("-".repeat(55));
        System.out.println("1. Agregar Meta Personal");
        System.out.println("2. Agregar Compromiso Fijo");
        System.out.println("3. Marcar como Completado");
        System.out.println("4. Mostrar Semana");
        System.out.println("5. Próximos (30 min)");
        System.out.println("6. Vencidos");
        System.out.println("7. Modificar actividad");
        System.out.println("8. Eliminar actividad");
        System.out.println("9. Ver Estadísticas");
        System.out.println("10. Exportar Reporte");
        System.out.println("11. Cargar Datos Demo");
        System.out.println("12. Ayuda");
        System.out.println("0. Salir");
        System.out.println("-".repeat(55));
    }

    private static void executeOption(int option) {
        switch (option) {
            case 1 -> addPersonalGoalMenu();
            case 2 -> addFixedCommitmentMenu();
            case 3 -> markAsCompletedMenu();
            case 4 -> System.out.println(WEEK);
            case 5 -> showList(WEEK.getUpcoming(), "PRÓXIMOS (30 min)");
            case 6 -> showList(WEEK.getOverdue(), "VENCIDOS");
            case 7 -> modifyActivityMenu();
            case 8 -> deleteActivityMenu();
            case 9 -> showStatistics();
            case 10 -> exportReportMenu();
            case 11 -> loadSampleDataMenu();
            case 12 -> showHelp();
            case 0 -> System.out.println("¡Hasta luego! 👋");
            default -> System.out.println("Opción inválida.");
        }
    }

    private static void addPersonalGoalMenu() {
        System.out.println("\n=== NUEVA META PERSONAL ===");
        System.out.println("0. Volver");
        String name = readText("Nombre: ");
        if (name.equals("0")) return;
        String type = readText("Tipo (ej. correr): ");
        if (type.equals("0")) return;
        int sessionDuration = readInt("Duración por sesión (min): ");
        if (sessionDuration == 0) return;
        double targetMinutes = readDouble("Minutos objetivo semanal: ");
        if (targetMinutes == 0) return;

        PersonalGoal goal = new PersonalGoal("", name, sessionDuration, type, targetMinutes);
        String result = WEEK.add(goal);
                if (result == null) {
            System.out.println("✓ Meta agregada con éxito. ID: " + goal.getId());
        } else {
            System.out.println("✗ Error: " + result);
        }
    }

    private static void addFixedCommitmentMenu() {
        System.out.println("\n=== NUEVO COMPROMISO ===");
        System.out.println("0. Volver");
        String name = readText("Nombre: ");
        if (name.equals("0")) return;
        int duration = readInt("Duración (min): ");
        if (duration == 0) return;
        LocalDateTime dateTime = readDateTime("Fecha y hora (dd/MM/yyyy HH:mm): ");
        if (dateTime == null) return;

        FixedCommitment c = new FixedCommitment("", name, duration, false, dateTime);
        String result = WEEK.add(c);
                if (result == null) {
            System.out.println("✓ Compromiso agregado con éxito. ID: " + c.getId());
        } else {
            System.out.println("✗ Error: " + result);
        }
    }
    private static void deleteActivityMenu() {
        String id = readText("ID a eliminar (ej. G1, c2): ");
        if (id.equals("0")) return;

        String normalizedId = id.trim().toUpperCase();

        if (WEEK.remove(id)) {
            System.out.println("✓ Actividad eliminada: " + normalizedId);
        } else {
            System.out.println("✗ No existe actividad con ID: " + normalizedId);
        }
    }

    private static void markAsCompletedMenu() {
        String input = readText("ID (ej. G1, C2): ");
        if (input.equals("0")) return;

        String normalizedId = input.trim().toUpperCase();

        Activity activity = WEEK.findById(normalizedId);
        if (activity == null) {
            System.out.println("No existe actividad con ese ID.");
            return;
        }
        if (activity.getCompleted()) {
            System.out.println("Ya está completada.");
            return;
        }

        // no completar compromiso futuro
        if (activity instanceof FixedCommitment fc) {
                        if (fc.getScheduledTime().isAfter(LocalDateTime.now(ZONE))) {
                System.out.println("✗ No puedes completar un compromiso futuro.");
                return;
            }
        }

        if (activity instanceof PersonalGoal goal) {
            double minutes = readDouble("Minutos realizados: ");
            if (minutes > 0) goal.addMinutes(minutes);
        }

        activity.setCompleted(true);
        System.out.println("✓ Completado. Puntos: " + activity.calculatePoints());
    }

    private static void showList(java.util.List<?> list, String title) {
        System.out.println("\n" + title + ": " + list.size());
        if (list.isEmpty()) System.out.println("  Ninguno.");
        else list.forEach(item -> System.out.println("  • " + item));
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = SC.nextLine().trim();
                if (input.equals("0")) return 0;
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Número inválido.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = SC.nextLine().trim();
                if (input.equals("0")) return 0;
                return Double.parseDouble(input);
            } catch (Exception e) {
                System.out.println("Número decimal inválido.");
            }
        }
    }

    private static LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SC.nextLine().trim();
            if (input.equals("0")) return null;
            try {
                return LocalDateTime.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Formato: dd/MM/yyyy HH:mm");
    }
        }
    }

    private static void showStatistics() {
        WeekStatistics stats = new WeekStatistics(WEEK);
        System.out.println("\n" + stats.generateReport());
    }

    private static void modifyActivityMenu() {
        String input = readText("ID a modificar (ej. G1, C2): ");
        if (input.equals("0")) return;

        String normalizedId = input.trim().toUpperCase();
        Activity activity = WEEK.findById(normalizedId);

        if (activity == null) {
            System.out.println("No existe actividad con ID: " + normalizedId);
            return;
        }

        System.out.println("\nActividad actual:");
        System.out.println("  • " + activity);

        if (activity instanceof PersonalGoal goal) {
            modifyPersonalGoal(goal);
        } else if (activity instanceof FixedCommitment fc) {
            modifyFixedCommitment(fc);
        }
    }

    private static void modifyPersonalGoal(PersonalGoal goal) {
        System.out.println("\n=== MODIFICAR META PERSONAL ===");
        System.out.println("0. Volver al menú anterior");
        System.out.println("Dejar en blanco para mantener actual");

        // copia temporal
        PersonalGoal tempGoal;
        try {
            tempGoal = goal.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Error interno: no se puede clonar la meta.");
            return;
        }

        boolean cancelled = false;

        String name = readText("Nuevo nombre [" + tempGoal.getName() + "]: ");
        if (name.equals("0")) cancelled = true;
        else if (!name.isEmpty()) {
            try {
                java.lang.reflect.Field nameField = Activity.class.getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(tempGoal, name);
            } catch (Exception e) {
                System.out.println("Error actualizando nombre.");
                return;
            }
        }

        if (cancelled) {
            System.out.println("Modificación cancelada.");
            return;
        }

        String type = readText("Nuevo tipo [" + tempGoal.getExerciseType() + "]: ");
        if (type.equals("0")) cancelled = true;
        else if (!type.isEmpty()) {
            try {
                java.lang.reflect.Field typeField = PersonalGoal.class.getDeclaredField("exerciseType");
                typeField.setAccessible(true);
                typeField.set(tempGoal, type);
            } catch (Exception e) {
                System.out.println("Error actualizando tipo.");
                return;
            }
        }

        if (cancelled) {
            System.out.println("Modificación cancelada.");
            return;
        }

        boolean targetModified = false;
        while (!targetModified && !cancelled) {
            String targetInput = readText("Nuevo objetivo (min) [" + tempGoal.getTargetMinutes() + "] (0 para cancelar): ");
            if (targetInput.equals("0")) {
                cancelled = true;
                break;
            }
            if (targetInput.isEmpty()) {
                targetModified = true;
                break;
            }

            try {
                double newTarget = Double.parseDouble(targetInput);
                if (newTarget < tempGoal.getAchievedMinutes()) {
                    System.out.println("Error: No puedes bajar el objetivo por debajo de lo ya logrado ("
                            + tempGoal.getAchievedMinutes() + " min).");
                    continue;
                }
                if (newTarget <= 0) {
                    System.out.println("El objetivo debe ser mayor que 0.");
                    continue;
                }

                java.lang.reflect.Field targetField = PersonalGoal.class.getDeclaredField("targetMinutes");
                targetField.setAccessible(true);
                targetField.set(tempGoal, newTarget);
                System.out.println("Objetivo actualizado a " + newTarget + " min.");
                targetModified = true;

            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debe ser un número.");
            } catch (Exception e) {
                System.out.println("Error interno.");
                cancelled = true;
            }
        }

        // aplicar solo si no se modifico
        if (cancelled) {
            System.out.println("Modificación cancelada.");
        } else {
            // copiar datos de tempGoal
            try {
                java.lang.reflect.Field nameField = Activity.class.getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(goal, nameField.get(tempGoal));

                java.lang.reflect.Field typeField = PersonalGoal.class.getDeclaredField("exerciseType");
                typeField.setAccessible(true);
                typeField.set(goal, typeField.get(tempGoal));

                java.lang.reflect.Field targetField = PersonalGoal.class.getDeclaredField("targetMinutes");
                targetField.setAccessible(true);
                targetField.set(goal, targetField.get(tempGoal));

                System.out.println("Meta actualizada: " + goal.getId());
            } catch (Exception e) {
                System.out.println("Error aplicando cambios.");
            }
        }
    }


    private static void modifyFixedCommitment(FixedCommitment fc) {
        System.out.println("\n=== MODIFICAR COMPROMISO FIJO ===");
        System.out.println("0. Volver al menú anterior");
        System.out.println("Dejar en blanco para mantener actual");

        // copia temporal
        FixedCommitment tempFc;
        try {
            tempFc = fc.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("Error interno: no se puede clonar el compromiso.");
            return;
        }

        boolean cancelled = false;

        String name = readText("Nuevo nombre [" + tempFc.getName() + "]: ");
        if (name.equals("0")) cancelled = true;
        else if (!name.isEmpty()) {
            try {
                java.lang.reflect.Field nameField = Activity.class.getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(tempFc, name);
            } catch (Exception e) {
                System.out.println("Error actualizando nombre.");
                return;
            }
        }

        if (cancelled) {
            System.out.println("Modificación cancelada.");
            return;
        }

        String durationInput = readText("Nueva duración (min) [" + tempFc.getDuration() + "]: ");
        if (durationInput.equals("0")) cancelled = true;
        else if (!durationInput.isEmpty()) {
            try {
                int newDuration = Integer.parseInt(durationInput);
                if (newDuration <= 0) {
                    System.out.println("La duración debe ser mayor que 0.");
                } else {
                    java.lang.reflect.Field durationField = Activity.class.getDeclaredField("duration");
                    durationField.setAccessible(true);
                    durationField.set(tempFc, newDuration);
                }
            } catch (Exception e) {
                System.out.println("Duración inválida.");
                return;
            }
        }

        if (cancelled) {
            System.out.println("Modificación cancelada.");
            return;
        }

        boolean dateModified = false;
        while (!dateModified && !cancelled) {
            String dateInput = readText("Nueva fecha/hora (dd/MM/yyyy HH:mm) [" +
                    tempFc.getScheduledTime().format(DATE_FORMAT) + "] (0 para cancelar): ");
            if (dateInput.equals("0")) {
                cancelled = true;
                break;
            }
            if (dateInput.isEmpty()) {
                dateModified = true;
                break;
            }

            try {
                LocalDateTime newDateTime = LocalDateTime.parse(dateInput, DATE_FORMAT);
                if (newDateTime.isBefore(LocalDateTime.now(ZONE))) {
                    System.out.println("No se puede programar en el pasado.");
                    continue;
                }

                java.lang.reflect.Field timeField = FixedCommitment.class.getDeclaredField("scheduledTime");
                timeField.setAccessible(true);
                timeField.set(tempFc, newDateTime);

                // verificar conflicto
                List<FixedCommitment> others = WEEK.getFixedCommitments().stream()
                        .filter(c -> !c.getId().equals(fc.getId()))
                        .toList();

                if (tempFc.hasConflict(others)) {
                    System.out.println("Conflicto de horario con otro compromiso.");
                    timeField.set(tempFc, fc.getScheduledTime()); // revertir
                    continue;
                }

                System.out.println("Fecha/hora actualizada.");
                dateModified = true;

            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Usa: dd/MM/yyyy HH:mm");
            } catch (Exception e) {
                System.out.println("Error al actualizar fecha/hora.");
                cancelled = true;
            }
        }

        // aplicar cambios si no se cancelo
        if (cancelled) {
            System.out.println("Modificación cancelada.");
        } else {
            try {
                // copiar datos de tempFc a fc original
                java.lang.reflect.Field nameField = Activity.class.getDeclaredField("name");
                nameField.setAccessible(true);
                nameField.set(fc, nameField.get(tempFc));

                java.lang.reflect.Field durationField = Activity.class.getDeclaredField("duration");
                durationField.setAccessible(true);
                durationField.set(fc, durationField.get(tempFc));

                java.lang.reflect.Field timeField = FixedCommitment.class.getDeclaredField("scheduledTime");
                timeField.setAccessible(true);
                timeField.set(fc, timeField.get(tempFc));

                System.out.println("Compromiso actualizado: " + fc.getId());
            } catch (Exception e) {
                System.out.println("Error aplicando cambios.");
            }
        }
    }



    private static void exportReportMenu() {
        System.out.println("\n=== EXPORTAR REPORTE ===");
        System.out.println("1. Reporte completo semanal");
        System.out.println("2. Agenda de hoy");
        System.out.println("0. Volver");
        
        int option = readInt("Opción: ");
        
        switch (option) {
            case 1 -> {
                String filename = DataExporter.generateFilename();
                if (DataExporter.exportWeekSummary(WEEK, filename)) {
                    System.out.println("✓ Reporte exportado: " + filename);
                } else {
                    System.out.println("✗ Error al exportar reporte");
                }
            }
            case 2 -> {
                String filename = "agenda_hoy.txt";
                if (DataExporter.exportTodaySchedule(WEEK, filename)) {
                    System.out.println("✓ Agenda exportada: " + filename);
                } else {
                    System.out.println("✗ Error al exportar agenda");
                }
            }
            case 0 -> { }
            default -> System.out.println("Opción inválida.");
        }
    }

    private static void loadSampleDataMenu() {
        System.out.println("\n=== CARGAR DATOS DEMO ===");
        System.out.println("⚠️ Esto agregará actividades de ejemplo");
        System.out.println("1. Cargar todo (metas + compromisos)");
        System.out.println("2. Solo metas de ejemplo");
        System.out.println("3. Solo compromisos de ejemplo");
        System.out.println("0. Cancelar");
        
        int option = readInt("Opción: ");
        
        switch (option) {
            case 1 -> {
                SampleDataLoader.loadSampleData(WEEK);
                System.out.println("\n👉 Usa la opción 4 para ver tu nueva agenda!");
            }
            case 2 -> SampleDataLoader.loadSampleGoals(WEEK);
            case 3 -> SampleDataLoader.loadSampleCommitments(WEEK);
            case 0 -> { /* Cancelar */ }
            default -> System.out.println("Opción inválida.");
        }
    }

    private static void showHelp() {
        System.out.println("\n=== AYUDA ===");
        System.out.println("🎯 METAS PERSONALES:");
        System.out.println("  • Actividades recurrentes (ejercicio, lectura, etc.)");
        System.out.println("  • Define minutos objetivo por semana");
        System.out.println("  • Acumula progreso hasta completar la meta");
        System.out.println("  • Puntos = porcentaje de progreso");
        System.out.println("\n📅 COMPROMISOS FIJOS:");
        System.out.println("  • Citas con fecha y hora específica");
        System.out.println("  • No permite crear en el pasado");
        System.out.println("  • Detecta conflictos de horario");
        System.out.println("  • 100 puntos si se completa");
        System.out.println("\n🔄 CONSEJOS:");
        System.out.println("  • Usa ID para identificar actividades (G1, C2, etc.)");
        System.out.println("  • Completa actividades para ganar puntos");
        System.out.println("  • Revisa 'Próximos' regularmente");
        System.out.println("  • Formato de fecha: dd/MM/yyyy HH:mm");
                System.out.println("\n🎯 OBJETIVO: ¡Maximiza tus puntos semanales!");
    }

    private static void showWelcomeMessage() {
        System.out.println("\n" + "-".repeat(55));
        System.out.println(" ".repeat(3) + "✨✨✨✨✨ BIENVENIDO A TIME MANAGER ✨✨✨✨✨");
        System.out.println(" ".repeat(7) +"📅 Tu organizador semanal inteligente 📅");
        System.out.println("-".repeat(55));
        System.out.println("🎯 Gestiona metas personales y compromisos fijos");
        System.out.println("🏆 Gana puntos completando tus actividades");
        System.out.println("📈 Visualiza tu progreso y estadísticas");
        System.out.println("\n💡 CONSEJO: Usa la opción 11 para cargar datos demo");
        System.out.println("-".repeat(55));
    }
}