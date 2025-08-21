package org.commercetron.utils;

import org.commercetron.dao.UserDAO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Dienstprogrammklasse zur Eingabevalidierung und -verarbeitung.
 * Beinhaltet Methoden zur Benutzereingabe von Zahlen, Text, Datumswerten, Booleans und UUIDs,
 * sowie Validierung und Formatierung für E-Mail-Adressen und Preise.
 */
public final class InputUtils {

    // Datenzugriffsobjekt zur Prüfung der E-Mail-Verfügbarkeit
    private static final UserDAO dao = new UserDAO();

    // Privater Konstruktor verhindert Instanziierung dieser Utility-Klasse
    private InputUtils() {}

    // Fehlermeldung bei ungültiger Zahleneingabe
    private static final String NUMBER_ERROR = "Bitte geben Sie eine gültige Zahl ein.";

    /* ============================================================================
     *   Zahleneingaben
     * ============================================================================ */

    /**
     * Liest eine gültige Ganzzahl vom Benutzer ein.
     * Nur positive Werte erlaubt.
     */
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

    /**
     * Liest eine gültige Gleitkommazahl (double) vom Benutzer ein.
     */
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

    /* ============================================================================
     *   Datumseingaben
     * ============================================================================ */

    /**
     * Liest ein Datum im Format yyyy-MM-dd ein.
     */
    public static LocalDate readDateInput(String prompt, Scanner scanner) {
        return readDateInput(prompt, scanner, false);
    }

    /**
     * Liest ein Datum ein, erlaubt optional leere Eingaben (→ null).
     */
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

    /* ============================================================================
     *   Text- und Boolean-Eingaben
     * ============================================================================ */

    /**
     * Liest einen String ein, überprüft auf Länge und Leere.
     * Eingabe "0" bricht die Eingabe mit Rückgabe null ab.
     */
    public static String readStringInput(Scanner scanner, int maxLength) {
        while (true) {
            String text = scanner.nextLine().trim().replaceAll("\\s+", " ");
            if (text.equals("0")) return null; // Abbruchsignal
            if (text.isEmpty()) {
                System.out.println("Eingabe darf nicht leer sein.");
            } else if (text.length() > maxLength) {
                System.out.println("Eingabe ist zu lang (max. " + maxLength + " Zeichen).");
            } else {
                return text;
            }
        }
    }

    /**
     * Liest eine Ja/Nein-Eingabe (j/n) ein und gibt true/false zurück.
     */
    public static boolean readBooleanInput(String prompt, Scanner scanner) {
        System.out.print(prompt + " (j/n): ");
        String in = scanner.nextLine().trim().toLowerCase();
        return in.equals("j") || in.equals("ja");
    }

    /* ============================================================================
     *   UUID-Eingaben
     * ============================================================================ */

    /**
     * Liest eine UUID vom Benutzer ein.
     * Gibt null zurück, wenn "0" eingegeben wird.
     */
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

    /**
     * Liest eine UUID ein, die in der übergebenen Menge enthalten sein muss.
     * Gibt null zurück, wenn "0" eingegeben wird.
     */
    public static UUID readExistingUuid(Scanner scanner, Set<UUID> validUuids) {
        while (true) {
            UUID id = readUuid(scanner);
            if (id == null) return null;
            if (validUuids.contains(id)) return id;
            System.out.println("Diese UUID existiert nicht im System.");
        }
    }

    /* ============================================================================
     *   Hilfs-/Formatierungs-Methoden
     * ============================================================================ */

    /**
     * Formatiert einen Preis als Währungswert im deutschen Format (z. B. 1.234,56 €).
     */
    public static String formatPrice(double price) {
        return String.format(Locale.GERMANY, "%,.2f €", price);
    }

    /**
     * Prüft mit regulärem Ausdruck, ob eine E-Mail-Adresse formal gültig ist.
     */
    public static boolean isValidEmail(String email) {
        return email != null &&
                email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Alternativer E-Mail-Validator mit etwas anderem Regex.
     */
    public static boolean isValidEmailFormat(String email) {
        return email != null &&
                email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");
    }

    /**
     * Prüft, ob eine E-Mail-Adresse bereits im System vergeben ist.
     */
    public static boolean isEmailAvailable(String email) {
        return !dao.emailExists(email);
    }

    /**
     * Kombiniert Format- und Verfügbarkeitsprüfung für E-Mail-Eingaben.
     * Löst im Fehlerfall eine Exception mit einer spezifischen Nachricht aus.
     */
    public static boolean isValidDateEmailInput(String email) {
        if (!isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Ungültiges E-Mail-Format.");
        }
        if (!isEmailAvailable(email)) {
            throw new IllegalArgumentException("Diese E-Mail ist bereits vergeben.");
        }
        return true;
    }
}