package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

import com.vaadin.flow.data.renderer.ComponentRenderer;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;


import java.io.ByteArrayInputStream;

import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


import lombok.Setter;
import org.commercetron.beans.*;

import org.commercetron.controller.*;
import org.commercetron.dao.*;
import org.commercetron.utils.InputUtils;


@PageTitle("Home")
@Route("home")
public class HomeView extends Composite<VerticalLayout> {

    // --- DAO-Objekte für Datenbankzugriffe ---
    private final ProductsDAO  dao = new ProductsDAO();         // DAO für Produkte
    private final ProductsController productsController;
    private final KategorieDAO katDao;      // DAO für Kategorien
    private final KategorieController kategorieController;
    private final BestellungDAO bestellungDAO;// DAO für Bestellungen
    private final BestellungController bstellungController;
    private final WarenkorbController warenkorbController;
    private final WunschlisteDAO wunschlisteDAO  = new WunschlisteDAO();        // DAO für Wunschliste
    private final WunchlisteCntroller wunchlisteCntroller;
    private final ZahlungDAO zahlungDAO; // DAO für Zahlungen
    private final ZahlungController zahlungController;
    private final BewertungDAO bewertungDAO; // DAO für Bewertungen
    private final BewertungController bewertungController;
    private final UserDAO userDAO;                               // DAO für User
    private final UserController userController;

    // --- UI-Komponenten ---
    private MultiSelectListBox<Products> avatarItems = new MultiSelectListBox<>(); // Produktübersicht / Auswahl
    private MultiSelectListBox<Products> merklisteBox = new MultiSelectListBox<>(); // Wunschliste
    private VerticalLayout einkaufswagenLayout;    // Layout für Einkaufswagen
    private VerticalLayout kassaLayout;            // Layout für Kassa/Bezahlung
    private VerticalLayout bestellungLayout;       // Layout für Bestellübersicht
    private VerticalLayout productsInfoLayout = new VerticalLayout(); // Detailinformationen zum ausgewählten Produkt
    private VerticalLayout merklisteLayout = new VerticalLayout();    // Layout für Merkliste
    private Image productImage = new Image();      // Anzeige für Produktbilder
    private Tab searchTab;                          // Tab für Suchergebnisse

    // --- Geschäftslogik / Datenhaltung ---
    private final List<Products> einkaufswagen = new ArrayList<>(); // temporäre lokale Einkaufswagenliste
    private final List<List<Products>> bestellverlauf = new ArrayList<>(); // lokale Historie
    private final List<Products> merkliste = new ArrayList<>();      // lokale Wunschliste
    private Map<Tab, Kategorie> tabToCategory = new HashMap<>();     // Zuordnung Tab -> Kategorie
    private int passwortVersuche = 0;                               // Zähler für Loginversuche
    private long passwortSperreBis = 0;                              // Zeitstempel Sperre

    private Warenkorb warenkorb;                                     // aktuelle Warenkorbinstanz

    // --- Aktueller Benutzer ---
    @Setter
    private User currentUser;

    /**
     * Konstruktor der HomeView – Hauptansicht nach erfolgreicher Benutzeranmeldung.
     * Initialisiert DAOs, prüft den angemeldeten Benutzer, lädt die UI-Komponenten
     * und synchronisiert Wunschliste mit der Datenbank.
     */
    public HomeView() {

        // DAO-Initialisierung
        this.bestellungDAO = new BestellungDAO();

        ProductsDAO dao = new ProductsDAO(); // hier initialisieren
        this.productsController = new ProductsController(dao);
        this.katDao  = new KategorieDAO(); // hier initialisieren
        this.kategorieController = new KategorieController(katDao);
        BestellungDAO bestellungDAO  = new BestellungDAO(); // hier initialisieren
        this.bstellungController= new BestellungController(bestellungDAO);
        WarenkorbDAO warenkorbDAO = new WarenkorbDAO(Warenkorb.class);
        this.warenkorbController = new WarenkorbController(warenkorbDAO);//        this.wunschlisteDAO = wunschlisteDAO;
        this.wunchlisteCntroller = new WunchlisteCntroller(wunschlisteDAO);
        this.zahlungDAO = new ZahlungDAO();
        this.zahlungController = new ZahlungController(zahlungDAO);
        this.bewertungDAO = new BewertungDAO();
        this.bewertungController = new BewertungController(bewertungDAO);
        this.userDAO = new UserDAO();
        this.userController = new UserController(userDAO);

        // Abruf des aktuell angemeldeten Benutzers aus Vaadin-Session
        this.currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);

        // Sicherheitsprüfung: kein Benutzer angemeldet -> Weiterleitung zur Login-Seite
        if (this.currentUser == null) {
            UI.getCurrent().navigate("anmeldung");
            return;
        }

        // Aufbau der UI-Komponenten
        initLayout();

        // Wunschliste initial laden
        updateWunschlisteFromDatabase();
    }

    /**
     * Initialisiert das Hauptlayout der Startseite.
     * Diese Methode baut die Benutzeroberfläche für die Produktübersicht,
     * Produktauswahl, Interaktionen wie Warenkorb/Merkliste sowie die Navigation über Tabs auf.
     */
    private void initLayout() {

        // ---------- Kopfzeile mit Shop-Titel und Suchfeld ----------
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassNames("shop-header", "home-toolbar");
        layoutRow.setWidthFull();
        layoutRow.setHeight("84px");
        layoutRow.setPadding(true);
        layoutRow.setSpacing(true);
        layoutRow.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layoutRow.getStyle()
                .set("background-color", "#f9f9f9")
                .set("border-bottom", "1px solid #e0e0e0")
                .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.03)");

        Div mark = new Div(VaadinIcon.CART.create());
        mark.addClassName("brand-mark");

        H1 h1 = new H1("InnoShop");
        h1.addClassName("brand-title");
        h1.getStyle()
                .set("margin", "0")
                .set("font-size", "1.45rem")
                .set("font-weight", "800")
                .set("color", "var(--innoshop-text)");

        HorizontalLayout brand = new HorizontalLayout(mark, h1);
        brand.setAlignItems(FlexComponent.Alignment.CENTER);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Produkt suchen...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));

        layoutRow.add(brand, searchField); // Header-Layout mit Titel und Suche

        // ---------- Gesamtlayout vorbereiten ----------
        VerticalLayout mainLayout = getContent();
        mainLayout.removeAll();
        mainLayout.setWidthFull();
        mainLayout.setPadding(false);
        mainLayout.setSpacing(false);
        mainLayout.addClassName("home-page");
        mainLayout.add(layoutRow); // Header hinzufügen

        // ---------- Kategorie-Tabs ----------
        Tabs categoryTabs = new Tabs();
        categoryTabs.addClassName("category-tabs");
        categoryTabs.setWidthFull();
        setCategoryTabs(categoryTabs, avatarItems); // Tabs aus Datenquelle initialisieren

        // ---------- Produktauswahl-Bereich ----------
        avatarItems.addClassName("catalog-list");
        avatarItems.setWidthFull();

        // Produktbild
        productImage.setWidth("160px");
        productImage.setHeight("200px");
        productImage.addClassName("detail-image");
        productImage.getStyle().set("object-fit", "contain"); // Kein Verzerren

        // Buttons für Warenkorb und Merkliste
        Button buttonPrimary = new Button("In den Einkaufswagen");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button buttonPrimary2 = new Button("Merkliste hinzufügen");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Produktinfo-Bereich
        productsInfoLayout.setId("product-info");
        productsInfoLayout.setWidthFull();

        // Button-Layout
        VerticalLayout buttonLayout = new VerticalLayout(buttonPrimary, buttonPrimary2);
        buttonLayout.addClassName("detail-actions");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setPadding(false);
        buttonLayout.getStyle()
                .set("margin-top", "10px")
                .set("align-items", "stretch");
        buttonPrimary.setWidthFull();
        buttonPrimary2.setWidthFull();

        // Linker Bereich mit Produktbild und Buttons
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.addClassName("detail-media");
        leftLayout.setWidth("230px");
        leftLayout.setPadding(false);
        leftLayout.setSpacing(true);
        leftLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Div spacer = new Div(); // Platzhalter für gleichmäßige Verteilung
        leftLayout.add(productImage, buttonLayout);

        // Rechter Bereich mit Produktinformationen
        VerticalLayout rightInfoLayout = new VerticalLayout();
        rightInfoLayout.addClassName("detail-info");
        rightInfoLayout.setWidthFull();
        rightInfoLayout.getStyle().set("overflow", "visible");
        rightInfoLayout.setSpacing(true);
        rightInfoLayout.setPadding(false);

        productsInfoLayout.setWidthFull();
        productsInfoLayout.setHeightFull();
        productsInfoLayout.getStyle().set("overflow", "visible");
        rightInfoLayout.add(productsInfoLayout);
        rightInfoLayout.expand(productsInfoLayout);

        // Komplette Produkt-Detailkarte
        HorizontalLayout productCard = new HorizontalLayout(leftLayout, rightInfoLayout);
        productCard.addClassName("detail-card");
        productCard.setSpacing(true);
        productCard.setWidthFull();
        productCard.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radius", "8px")
                .set("padding", "1rem")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("background-color", "white");

        // Hauptbereich mit Produktauswahl und Detailansicht
        HorizontalLayout mainContentLayout = new HorizontalLayout(
                avatarItems,
                new Hr(), // Visuelle Trennung
                productCard
        );
        mainContentLayout.addClassName("catalog-layout");
        mainContentLayout.setWidthFull();
        mainContentLayout.setSpacing(true);
        mainContentLayout.setAlignItems(FlexComponent.Alignment.START);
        mainContentLayout.expand(avatarItems);

        // Neues Layout setzen
        mainLayout.removeAll();
        mainLayout.setSpacing(false);
        mainLayout.add(
                layoutRow,
                categoryTabs,
                mainContentLayout
        );

        // ---------- Event Listener ----------

        // Produkt-Auswahl ändert Bild und Info
        avatarItems.addValueChangeListener(event -> {
            List<Products> selected = new ArrayList<>(event.getValue());
            if (!selected.isEmpty()) {
                updateProductImageWithProductInfo(selected.get(0));
            } else {
                productImage.setSrc(new StreamResource(
                        "default.png",
                        () -> getClass().getResourceAsStream("/images/default.png")
                ));
            }
        });

        // Button: Produkte in Warenkorb
        buttonPrimary.addClickListener(event -> {
            List<Products> selected = new ArrayList<>(avatarItems.getSelectedItems());
            if (!selected.isEmpty()) {
                for (Products p : selected) {
                    warenkorbController.getFuegeProduktHinzu(currentUser, p, 1);
                }
                updateEinkaufswagenFromDatabase();
                Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");
                avatarItems.clear();
            }
        });

        // Button: Produkte in Merkliste
        buttonPrimary2.addClickListener(event -> {
            List<Products> selected = new ArrayList<>(avatarItems.getSelectedItems());
            if (!selected.isEmpty()) {
                for (Products p : selected) {
                    wunchlisteCntroller.getfuegeProduktHinzu(currentUser, p);
                }
                updateWunschlisteFromDatabase();
                Notification.show("Produkte wurden zur Merkliste hinzugefügt!");
                avatarItems.clear();
            }
        });

        // Zufälliges Produkt initial anzeigen
        List<Products> randomProducts = productsController.getRandom(1);
        if (!randomProducts.isEmpty()) {
            updateProductImageWithProductInfo(randomProducts.get(0));
        } else {
            productImage.setSrc(new StreamResource(
                    "default.png",
                    () -> getClass().getResourceAsStream("/images/default.png")));
        }

        // Suchfeld: Filterung nach Kategorie + Suchbegriff
        searchField.addValueChangeListener(event -> {
            String searchTerm = event.getValue().trim().toLowerCase();
            Tab selectedTab = categoryTabs.getSelectedTab();
            Kategorie selectedKategorie = tabToCategory.get(selectedTab);

            List<Products> filteredProducts;
            if (selectedKategorie != null) {
                filteredProducts = productsController.getByKategorie(selectedKategorie).stream()
                        .filter(p -> p.getProductsName().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList());
            } else {
                filteredProducts = productsController.getAlleAktiven().stream()
                        .filter(p -> p.getProductsName().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList());

                if (selectedTab != searchTab) {
                    categoryTabs.setSelectedTab(searchTab); // Automatisch in Such-Tab wechseln
                }
            }

            setAvatarItemsSampleData(avatarItems, filteredProducts);
            if (!filteredProducts.isEmpty()) {
                updateProductImageWithProductInfo(filteredProducts.get(0));
            } else {
                productImage.setSrc("path/to/default/image.png");
            }
        });

        // ---------- TabSheet: Weitere Benutzeraktionen ----------
        TabSheet tabSheet = new TabSheet();
        tabSheet.addClassName("tab-sheet");
        tabSheet.setWidthFull();
        tabSheet.setMinHeight("420px");

        tabSheet.add("Einkaufswagen", createEinkaufswagenContent());
        tabSheet.add("Merkliste", createMerklisteContent());
        tabSheet.add("Bestellung", createBestellungContent());
        tabSheet.add("Kassa", createKassaContent());
        tabSheet.add("Profil", createProfilContent());

        mainLayout.add(tabSheet);

        // Initialen Zustand des Einkaufswagens aus Datenbank laden
        updateEinkaufswagenFromDatabase();
    }
    /**
     * Initialisiert die Kategorietabs für die Benutzeroberfläche.
     * Jeder Tab repräsentiert eine Produktkategorie oder die Suchfunktion.
     * Beim Wechsel des Tabs wird die Produktliste (MultiSelectListBox) entsprechend aktualisiert.
     *
     * @param tabs das Tabs-Element, dem die Kategorietabs hinzugefügt werden
     * @param avatarItems die Produktliste, die auf Basis des ausgewählten Tabs aktualisiert wird
     */
    private void setCategoryTabs(Tabs tabs, MultiSelectListBox<Products> avatarItems) {
        // Zuordnung zwischen Tabs und Kategorien
        Map<Tab, Kategorie> tabToCategory = new HashMap<>();

        // Such-Tab erstellen (ohne zugeordnete Kategorie)
        Tab searchTab = new Tab("🔍 Suche");
        tabToCategory.put(searchTab, null);  // Such-Tab ist keiner Kategorie zugeordnet
        tabs.add(searchTab);

        // Kategorien aus der Datenbank laden und je einen Tab pro Kategorie erstellen
        List<Kategorie> kategorien = kategorieController.getAll();
        for (Kategorie kategorie : kategorien) {
            Tab tab = new Tab(kategorie.getName());
            tabToCategory.put(tab, kategorie);
            tabs.add(tab);
        }

        // Listener: Reagiert auf Tabwechsel
        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();  // Aktuell ausgewählter Tab
            Kategorie selectedKategorie = tabToCategory.get(selectedTab);  // Zugehörige Kategorie (kann null sein)

            if (selectedKategorie != null) {
                // Produkte der ausgewählten Kategorie laden und anzeigen
                List<Products> products = productsController.getAlleAktivenInKategorie(selectedKategorie);
                setAvatarItemsSampleData(avatarItems, products);
            } else if (selectedTab == searchTab) {
                // Im Such-Tab: Alle aktiven Produkte anzeigen
                List<Products> products = productsController.getAlleAktiven();
                setAvatarItemsSampleData(avatarItems, products);
            }
        });
    }

    /**
     * Erzeugt das UI für die Benutzerprofilverwaltung.
     * Beinhaltet Anzeige und Bearbeitung von Name, Adresse, E-Mail sowie Passwortänderung.
     * Zudem ist ein Logout-Button integriert.
     */
    private Div createProfilContent() {
        Div profilLayout = new Div(); // Hauptcontainer für das Profil
        profilLayout.setWidthFull();

        H3 title = new H3("Konto verwalten");

        // Eingabefelder für Benutzerdaten
        FormLayout formLayout = new FormLayout();
        TextField nameField = new TextField("Vollständiger Name");
        TextField addressField = new TextField("Adresse");
        EmailField emailField = new EmailField("Email");
        PasswordField altePasswortField = new PasswordField("Altes Passwort");
        PasswordField passwordField = new PasswordField("Neues Passwort");
        PasswordField passwordField1 = new PasswordField("Passwort bestätigen");

        // Benutzerinformationen vorausfüllen (sofern eingeloggt)
        if (currentUser != null) {
            nameField.setValue(currentUser.getUser() != null ? currentUser.getUser() : "");
            addressField.setValue(currentUser.getAdresse() != null ? currentUser.getAdresse() : "");
            emailField.setValue(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        } else {
            Notification.show("Kein gültiger Benutzer!", 3000, Notification.Position.MIDDLE);
        }

        formLayout.add(nameField, addressField, emailField, altePasswortField, passwordField, passwordField1);

        // Speichern-Button: Validierung & Aktualisierung
        Button saveButton = new Button("Aktualisieren", e -> {
            boolean etwasGeaendert = false;

            // Werte aus den Feldern auslesen
            String name = nameField.getValue();
            String adresse = addressField.getValue();
            String email = emailField.getValue();
            String altePasswort = altePasswortField.getValue();
            String passwort = passwordField.getValue();
            String passwort1 = passwordField1.getValue();

            // Änderungen prüfen und übernehmen
            if (!name.equals(currentUser.getUser())) {
                currentUser.setUser(name);
                etwasGeaendert = true;
            }

            if (!adresse.equals(currentUser.getAdresse())) {
                currentUser.setAdresse(adresse);
                etwasGeaendert = true;
            }

            if (!email.equals(currentUser.getEmail())) {
                if (!InputUtils.isValidDateEmailInput(email)) {
                    Notification.show("Ungültige E-Mail-Adresse!", 3000, Notification.Position.MIDDLE);
                    return;
                }
                currentUser.setEmail(email);
                etwasGeaendert = true;
            }

            // Passwortänderung nur, wenn eines der Felder befüllt ist
            boolean willPasswortAendern =
                    !altePasswort.isEmpty() ||
                            !passwort.isEmpty() ||
                            !passwort1.isEmpty();

            long jetzt = System.currentTimeMillis();

            if (willPasswortAendern) {

                // Passwort-Sperrung bei wiederholten Fehlversuchen
                if (jetzt < passwortSperreBis) {
                    long sekunden = (passwortSperreBis - jetzt) / 1000;
                    Notification.show("Zu viele Fehlversuche. Bitte versuche es in " + sekunden + " Sekunden erneut.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (altePasswort.isEmpty()) {
                    Notification.show("Bitte altes Passwort eingeben.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (!altePasswort.equals(currentUser.getPassword())) {
                    passwortVersuche++;

                    if (passwortVersuche >= 3) {
                        passwortSperreBis = jetzt + 3 * 60 * 1000; // 3 Minuten Sperre
                        passwortVersuche = 0;
                        Notification.show("Zu viele falsche Passwörter. Passwortänderung gesperrt für 3 Minuten.", 3000, Notification.Position.MIDDLE);
                    } else {
                        Notification.show("Falsches Passwort! Versuch " + passwortVersuche + " von 3.", 3000, Notification.Position.MIDDLE);
                    }
                    return;
                }

                // Passwortversuche zurücksetzen bei erfolgreicher Validierung
                passwortVersuche = 0;

                if (passwort.length() < 8) {
                    Notification.show("Neues Passwort muss mindestens 8 Zeichen lang sein.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                if (!passwort.equals(passwort1)) {
                    Notification.show("Neue Passwörter stimmen nicht überein.", 3000, Notification.Position.MIDDLE);
                    return;
                }

                currentUser.setPassword(passwort);
                etwasGeaendert = true;

                // Felder leeren nach erfolgreicher Passwortänderung
                altePasswortField.clear();
                passwordField.clear();
                passwordField1.clear();
            }

            // Änderungen speichern
            if (etwasGeaendert) {
                userController.update(currentUser);
                Notification.show("Profil erfolgreich aktualisiert.", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Keine Änderungen vorgenommen.", 3000, Notification.Position.MIDDLE);
            }
        });

        // Logout-Button zum Abmelden und Beenden der Session
        Button logoutButton = new Button("Abmelden", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        logoutButton.addClickListener(e -> {
            // Benutzer aus der Session entfernen
            VaadinSession.getCurrent().setAttribute(User.class, null);

            // Zurück zur Startseite navigieren
            UI.getCurrent().navigate("");

            // Session leicht verzögert beenden (sicherstellen, dass Navigation abgeschlossen ist)
            UI ui = UI.getCurrent();
            new Thread(() -> {
                try {
                    Thread.sleep(500); // kleine Verzögerung
                } catch (InterruptedException ignored) {}

                ui.access(() -> VaadinSession.getCurrent().close());
            }).start();
        });

        // Komponenten hinzufügen
        profilLayout.add(title, formLayout, saveButton, logoutButton);

        return profilLayout;
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
                        sanitizeFileName(p.getProductsName()) + ".jpg",
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
                wunchlisteCntroller.getproductsEntfernen(currentUser, p);
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
            // Lokalen Einkaufswagen leeren
            einkaufswagen.clear();

            // Warenkorb aus DB holen
            Warenkorb warenkorb = warenkorbController.getWarenkorbVonUser(currentUser);

            if (warenkorb != null) {
                warenkorb.getProdukteMitMenge().clear();     // Produkte entfernen
                warenkorb.setGesamtPreis(0);
                warenkorb.setVersandPreis(0);
                warenkorbController.update(warenkorb);              // In DB speichern
            }

            updateEinkaufswagenView(); // UI aktualisieren
            Notification.show("Warenkorb wurde geleert.");
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
     * Synchronisiert die lokale Einkaufswagenansicht mit den aktuellen Daten des Benutzers aus der Datenbank.
     * <p>
     * Die Methode aktualisiert sowohl die interne Datenstruktur {@code einkaufswagen} als auch das visuelle Layout
     * ({@code einkaufswagenLayout}), sodass Änderungen im Warenkorb (z. B. Mengenanpassungen) sofort sichtbar werden.
     * </p>
     *
     * <ul>
     *   <li>Lädt den aktuellen {@link Warenkorb} des eingeloggten Benutzers.</li>
     *   <li>Erzeugt für jedes Produkt eine Zeile mit Bild, Informationen und Mengensteuerung.</li>
     *   <li>Bietet Buttons (+/-) zur Mengenänderung, die den Warenkorb in der Datenbank synchronisieren.</li>
     *   <li>Zeigt eine Meldung an, falls der Warenkorb leer ist.</li>
     * </ul>
     */
    private void updateEinkaufswagenFromDatabase() {
        // Lokale Liste und Layout leeren, um mit einer frischen Ansicht zu starten
        einkaufswagen.clear();
        einkaufswagenLayout.removeAll();

        // Warenkorb für aktuellen Benutzer laden
        Warenkorb warenkorb = warenkorbController.getWarenkorbVonUser(currentUser);

        if (warenkorb != null && !warenkorb.getProdukteMitMenge().isEmpty()) {
            // Für jedes Produkt im Warenkorb eine UI-Zeile erzeugen
            for (Map.Entry<Products, Integer> entry : warenkorb.getProdukteMitMenge().entrySet()) {
                Products produkt = entry.getKey();
                int menge = entry.getValue();

                // Produkt in lokale Liste aufnehmen
                einkaufswagen.add(produkt);

                // --- Layout für ein Produkt ---
                HorizontalLayout produktZeile = new HorizontalLayout();
                produktZeile.setAlignItems(FlexComponent.Alignment.CENTER);
                produktZeile.setSpacing(true);
                produktZeile.setPadding(true);
                produktZeile.getStyle().set("border-bottom", "1px solid #ccc");
                produktZeile.setWidthFull();

                // --- Produktbild ---
                Image produktBild = new Image();
                produktBild.setWidth("100px");
                produktBild.setHeight("100px");

                if (produkt.getImage() != null && produkt.getImage().length > 0) {
                    // Falls Bild vorhanden: als StreamResource darstellen
                    StreamResource bildResource = new StreamResource(
                            produkt.getProductsName().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_") + ".jfif",
                            () -> new ByteArrayInputStream(produkt.getImage())
                    );
                    produktBild.setSrc(bildResource);
                } else {
                    // Platzhalterbild anzeigen
                    produktBild.setSrc("images/default-product.jpg");
                }

                // --- Produktinformationen ---
                VerticalLayout produktInfo = new VerticalLayout();
                produktInfo.setPadding(false);
                produktInfo.setSpacing(false);
                produktInfo.setWidth("200px");

                Paragraph name = new Paragraph(produkt.getProductsName());
                name.getStyle().set("font-weight", "bold").set("margin", "0");

                Paragraph einzelpreis = new Paragraph("Einzelpreis: " + produkt.getPreis() + " €");
                einzelpreis.getStyle().set("margin", "0");

                Paragraph gesamtpreis = new Paragraph("Gesamt: " +String.format ("%.2f",produkt.getPreis() * menge) + " €");
                gesamtpreis.getStyle().set("margin", "0").set("color", "gray");

                produktInfo.add(name, einzelpreis, gesamtpreis);

                // --- Mengensteuerung ---
                TextField mengeField = new TextField();
                mengeField.setValue(String.valueOf(menge));
                mengeField.setWidth("50px");
                mengeField.setReadOnly(true); // Menge nur über Buttons veränderbar
                mengeField.getStyle().set("text-align", "center");

                // Button: Menge reduzieren
                Button minusButton = new Button("-", click -> {
                    int aktuelleMenge = Integer.parseInt(mengeField.getValue());
                    if (aktuelleMenge > 1) {
                        int neueMenge = aktuelleMenge - 1;
                        mengeField.setValue(String.valueOf(neueMenge));

                        // Warenkorb-Daten anpassen
                        warenkorb.getProdukteMitMenge().put(produkt, neueMenge);
                        warenkorb.setGesamtPreis(warenkorbController.berechneGesamtpreis(warenkorb.getProdukteMitMenge()));

                        // Warenkorb in DB aktualisieren
                        warenkorbController.update(warenkorb);

                        // Ansicht neu laden
                        updateEinkaufswagenFromDatabase();
                    }
                });
                minusButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

                // Button: Menge erhöhen
                Button plusButton = new Button("+", click -> {
                    int aktuelleMenge = Integer.parseInt(mengeField.getValue());
                    int neueMenge = aktuelleMenge + 1;
                    mengeField.setValue(String.valueOf(neueMenge));

                    // Warenkorb-Daten anpassen
                    warenkorb.getProdukteMitMenge().put(produkt, neueMenge);
                    warenkorb.setGesamtPreis(warenkorbController.berechneGesamtpreis(warenkorb.getProdukteMitMenge()));

                    // Warenkorb in DB aktualisieren
                    warenkorbController.update(warenkorb);

                    // Ansicht neu laden
                    updateEinkaufswagenFromDatabase();
                });
                plusButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

                // Steuerung in einer Zeile darstellen
                HorizontalLayout mengeSteuerung = new HorizontalLayout(minusButton, mengeField, plusButton);
                mengeSteuerung.setAlignItems(FlexComponent.Alignment.CENTER);

                // Produktzeile zusammensetzen
                produktZeile.add(produktBild, produktInfo, mengeSteuerung);

                // Zeile ins Hauptlayout einfügen
                einkaufswagenLayout.add(produktZeile);
            }
        } else {
            // Wenn Warenkorb leer ist
            einkaufswagenLayout.add(new Text("Warenkorb ist leer."));
        }
    }
    /**
     * Lädt die Wunschliste des aktuellen Benutzers aus der Datenbank
     * und aktualisiert die lokale Datenstruktur sowie die Darstellung in der UI.
     * <p>
     * Die Methode leert zunächst die lokale Liste {@code merkliste},
     * lädt anschließend die gespeicherten Produkte aus der Datenbank
     * (falls vorhanden), und aktualisiert dann die Darstellungskomponente
     * {@code merklisteBox}, die automatisch über einen Renderer visuell aktualisiert wird.
     */
    private void updateWunschlisteFromDatabase() {
        // Leert die aktuelle Wunschliste im Speicher
        merkliste.clear();

        // Holt die Wunschliste des aktuellen Benutzers aus der Datenbank
        Wunschliste wunschliste = wunchlisteCntroller.getWunschlisteVonUser(currentUser);

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
     * <p>
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
     * <p>
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
        List<Bestellung> bestellungs = bstellungController.getBestellungenVonUser(currentUser);

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
                            sanitizeFileName(p.getProductsName()) + ".jpg",
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

                Paragraph gesamt = new Paragraph("Gesamt: " + String.format("%.2f", p.getPreis() * menge) + " €");
                gesamt.getStyle().set("margin", "0").set("color", "gray");

                info.add(name, preis, gesamt);
                productsZeile.add(img, info); // Fügt Bild und Infos zur Produktzeile hinzu

                // Layout zur Darstellung der Bewertungselemente (horizontal)
                HorizontalLayout bewertungLayout = new HorizontalLayout();
                bewertungLayout.setAlignItems(FlexComponent.Alignment.CENTER);

                // Speicher für den ausgewählten Bewertungswert (1–5 Sterne)
                final int[] ausgewaehlteSterne = {0};

                // Sternebewertung erzeugen und Wert merken
                HorizontalLayout sterne = erzeugeSterneBewertung(wert -> {
                    ausgewaehlteSterne[0] = wert;
                });

                // Kommentarfeld
                TextField commentField = new TextField();
                commentField.setPlaceholder("Kommentar...");
                commentField.setWidth("200px");

                // Button zum Absenden der Bewertung
                Button speichernBtn = new Button("Speichern");
                speichernBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                speichernBtn.addClickListener(event -> {
                    if (ausgewaehlteSterne[0] == 0) {
                        Notification.show("Bitte wähle eine Bewertung (Sterne) aus.");
                        return;
                    }

                    Bewertung bewertung = new Bewertung();
                    bewertung.setUser(currentUser);
                    bewertung.setProducts(p);
                    bewertung.setRating("★".repeat(ausgewaehlteSterne[0]));
                    bewertung.setComment(commentField.getValue());

                    bewertungController.create(bewertung);
                    Notification.show("Bewertung gespeichert!");
                });

                // Baue das komplette Layout zusammen
                bewertungLayout.add(sterne, commentField, speichernBtn);

                // Produktzeile und Bewertungszeile zur Bestellung hinzufügen
                bestellungBox.add(productsZeile, bewertungLayout);
            }

            // Metainformationen zur Bestellung anzeigen
            bestellungBox.add(new Paragraph("Datum: " + b.getBestelldatum()));
            bestellungBox.add(new Paragraph("Versand: " + (b.isVersand() ? "Erfolgreich" : "Offen")));
            bestellungBox.add(new Paragraph("Gesamtpreis: " + String.format("%.2f", b.getPreis()) + " €"));
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
     * Bereitet die Kassenansicht (Checkout) visuell vor.
     *
     * <p>Diese Methode holt den aktuellen Warenkorb des eingeloggten Benutzers
     * und zeigt alle enthaltenen Produkte mit Mengenangabe, Einzelpreis und
     * Zwischensumme an. Am Ende wird der Gesamtpreis aller Produkte berechnet
     * und dargestellt.</p>
     *
     * <p>Falls der Warenkorb leer ist oder kein Warenkorb existiert, wird
     * stattdessen ein Hinweis angezeigt.</p>
     */
    private void prepareCheckoutView() {
        // Vorherigen Inhalt der Kassenansicht entfernen
        kassaLayout.removeAll();

        // Warenkorb des aktuellen Benutzers abrufen
        Warenkorb warenkorb = warenkorbController.getWarenkorbVonUser(currentUser);

        // Falls kein Warenkorb oder keine Produkte vorhanden sind → Hinweis anzeigen
        if (warenkorb == null || warenkorb.getProdukteMitMenge().isEmpty()) {
            kassaLayout.add(new Text("Keine Produkte im Einkaufswagen."));
            return;
        }

        double gesamtpreis = 0.0;

        // Alle Produkte mit Menge und Preis durchlaufen
        for (Map.Entry<Products, Integer> entry : warenkorb.getProdukteMitMenge().entrySet()) {
            Products produkt = entry.getKey();
            int menge = entry.getValue();
            double produktSumme = produkt.getPreis() * menge;

            // Formatierte Anzeige für Produkt, Menge, Gesamtpreis und Einzelpreis
            String info = String.format(
                    "%s x%d - %.2f € (%.2f €/Stück)",
                    produkt.getProductsName(),
                    menge,
                    produktSumme,
                    produkt.getPreis()
            );

            // Informationen in die Ansicht einfügen
            kassaLayout.add(new Text(info));

            // Trenner zur besseren Übersicht
            kassaLayout.add(new Hr());

            // Gesamtsumme aufaddieren
            gesamtpreis += produktSumme;
        }

        // Endgültigen Gesamtpreis anzeigen
        String formatted = String.format("%.2f", gesamtpreis);
        kassaLayout.add(new Text("Gesamtpreis: " + formatted + " €"));
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
                warenkorbController.getFuegeProduktHinzu(currentUser, p, 1);
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
        Warenkorb warenkorb = warenkorbController.getWarenkorbVonUser(currentUser);
        if (warenkorb == null || warenkorb.getProdukteMitMenge().isEmpty()) {
            Notification.show("Der Einkaufswagen ist leer.");
            return;
        }

        // Bestellung auf Basis des Warenkorbs erstellen und speichern
        Bestellung bestellung = bstellungController.erstelleBestellung(
                currentUser,
                warenkorb.getProdukteMitMenge(),
                true // Bezahlstatus: true = bezahlt
        );

        // Bestellverlauf für spätere Übersicht speichern
        bestellverlauf.add(new ArrayList<>(warenkorb.getProdukteMitMenge().keySet()));

        // Warenkorb leeren (Datenbank und UI)
        warenkorbController.getleereWarenkorb(currentUser);
        einkaufswagen.clear();
        // Produkte im Warenkorb durchgehen und Bestand reduzieren
        for (Map.Entry<Products, Integer> eintrag : warenkorb.getProdukteMitMenge().entrySet()) {
            Products produkt = eintrag.getKey();
            int gekaufteMenge = eintrag.getValue();
            int aktuelleMenge = produkt.getBestand();

            if (aktuelleMenge >= gekaufteMenge) {
                produkt.setBestand(aktuelleMenge - gekaufteMenge);
                productsController.update(
                        produkt); // Bestand in der DB aktualisieren
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
        zahlungController.getSpeichereZahlung(bestellung.getPreis(), bestellung);
    }

    /**
     * Zeigt dem Nutzer eine Liste aller bisherigen Zahlungen in einer ComboBox an.
     * Beim Auswählen einer Rechnung werden Rechnungsnummer, Datum und Betrag angezeigt.
     */
    private void zeigeZahlungHistory() {
        // Lade alle Zahlungen des aktuellen Benutzers (über dessen Warenkorb)
        List<Zahlung> zahlungs = zahlungController.getZahlungenVonUser(currentUser);

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
                        new Paragraph("Gesamtbetrag: " + String.format("%.2f", selected.getBetrag()) + " €")
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
     * @param products           Die Liste der darzustellenden Produkte.
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
            layout.addClassName("list-product-row");
            layout.setAlignItems(FlexComponent.Alignment.CENTER); // Bild & Text vertikal zentrieren

            Image image;

            // Wenn Produktbild vorhanden ist, in StreamResource umwandeln und anzeigen
            try {
                if (item.getImage() != null && item.getImage().length > 10) {
                    StreamResource resource = new StreamResource(
                            sanitizeFileName(item.getProductsName()) + ".jpg",
                            () -> new ByteArrayInputStream(item.getImage())
                    );
                    image = new Image(resource, "Produktbild");
                } else {
                    image = new Image("/images/standard.png", "Kein Bild");
                }
            } catch (Exception e) {
                image = new Image("/images/standard.png", "Fehler beim Laden");
            }

            // Setze Bildgröße (optisch ansprechend)
            image.setWidth("50px");
            image.setHeight("70px");

            // Lade alle Bewertungen zum aktuellen Produkt
            List<Bewertung> bewertungen = bewertungController.getBewertungenZuProdukt(item);

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
            Span sterneSpan = new Span(" " + sterne);
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

        // Bild setzen: Wenn Produktbild vorhanden ist, als StreamResource anzeigen
        if (product.getImage() != null && product.getImage().length > 10) {
            String safeFileName = sanitizeFileName(product.getProductsName()) + ".jfif";
            StreamResource resource = new StreamResource(
                    safeFileName,
                    () -> new ByteArrayInputStream(product.getImage())
            );
            productImage.setSrc(resource);
        } else {
            // Wenn kein Bild vorhanden ist, Platzhalterbild verwenden
            productImage.setSrc("images/default-product.jpg"); // Pfad ggf. anpassen
        }

        // HIER: feste Größe immer setzen, damit Layout stabil bleibt
        productImage.setWidth("160px");
        productImage.setHeight("200px");
        productImage.getStyle()
                .set("max-width", "160px")
                .set("max-height", "200px")
                .set("object-fit", "contain")
                .set("border", "1px solid lightgray"); // Nur zur visuellen Kontrolle

        // Neues vertikales Layout zur Darstellung der Produktinformationen
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.addClassName("detail-info-stack");
        infoLayout.setSpacing(false);
        infoLayout.setPadding(false);

        // Produktname, Status, Preis und Lagerbestand anzeigen
        infoLayout.add(new H4(product.getProductsName()));
        infoLayout.add(new Paragraph(product.getStatus()));
        infoLayout.add(new Paragraph("Preis: " + product.getPreis() + " €"));
        infoLayout.add(new Paragraph("Verfügbar auf Lager: " + product.getBestand()));

        // Alle Bewertungen zum Produkt aus der Datenbank abrufen
        List<Bewertung> bewertungen = bewertungController.getBewertungenZuProdukt(product);

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
     * <p>
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

    private String sanitizeFileName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}


