package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.commercetron.beans.Admin;
import org.commercetron.beans.User;
import org.commercetron.beans.Warenkorb;
import org.commercetron.beans.Wunschliste;
import org.commercetron.controller.UserController;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.UserDAO;
import org.commercetron.utils.InputUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

/**
 * View für die Funktion "Passwort vergessen".
 * Erlaubt anonymen Nutzern die Eingabe ihrer E-Mail-Adresse,
 * um ein neues Passwort anzufordern.
 */
@Route("passwordVergessenView")
@PageTitle("PasswordVergessenView")
@AnonymousAllowed
public class PasswordVergessenView extends Composite<VerticalLayout> {

    public PasswordVergessenView() {
        initLayout();
    }

    /**
     * Initialisiert das Layout der View.
     * Erstellt ein zentriertes Formular zur E-Mail-Eingabe.
     */
    private void initLayout() {
        VerticalLayout root = getContent();
        root.setSizeFull();
        root.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        root.setAlignItems(FlexComponent.Alignment.CENTER);

        // Container-Karte mit Styling
        Div card = new Div();
        card.getStyle()
                .set("padding", "2rem")
                .set("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.1)")
                .set("border-radius", "8px")
                .set("background-color", "white")
                .set("width", "100%")
                .set("max-width", "400px");

        // Titel und Beschreibung
        H3 title = new H3("Neues Passwort erstellen");
        Paragraph description = new Paragraph("Gib deine bei InnoShop hinterlegte E-Mail-Adresse an, um ein neues Passwort zu vergeben.");

        // E-Mail-Eingabefelder
        EmailField emailField = new EmailField("E-Mail");
        emailField.setPlaceholder("beispiel@domain.com");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setWidthFull();

        EmailField emailConfirmField = new EmailField("E-Mail bestätigen");
        emailConfirmField.setPlaceholder("beispiel@domain.com");
        emailConfirmField.setRequiredIndicatorVisible(true);
        emailConfirmField.setWidthFull();

        // Absende-Button
        Button submitButton = new Button("Absenden");
        submitButton.setWidthFull();
        submitButton.getStyle().set("margin-top", "1rem");

        // Klick-Listener zur Validierung und Reaktion
        submitButton.addClickListener(event -> {
            String email = emailField.getValue();
            String emailConfirm = emailConfirmField.getValue();

            // Prüfung: E-Mail darf nicht leer sein
            if (email == null || email.isEmpty()) {
                Notification.show("Bitte E-Mail-Adresse eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }
            // Prüfung: Format der E-Mail-Adresse
            if (!email.contains("@")) {
                Notification.show("Bitte gültige E-Mail-Adresse eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Prüfung: Bestätigungsfeld ausgefüllt?
            if (emailConfirm == null || emailConfirm.isEmpty()) {
                Notification.show("Bitte E-Mail-Adresse zur Bestätigung erneut eingeben.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Prüfung: Stimmen beide E-Mails überein?
            if (!email.equals(emailConfirm)) {
                Notification.show("E-Mail-Adressen stimmen nicht überein.", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Hinweis: Hier würde das tatsächliche E-Mail-Senden erfolgen
            Notification.show("Bestätigung-E-Mail wurde simuliert gesendet.", 3000, Notification.Position.TOP_CENTER);

            // Weiterleitung zur Login-Seite nach kurzer Wartezeit
            UI.getCurrent().getPage().executeJs(
                    "setTimeout(function() { window.location.href='anmelden'; }, 3000);"
            );
        });

        // Eingabefelder vertikal anordnen
        VerticalLayout fieldsLayout = new VerticalLayout(emailField, emailConfirmField);
        fieldsLayout.setSpacing(true);
        fieldsLayout.setPadding(false);
        fieldsLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldsLayout.setWidthFull();

        // Formular-Layout zusammensetzen
        VerticalLayout formLayout = new VerticalLayout(title, description, fieldsLayout, submitButton);
        formLayout.setSpacing(true);
        formLayout.setPadding(false);
        formLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        formLayout.setWidthFull();

        // Formular zur Karte hinzufügen und Karte zur Root-Ansicht
        card.add(formLayout);
        root.add(card);
    }
}
