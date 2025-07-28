package org.commercetron.gui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

import com.vaadin.flow.data.renderer.ComponentRenderer;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;


import java.awt.*;
import java.io.ByteArrayInputStream;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;


import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.Setter;
import org.commercetron.beans.*;

import org.commercetron.dao.*;


@PageTitle("Home")
@Route("home")

public class HomeView extends Composite<VerticalLayout> {

    private final ProductsDAO dao;
    private final KategorieDAO katDao;
    private MultiSelectListBox<Products> avatarItems = new MultiSelectListBox<>();
    private VerticalLayout einkaufswagenLayout;
    private final List<Products> einkaufswagen = new ArrayList<>();
    private VerticalLayout kassaLayout;
    private final List<List<Products>> bestellverlauf = new ArrayList<>();
    private VerticalLayout bestellungLayout;
    private final BestellungDAO bestellungDAO;
    private Warenkorb warenkorb;
    private final WarenkorbDAO warenkorbDAO = new WarenkorbDAO(Warenkorb.class);
    private final WunschlisteDAO wunschlisteDAO = new WunschlisteDAO();
    private MultiSelectListBox<Products> merklisteBox = new MultiSelectListBox<>();
    private ZahlungDAO zahlungDAO = new ZahlungDAO();
    private final BewertungDAO bewertungDAO = new BewertungDAO();
    private VerticalLayout productsInfoLayout = new VerticalLayout();
    private VerticalLayout merklisteLayout = new VerticalLayout();
    private final List<Products> merkliste = new ArrayList<>();

    // Setze den aktuellen Benutzer
    @Setter
    private User currentUser;


    //    private Avatar avatar = new Avatar();
    private Image productImage = new Image();

    /**
     * Konstruktor der HomeView – Hauptansicht nach erfolgreicher Benutzeranmeldung.
     * Initialisiert DAOs, prüft den angemeldeten Benutzer, und lädt die UI-Komponenten.
     */
    public HomeView() {
        // Initialisierung der DAO-Objekte für Datenzugriffe
        this.bestellungDAO = new BestellungDAO();
        this.katDao = new KategorieDAO();

        // DAO für Produktzugriffe – anonyme Instanz (kann ggf. später überschrieben/erweitert werden)
        this.dao = new ProductsDAO() {
        };

        // Abruf des aktuell angemeldeten Benutzers aus der aktuellen Vaadin-Session
        this.currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);

        // Sicherheitsprüfung: Falls kein Benutzer angemeldet ist, Weiterleitung zur Anmeldeseite
        if (this.currentUser == null) {
            UI.getCurrent().navigate("anmeldung");
            return;
        }

        // Initialisierung des UI-Layouts (Header, Produktauswahl, Tabs etc.)
        initLayout();

        // Wunschliste aus der Datenbank laden und initial anzeigen
        updateWunschlisteFromDatabase();
    }

    /**
     * Initialisiert das Hauptlayout der View für die Shop-Oberfläche.
     * Enthält die Kopfzeile, Produktauswahl, Produktinformationen, Aktionsbuttons
     * sowie Tabs für Einkaufswagen, Merkliste, Bestellungen und Kasse.
     */
    private void initLayout() {

        // --- Kopfzeile mit Shop-Titel ---
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.setWidthFull();
        layoutRow.setHeight("100px"); // Moderner, kompakter Header
        layoutRow.setPadding(true);
        layoutRow.setSpacing(true);
        layoutRow.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Optionales Styling: dezenter Hintergrund und Schatten für optische Trennung
        layoutRow.getStyle()
                .set("background-color", "#f9f9f9")
                .set("border-bottom", "1px solid #e0e0e0")
                .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.03)");

        // Shop-Titel
        H1 h1 = new H1("🛍️ InnoShop");
        h1.getStyle()
                .set("margin", "0")
                .set("font-size", "2rem")
                .set("font-weight", "600")
                .set("color", "#333");

        layoutRow.add(h1); // Titel dem Header-Layout hinzufügen

        // --- Haupt-Layout (Vertical) vorbereiten ---
        VerticalLayout mainLayout = getContent();
        mainLayout.removeAll();           // Vorherige Inhalte löschen
        mainLayout.setWidthFull();
        mainLayout.add(layoutRow);        // Header hinzufügen

        // --- 1. Produktauswahl-Bereich ---
        // Kategorieauswahl über ComboBox
        ComboBox<Kategorie> comboBox = new ComboBox<>("Kategorie wählen");
        comboBox.setWidth("200px");
        setComboBoxSampleData(comboBox); // Initialisiere mit Kategorien und Listener

        // Avatar-Galerie für Produktauswahl
        avatarItems.setWidth("300px");

        // Produktbildanzeige
        productImage.setWidth("180px");
        productImage.setHeight("250px");

        // Aktions-Buttons
        Button buttonPrimary = new Button("In den Einkaufswagen");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button buttonPrimary2 = new Button("Merkliste hinzufügen");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Produktinformationsbereich
        productsInfoLayout.setId("product-info");
        productsInfoLayout.setWidth("300px");

        // Layout zur horizontalen Anordnung von Kategorie, Produkten, Bild, Info & Buttons
        HorizontalLayout produktAuswahlLayout = new HorizontalLayout(
                comboBox,
                avatarItems,
                productImage,
                productsInfoLayout,
                buttonPrimary,
                buttonPrimary2
        );
        produktAuswahlLayout.setWidthFull();
        produktAuswahlLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        mainLayout.add(produktAuswahlLayout); // Auswahlbereich ins Hauptlayout einfügen

        // Listener: Bei Produktauswahl Produktbild und Infos aktualisieren
        avatarItems.addValueChangeListener(event -> {
            List<Products> selected = new ArrayList<>(event.getValue());
            if (!selected.isEmpty()) {
                updateProductImageWithProductInfo(selected.get(0)); // Details anzeigen
            } else {
                productImage.setSrc("path/to/default/image.png"); // Platzhalterbild
            }
        });

        // Listener: Produkte zum Einkaufswagen hinzufügen
        buttonPrimary.addClickListener(event -> {
            List<Products> selected = new ArrayList<>(avatarItems.getSelectedItems());
            if (!selected.isEmpty()) {
                for (Products p : selected) {
                    warenkorbDAO.fuegeProduktHinzu(currentUser, p, 1);
                }
                updateEinkaufswagenFromDatabase(); // Einkaufswagen-UI aktualisieren
                Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");
                avatarItems.clear(); // Auswahl zurücksetzen
            }
        });

        // Listener: Produkte zur Merkliste hinzufügen
        buttonPrimary2.addClickListener(event -> {
            List<Products> selected = new ArrayList<>(avatarItems.getSelectedItems());
            if (!selected.isEmpty()) {
                for (Products p : selected) {
                    wunschlisteDAO.fuegeProduktHinzu(currentUser, p);
                }
                updateWunschlisteFromDatabase(); // Wunschliste aktualisieren
                Notification.show("Zur Merkliste hinzugefügt.");
                avatarItems.clear(); // Auswahl zurücksetzen
            }
        });

        // --- 2. Tab-Bereich: Einkaufswagen, Merkliste, Bestellung, Kassa ---
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidthFull();
        tabSheet.setHeight("400px");

        // Einzelne Tabs mit ihren zugehörigen Inhalten
        tabSheet.add("Einkaufswagen", createEinkaufswagenContent());
        tabSheet.add("Merkliste", createMerklisteContent());
        tabSheet.add("Bestellung", createBestellungContent());
        tabSheet.add("Kassa", createKassaContent());

        mainLayout.add(tabSheet); // Tabs ans Hauptlayout anhängen

        // Initialer Zustand des Einkaufswagens laden
        updateEinkaufswagenFromDatabase();
    }

    /**
     * Initialisiert eine ComboBox mit Kategorien aus der Datenbank und verknüpft eine Listener-Logik,
     * die bei Auswahl einer Kategorie die zugehörigen Produkte lädt und darstellt.
     *
     * @param comboBox Die ComboBox, die mit Kategorien befüllt werden soll.
     */
    private void setComboBoxSampleData(ComboBox<Kategorie> comboBox) {
        // Lade alle verfügbaren Kategorien aus der Datenbank
        List<Kategorie> kategories = katDao.findAll();

        // Setze die Kategorien als Auswahlmöglichkeiten in die ComboBox
        comboBox.setItems(kategories);

        // Bestimme, welcher Text (Label) für jede Kategorie angezeigt wird (hier: der Name)
        comboBox.setItemLabelGenerator(Kategorie::getName);

        // Listener: Reagiere auf Änderungen der Auswahl in der ComboBox
        comboBox.addValueChangeListener(event -> {
            Kategorie selectedCategory = event.getValue();  // Die aktuell ausgewählte Kategorie

            if (selectedCategory != null) {
                // Lade alle Produkte, die zur ausgewählten Kategorie gehören
                List<Products> products = dao.findByKategorie(selectedCategory);

                // Aktualisiere die UI-Komponente mit den geladenen Produkten (z. B. Avatare, Produktliste)
                setAvatarItemsSampleData(avatarItems, products);
            }
        });
    }

    /**
     * Erstellt und konfiguriert die UI-Komponente zur Anzeige und Verwaltung der Merkliste (Wunschliste).
     *
     * @return Ein Div-Container mit der Merkliste, Darstellung der Produkte und Aktions-Buttons.
     */
    private Div createMerklisteContent() {
        // Hauptcontainer für die Merkliste
        Div merklisteContent = new Div();

        // Konfiguriere den Renderer für die Anzeige der Produkte in der Merkliste (Grid, ListBox o.ä.)
        merklisteBox.setRenderer(new ComponentRenderer<>(p -> {
            // Layout für eine Produktzeile
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setSpacing(true);
            row.setWidthFull();

            // Produktbild vorbereiten
            Image img = new Image();
            img.setWidth("50px");
            img.setHeight("50px");

            // Wenn ein Produktbild vorhanden ist, verwende es – andernfalls Standardbild
            if (p.getImage() != null && p.getImage().length > 0) {
                StreamResource res = new StreamResource(
                        p.getProductsName() + ".jpg",
                        () -> new ByteArrayInputStream(p.getImage())
                );
                img.setSrc(res);
            } else {
                img.setSrc("images/default-product.jpg");
            }

            // Layout für die Produktinformationen (Name, Preis)
            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);

            // Produktname fett dargestellt
            Paragraph name = new Paragraph(p.getProductsName());
            name.getStyle().set("font-weight", "bold").set("margin", "0");

            // Einzelpreis des Produkts
            Paragraph preis = new Paragraph("Einzelpreis: " + p.getPreis() + " €");
            preis.getStyle().set("margin", "0");

            // Füge Produktname und Preis zum Info-Bereich hinzu
            info.add(name, preis);

            // Füge Bild und Info-Bereich zur Zeile hinzu
            row.add(img, info);

            return row; // Rückgabe der vollständigen Produktzeile
        }));

        // Button: Ausgewählte Produkte in den Einkaufswagen übernehmen
        Button addToCartButton = new Button("In den Einkaufswagen", event -> {
            List<Products> selectedProducts = new ArrayList<>(merklisteBox.getSelectedItems());
            addToCart(selectedProducts); // Methode zum Hinzufügen in den Einkaufswagen aufrufen
        });

        // Button: Ausgewählte Produkte aus der Merkliste entfernen
        Button removeFromWishlistButton = new Button("Aus Merkliste entfernen", event -> {
            List<Products> selectedProducts = new ArrayList<>(merklisteBox.getSelectedItems());

            // Entferne jedes ausgewählte Produkt aus der Datenbank-Wunschliste des aktuellen Nutzers
            for (Products p : selectedProducts) {
                wunschlisteDAO.productsEntfernen(currentUser, p);
            }

            // Aktualisiere die lokale Wunschliste und UI-Ansicht
            updateWunschlisteFromDatabase();
            merklisteBox.setItems(merkliste);
        });

        // Füge ListBox/Grid und Buttons zum Hauptcontainer hinzu
        merklisteContent.add(merklisteBox, addToCartButton, removeFromWishlistButton);

        // Lade initial den aktuellen Zustand der Merkliste aus der Datenbank
        updateWunschlisteFromDatabase();

        // Rückgabe des vollständigen UI-Containers für die Merkliste
        return merklisteContent;
    }

    /**
     * Erstellt und initialisiert den UI-Content für den Einkaufswagenbereich.
     *
     * @return Ein Div-Container mit der Einkaufswagenanzeige und den Steuerungselementen.
     */
    private Div createEinkaufswagenContent() {
        // Hauptcontainer für den Einkaufswagen-Content
        Div einkaufswagenContent = new Div();

        // Vertikales Layout zur Darstellung der Produktliste im Einkaufswagen
        einkaufswagenLayout = new VerticalLayout();

        // Initiale Aktualisierung und Anzeige des Einkaufswagens
        updateEinkaufswagenView();

        // Button zum Löschen aller Produkte aus dem Einkaufswagen
        Button clearCartButton = new Button("Löschen", event -> {
            einkaufswagen.clear();          // Lokale Produktliste leeren
            updateEinkaufswagenView();      // UI aktualisieren, um den leeren Zustand anzuzeigen
        });

        // Button zur Navigation zur Kasse / Checkout-Prozess
        Button goToCheckoutButton = new Button("Zur Kassa gehen", event -> {
            Notification.show("Zur Kassa geleitet");  // Kurze Benachrichtigung für den Nutzer
            prepareCheckoutView();                      // Checkout-View vorbereiten und anzeigen
        });

        // Styling: Fehler-Theme für den Lösch-Button (rot, warnend)
        clearCartButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Styling: Primär-Theme für den Kassen-Button (hervorgehoben)
        goToCheckoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Horizontal anordnen der beiden Buttons mit Abstand
        HorizontalLayout buttons = new HorizontalLayout(clearCartButton, goToCheckoutButton);
        buttons.setSpacing(true);

        // Füge das Produkt-Layout und die Buttons dem Hauptcontainer hinzu
        einkaufswagenContent.add(einkaufswagenLayout, buttons);

        // Rückgabe des vollständigen Einkaufswagen-UI-Containers
        return einkaufswagenContent;
    }

    /**
     * Synchronisiert die lokale Einkaufswagenansicht mit dem aktuellen Warenkorb des Nutzers aus der Datenbank.
     * Dabei wird die lokale Produktliste zurückgesetzt und das UI-Layout für die Anzeige aktualisiert.
     */
    private void updateEinkaufswagenFromDatabase() {
        // Leere die lokale Produktliste und das UI-Layout, um veraltete Einträge zu entfernen
        einkaufswagen.clear();
        einkaufswagenLayout.removeAll();

        // Lade den Warenkorb des angemeldeten Nutzers aus der Datenbank
        Warenkorb warenkorb = warenkorbDAO.findeWarenkorbVonUser(currentUser);

        // Prüfe, ob der Warenkorb existiert und Produkte enthält
        if (warenkorb != null && !warenkorb.getProdukteMitMenge().isEmpty()) {
            // Iteriere über alle Produkte inklusive ihrer jeweiligen Menge
            for (Map.Entry<Products, Integer> entry : warenkorb.getProdukteMitMenge().entrySet()) {
                Products produkt = entry.getKey();
                int menge = entry.getValue();

                // Ergänze das Produkt in die lokale Einkaufswagen-Datenstruktur
                einkaufswagen.add(produkt);

                // Erzeuge eine visuelle Produktzeile im Einkaufswagen-Layout
                HorizontalLayout produktZeile = new HorizontalLayout();
                produktZeile.setAlignItems(FlexComponent.Alignment.CENTER);
                produktZeile.setSpacing(true);
                produktZeile.setPadding(true);
                produktZeile.getStyle().set("border-bottom", "1px solid #ccc");
                produktZeile.setWidthFull();

                // Erstelle ein Bild-Element und setze die Quelle abhängig von Verfügbarkeit des Produktbildes
                Image produktBild = new Image();
                produktBild.setWidth("100px");
                produktBild.setHeight("100px");
                if (produkt.getImage() != null && produkt.getImage().length > 0) {
                    StreamResource bildResource = new StreamResource(
                            produkt.getProductsName() + ".jfif",
                            () -> new ByteArrayInputStream(produkt.getImage())
                    );
                    produktBild.setSrc(bildResource);
                } else {
                    // Fallback auf Standardbild, wenn kein Produktbild verfügbar ist
                    produktBild.setSrc("images/default-product.jpg");
                }

                // Erstelle ein vertikales Layout zur Anzeige von Produktinformationen (Name, Einzelpreis, Gesamtpreis)
                VerticalLayout produktInfo = new VerticalLayout();
                produktInfo.setPadding(false);
                produktInfo.setSpacing(false);
                produktInfo.setWidthFull();

                // Produktname und Menge (z.B. "Apfel x 3"), prominent hervorgehoben
                Paragraph name = new Paragraph(produkt.getProductsName() + " x " + menge);
                name.getStyle().set("font-weight", "bold").set("margin", "0");

                // Einzelpreis des Produkts
                Paragraph einzelpreis = new Paragraph("Einzelpreis: " + produkt.getPreis() + " €");
                einzelpreis.getStyle().set("margin", "0");

                // Gesamtpreis (Einzelpreis * Menge), optisch dezent gestaltet
                Paragraph gesamtpreis = new Paragraph("Gesamt: " + (produkt.getPreis() * menge) + " €");
                gesamtpreis.getStyle().set("margin", "0").set("color", "gray");

                // Füge alle Produktinformationen dem Layout hinzu
                produktInfo.add(name, einzelpreis, gesamtpreis);

                // Füge Bild und Produktinformationen nebeneinander in die Produktzeile ein
                produktZeile.add(produktBild, produktInfo);

                // Ergänze die Produktzeile in das übergeordnete Einkaufswagen-Layout
                einkaufswagenLayout.add(produktZeile);
            }
        } else {
            // Falls keine Produkte vorhanden sind, zeige eine entsprechende Platzhalter-Nachricht an
            einkaufswagenLayout.add(new Text("Warenkorb ist leer."));
        }
    }
    /**
     * Lädt die Wunschliste des aktuellen Benutzers aus der Datenbank
     * und aktualisiert die lokale Datenstruktur sowie die Darstellung in der UI.
     *
     * Die Methode leert zunächst die lokale Liste {@code merkliste},
     * lädt anschließend die gespeicherten Produkte aus der Datenbank
     * (falls vorhanden), und aktualisiert dann die Darstellungskomponente
     * {@code merklisteBox}, die automatisch über einen Renderer visuell aktualisiert wird.
     */
    private void updateWunschlisteFromDatabase() {
        // Leert die aktuelle Wunschliste im Speicher
        merkliste.clear();

        // Holt die Wunschliste des aktuellen Benutzers aus der Datenbank
        Wunschliste wunschliste = wunschlisteDAO.findeWunschlisteVonUser(currentUser);

        // Falls eine Wunschliste existiert und Produkte enthält, übernehme diese in die lokale Liste
        if (wunschliste != null && !wunschliste.getProducts().isEmpty()) {
            merkliste.addAll(wunschliste.getProducts());
        }

        // Aktualisiert die visuelle Komponente merklisteBox mit den neuen Daten
        // Die Darstellung erfolgt durch den konfigurierten Renderer automatisch
        merklisteBox.setItems(merkliste);
    }


    /**
     * Aktualisiert die visuelle Darstellung des Einkaufswagens.
     *
     * Diese Methode leert zuerst das bestehende Layout des Einkaufswagens
     * und fügt anschließend für jedes Produkt im Einkaufswagen eine neue Zeile mit dem Produktnamen und -preis hinzu.
     */
    public void updateEinkaufswagenView() {
        // Entfernt alle bestehenden Komponenten aus dem Einkaufswagen-Layout
        einkaufswagenLayout.removeAll();

        // Fügt für jedes Produkt im Einkaufswagen eine Zeile mit Produktinformationen hinzu
        for (Products p : einkaufswagen) {
            HorizontalLayout productLayout = new HorizontalLayout();
            productLayout.add(new Text(p.getProductsName() + " - " + p.getPreis() + " €"));
            einkaufswagenLayout.add(productLayout);
        }
    }

    /**
     * Erstellt das Layout für die Bestellübersicht des Benutzers.
     *
     * Diese Methode initialisiert das Bestell-Layout, ruft die aktuelle Bestellübersicht des
     * eingeloggten Benutzers ab und fügt das Layout dem zurückgegebenen Container hinzu.
     *
     * @return Ein {@link Div}, der die visuelle Darstellung aller bisherigen Bestellungen enthält.
     */
    private Div createBestellungContent() {
        // Container für den Bestellinhalt
        Div bestellungContent = new Div();

        // Initialisiere das vertikale Layout zur Darstellung der Bestellungen
        bestellungLayout = new VerticalLayout();

        // Aktualisiert das Layout mit den Bestellungen des aktuellen Benutzers
        updateBestellungView();

        // Fügt das Layout mit den Bestellungen dem übergeordneten Div hinzu
        bestellungContent.add(bestellungLayout);

        // Gibt das komplette Bestell-Layout zurück
        return bestellungContent;
    }

    /**
     * Aktualisiert die Ansicht der Bestellungen des aktuell eingeloggten Benutzers.
     * Für jede Bestellung werden die enthaltenen Produkte inklusive Bild, Menge, Preis und Bewertungsmöglichkeit angezeigt.
     * Zusätzlich wird das Bestelldatum, Versandstatus und Gesamtpreis pro Bestellung dargestellt.
     */
    private void updateBestellungView() {
        // Entfernt vorherige Inhalte aus dem Layout
        bestellungLayout.removeAll();

        // Prüft, ob ein Benutzer eingeloggt ist
        if (currentUser == null) {
            bestellungLayout.add(new Text("Kein Benutzer eingeloggt."));
            return;
        }

        // Ruft die Liste aller Bestellungen des aktuellen Benutzers ab
        List<Bestellung> bestellungs = bestellungDAO.findeBestellungenVonUser(currentUser);

        // Wenn keine Bestellungen vorhanden sind, wird eine entsprechende Nachricht angezeigt
        if (bestellungs.isEmpty()) {
            bestellungLayout.add(new Text("Keine Bestellungen vorhanden."));
            return;
        }

        int bestellungNr = 1; // Zähler zur Anzeige der Bestellnummern

        // Iteration über alle Bestellungen
        for (Bestellung b : bestellungs) {
            VerticalLayout bestellungBox = new VerticalLayout();
            bestellungBox.setPadding(true);
            bestellungBox.setSpacing(true);

            // Überschrift für die Bestellung
            bestellungBox.add(new H4("Bestellung #" + bestellungNr++));

            // Iteration über alle Produkte in der Bestellung
            for (Map.Entry<Products, Integer> eintrag : b.getProdukteMitMenge().entrySet()) {
                Products p = eintrag.getKey();
                int menge = eintrag.getValue();

                // Layout zur Darstellung einer Produktzeile
                HorizontalLayout productsZeile = new HorizontalLayout();
                productsZeile.setAlignItems(FlexComponent.Alignment.CENTER);
                productsZeile.setSpacing(true);

                // Bild des Produkts
                Image img = new Image();
                img.setWidth("100px");
                img.setHeight("100px");

                if (p.getImage() != null && p.getImage().length > 0) {
                    // Wenn ein Produktbild vorhanden ist, wird es als StreamResource eingebunden
                    StreamResource res = new StreamResource(
                            p.getProductsName() + ".jfif",
                            () -> new ByteArrayInputStream(p.getImage()));
                    img.setSrc(res);
                } else {
                    // Platzhalterbild, falls kein Bild verfügbar ist
                    img.setSrc("path/to/default/image.png");
                }

                // Produktinformationen wie Name, Einzelpreis und Gesamtpreis
                VerticalLayout info = new VerticalLayout();
                info.setPadding(false);
                info.setSpacing(false);
                info.setWidthFull();

                Paragraph name = new Paragraph(p.getProductsName() + " x " + menge);
                name.getStyle().set("font-weight", "bold").set("margin", "0");

                Paragraph preis = new Paragraph("Einzelpreis: " + p.getPreis() + " €");
                preis.getStyle().set("margin", "0");

                Paragraph gesamt = new Paragraph("Gesamt: " + (p.getPreis() * menge) + " €");
                gesamt.getStyle().set("margin", "0").set("color", "gray");

                info.add(name, preis, gesamt);
                productsZeile.add(img, info); // Fügt Bild und Infos zur Produktzeile hinzu

                // Bewertungs-Layout (z. B. 1–5 Sterne)
                HorizontalLayout bewertungLayout = erzeugeSterneBewertung(wert -> {
                    Bewertung bewertung = new Bewertung();
                    bewertung.setUser(currentUser);
                    bewertung.setProducts(p);
                    bewertung.setRating("★".repeat(wert));
                    bewertungDAO.save(bewertung);
                    Notification.show("Bewertung gespeichert!");
                });

                // Kommentarfeld für optionale Textbewertung
                TextField commentField = new TextField();
                commentField.setPlaceholder("Kommentar...");
                commentField.setWidth("200px");

                // Button zum Absenden der Bewertung
                Button bewertenBtn = new Button("Speichern");
                bewertenBtn.addClickListener(event -> {
                    Bewertung bewertung = new Bewertung();
                    bewertung.setUser(currentUser);
                    bewertung.setProducts(p);
                    bewertung.setComment(commentField.getValue());
                    bewertungDAO.save(bewertung);
                    Notification.show("Bewertung gespeichert!");
                });

                // Füge Bewertungskomponenten zur Bewertungsspalte hinzu
                bewertungLayout.add(commentField, bewertenBtn);

                // Produktzeile und Bewertungszeile zur Bestellung hinzufügen
                bestellungBox.add(productsZeile, bewertungLayout);
            }

            // Metainformationen zur Bestellung anzeigen
            bestellungBox.add(new Paragraph("Datum: " + b.getBestelldatum()));
            bestellungBox.add(new Paragraph("Versand: " + (b.isVersand() ? "Erfolgreich" : "Offen")));
            bestellungBox.add(new Paragraph("Gesamtpreis: " + b.getPreis() + " €"));
            bestellungBox.add(new Hr());

            // Trennlinie zwischen den Bestellungen
            bestellungLayout.add(new Hr());

            // Bestellung zur Ansicht hinzufügen
            bestellungLayout.add(bestellungBox);
        }
    }

    /**
     * Erstellt das Layout für den Kassenbereich (Checkout-Bereich).
     * Beinhaltet die Anzeige vergangener Zahlungen und einen Button zur Durchführung der Bezahlung.
     *
     * @return ein Div-Container mit dem Kassenlayout und dem Button
     */
    private Div createKassaContent() {
        // Haupt-Container für den Kassenbereich
        Div kassaContent = new Div();

        // Layout zur Anzeige der Zahlungen und Bestellungen
        kassaLayout = new VerticalLayout();

        // Button zur Auslösung der Bezahlung
        Button payButton = new Button("Zahlen", event -> {
            // Logik zur Abwicklung der Zahlung wird in processPayment() ausgelagert

            processPayment();
        });

        // Zeigt dem Benutzer bereits getätigte Zahlungen (Rechnungsauswahl etc.)
        zeigeZahlungHistory();

        // Füge Layout und Button zum Container hinzu
        kassaContent.add(kassaLayout, payButton);

        // Rückgabe des fertigen Kasseninhalts
        return kassaContent;
    }

    /**
     * Bereitet die Kassenansicht (Checkout) vor.
     * Zeigt alle Produkte aus dem Einkaufswagen mit Einzelpreisen und berechnet den Gesamtpreis.
     * Falls der Einkaufswagen leer ist, wird ein entsprechender Hinweis angezeigt.
     */
    private void prepareCheckoutView() {
        // Vorherigen Inhalt des Layouts leeren
        kassaLayout.removeAll();

        // Falls der Einkaufswagen leer ist, zeige eine entsprechende Nachricht
        if (einkaufswagen.isEmpty()) {
            kassaLayout.add(new Text("Keine Produkte im Einkaufswagen."));
            return; // Wichtig: Danach keine weitere Verarbeitung nötig
        }

        double gesamtpreis = 0.0;

        // Durchlaufe alle Produkte im Einkaufswagen
        for (Products p : einkaufswagen) {
            // Zeige den Produktnamen und Preis
            kassaLayout.add(new Text(p.getProductsName() + " - " + p.getPreis() + " €"));

            // Preis zum Gesamtpreis addieren
            gesamtpreis += p.getPreis();

            // Trennlinie zwischen den Produkten
            kassaLayout.add(new Hr());
        }

        // Gesamtpreis am Ende anzeigen
        kassaLayout.add(new Text("Gesamtpreis: " + gesamtpreis + " €"));
    }

    /**
     * Fügt eine Liste von Produkten dem Warenkorb des aktuellen Benutzers hinzu.
     * Prüft zunächst, ob Produkte vorhanden sind und ein Benutzer eingeloggt ist.
     * Anschließend wird der Warenkorb in der Datenbank aktualisiert und die UI aktualisiert.
     *
     * @param products Liste der hinzuzufügenden Produkte
     */
    private void addToCart(List<Products> products) {
        // Sicherstellen, dass Produkte vorhanden sind und ein Benutzer eingeloggt ist
        if (products != null && !products.isEmpty() && currentUser != null) {

            // Jedes Produkt wird mit Menge 1 dem Warenkorb hinzugefügt
            for (Products p : products) {
                warenkorbDAO.fuegeProduktHinzu(currentUser, p, 1);
            }

            // Lokalen Einkaufswagen aus der Datenbank neu laden
            updateEinkaufswagenFromDatabase();

            // Erfolgsnachricht anzeigen
            Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt");

        } else {
            // Fehlerhinweis anzeigen, wenn Produkte fehlen oder kein Benutzer eingeloggt ist
            Notification.show("Keine Produkte ausgewählt oder Benutzer nicht eingeloggt");
        }
    }

    /**
     * Verarbeitet die Bezahlung durch den eingeloggten Benutzer.
     * Erstellt eine Bestellung, speichert sie, leert den Warenkorb und aktualisiert die UI.
     */
    private void processPayment() {
        // Sicherstellen, dass ein Benutzer eingeloggt ist
        if (currentUser == null) {
            Notification.show("Kein Benutzer eingeloggt.");
            return;
        }

        // Warenkorb des aktuellen Benutzers abrufen
        Warenkorb warenkorb = warenkorbDAO.findeWarenkorbVonUser(currentUser);
        if (warenkorb == null || warenkorb.getProdukteMitMenge().isEmpty()) {
            Notification.show("Der Einkaufswagen ist leer.");
            return;
        }

        // Bestellung auf Basis des Warenkorbs erstellen und speichern
        Bestellung bestellung = bestellungDAO.erstelleBestellung(
                currentUser,
                warenkorb.getProdukteMitMenge(),
                true // Bezahlstatus: true = bezahlt
        );

        // Bestellverlauf für spätere Übersicht speichern
        bestellverlauf.add(new ArrayList<>(warenkorb.getProdukteMitMenge().keySet()));

        // Warenkorb leeren (Datenbank und UI)
        warenkorbDAO.leereWarenkorb(currentUser);
        einkaufswagen.clear();
        // Produkte im Warenkorb durchgehen und Bestand reduzieren
        for (Map.Entry<Products, Integer> eintrag : warenkorb.getProdukteMitMenge().entrySet()) {
            Products produkt = eintrag.getKey();
            int gekaufteMenge = eintrag.getValue();
            int aktuelleMenge = produkt.getBestand();

            if (aktuelleMenge >= gekaufteMenge) {
                produkt.setBestand(aktuelleMenge - gekaufteMenge);
                dao.update(produkt); // Bestand in der DB aktualisieren
            } else {
                Notification.show("Nicht genug Bestand für: " + produkt.getProductsName());
            }
        }

        // UI aktualisieren
        updateEinkaufswagenFromDatabase(); // Warenkorb aus Datenbank neu laden
        prepareCheckoutView();             // Ansicht für neuen Checkout vorbereiten
        updateBestellungView();            // Bestellübersicht neu laden


        // Erfolgsmeldung anzeigen
        Notification.show("Bezahlung abgeschlossen. Bestellung gespeichert!");

        // Zahlung in der Datenbank speichern
        zahlungDAO.speichereZahlung( bestellung.getPreis(), bestellung);
    }

    /**
     * Zeigt dem Nutzer eine Liste aller bisherigen Zahlungen in einer ComboBox an.
     * Beim Auswählen einer Rechnung werden Rechnungsnummer, Datum und Betrag angezeigt.
     */
    private void zeigeZahlungHistory() {
        // Lade alle Zahlungen des aktuellen Benutzers (über dessen Warenkorb)
        List<Zahlung> zahlungs = zahlungDAO.findeZahlungenVonUser(currentUser);

        // Vorherige Inhalte des Layouts entfernen
        kassaLayout.removeAll();

        // Falls keine Zahlungen vorhanden sind, zeige Hinweis
        if (zahlungs.isEmpty()) {
            kassaLayout.add(new Text("Keine Zahlungen gefunden."));
            return;
        }

        // ComboBox zur Auswahl einer Rechnung
        ComboBox<Zahlung> rechnungBox = new ComboBox<>();
        rechnungBox.setWidth("300px");
        rechnungBox.setItems(zahlungs);

        // Formatierung des Anzeige-Labels für jede Rechnung
        rechnungBox.setItemLabelGenerator(z ->
                "Rechnung #" + z.getRechnungsnummer() + " vom " + z.getZahlungDatum());

        // Layout zur Anzeige der Rechnungsdetails
        VerticalLayout detailsLayout = new VerticalLayout();
        detailsLayout.setPadding(true);
        detailsLayout.setSpacing(true);

        // Event: Nutzer wählt eine Rechnung aus
        rechnungBox.addValueChangeListener(event -> {
            detailsLayout.removeAll(); // Alte Inhalte entfernen
            Zahlung selected = event.getValue(); // Ausgewählte Zahlung

            if (selected != null) {
                // Rechnungsdetails anzeigen
                detailsLayout.add(
                        new Paragraph("Rechnungsnummer: " + selected.getRechnungsnummer()),
                        new Paragraph("Zahlungsdatum: " + selected.getZahlungDatum()),
                        new Paragraph("Gesamtbetrag: " + selected.getBetrag() + " €")
                );

                // Gekaufte Produkte anzeigen (falls vorhanden)
                Bestellung bestellung = selected.getBestellung();
                if (bestellung != null && bestellung.getProdukteMitMenge() != null) {
                    detailsLayout.add(new Hr(), new Text("Produkte:"));

                    for (Map.Entry<Products, Integer> entry : bestellung.getProdukteMitMenge().entrySet()) {
                        Products produkt = entry.getKey();
                        int menge = entry.getValue();
                        double einzelpreis = produkt.getPreis();
                        double gesamt = einzelpreis * menge;

                        String produktInfo = String.format(
                                "- %s x%d = %.2f €", produkt.getProductsName(), menge, gesamt
                        );

                        detailsLayout.add(new Text(produktInfo));
                    }
                }
            }
        });

        // Alles im Layout anzeigen
        kassaLayout.add(new Text("Rechnungsübersicht:"), rechnungBox, detailsLayout);
    }


    /**
     * Konfiguriert eine MultiSelectListBox mit einer benutzerdefinierten Darstellung für Produktobjekte.
     * Jedes Produkt wird mit Bild, Name, Preis und Sternebewertung angezeigt.
     *
     * @param multiSelectListBox Die MultiSelectListBox, in die die Produkte geladen werden sollen.
     * @param products            Die Liste der darzustellenden Produkte.
     */
    private void setAvatarItemsSampleData(MultiSelectListBox<Products> multiSelectListBox, List<Products> products) {

        // Setzt die Produktliste als auswählbare Elemente in der ListBox
        multiSelectListBox.deselectAll();
        multiSelectListBox.clear();
        multiSelectListBox.setItems(products);

        // Definiert ein benutzerdefiniertes Rendering für jedes Produkt (als UI-Komponente)
        multiSelectListBox.setRenderer(new ComponentRenderer<>(item -> {
            // Layout für die horizontale Darstellung von Bild + Infos
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER); // Bild & Text vertikal zentrieren

            Image image;

            // Wenn Produktbild vorhanden ist, in StreamResource umwandeln und anzeigen
            try {
                if (item.getImage() != null && item.getImage().length > 10) {
                    StreamResource resource = new StreamResource(
                            item.getProductsName() + ".jpg",
                            () -> new ByteArrayInputStream(item.getImage())
                    );
                    image = new Image(resource, "Produktbild");
                } else {
                    image = new Image("images/standard.png", "Kein Bild");
                }
            } catch (Exception e) {
                image = new Image("images/standard.png", "Fehler beim Laden");
            }

            // Setze Bildgröße (optisch ansprechend)
            image.setWidth("50px");
            image.setHeight("70px");

            // Lade alle Bewertungen zum aktuellen Produkt
            List<Bewertung> bewertungen = bewertungDAO.findeBewertungenZuProdukt(item);

            // Durchschnitt berechnen: Anzahl der Sterne basiert auf der Länge des Rating-Strings (z. B. "★★★")
            double avgRating = bewertungen.stream()
                    .mapToInt(b -> b.getRating() != null ? b.getRating().length() : 0)
                    .average()
                    .orElse(0);

            // Darstellung der Bewertung in Sternen (max. 5)
            String sterne = "☆☆☆☆☆";
            if (avgRating > 0) {
                sterne = "★".repeat((int) Math.round(avgRating)); // Runde Bewertung auf ganze Zahl
                while (sterne.length() < 5) sterne += "☆"; // Ergänze auf 5 Zeichen
            }

            // UI-Komponente für die Sterneanzeige
            Span sterneSpan = new Span("⭑ " + sterne);
            sterneSpan.getStyle()
                    .set("font-size", "20px")
                    .set("color", "#FFD700"); // Goldfarbe

            // Vertikales Layout für Produktname, Preis und Sterne
            VerticalLayout info = new VerticalLayout();
            info.setPadding(false);
            info.setSpacing(false);

            // Produktname + Preis kombinieren
            Span namePreis = new Span(item.getProductsName() + " - " + item.getPreis() + " €");

            // Informationen hinzufügen
            info.add(namePreis, sterneSpan);

            // Bild und Textinfos zusammen ins horizontale Layout setzen
            layout.add(image, info);

            // Rückgabe der vollständigen Komponente für ein Produkt
            return layout;
        }));
    }

    /**
     * Aktualisiert die Produktdarstellung in der Benutzeroberfläche.
     * Zeigt das Produktbild, Produktinformationen, durchschnittliche Sternebewertung,
     * interaktive Bewertungskomponente und bis zu drei Kundenkommentare.
     *
     * @param product Das ausgewählte Produkt, dessen Details angezeigt werden sollen.
     */
    private void updateProductImageWithProductInfo(Products product) {
        // Konsolenausgabe zur Fehleranalyse (kann später entfernt werden)
        System.out.println("Aktualisiere Bild mit Produkt: " + product.getProductsName());

        // Bild setzen: Wenn Produktbild vorhanden ist, als StreamResource anzeigen
        if (product.getImage() != null && product.getImage().length > 0) {
            StreamResource resource = new StreamResource(
                    product.getProductsName() + ".jfif",
                    () -> new ByteArrayInputStream(product.getImage())
            );
            productImage.setSrc(resource);
        } else {
            // Wenn kein Bild vorhanden ist, Platzhalterbild verwenden
            productImage.setSrc("path/to/default/image.png");
        }

        // Neues vertikales Layout zur Darstellung der Produktinformationen
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        // Produktname, Status, Preis und Lagerbestand anzeigen
        infoLayout.add(new H4(product.getProductsName()));
        infoLayout.add(new Paragraph(product.getStatus()));
        infoLayout.add(new Paragraph("Preis: " + product.getPreis() + " €"));
        infoLayout.add(new Paragraph("Verfügbar auf Lager: " + product.getBestand()));

        // Alle Bewertungen zum Produkt aus der Datenbank abrufen
        List<Bewertung> bewertungen = bewertungDAO.findeBewertungenZuProdukt(product);

        // Durchschnittliche Bewertung auf Basis der Länge des Rating-Strings berechnen
        double avgRating = bewertungen.stream()
                .mapToInt(b -> b.getRating() != null ? b.getRating().length() : 0)
                .average()
                .orElse(0);

        // Sterne-Visualisierung basierend auf der Durchschnittsbewertung erzeugen
        String sterne = "☆☆☆☆☆"; // Standardanzeige bei keiner Bewertung
        if (avgRating > 0) {
            sterne = "★".repeat((int) Math.round(avgRating)); // Runde auf ganze Sterne
            while (sterne.length() < 5) sterne += "☆";         // Auf 5 Sterne auffüllen
        }

        // Sternebewertung visuell im UI anzeigen
        Span sterneSpan = new Span(" " + sterne);
        sterneSpan.getStyle().set("font-size", "20px").set("color", "#FFD700");
        infoLayout.add(sterneSpan);

        // Bis zu drei vorhandene Kommentare unter dem Produkt anzeigen
        for (int i = 0; i < Math.min(3, bewertungen.size()); i++) {
            Bewertung b = bewertungen.get(i);
            infoLayout.add(new Paragraph("„" + b.getComment() + "“"));
        }

        // Vorherige Produktinformationen im Layout entfernen und aktualisieren
        productsInfoLayout.removeAll();
        productsInfoLayout.add(infoLayout);
    }

    /**
     * Erzeugt eine interaktive horizontale Sternebewertung (1–5 Sterne).
     * Jeder Stern ist klickbar und ruft einen Callback mit dem gewählten Wert (1–5) auf.
     *
     * Die Darstellung zeigt standardmäßig leere Sterne (☆). Nach Auswahl eines Sterns
     * werden alle Sterne bis zu diesem Index gefüllt dargestellt (★).
     *
     * @param callback Funktion, die aufgerufen wird, wenn ein Stern ausgewählt wurde (z. B. zum Speichern).
     * @return Ein HorizontalLayout mit klickbaren Sternen zur Anzeige in der UI.
     */
    private HorizontalLayout erzeugeSterneBewertung(Consumer<Integer> callback) {

        // Layout für die horizontale Anordnung der Sterne
        HorizontalLayout sternLayout = new HorizontalLayout();
        sternLayout.setSpacing(false); // Kein Zwischenraum zwischen Sternen
        sternLayout.setPadding(false); // Kein Innenabstand

        List<Span> sterne = new ArrayList<>();   // Liste zur Verwaltung aller Stern-Spans
        final int[] aktuelleBewertung = {0};     // Aktuell ausgewählter Bewertungswert (mutable für Lambda-Ausdruck)

        // Erzeuge 5 Sterne, nummeriert von 1 bis 5
        for (int i = 1; i <= 5; i++) {
            Span stern = new Span("☆"); // Standardmäßig leerer Stern

            // Stil des Sternsymbols
            stern.getStyle()
                    .set("font-size", "24px")   // Größe des Sternzeichens
                    .set("cursor", "pointer")   // Zeiger-Maus beim Hover
                    .set("color", "#FFD700");   // Goldfarbe für Sterne

            final int bewertungWert = i; // Muss final oder effektiv final sein für Lambda

            // Klick-Listener für den Stern
            stern.addClickListener(event -> {
                // Speichere den neuen Bewertungswert
                aktuelleBewertung[0] = bewertungWert;

                // Aktualisiere alle Sterne entsprechend der Auswahl
                for (int j = 0; j < sterne.size(); j++) {
                    // Sterne bis zur Bewertung füllen (★), andere leer lassen (☆)
                    sterne.get(j).setText(j < bewertungWert ? "★" : "☆");
                }

                // Callback-Funktion aufrufen, um Bewertungswert weiterzugeben (z. B. speichern)
                callback.accept(bewertungWert);
            });

            // Stern zur internen Liste und zum Layout hinzufügen
            sterne.add(stern);
            sternLayout.add(stern);
        }

        // Rückgabe des fertigen Layouts mit klickbaren Sternen
        return sternLayout;
    }
}


