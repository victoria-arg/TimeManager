import model.*;
import utils.TimeUtils;
import utils.DataExporter;
import utils.SampleDataLoader;
import java.time.*;
import java.time.format.*;
import java.util.Scanner;

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
        System.out.println("-".repeat(40));
        System.out.println(" ".repeat(11)+ " · · · MENU · · · ");
        System.out.println("-".repeat(40));
        System.out.println("1. Agregar Meta Personal");
        System.out.println("2. Agregar Compromiso Fijo");
        System.out.println("3. Marcar como Completado");
        System.out.println("4. Mostrar Semana");
                System.out.println("5. Próximos (30 min)");
        System.out.println("6. Vencidos");
        System.out.println("7. Eliminar actividad");
                System.out.println("8. Ver Estadísticas");
                System.out.println("9. Exportar Reporte");
        System.out.println("10. Cargar Datos Demo");
        System.out.println("11. Ayuda");
        System.out.println("0. Salir");
        System.out.println("-".repeat(40));
    }

    private static void executeOption(int option) {
        switch (option) {
            case 1 -> addPersonalGoalMenu();
            case 2 -> addFixedCommitmentMenu();
            case 3 -> markAsCompletedMenu();
            case 4 -> System.out.println(WEEK);
            case 5 -> showList(WEEK.getUpcoming(), "PRÓXIMOS (30 min)");
                        case 6 -> showList(WEEK.getOverdue(), "VENCIDOS");
            case 7 -> deleteActivityMenu();
                        case 8 -> showStatistics();
                        case 9 -> exportReportMenu();
            case 10 -> loadSampleDataMenu();
            case 11 -> showHelp();
            case 0 -> System.out.println("¡Hasta luego! 👋");
            default -> System.out.println("Opción inválida.");
        }
    }

    private static void addPersonalGoalMenu() {
        System.out.println("\n--- NUEVA META PERSONAL ---");
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
        System.out.println("\n--- NUEVO COMPROMISO ---");
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
        String id = readText("ID a eliminar (ej. G1, C2): ");
        if (id.equals("0")) return;

                if (WEEK.remove(id)) {
            System.out.println("✓ Actividad eliminada: " + id);
        } else {
            System.out.println("✗ No existe actividad con ID: " + id);
        }
    }

    private static void markAsCompletedMenu() {
        String input = readText("ID (ej. G1, C2): ");
        if (input.equals("0")) return;

        Activity activity = WEEK.findById(input);
        if (activity == null) {
            System.out.println("No existe actividad con ese ID.");
            return;
        }
        if (activity.getCompleted()) {
            System.out.println("Ya está completada.");
            return;
        }

        // ← REGLA: NO COMPLETAR COMPROMISO FUTURO
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
            case 0 -> { /* Volver */ }
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
        System.out.println("\n=== AYUDA - TIME MANAGER ===");
        System.out.println("\n🎯 METAS PERSONALES:");
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
        System.out.println("\n" + "=".repeat(55));
        System.out.println("✨✨✨✨✨ BIENVENIDO A TIME MANAGER ✨✨✨✨✨");
        System.out.println("       📅 Tu organizador semanal inteligente 📅");
        System.out.println("=".repeat(55));
        System.out.println("🎯 Gestiona metas personales y compromisos fijos");
        System.out.println("🏆 Gana puntos completando tus actividades");
        System.out.println("📈 Visualiza tu progreso y estadísticas");
        System.out.println("\n💡 CONSEJO: Usa la opción 10 para cargar datos demo");
        System.out.println("=".repeat(55));
    }
}