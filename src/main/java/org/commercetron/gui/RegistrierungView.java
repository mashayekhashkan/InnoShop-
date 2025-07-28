package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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

@Route("registrieren")
@PageTitle("Registrieren")
@AnonymousAllowed
public class RegistrierungView extends Composite<VerticalLayout> {

    private final UserDAO dao = new UserDAO();

    private final BaseController<User> controller = new BaseController<User>(dao);

    public RegistrierungView() {

        initLayout();
    }

    private void initLayout() {
        VerticalLayout content = getContent();
        content.setWidth("100%");
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout layoutColumn = new VerticalLayout();
        layoutColumn.setWidth("100%");
        layoutColumn.setMaxWidth("800px");

        H3 title = new H3("Personal Information");

        FormLayout formLayout = new FormLayout();
        TextField nameField = new TextField("Vollständiger Name");
        TextField addressField = new TextField("Adresse");
        DatePicker birthDateField = new DatePicker("Geburtstag");
        TextField passwordField = new TextField("Passwort");
        EmailField emailField = new EmailField("Email");

        formLayout.add(nameField, addressField, birthDateField, passwordField, emailField);

        Button saveButton = new Button("Speichern", e -> {
            String name = nameField.getValue();
            String adresse = addressField.getValue();
            LocalDate geburtstag = birthDateField.getValue();
            String passwort = passwordField.getValue();
            String email = emailField.getValue();

            // Validierung
            User user = null;
            try {
                user = new User();
                user.setUser(name);
                user.setAdresse(adresse);
                user.setGeburtstag(geburtstag);
                user.setPassword(passwort);
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

                // Validierung
//                if (!InputUtils.isValidDateEmailInput(user.getEmail())) {
//                    Notification.show("Ungültige E-Mail-Adresse!", 3000, Notification.Position.MIDDLE);
//                    return;
//                }

                controller.create(user);
                Notification.show("Registrierung erfolgreich!", 3000, Notification.Position.TOP_CENTER);
                getUI().ifPresent(ui -> ui.navigate("anmelden"));

            } catch (Exception ex) {
                InputUtils.isValidDateEmailInput(user.getEmail());
                Notification.show("Fehler beim Speichern des Benutzers: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Abbrechen", e -> getUI().ifPresent(ui -> ui.navigate("anmeldung")));

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.addClassName(LumoUtility.Gap.MEDIUM);

        layoutColumn.add(title, formLayout, buttons);
        content.add(layoutColumn);
    }
}

