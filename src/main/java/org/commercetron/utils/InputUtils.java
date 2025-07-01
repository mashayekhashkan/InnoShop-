package org.commercetron.utils;

import org.commercetron.beans.Kategorie;
import org.commercetron.beans.User;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.interfase.DaoInterface;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public final class InputUtils {

private static final UserDAO dao = new UserDAO() {
    @Override
    public User findByEmail(String email) {
        return null;
    }
};
    private InputUtils() {}                       // Verhindert Instanziierung


    private static final String NUMBER_ERROR = "Bitte geben Sie eine gültige Zahl ein.";

    /* ---------------------------------------------------------------------------
     *   Zahleneingaben
     * --------------------------------------------------------------------------- */

    public static int readIntInput(String prompt, Scanner scanner) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < 0) throw new IllegalArgumentException(NUMBER_ERROR);
                return value;
            } catch (IllegalArgumentException ex) {
                System.out.println(NUMBER_ERROR);
            }
        }
    }

    public static double readDoubleInput(String prompt, Scanner scanner) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println(NUMBER_ERROR);
            }
        }
    }

    /* ---------------------------------------------------------------------------
     *   Datumseingaben
     * --------------------------------------------------------------------------- */

    public static LocalDate readDateInput(String prompt, Scanner scanner) {
        return readDateInput(prompt, scanner, false);
    }

    public static LocalDate readDateInput(String prompt,
                                          Scanner scanner,
                                          boolean allowEmptyReturnNull) {
        while (true) {
            try {
                System.out.print(prompt);
                String in = scanner.nextLine().trim();
                if (allowEmptyReturnNull && in.isEmpty()) return null;
                return LocalDate.parse(in);
            } catch (DateTimeParseException ex) {
                System.out.println("Bitte Datum im Format yyyy-MM-dd eingeben.");
            }
        }
    }

    /* ---------------------------------------------------------------------------
     *   Text- und Boolean-Eingaben
     * --------------------------------------------------------------------------- */

    public static String readStringInput(Scanner scanner, int maxLength) {
        while (true) {
            String text = scanner.nextLine().trim().replaceAll("\\s+", " ");
            if (text.equals("0")) return null;                 // Abbruch
            if (text.isEmpty()) {
                System.out.println("Eingabe darf nicht leer sein.");
            } else if (text.length() > maxLength) {
                System.out.println("Eingabe ist zu lang (max. " + maxLength + " Zeichen).");
            } else {
                return text;
            }
        }
    }

    public static boolean readBooleanInput(String prompt, Scanner scanner) {
        System.out.print(prompt + " (j/n): ");
        String in = scanner.nextLine().trim().toLowerCase();
        return in.equals("j") || in.equals("ja");
    }

    /* ---------------------------------------------------------------------------
     *   UUID-Eingaben
     * --------------------------------------------------------------------------- */

    /** Liest eine UUID oder gibt {@code null} zurück, wenn der Benutzer 0 eingibt. */
    public static UUID readUuid(Scanner scanner) {
        while (true) {
            try {
                System.out.print("UUID eingeben (0 zum Abbrechen): ");
                String in = scanner.nextLine().trim();
                if (in.equals("0")) return null;
                return UUID.fromString(in);
            } catch (IllegalArgumentException ex) {
                System.out.println("Ungültige UUID – bitte erneut eingeben.");
            }
        }
    }

    /** Liest eine UUID, die in {@code validUuids} enthalten sein muss (oder 0 zum Abbrechen). */
    public static UUID readExistingUuid(Scanner scanner, Set<UUID> validUuids) {
        while (true) {
            UUID id = readUuid(scanner);
            if (id == null) return null;
            if (validUuids.contains(id)) return id;
            System.out.println("Diese UUID existiert nicht im System.");
        }
    }

    /* ---------------------------------------------------------------------------
     *   Hilfs-/Format-Methoden
     * --------------------------------------------------------------------------- */

    /** Formatiert Preise konsistent als 1 234,56 € (deutsches Locale). */
    public static String formatPrice(double price) {
        return String.format(Locale.GERMANY, "%,.2f €", price);
    }

    /** Einfache Regex-Validierung für E-Mail-Adressen. */
    public static boolean isValidEmail(String email) {
        return email != null &&
                email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidEmailFormat(String emauil){
        return emauil != null && emauil.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");
    }

    public static boolean isEmailAvailable(String email) {
        return !dao.emailExists(email);
    }

    public static void isValidDateEmailInput(String email){
        if (!isValidEmailFormat(email)){
            throw new IllegalArgumentException("Ungültiges E-Mail-Format.");
        }
        if (!isEmailAvailable(email)){
            throw new IllegalArgumentException("Diese E-Mail ist bereits vergeben.");
        }
    }
}
