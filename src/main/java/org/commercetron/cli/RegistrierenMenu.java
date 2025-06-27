package org.commercetron.cli;

import org.commercetron.beans.User;
import org.commercetron.controller.UserController;
import org.commercetron.dao.UserDAO;
import org.commercetron.utils.InputUtils;

import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class RegistrierenMenu {
    private static final UserDAO dao = new UserDAO();
    private static final UserController controller = new UserController(dao);

    public User register(Scanner scanner) {
        System.out.println("=== Registrierung ===");

        System.out.print("Name: ");
        String name = InputUtils.readStringInput(scanner, 50);

        System.out.print("Adresse: ");
        String adresse = InputUtils.readStringInput(scanner, 100);

        String email;
        System.out.print("E-Mail: ");
        email = InputUtils.readStringInput(scanner, 100);
        try {
            InputUtils.isValidDateEmailInput(email);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }


        System.out.print("Passwort: ");
        String password = InputUtils.readStringInput(scanner, 100);
        User neueAnmeldung = new User();
        neueAnmeldung.setCustomerName(neueAnmeldung.getCustomerName());
        neueAnmeldung.setAdresse(neueAnmeldung.getAdresse());
        neueAnmeldung.setEmail(neueAnmeldung.getEmail());
        neueAnmeldung.setPassword(neueAnmeldung.getPassword());
        controller.create(neueAnmeldung);
        System.out.println("Registrierung wurde erfolgreich erstellt.");


        return null;
    }
}
