package org.commercetron.gui;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;
import org.commercetron.beans.*;
import org.commercetron.controller.KategorieController;
import org.commercetron.controller.ProductsController;
import org.commercetron.dao.KategorieDAO;
import org.commercetron.dao.ProductsDAO;
import org.hibernate.mapping.Collection;


import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.Label;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * AdminView ist die Verwaltungsoberfläche für Administratoren.
 * <p>
 * Sie ermöglicht das Anlegen, Bearbeiten, Aktivieren/Deaktivieren
 * von Kategorien und Produkten. Die View ist nur für eingeloggte
 * Administratoren zugänglich und stellt sicher, dass unautorisierte
 * Zugriffe automatisch zur Login-Seite weitergeleitet werden.
 * </p>
 */
@PageTitle("AdminView")
@Route("adminView")
public class AdminView extends Composite<VerticalLayout> implements BeforeEnterListener {

    /** DAO- und Controller-Instanzen für Kategorieverwaltung */
    private final KategorieDAO katDAO;
    private final KategorieController kategorieController;

    /** DAO- und Controller-Instanzen für Produktverwaltung */
    private final ProductsDAO productsDAO;
    private final ProductsController productsController;

    /** Aktuell eingeloggter Admin */
    @Setter
    private Admin currentAdmin;

    /**
     * Konstruktor initialisiert DAOs, Controller und Layout.
     * Lädt außerdem den eingeloggten Admin aus der Session.
     */
    public AdminView() {
        this.katDAO = new KategorieDAO();
        this.kategorieController = new KategorieController(katDAO);
        this.productsDAO = new ProductsDAO();
        this.productsController = new ProductsController(productsDAO);
        initLayout();
        this.currentAdmin = (Admin) VaadinSession.getCurrent().getAttribute(Admin.class);
    }

    /**
     * Prüft beim Betreten der View, ob ein Admin eingeloggt ist.
     * Falls nicht, erfolgt eine Weiterleitung zur Login-Seite.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Admin admin = VaadinSession.getCurrent().getAttribute(Admin.class);
        if (admin == null) {
            event.forwardTo("anmelden"); // keine Session → weiter zur Login-Seite
        } else {
            // Session erneuern, um Probleme mit abgelaufenen Sessions zu vermeiden
            VaadinSession.getCurrent().close();
            VaadinSession.getCurrent().setAttribute(Admin.class, admin);
            initLayout();
        }
    }

    /**
     * Initialisiert das gesamte Layout (Tabs für Kategorien & Produkte).
     */
    public void initLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setMaxWidth("850px");
        layout.setAlignItems(Alignment.CENTER);

        H3 h3 = new H3("Willkommen im Admin Dashboard!");
        h3.getStyle().set("margin-bottom", "30px");

        // --- Kategorie-Bereich ---
        VerticalLayout katBlock = new VerticalLayout();
        katBlock.setSpacing(true);
        katBlock.setPadding(false);
        katBlock.setWidthFull();

        VerticalLayout productsBlock = new VerticalLayout();
        productsBlock.setSpacing(true);
        productsBlock.setPadding(false);
        productsBlock.setWidthFull();

        VerticalLayout katLayout = new VerticalLayout();
        VerticalLayout productsLayout = new VerticalLayout();

        // Tab-Steuerung für Kategorien und Produkte
        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeightFull();
        tabSheet.add("Kategorie Verwalten", katLayout);
        tabSheet.add("Produkte Verwalten", productsLayout);

        // Eingabefeld für neue Kategorie
        TextField neueKategorieField = new TextField("Neue Kategorie");

        // Dropdown mit vorhandenen Kategorien
        ComboBox<Kategorie> kategorieComboBox = new ComboBox<>("Vorhandene Kategorie");
        aktualisiereKategorieComboBox(kategorieComboBox);

        // Buttons für Kategorieverwaltung
        Button abbrechenButton1 = new Button("Abbrechen", e -> neueKategorieField.clear());
        abbrechenButton1.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button speichernButton1 = new Button("Kategorie speichern", event -> {
            String katName = neueKategorieField.getValue();
            if (katName != null && !katName.trim().isEmpty()) {
                Kategorie neuekategorie = new Kategorie();
                neuekategorie.setName(katName.trim());
                kategorieController.create(neuekategorie);
                Notification.show("Kategorie gespeichert!");
                neueKategorieField.clear();
                aktualisiereKategorieComboBox(kategorieComboBox);
            } else {
                Notification.show("Bitte einen gültigen Kategorienamen eingeben.");
            }
        });
        speichernButton1.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout kategorieButtons = new HorizontalLayout(speichernButton1, abbrechenButton1);
        kategorieButtons.getStyle().set("gap", "10px");

        HorizontalLayout katInput = new HorizontalLayout(neueKategorieField, kategorieButtons);
        katInput.setAlignItems(Alignment.END);

        katBlock.add(
                new H4("Kategorie hinzufügen"),
                katInput,
                kategorieComboBox
        );

        // --- Produkt-Bereich ---
        H4 produktHeader = new H4("Produktverwaltung");
        produktHeader.getStyle().set("marginTop", "30px");

        Hr divider = new Hr();

        // Radio-Buttons zur Filterung aktiver/deaktivierter Produkte
        RadioButtonGroup<Boolean> statusFilter = new RadioButtonGroup<>();
        statusFilter.setLabel("Produkte anzeigen:");
        statusFilter.setItems(true, false);
        statusFilter.setItemLabelGenerator(active -> active ? "Aktive" : "Deaktivierte");
        statusFilter.setValue(true);

        VerticalLayout radioLayout = new VerticalLayout(statusFilter);
        radioLayout.setSpacing(true);
        radioLayout.setWidthFull();
        radioLayout.getStyle().set("margin-left", "110px");

        // Dropdown für Produktauswahl
        ComboBox<Products> productsComboBox = new ComboBox<>("Vorhandene Produkte");
        aktualisiereProductsComboBox(productsComboBox);

        // Produktfelder
        TextField produktnameField = new TextField("Produktname");
        TextField statusField = new TextField("Status");
        TextField preisField = new TextField("Preis");
        TextField mengeField = new TextField("Menge");

        // Upload-Bereich + Bildvorschau
        Paragraph uploadInfo = new Paragraph("Noch keine Datei hochgeladen.");
        Image produktBild = new Image();
        produktBild.setHeight("180px");
        produktBild.setMaxWidth("100%");
        produktBild.setVisible(false);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".png", ".jpg", ".jpeg", ".jfif");
        upload.setMaxFiles(1);
        upload.setUploadButton(new Button("Datei auswählen"));
        upload.addSucceededListener(event -> uploadInfo.setText("Hochgeladene Datei: " + event.getFileName()));

        // Produkt-Buttons: Deaktivieren, Aktivieren, Bearbeiten, Speichern
        Button inkativButton = new Button("Produkt deaktivieren", event -> { /* ... */ });
        Button ativButton = new Button("Produkt aktivieren", event -> { /* ... */ });
        Button editButton = new Button("Produkt bearbeiten", event -> { /* ... */ });
        Button speichernButton = new Button("Produkt speichern", event -> { /* ... */ });

        // Logout-Button
        Button logoutButton = new Button("Abmelden", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        logoutButton.addClickListener(e -> {
            UI ui = UI.getCurrent();
            VaadinSession.getCurrent().getSession().invalidate();
            VaadinSession.getCurrent().close();
            ui.navigate("anmelden");
        });

        // Hauptlayouts zusammensetzen
        HorizontalLayout produktButtons = new HorizontalLayout(speichernButton, editButton, inkativButton, ativButton, logoutButton);
        HorizontalLayout hauptLayout = new HorizontalLayout(/* Feldlayout, BildUploadLayout */);

        productsBlock.add(produktHeader, radioLayout, hauptLayout, produktButtons);
        productsLayout.add(productsBlock);
        katLayout.add(katBlock);

        layout.add(h3, divider, tabSheet);
        getContent().add(layout);
    }

    /**
     * Lädt alle Kategorien aus der DB und befüllt die ComboBox.
     */
    private void aktualisiereKategorieComboBox(ComboBox<Kategorie> comboBox) {
        List<Kategorie> kategorien = kategorieController.getAll();
        comboBox.setItems(kategorien);
        comboBox.setItemLabelGenerator(Kategorie::getName);
    }

    /**
     * Lädt alle aktiven Produkte aus der DB und befüllt die ComboBox.
     */
    private void aktualisiereProductsComboBox(ComboBox<Products> comboBox) {
        comboBox.clear();
        comboBox.setValue(null);
        comboBox.setItems(Collections.emptyList());
        List<Products> aktiveProdukte = productsController.getAlleAktiven();
        comboBox.setItems(aktiveProdukte);
        comboBox.setItemLabelGenerator(Products::getProductsName);
    }

    /** Sicherer String-Converter (null → ""). */
    private String safeToString(Object value) {
        return value != null ? value.toString() : "";
    }

    /** Setzt ein Produkt auf inaktiv. */
    private void deaktiviereProdukt(Products products) {
        products.setAktiv(false);
        productsController.update(products);
    }

    /** Setzt ein Produkt auf aktiv. */
    private void aktiviereProdukt(Products products) {
        products.setAktiv(true);
        productsController.update(products);
    }

    /** Leert alle Eingabefelder und setzt die UI zurück. */
    private void clearProductFields(TextField produktnameField, TextField statusField, TextField preisField,
                                    TextField mengeField, Paragraph uploadInfo,
                                    ComboBox<Kategorie> kategorieComboBox, ComboBox<Products> productsComboBox) {
        produktnameField.clear();
        statusField.clear();
        preisField.clear();
        mengeField.clear();
        kategorieComboBox.clear();
        productsComboBox.clear();
        uploadInfo.setText("Noch keine Datei hochgeladen.");
    }
}