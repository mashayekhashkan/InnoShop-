package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;
import org.commercetron.controller.BaseController;
import org.commercetron.dao.UserDAO;
import org.commercetron.utils.InputUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

@Route("registrieren")
@PageTitle("Registrieren")
@AnonymousAllowed
public class RegistrierungView extends Composite<VerticalLayout> {

    private final BaseController<User> controller;

    public RegistrierungView() {
        this.controller = new BaseController<>(new UserDAO());
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
        card.addClassNames("auth-card", "auth-card-wide");

        Div brandMark = new Div(VaadinIcon.CART.create());
        brandMark.addClassName("brand-mark");

        H2 title = new H2("Konto erstellen");
        title.addClassName("auth-title");
        Paragraph subtitle = new Paragraph("Erstelle dein InnoShop-Konto fuer Warenkorb, Merkliste und Bestellungen.");
        subtitle.addClassName("muted-text");

        TextField nameField = new TextField("Vollstaendiger Name");
        TextField addressField = new TextField("Adresse");
        DatePicker birthDateField = new DatePicker("Geburtstag");
        PasswordField passwordField = new PasswordField("Passwort");
        EmailField emailField = new EmailField("E-Mail");

        nameField.setPrefixComponent(VaadinIcon.USER.create());
        addressField.setPrefixComponent(VaadinIcon.HOME.create());
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());

        nameField.setWidthFull();
        addressField.setWidthFull();
        birthDateField.setWidthFull();
        passwordField.setWidthFull();
        emailField.setWidthFull();

        Button saveButton = new Button("Registrieren", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setWidthFull();

        Button cancelButton = new Button("Zurueck zum Login", VaadinIcon.ARROW_LEFT.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.setWidthFull();
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("anmelden")));

        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
        actions.setWidthFull();
        actions.setFlexGrow(1, saveButton, cancelButton);

        VerticalLayout formLayout = new VerticalLayout(
                brandMark,
                title,
                subtitle,
                nameField,
                addressField,
                birthDateField,
                passwordField,
                emailField,
                actions
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(false);
        formLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        formLayout.setWidthFull();

        card.add(formLayout);
        root.add(card);

        saveButton.addClickListener(e -> saveUser(nameField, addressField, birthDateField, passwordField, emailField));
    }

    private void saveUser(TextField nameField, TextField addressField, DatePicker birthDateField, PasswordField passwordField, EmailField emailField) {
        String name = nameField.getValue().trim();
        String address = addressField.getValue().trim();
        LocalDate birthDate = birthDateField.getValue();
        String password = passwordField.getValue();
        String email = emailField.getValue().trim();

        if (name.isEmpty()) {
            Notification.show("Bitte vollstaendigen Namen eingeben.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (address.isEmpty()) {
            Notification.show("Bitte Adresse eingeben.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            Notification.show("Bitte gueltiges Geburtsdatum eingeben.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (password == null || password.length() < 8) {
            Notification.show("Passwort muss mindestens 8 Zeichen lang sein.", 3000, Notification.Position.MIDDLE);
            return;
        }
        if (email.isEmpty() || !InputUtils.isValidDateEmailInput(email)) {
            Notification.show("Bitte gueltige E-Mail-Adresse eingeben.", 3000, Notification.Position.MIDDLE);
            return;
        }

        try {
            User user = new User();
            user.setUser(name);
            user.setAdresse(address);
            user.setGeburtstag(birthDate);
            user.setPassword(password);
            user.setEmail(email);

            Warenkorb warenkorb = new Warenkorb();
            warenkorb.setUser(user);
            warenkorb.setProdukteMitMenge(new HashMap<>());
            warenkorb.setGesamtPreis(0.0);
            warenkorb.setVersandPreis(0.0);

            Wunschliste wunschliste = new Wunschliste();
            wunschliste.setUser(user);
            wunschliste.setProducts(new HashSet<>());

            user.setWarenkorb(warenkorb);
            user.setWunschliste(wunschliste);

            controller.create(user);
            Notification.show("Registrierung erfolgreich!", 3000, Notification.Position.TOP_CENTER);
            getUI().ifPresent(ui -> ui.navigate("anmelden"));
        } catch (Exception ex) {
            Notification.show("Fehler beim Speichern des Benutzers: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }
}
