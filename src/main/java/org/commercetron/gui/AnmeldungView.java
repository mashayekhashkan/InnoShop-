package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Route("anmelden")
@PageTitle("Anmelden")
@AnonymousAllowed
public class AnmeldungView extends Composite<VerticalLayout> {

    private final UserController userController;
    private final AdminControler adminControler;
    private final ProductsController productsController;
    private final WarenkorbController warenkorbController;
    private final Map<String, Integer> loginAttempts = new HashMap<>();
    private final Map<String, Long> lockedUntil = new HashMap<>();

    public AnmeldungView() {
        this.userController = new UserController(new UserDAO());
        this.productsController = new ProductsController(new ProductsDAO());
        this.warenkorbController = new WarenkorbController(new WarenkorbDAO(Warenkorb.class));
        this.adminControler = new AdminControler(new AdminDAO());
        initLayout();
    }

    private void initLayout() {
        VerticalLayout root = getContent();
        root.removeAll();
        root.setSizeFull();
        root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        root.setAlignItems(FlexComponent.Alignment.CENTER);
        root.addClassName("auth-page");

        Div card = new Div();
        card.addClassName("auth-card");

        Div brandMark = new Div(VaadinIcon.CART.create());
        brandMark.addClassName("brand-mark");

        H2 title = new H2("Anmelden");
        title.addClassName("auth-title");
        Paragraph subtitle = new Paragraph("Melde dich an, um Warenkorb, Merkliste und Bestellungen zu verwalten.");
        subtitle.addClassName("muted-text");

        TextField emailField = new TextField("E-Mail oder Admin-Name");
        emailField.setPlaceholder("name@domain.com");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setWidthFull();

        PasswordField passwordField = new PasswordField("Passwort");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setWidthFull();

        Span errorLabel = new Span();
        errorLabel.addClassName("error-text");
        errorLabel.setVisible(false);

        Button loginButton = new Button("Einloggen", VaadinIcon.SIGN_IN.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();

        Button registerButton = new Button("Neues Konto erstellen");
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        registerButton.setWidthFull();
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("registrieren")));

        Button forgotPassword = new Button("Passwort vergessen?");
        forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        forgotPassword.setWidthFull();
        forgotPassword.addClickListener(event -> getUI().ifPresent(ui -> ui.navigate("passwordVergessenView")));

        VerticalLayout formLayout = new VerticalLayout(
                brandMark,
                title,
                subtitle,
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

        loginButton.addClickListener(e -> login(emailField, passwordField, errorLabel));
    }

    private void login(TextField emailField, PasswordField passwordField, Span errorLabel) {
        String email = emailField.getValue().trim();
        String password = passwordField.getValue();
        long now = System.currentTimeMillis();

        if (lockedUntil.containsKey(email)) {
            long unlockedTime = lockedUntil.get(email);
            if (now < unlockedTime) {
                long secondsLeft = (unlockedTime - now) / 1000;
                showError(errorLabel, "Konto gesperrt. Bitte warte " + secondsLeft + " Sekunden.");
                return;
            }
            lockedUntil.remove(email);
            loginAttempts.remove(email);
        }

        if (email.isEmpty() || password.isEmpty()) {
            showError(errorLabel, "Bitte E-Mail und Passwort eingeben.");
            return;
        }

        User user = userController.findByEmail(email);
        boolean userSuccess = user != null && user.getPassword().equals(password);

        Admin admin = null;
        boolean adminSuccess = false;
        if (!userSuccess) {
            admin = adminControler.getByExactName(email);
            adminSuccess = admin != null && admin.getPassword().equals(password);
        }

        if (userSuccess || adminSuccess) {
            loginAttempts.remove(email);
            lockedUntil.remove(email);
            errorLabel.setVisible(false);

            if (userSuccess) {
                VaadinSession.getCurrent().setAttribute(User.class, user);
                addPendingProductToCart(user);
                getUI().ifPresent(ui -> ui.navigate("home"));
            } else {
                VaadinSession.getCurrent().setAttribute(Admin.class, admin);
                getUI().ifPresent(ui -> ui.navigate("adminView"));
            }
            return;
        }

        int attempts = loginAttempts.getOrDefault(email, 0) + 1;
        loginAttempts.put(email, attempts);

        if (attempts >= 3) {
            lockedUntil.put(email, now + 3 * 60 * 1000);
            showError(errorLabel, "Zu viele Fehlversuche. Konto fuer 3 Minuten gesperrt.");
        } else {
            showError(errorLabel, "Benutzername oder Passwort ist ungueltig. (" + attempts + "/3)");
        }
    }

    private void addPendingProductToCart(User user) {
        Object pendingProductId = VaadinSession.getCurrent().getAttribute("pendingProductId");
        if (pendingProductId == null) {
            return;
        }

        UUID productId = (UUID) pendingProductId;
        Products product = (Products) productsController.getById(productId);
        if (product != null) {
            warenkorbController.getFuegeProduktHinzu(user, product, 1);
            Notification.show("Produkt wurde dem Einkaufswagen hinzugefuegt!");
        }

        VaadinSession.getCurrent().setAttribute("pendingProductId", null);
    }

    private void showError(Span errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
