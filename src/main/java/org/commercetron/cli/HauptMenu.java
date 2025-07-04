package org.commercetron.cli;

import org.commercetron.controller.BaseController;

import org.commercetron.dao.BaseDAO;


import java.sql.SQLException;
import java.util.Scanner;

import static org.commercetron.utils.InputUtils.readIntInput;

public class HauptMenu {
    private BaseController controller;
    private BaseDAO dao;

    public static void hauptMenu(Scanner scanner) {
        BaseDAO dao = null;

        boolean exit = false;

        // Begrüßung und Hinweis
        System.out.println("==========================================================");
        System.out.println("                      InnoShop                            ");
        System.out.println("==========================================================");
        System.out.println("Willkommen zu InnoShop – Ihrem modernen E-Commerce-System!");
        System.out.println();
        System.out.println("InnoShop ermöglicht die effiziente Verwaltung von:");
        System.out.println("- Produkten, Kategorien und Bestellungen");
        System.out.println("- Benutzern, Warenkörben und Wunschlisten");
        System.out.println("- Bewertungen und Benutzerkonten");
        System.out.println();
        System.out.println("Funktionen:");
        System.out.println("- Benutzerregistrierung & Login");
        System.out.println("- Produktdarstellung mit Bild & Preis");
        System.out.println("- Warenkorb- und Bestellabwicklung");
        System.out.println("- Wunschliste & Produktbewertungen");
        System.out.println("- Admin-Panel zur Verwaltung aller Inhalte");
        System.out.println();
        System.out.println("Technologien:");
        System.out.println("Java | Vaadin | Hibernate (JPA) | PostgreSQL");
        System.out.println("==========================================================");

        while (!exit) {
            printMainMenu();
            int choice = readIntInput("Bitte wählen Sie eine Option: ", scanner);

            try {
                switch (choice) {
                    case 1:
                        System.out.println("\"==== Benutzerregistrierung ====\"");
                        RegistrierenMenu regMenu = new RegistrierenMenu();
                        regMenu.register(scanner);
                        break;
                    case 2:

                        break;

                    case 0:
                        exit = true;
                        System.out.println("Auf Wiedersehen!");

                        break;
                    default:
                        System.out.println("Ungültige Option. Bitte versuchen Sie es erneut.");
                }
            } catch (Exception e) {
                System.err.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
                System.err.println("Dies könnte bedeuten, dass eine der erforderlichen Klassen noch nicht vollständig implementiert ist.");
                e.printStackTrace();
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n======== InnoShop ========");
        System.out.println("1. Registrieren");
        System.out.println("2. Einlogen");
        System.out.println("0. Beenden");
    }
}
