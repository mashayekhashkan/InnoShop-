package org.commercetron.gui;


import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import lombok.extern.slf4j.Slf4j;
import org.commercetron.beans.Admin;
import org.commercetron.beans.Products;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.controller.AdminControler;
import org.commercetron.controller.ProductsController;
import org.commercetron.controller.UserController;
import org.commercetron.controller.WarenkorbController;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.dao.WarenkorbDAO;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * AnmeldungView stellt die Login-Oberfläche für Benutzer und Administratoren dar.
 *
 * Funktionen:
 * - Login mit Benutzer- oder Admin-Konto
 * - Validierung von E-Mail- und Passwortfeldern
 * - Fehlerversuchs-Zählung mit Sperrmechanismus (Lockout nach 3 Fehlversuchen für 3 Minuten)
 * - Weiterleitung nach erfolgreichem Login (Benutzer → Home, Admin → AdminView)
 * - Automatische Warenkorb-Aktualisierung, falls ein Produkt vorm Login ausgewählt wurde
 */
@Slf4j
@Route("anmelden")
@PageTitle("Anmelden")
@AnonymousAllowed
public class AnmeldungView extends Composite<VerticalLayout> {

    // Datenzugriffsobjekte für Benutzer, Admins, Produkte und Warenkorb
    private final UserDAO dao;
    private final UserController userController;
    private final AdminDAO adminDAO;
    private final AdminControler adminControler;
    private final ProductsDAO productsDAO;
    private final ProductsController productsController;
    private final WarenkorbDAO warenkorbDAO = new WarenkorbDAO(Warenkorb.class);
    private final WarenkorbController warenkorbController;
    // Maps für fehlgeschlagene Logins und Sperrfristen
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Map<String, Long> lockedUntil = new HashMap<>();

    /**
     * Konstruktor: Initialisiert das Layout der Anmeldemaske.
     */
    public AnmeldungView() {
        this.dao = new UserDAO();
        this.userController = new UserController(dao);
        this.productsDAO = new ProductsDAO();
        this.productsController = new ProductsController(productsDAO);
        this.warenkorbController = new WarenkorbController(warenkorbDAO);
        this.adminDAO = new AdminDAO();
        this.adminControler = new AdminControler(adminDAO);
        initLayout();
    }

    /**
     * Baut das Layout und die Anmeldelogik auf:
     * - Formularfelder
     * - Buttons (Login, Registrierung, Passwort vergessen)
     * - Fehlerbehandlung (Eingabevalidierung, Fehlversuche, Lockout)
     */
    private void initLayout() {
        VerticalLayout root = getContent();
        root.setSizeFull();
        root.setJustifyContentMode(JustifyContentMode.CENTER);
        root.setAlignItems(Alignment.CENTER);

        // Card-Komponente für optisch zentrierte Darstellung
        Div card = new Div();
        card.getStyle()
                .set("padding", "2rem")
                .set("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("width", "100%")
                .set("max-width", "400px");

        // Titel
        H2 title = new H2("Anmeldung");

        // Eingabefeld: E-Mail
        TextField emailField = new TextField("E-Mail");
        emailField.setPlaceholder("beispiel@domain.com");
        emailField.setWidthFull();

        // Eingabefeld: Passwort
        PasswordField passwordField = new PasswordField("Passwort");
        passwordField.setWidthFull();

        // Fehlerlabel (wird nur bei Validierungsfehlern sichtbar)
        Span errorLabel = new Span();
        errorLabel.getStyle().set("color", "red");
        errorLabel.setVisible(false);

        // Login-Button
        Button loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();

        // Button: Registrierung
        Button registerButton = new Button("Neues Konto erstellen");
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerButton.setWidthFull();
        registerButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("registrieren"))
        );

        // Button: Passwort zurücksetzen
        Button forgotPassword = new Button("Passwort vergessen?");
        forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        forgotPassword.setWidthFull();
        forgotPassword.addClickListener(event ->
                getUI().ifPresent(ui -> ui.navigate("passwordVergessenView"))
        );

        // Layout für Formularelemente
        VerticalLayout formLayout = new VerticalLayout(
                title,
                emailField,
                passwordField,
                loginButton,
                errorLabel,
                registerButton,
                forgotPassword
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(false);
        formLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        formLayout.setWidthFull();

        card.add(formLayout);
        root.add(card);

        // Login-Logik
        loginButton.addClickListener(e -> {
            String email = emailField.getValue();
            String password = passwordField.getValue();
            long now = System.currentTimeMillis();

            // Überprüfen, ob Konto gesperrt ist
            if (lockedUntil.containsKey(email)) {
                long unlockedTime = lockedUntil.get(email);
                if (now < unlockedTime) {
                    long secondsLeft = (unlockedTime - now) / 1000;
                    errorLabel.setText("Konto gesperrt. Bitte warte " + secondsLeft + " Sekunden.");
                    errorLabel.setVisible(true);
                    return;
                } else {
                    // Lockout abgelaufen → zurücksetzen
                    lockedUntil.remove(email);
                    loginAttempts.remove(email);
                }
            }

            // Pflichtfeldprüfung
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Bitte E-Mail und Passwort eingeben.");
                errorLabel.setVisible(true);
                return;
            }

            // Benutzer-Authentifizierung
            User user = userController.findByEmail(email);
            boolean userSuccess = user != null && user.getPassword().equals(password);

            // Admin-Authentifizierung (falls User nicht erfolgreich)
            Admin admin = null;
            boolean adminSuccess = false;
            if (!userSuccess) {
                admin = adminControler.getByExactName(email);
                adminSuccess = admin != null && admin.getPassword().equals(password);
            }

            // Erfolgreicher Login
            if (userSuccess || adminSuccess) {
                // Fehlversuche zurücksetzen
                loginAttempts.remove(email);
                lockedUntil.remove(email);
                errorLabel.setVisible(false);

                if (userSuccess) {
                    // User in Session speichern
                    VaadinSession.getCurrent().setAttribute(User.class, user);

                    // Falls während nicht eingeloggtem Zustand ein Produkt angefordert wurde
                    Object pendingProductId = VaadinSession.getCurrent().getAttribute("pendingProductId");
                    if (pendingProductId != null) {
                        UUID productId = (UUID) pendingProductId;
                        Products product = (Products) productsController.getById(productId);

                        if (product != null) {
                            warenkorbController.getFuegeProduktHinzu(user, product, 1);
                            Notification.show("Produkt wurde dem Einkaufswagen hinzugefügt!");
                        }

                        // Pending-Eintrag zurücksetzen
                        VaadinSession.getCurrent().setAttribute("pendingProductId", null);
                    }

                    // Navigation zum Home-Bereich
                    getUI().ifPresent(ui -> ui.navigate("home"));
                } else {
                    // Admin in Session speichern und weiterleiten
                    VaadinSession.getCurrent().setAttribute(Admin.class, admin);
                    getUI().ifPresent(ui -> ui.navigate("adminView"));
                }
                return;
            }

            // Fehlversuch zählen
            int attempts = loginAttempts.getOrDefault(email, 0) + 1;
            loginAttempts.put(email, attempts);

            if (attempts >= 3) {
                // Konto für 3 Minuten sperren
                lockedUntil.put(email, now + 3 * 60 * 1000);
                errorLabel.setText("Zu viele Fehlversuche. Konto für 3 Minuten gesperrt.");
            } else {
                errorLabel.setText("Benutzername oder Passwort ist ungültig. (" + attempts + "/3)");
            }

            errorLabel.setVisible(true);
        });
    }
}

