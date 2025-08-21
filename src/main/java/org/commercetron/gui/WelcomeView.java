package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.pro.licensechecker.Product;
import org.commercetron.beans.User;
import org.commercetron.controller.ProductsController;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.beans.Products;
import org.commercetron.dao.UserDAO;

import java.util.Base64;
import java.util.List;


// Setzt den Seitentitel im Browser-Tab (hier leer → kein Titel)
@PageTitle("")
// Route für diese View – leere Route bedeutet: Startseite der Anwendung
@Route("")
// Erlaubt den Zugriff ohne vorherige Authentifizierung
@AnonymousAllowed
public class WelcomeView extends Composite<VerticalLayout> {

    // DAO zur Datenbeschaffung von Produktinformationen
    private final ProductsDAO dao;
    private final ProductsController controller;

    // Konstruktor
    public WelcomeView() {
        this.dao = new ProductsDAO();// Initialisierung des Datenzugriffsobjekts
        this.controller = new ProductsController(dao);
        initLayout();                 // Aufbau des UI-Layouts
    }

    /**
     * Initialisiert das gesamte Layout der Startseite (Welcome Page).
     * Beinhaltet Kopfzeile, Navigation, Produktübersicht und versteckten Admin-Link.
     */
    private void initLayout() {
        // ---------- Kopfzeile mit Überschrift ----------
        HorizontalLayout layoutRow = new HorizontalLayout();
        H1 h1 = new H1("Welcome to InnoShop"); // Hauptüberschrift der Seite
        layoutRow.add(h1);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("150px");
        layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutRow.setAlignItems(FlexComponent.Alignment.END);

        // ---------- Buttons für Anmelden und Registrieren ----------
        Button buttonLogin = new Button("Anmelden");
        Button buttonRegister = new Button("Registrieren");
        buttonLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonRegister.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Layout für die Buttons (zentriert)
        HorizontalLayout buttonRow = new HorizontalLayout(buttonLogin, buttonRegister);
        buttonRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonRow.setWidthFull();
        buttonRow.getStyle().set("margin-bottom", "20px");

        // Navigation beim Klick auf Buttons
        buttonLogin.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("anmelden")));
        buttonRegister.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("registrieren")));

        // ---------- Anzeige zufälliger Produkte ----------
        Div productGrid = new Div(); // Container für Produktkarten
        productGrid.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "30px")
                .set("justify-content", "center")
                .set("padding", "60px");

        // Vier zufällige Produkte abrufen und anzeigen
        List<Products> randomProducts = controller.getRandom(4);
        for (Products product : randomProducts) {
            Div productCard = new Div(); // Einzelne Produktkarte
            productCard.getStyle()
                    .set("width", "250px")
                    .set("border", "1px solid #e0e0e0")
                    .set("border-radius", "24px")
                    .set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)")
                    .set("overflow", "hidden")
                    .set("background-color", "white")
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("align-items", "center")
                    .set("transition", "transform 0.2s, box-shadow 0.2s");

            // Hover-Effekt für die Karte
            productCard.getElement().addEventListener("mouseover", e ->
                    productCard.getStyle()
                            .set("box-shadow", "0 6px 15px rgba(0,0,0,0.1)")
                            .set("transform", "scale(1.03)")
            );
            productCard.getElement().addEventListener("mouseout", e ->
                    productCard.getStyle()
                            .set("box-shadow", "0 4px 10px rgba(0,0,0,0.05)")
                            .set("transform", "scale(1)")
            );

            // Produktbild als Base64 (aus Byte-Array)
            byte[] imageByte = product.getImage();
            String base64Image = Base64.getEncoder().encodeToString(imageByte);
            Image image = new Image("data:image/jpg;base64," + base64Image, "Produktbild");
            image.setWidth("100%");
            image.setHeight("150px");
            image.getStyle()
                    .set("object-fit", "cover")
                    .set("border-bottom", "1px solid #eee");

            // Produktname anzeigen
            H4 name = new H4(product.getProductsName());
            name.getStyle()
                    .set("font-size", "1.1em")
                    .set("margin", "10px 8px 0 8px")
                    .set("text-align", "center")
                    .set("white-space", "normal")
                    .set("overflow", "hidden");

            // Preis des Produkts anzeigen
            H4 preis = new H4(product.getPreis() + " €");
            preis.getStyle()
                    .set("font-size", "1em")
                    .set("color", "gray")
                    .set("margin", "5px 0 12px 0");

            // Kauf-Button → leitet zum Login weiter (für Gäste)
            Button buyButton = new Button("In den Einkaufwagen");
            buyButton.getStyle()
                    .set("margin-bottom", "10px")
                    .set("background-color", "#1976d2")
                    .set("color", "white")
                    .set("border-radius", "6px");

            // Klick: Produkt-ID zwischenspeichern und zum Login navigieren
            buyButton.addClickListener(e -> {
                VaadinSession.getCurrent().setAttribute("pendingProductId", product.getProductsId());
                getUI().ifPresent(ui -> ui.navigate("anmelden"));
            });

            // Produktkarte zusammensetzen
            productCard.add(image, name, preis, buyButton);
            productGrid.add(productCard);
        }

        // ---------- Versteckter Admin-Login-Button ----------
        Button adminButton = new Button(".");
        adminButton.getStyle()
                .set("background", "transparent")
                .set("color", "transparent")
                .set("border", "none")
                .set("font-size", "8px")
                .set("padding", "0")
                .set("position", "absolute")
                .set("bottom", "5px")
                .set("left", "5px");

        // Klick auf Admin-Button → Weiterleitung zur Admin-Login-Seite
        adminButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("adminLoginView")));

        // ---------- Zusammensetzen der gesamten Seite ----------
        getContent().setWidth("100%");
        getContent().add(layoutRow, buttonRow, productGrid, adminButton);
    }
}




