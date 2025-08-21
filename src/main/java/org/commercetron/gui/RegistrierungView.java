package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;
import org.commercetron.controller.BaseController;
import org.commercetron.controller.UserController;
import org.commercetron.dao.BaseDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.utils.InputUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

// Diese View ist über die Route "/registrieren" im Browser erreichbar
@Route("registrieren")
// Titel der Seite, der im Browser-Tab angezeigt wird
@PageTitle("Registrieren")
// Erlaubt den Zugriff auf diese View auch ohne Authentifizierung
@AnonymousAllowed
public class RegistrierungView extends Composite<VerticalLayout> {

    // Datenzugriffsobjekt für Benutzer
    private final UserDAO dao;
    // Basiskontroller für die Verwaltung von Benutzerobjekten
    private final BaseController<User> controller;

    // Konstruktor – initialisiert das Layout
    public RegistrierungView() {
        this.dao = new UserDAO();
        this.controller = new BaseController<>(dao);
        initLayout();
    }

    /**
     * Initialisiert das Layout der Registrierungsseite.
     * Erstellt Eingabefelder, Validierungslogik und ein Karten-Layout.
     */
    private void initLayout() {
        VerticalLayout root = getContent();
        root.setSizeFull();
        root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        root.setAlignItems(FlexComponent.Alignment.CENTER);

        // Karte zur zentrierten Anzeige des Formulars
        Div card = new Div();
        card.getStyle()
                .set("padding", "2rem")
                .set("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("width", "100%")
                .set("max-width", "500px")
                .set("max-height", "90vh")     // Maximale Höhe der Karte
                .set("overflow", "auto");      // Scrollbar bei Überlauf

        // Titel
        H3 title = new H3("Registrieren");

        // Eingabefelder für Benutzerinformationen
        TextField nameField = new TextField("Vollständiger Name");
        TextField addressField = new TextField("Adresse");
        DatePicker birthDateField = new DatePicker("Geburtstag");
        PasswordField passwordField = new PasswordField("Passwort");
        EmailField emailField = new EmailField("E-Mail");

        // Felder auf volle Breite setzen
        nameField.setWidthFull();
        addressField.setWidthFull();
        birthDateField.setWidthFull();
        passwordField.setWidthFull();
        emailField.setWidthFull();

        // Speicher-Button mit Primär-Stil
        Button saveButton = new Button("Speichern");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidthFull();

        // Abbrechen-Button mit Navigation zur Anmeldeseite
        Button cancelButton = new Button("Abbrechen", e -> getUI().ifPresent(ui -> ui.navigate("anmeldung")));
        cancelButton.setWidthFull();

        // Logik für das Speichern der Registrierung
        saveButton.addClickListener(e -> {
            String name = nameField.getValue();
            String adresse = addressField.getValue();
            LocalDate geburtstag = birthDateField.getValue();
            String passwort = passwordField.getValue();
            String email = emailField.getValue();

            // Validierungen der Eingaben
            if (name.isEmpty()) {
                Notification.show("Bitte vollständigen Namen eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (adresse.isEmpty()) {
                Notification.show("Bitte Adresse eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (geburtstag == null || geburtstag.isAfter(LocalDate.now())) {
                Notification.show("Bitte gültiges Geburtsdatum eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (passwort == null || passwort.length() < 8) {
                Notification.show("Passwort muss mindestens 8 Zeichen lang sein.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (email.isEmpty()) {
                Notification.show("Bitte E-Mail-Adresse eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }
            if (!InputUtils.isValidDateEmailInput(email)) {
                Notification.show("Bitte gültige E-Mail-Adresse eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }

            try {
                // Benutzerobjekt erstellen und mit Werten füllen
                User user = new User();
                user.setUser(name);
                user.setAdresse(adresse);
                user.setGeburtstag(geburtstag);
                user.setPassword(passwort);
                user.setEmail(email);

                // Neuer leerer Warenkorb für den Benutzer
                Warenkorb warenkorb = new Warenkorb();
                warenkorb.setUser(user);
                warenkorb.setProdukteMitMenge(new HashMap<>());
                warenkorb.setGesamtPreis(0.0);
                warenkorb.setVersandPreis(0.0);

                // Neue leere Wunschliste für den Benutzer
                Wunschliste wunschliste = new Wunschliste();
                wunschliste.setUser(user);
                wunschliste.setProducts(new HashSet<>());

                // Verknüpfen mit Benutzer
                user.setWarenkorb(warenkorb);
                user.setWunschliste(wunschliste);

                // Benutzer in Datenbank speichern
                controller.create(user);

                // Erfolgsnachricht
                Notification.show("Registrierung erfolgreich!", 3000, Notification.Position.TOP_CENTER);

                // Simulierte E-Mail und Weiterleitung zur Anmeldeseite
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // kurze Pause vor Nachricht
                        getUI().ifPresent(ui -> ui.access(() ->
                                Notification.show("Bestätigungs-E-Mail wurde simuliert gesendet.", 3000, Notification.Position.TOP_CENTER)
                        ));
                        Thread.sleep(3000); // weitere Pause vor Weiterleitung
                        getUI().ifPresent(ui -> ui.access(() -> ui.navigate("anmelden")));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(); // Fehlerprotokollierung
                    }
                }).start();

            } catch (Exception ex) {
                // Fehler beim Speichern
                Notification.show("Fehler beim Speichern des Benutzers: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
                ex.printStackTrace();
            }
        });

        // Formular-Layout mit allen Eingabefeldern und Buttons
        VerticalLayout formLayout = new VerticalLayout(
                title,
                nameField,
                addressField,
                birthDateField,
                passwordField,
                emailField,
                saveButton,
                cancelButton
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(false);
        formLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        formLayout.setWidthFull();

        // Layout zusammensetzen
        card.add(formLayout);
        root.add(card);
    }
}