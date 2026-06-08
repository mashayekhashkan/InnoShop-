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
import com.vaadin.flow.router.BeforeEnterObserver;
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
public class AdminView extends Composite<VerticalLayout> implements BeforeEnterObserver {

    /**
     * DAO- und Controller-Instanzen für Kategorieverwaltung
     */
    private final KategorieDAO katDAO;
    private final KategorieController kategorieController;

    /**
     * DAO- und Controller-Instanzen für Produktverwaltung
     */
    private final ProductsDAO productsDAO;
    private final ProductsController productsController;

    /**
     * Aktuell eingeloggter Admin
     */
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
            this.currentAdmin = admin;
            initLayout();
        }
    }

    /**
     * Initialisiert das gesamte Layout (Tabs für Kategorien & Produkte).
     * Hinweis: Diese Methode erzeugt das komplette Admin-Dashboard-UI in einem Schritt.
     * Struktur bleibt unverändert – Kommentare erläutern Zweck und mögliche Stolpersteine.
     */
    public void initLayout() {
        getContent().removeAll();
        getContent().setWidthFull();
        getContent().setPadding(false);
        getContent().setAlignItems(Alignment.CENTER);

        // Haupt-Container des Views (zentriert, max. 850px Breite)
        VerticalLayout layout = new VerticalLayout();
        layout.addClassName("admin-shell");
        layout.setWidth("100%");
        layout.setMaxWidth("1180px");
        layout.setAlignItems(Alignment.CENTER);

        // Begrüßungs-Header
        H3 h3 = new H3("Willkommen im Admin Dashboard!");
        h3.getStyle().set("margin-bottom", "30px");

        // --- Kategorie-Bereich ---
        // Container für den Kategorie-Teil im Tab
        VerticalLayout katBlock = new VerticalLayout();
        katBlock.setSpacing(true);
        katBlock.setPadding(false);
        katBlock.setWidthFull();

        // Container für den Produkt-Teil im Tab
        VerticalLayout productsBlock = new VerticalLayout();
        productsBlock.setSpacing(true);
        productsBlock.setPadding(false);
        productsBlock.setWidthFull();

        // Inhaltspanes für die Tabs
        VerticalLayout katLayout = new VerticalLayout();
        VerticalLayout productsLayout = new VerticalLayout();

        // Tab-Steuerung für Kategorien und Produkte
        TabSheet tabSheet = new TabSheet();
        tabSheet.addClassName("admin-tabs");
        tabSheet.setHeightFull();
        tabSheet.add("Kategorie Verwalten", katLayout);
        tabSheet.add("Produkte Verwalten", productsLayout);

        // Eingabefeld zum Anlegen einer neuen Kategorie
        TextField neueKategorieField = new TextField("Neue Kategorie");

        // Dropdown mit vorhandenen Kategorien (wird initial befüllt)
        ComboBox<Kategorie> kategorieComboBox = new ComboBox<>("Vorhandene Kategorie");
        aktualisiereKategorieComboBox(kategorieComboBox);

        // Buttons für die Kategorieverwaltung
        Button abbrechenButton1 = new Button("Abbrechen", e -> neueKategorieField.clear());
        abbrechenButton1.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button speichernButton1 = new Button("Kategorie speichern", event -> {
            String katName = neueKategorieField.getValue();
            if (katName != null && !katName.trim().isEmpty()) {
                if (katDAO.existsByName(katName)) {
                    Notification.show("Diese Kategorie existiert bereits.");
                    return;
                }
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

        Button entfernenButton1 = new Button("Kategorie entfernen", event -> {
            Kategorie selectedKategorie = kategorieComboBox.getValue();
            if (selectedKategorie == null) {
                Notification.show("Bitte zuerst eine Kategorie auswahlen.");
                return;
            }

            int productCount = productsDAO.findeAlleAktivenInKategorie(selectedKategorie).size()
                    + productsDAO.findeAlleDeaktivenInKategorie(selectedKategorie).size();
            if (productCount > 0) {
                Notification.show("Kategorie kann nicht entfernt werden, weil noch " + productCount + " Produkt(e) zugeordnet sind.");
                return;
            }

            boolean deleted = kategorieController.delete(selectedKategorie);
            if (deleted) {
                Notification.show("Kategorie entfernt.");
                kategorieComboBox.clear();
                aktualisiereKategorieComboBox(kategorieComboBox);
            } else {
                Notification.show("Kategorie konnte nicht entfernt werden.");
            }
        });
        entfernenButton1.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Button-Gruppe mit optischem Abstand
        HorizontalLayout kategorieButtons = new HorizontalLayout(speichernButton1, entfernenButton1, abbrechenButton1);
        kategorieButtons.addClassName("admin-action-row");
        kategorieButtons.getStyle().set("gap", "10px");

        // Eingabebereich (Feld + Buttons) für neue Kategorie
        HorizontalLayout katInput = new HorizontalLayout(neueKategorieField, kategorieButtons);
        katInput.addClassName("admin-category-row");
        katInput.setWidthFull();
        katInput.setAlignItems(Alignment.END);

        // Kategorie-Block zusammenbauen
        katBlock.add(
                new H4("Kategorie hinzufügen"),
                katInput,
                kategorieComboBox
        );

        // --- Produkt-Bereich ---
        // Überschrift für die Produktverwaltung
        H4 produktHeader = new H4("Produktverwaltung");
        produktHeader.getStyle().set("marginTop", "30px");

        // Trennlinie zwischen Header und Tabs
        Hr divider = new Hr();

        // Radio-Buttons zur Filterung (aktive vs. deaktivierte Produkte)
        RadioButtonGroup<Boolean> statusFilter = new RadioButtonGroup<>();
        statusFilter.setLabel("Produkte anzeigen:");
        statusFilter.setItems(true, false);
        statusFilter.setItemLabelGenerator(active -> active ? "Aktive" : "Deaktivierte");
        statusFilter.setValue(true);

        // Layout für den Statusfilter (positioniert leicht nach rechts)
        VerticalLayout radioLayout = new VerticalLayout();
        radioLayout.addClassName("admin-filter-row");
        radioLayout.setSpacing(true);
        radioLayout.setWidthFull();
        radioLayout.add(statusFilter);

        // Produktauswahl (bestehende Produkte)
        ComboBox<Products> productsComboBox = new ComboBox<>("Vorhandene Produkte");
        aktualisiereProductsComboBox(productsComboBox);

        // Produkt-Eingabefelder (Name, Status, Preis, Menge)
        TextField produktnameField = new TextField("Produktname");
        TextField statusField = new TextField("Status");
        TextField preisField = new TextField("Preis");
        TextField mengeField = new TextField("Menge");

        // Upload-Bereich + Bildvorschau für Produktbild
        Paragraph uploadInfo = new Paragraph("Noch keine Datei hochgeladen.");
        Image produktBild = new Image();
        produktBild.setHeight("180px");
        produktBild.setMaxWidth("100%");
        produktBild.setVisible(false);
        produktBild.getStyle()
                .set("border-radius", "6px")
                .set("box-shadow", "0 2px 5px rgba(0,0,0,0.1)");

        // Upload-Setup (speichert Datei im Speicher)
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".png", ".jpg", ".jpeg", ".jfif");
        upload.setMaxFiles(1);
        upload.setUploadButton(new Button("Datei auswählen"));
        upload.addSucceededListener(event -> {
            uploadInfo.setText("Hochgeladene Datei: " + event.getFileName());
        });
        upload.getStyle()
                .set("border", "2px dashed #aaa")
                .set("padding", "20px")
                .set("border-radius", "6px")
                .set("flex", "1");
        // .set("paddingpadding", "0px 1500px"); // (auskommentiert, ggf. Tippfehler in Property-Namen)

        // Button: Produkt deaktivieren (setzt aktiv-Status zurück)
        Button inkativButton = new Button("Produkt deaktivieren", event -> {
            Products selected = productsComboBox.getValue();
            if (selected != null) {
                try {
                    deaktiviereProdukt(selected);
                    Notification.show("Produkt deaktiviert");
                    aktualisiereProductsComboBox(productsComboBox);
                    clearProductFields(produktnameField, statusField, preisField, mengeField, uploadInfo, kategorieComboBox, productsComboBox);
                } catch (Exception e) {
                    Notification.show("Fehler beim deaktivieren: " + e.getMessage());
                }
            } else {
                Notification.show("Bitte Produkt auswählen");
            }
        });
        inkativButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Button: Produkt aktivieren
        Button ativButton = new Button("Produkt aktivieren", event -> {
            Products selected = productsComboBox.getValue();
            if (selected != null) {
                try {
                    aktiviereProdukt(selected);
                    Notification.show("Produkt aktiviert");
                    aktualisiereProductsComboBox(productsComboBox);
                    clearProductFields(produktnameField, statusField, preisField, mengeField, uploadInfo, kategorieComboBox, productsComboBox);
                } catch (Exception e) {
                    Notification.show("Fehler beim Aktivieren: " + e.getMessage());
                }
            } else {
                Notification.show("Bitte Produkt auswählen");
            }
        });
        ativButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Button: Produkt bearbeiten (überschreibt Felder des selektierten Produkts)
        Button editButton = new Button("Produkt bearbeiten", event -> {
            try {
                Products selected = productsComboBox.getValue();
                if (selected == null) {
                    Notification.show("Bitte Produkt auswahlen");
                    return;
                }

                selected.setProductsName(produktnameField.getValue());
                selected.setStatus(statusField.getValue());
                selected.setPreis(Double.parseDouble(preisField.getValue()));
                selected.setBestand(Integer.parseInt(mengeField.getValue()));
                if (buffer.getFileData() != null) {
                    byte[] imageBytes = buffer.getInputStream().readAllBytes();
                    selected.setImage(imageBytes);
                }

                productsController.update(selected);
                Notification.show("Produkt bearbeitet");
                aktualisiereProductsComboBox(productsComboBox);
                clearProductFields(produktnameField, statusField, preisField, mengeField, uploadInfo, kategorieComboBox, productsComboBox);
            } catch (IOException | NumberFormatException e) {
                Notification.show("Fehler beim Bearbeiten: " + e.getMessage());
            }
        });
        editButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Button: Neues Produkt speichern (inkl. optionalem Bild & Kategorie-Pflicht)
        Button speichernButton = new Button("Produkt speichern", event -> {
            try {
                // Bilddaten, falls vorhanden
                byte[] imageBytes = null;
                if (buffer.getFileData() != null) {
                    imageBytes = buffer.getInputStream().readAllBytes();
                }

                // Kategorie prüfen (Pflichtfeld)
                Kategorie ausgewaehlteKategorie = kategorieComboBox.getValue();
                if (ausgewaehlteKategorie == null) {
                    Notification.show("Bitte wähle eine Kategorie aus.");
                    return;
                }

                // Produktobjekt befüllen und persistieren
                Products produkt = new Products();
                produkt.setProductsName(produktnameField.getValue());
                produkt.setStatus(statusField.getValue());
                produkt.setPreis(Double.parseDouble(preisField.getValue()));
                produkt.setBestand(Integer.parseInt(mengeField.getValue()));
                produkt.setImage(imageBytes);
                produkt.setAktiv(true);
                produkt.setKategorie(ausgewaehlteKategorie); // WICHTIG!

                productsController.create(produkt);

                Notification.show("Produkt gespeichert!");
                aktualisiereProductsComboBox(productsComboBox); // optional, aber hilfreich
                clearProductFields(produktnameField, statusField, preisField, mengeField, uploadInfo, kategorieComboBox, productsComboBox);
            } catch (IOException | NumberFormatException e) {
                Notification.show("Fehler beim Speichern: " + e.getMessage());
            } catch (Exception ex) {
                Notification.show("Unerwarteter Fehler: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        speichernButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        // Produktauswahl reagiert auf Auswahl → Felder & Bild werden befüllt/geleert
        productsComboBox.addValueChangeListener(e -> {
            Products selected = e.getValue();
            if (selected != null) {
                produktnameField.setValue(selected.getProductsName());
                statusField.setValue(selected.getStatus());
                preisField.setValue(safeToString(selected.getPreis()));
                mengeField.setValue(safeToString(selected.getBestand()));
                if (selected.getImage() != null) {
                    produktBild.setSrc(new StreamResource("produkt-bild", () -> new ByteArrayInputStream(selected.getImage())));
                    produktBild.setVisible(true);
                } else {
                    produktBild.setVisible(false);
                }
            } else {
                produktnameField.clear();
                statusField.clear();
                preisField.clear();
                mengeField.clear();
                produktBild.setVisible(false);
            }
        });

        // Reaktion auf Kategorie-Auswahl + Statusfilter (aktiv/deaktiv) → füllt die Produktauswahl
        kategorieComboBox.addValueChangeListener(event1 -> {
            Kategorie selectedKategorie = event1.getValue();
            Boolean nurAktive = statusFilter.getValue();
            if (selectedKategorie != null) {
                List<Products> gefilterteProdukte = nurAktive
                        ? productsDAO.findeAlleAktivenInKategorie(selectedKategorie)
                        : productsDAO.findeAlleDeaktivenInKategorie(selectedKategorie);
                productsComboBox.setItems(gefilterteProdukte);
            } else {
                aktualisiereProductsComboBox(productsComboBox);
            }
        });

        statusFilter.addValueChangeListener(event -> {
            Kategorie selectedKategorie = kategorieComboBox.getValue();
            Boolean nurAktive = event.getValue();
            if (selectedKategorie != null) {
                List<Products> gefilterteProdukte = nurAktive
                        ? productsDAO.findeAlleAktivenInKategorie(selectedKategorie)
                        : productsDAO.findeAlleDeaktivenInKategorie(selectedKategorie);
                productsComboBox.clear();
                productsComboBox.setItems(gefilterteProdukte);
            } else if (Boolean.TRUE.equals(nurAktive)) {
                aktualisiereProductsComboBox(productsComboBox);
            } else {
                productsComboBox.clear();
                productsComboBox.setItems(productsController.getAlleDeaktiven());
            }
        });

        // Feldbreiten auf volle Breite setzen
        produktnameField.setWidthFull();
        statusField.setWidthFull();
        preisField.setWidthFull();
        mengeField.setWidthFull();
        productsComboBox.setWidthFull();
        kategorieComboBox.setWidthFull();

        // Einheitliche optische Skalierung der Eingabefelder
        produktnameField.getStyle().set("font-size", "1.1em");
        statusField.getStyle().set("font-size", "1.1em");
        preisField.getStyle().set("font-size", "1.1em");
        mengeField.getStyle().set("font-size", "1.1em");
        productsComboBox.getStyle().set("font-size", "1.1em");
        kategorieComboBox.getStyle().set("font-size", "1.1em");

        // Felder in Layout
        FormLayout feldLayout = new FormLayout();
        feldLayout.addClassName("admin-form");
        feldLayout.setWidth("100%");
        feldLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );
        feldLayout.addFormItem(kategorieComboBox, "");
        feldLayout.addFormItem(productsComboBox, "");
        feldLayout.addFormItem(produktnameField, "");
        feldLayout.addFormItem(statusField, "");
        feldLayout.addFormItem(preisField, "");
        feldLayout.addFormItem(mengeField, "");

        // Bild- und Uploadbereich rechts
        produktBild.setWidth("100%");
        produktBild.setHeight("260px"); // Oder mehr je nach Wunsch
        produktBild.getStyle().set("object-fit", "contain").set("border-radius", "6px");

        upload.setWidthFull();

        // Kombinierter Bereich: Bildvorschau + Upload + Info
        VerticalLayout bildUploadLayout = new VerticalLayout(produktBild, upload, uploadInfo);
        bildUploadLayout.addClassName("admin-upload-panel");
        bildUploadLayout.setWidth("100%");
        bildUploadLayout.setSpacing(true);
        bildUploadLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Optional: Größenverhältnisse steuern (derzeit auskommentiert)
        // hauptLayout.setFlexGrow(2, feldLayout);
        // hauptLayout.setFlexGrow(1, bildUploadLayout);

        // Abmelde-Button (invaldiert Session und navigiert zur Login-Seite)
        Button logoutButton = new Button("Abmelden", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        logoutButton.addClickListener(e -> {
            // Aktuelle UI-Instanz sichern
            UI ui = UI.getCurrent();

            // Session invalidieren und danach zur Login-Seite navigieren
            VaadinSession.getCurrent().getSession().invalidate(); // Session-Objekt auf HTTP-Ebene
            VaadinSession.getCurrent().close();                   // VaadinSession schließen

            ui.navigate("anmelden"); // Zur Login-Seite navigieren
        });

        // Separates Layout nur mit dem Upload (wird zusätzlich unten eingefügt)
        // Hinweis: Dieser zusätzliche Upload-Container ergänzt den oben definierten Upload-Bereich.

        // Button-Leiste für Produktaktionen (Speichern/Bearbeiten/Aktivieren/Deaktivieren/Logout)
        HorizontalLayout produktButtons = new HorizontalLayout(speichernButton, editButton, inkativButton, ativButton, logoutButton);
        produktButtons.addClassName("admin-action-row");

        // Beide in ein gemeinsames horizontales Layout (2-Spalten: Felder links, Bild/Upload rechts)
        HorizontalLayout hauptLayout = new HorizontalLayout(feldLayout, bildUploadLayout);
        hauptLayout.addClassName("admin-product-layout");
        hauptLayout.setWidthFull();
        hauptLayout.setSpacing(false);
        hauptLayout.setFlexGrow(2, feldLayout);
        hauptLayout.setFlexGrow(1, bildUploadLayout);

        // Erster Add-Aufruf (Header + Hauptlayout + Buttons)
        // Hinweis: Direkt darunter folgt ein zweiter Add-Aufruf mit mehr Komponenten.
        // Doppeltes Hinzufügen ähnlicher Komponenten kann zu doppelter Darstellung führen – falls das nicht gewünscht ist, prüfen.

        // Button-Leiste optisch verdichten
        produktButtons.getStyle().set("gap", "10px");
        produktButtons.setPadding(false);
        produktButtons.setSpacing(false);

        // Zweiter Add-Aufruf mit erweitertem Inhalt (inkl. Filter, Feldlayout, zusätzlichem Upload-Layout)
        // Achtung: produktHeader, feldLayout, hauptLayout und produktButtons werden hier erneut hinzugefügt.
        // Wenn doppelte Komponenten unerwünscht sind, ggf. konsolidieren (Struktur hier bewusst unverändert gelassen).
        productsBlock.add( produktHeader,
                radioLayout,
                hauptLayout,
                produktButtons);
        productsLayout.add(productsBlock);

        // Kategorie-Layout in den Kategorie-Tab einhängen
        katLayout.add(
                katBlock);
        // Gesamtes Layout zusammen mit Divider und Tabs auf die Seite bringen
        layout.add(
                h3,
                divider,
                tabSheet
        );

        // Inhalt an die aktuelle View anhängen
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

    /**
     * Sicherer String-Converter (null → "").
     */
    private String safeToString(Object value) {
        return value != null ? value.toString() : "";
    }

    /**
     * Setzt ein Produkt auf inaktiv.
     */
    private void deaktiviereProdukt(Products products) {
        products.setAktiv(false);
        productsController.update(products);
    }

    /**
     * Setzt ein Produkt auf aktiv.
     */
    private void aktiviereProdukt(Products products) {
        products.setAktiv(true);
        productsController.update(products);
    }

    private void clearProductFields(TextField produktnameField, TextField statusField, TextField preisField, TextField mengeField,
                                    Paragraph uploadInfo, ComboBox<Kategorie> kategorieComboBox, ComboBox<Products> productsComboBox) {
        produktnameField.clear();
        statusField.clear();
        preisField.clear();
        mengeField.clear();
        kategorieComboBox.clear();
        productsComboBox.clear();
        uploadInfo.setText("Noch keine Datei hochgeladen.");
    }
}
