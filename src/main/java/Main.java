import model.*;
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
        System.out.println(" ".repeat(6)+"BIENVENIDO A ·TIME MANAGER·");
        System.out.println(" ".repeat(3) + "Tu organizador semanal inteligente");
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
            case 0 -> System.out.println("¡Chau!");
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
            System.out.println("Checkmark Meta agregada con éxito. ID: " + goal.getId());
        } else {
            System.out.println("Cross Error: " + result);
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
            System.out.println("Checkmark Compromiso agregado con éxito. ID: " + c.getId());
        } else {
            System.out.println("Cross Error: " + result);
        }
    }
    private static void deleteActivityMenu() {
        String id = readText("ID a eliminar (ej. G1, C2): ");
        if (id.equals("0")) return;

        if (WEEK.remove(id)) {
            System.out.println("Checkmark Actividad eliminada: " + id);
        } else {
            System.out.println("Cross No existe actividad con ID: " + id);
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
                System.out.println("Cross No puedes completar un compromiso futuro.");
                return;
            }
        }

        if (activity instanceof PersonalGoal goal) {
            double minutes = readDouble("Minutos realizados: ");
            if (minutes > 0) goal.addMinutes(minutes);
        }

        activity.setCompleted(true);
        System.out.println("Checkmark Completado. Puntos: " + activity.calculatePoints());
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
}