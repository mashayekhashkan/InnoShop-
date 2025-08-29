package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.commercetron.beans.Products;
import org.commercetron.controller.ProductsController;
import org.commercetron.dao.ProductsDAO;

import java.util.Base64;
import java.util.List;

@PageTitle("InnoShop")
@Route("")
@AnonymousAllowed
public class WelcomeView extends Composite<VerticalLayout> {

    private final ProductsController controller;

    public WelcomeView() {
        ProductsDAO dao = new ProductsDAO();
        this.controller = new ProductsController(dao);
        initLayout();
    }

    private void initLayout() {
        VerticalLayout root = getContent();
        root.removeAll();
        root.setWidthFull();
        root.setPadding(false);
        root.setSpacing(false);
        root.addClassName("shop-page");

        Button loginButton = new Button("Anmelden");
        Button registerButton = new Button("Konto erstellen");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("anmelden")));
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("registrieren")));

        Div copy = new Div();
        copy.addClassName("hero-copy");
        copy.add(
                new H1("InnoShop"),
                new Paragraph("Entdecke ausgewaehlte Produkte, lege Favoriten an und kaufe mit einem klaren, modernen Shopping-Erlebnis ein.")
        );

        HorizontalLayout actions = new HorizontalLayout(loginButton, registerButton);
        actions.addClassName("hero-actions");
        copy.add(actions);

        VerticalLayout heroPanel = new VerticalLayout();
        heroPanel.addClassName("hero-panel");
        heroPanel.setPadding(false);
        heroPanel.setSpacing(true);
        heroPanel.setAlignItems(FlexComponent.Alignment.STRETCH);

        Span kicker = new Span("SHOPPING PLATFORM");
        kicker.addClassName("section-kicker");
        heroPanel.add(kicker, new H2("Alles fuer deinen naechsten Einkauf."));

        HorizontalLayout stats = new HorizontalLayout(
                createHeroStat("4+", "Produktbereiche"),
                createHeroStat("24/7", "Online verfuegbar")
        );
        stats.setWidthFull();
        stats.setFlexGrow(1, stats.getComponentAt(0), stats.getComponentAt(1));
        heroPanel.add(stats, new Paragraph("Schneller Zugriff auf Warenkorb, Merkliste, Bestellungen und Profil nach dem Login."));

        Div hero = new Div(copy, heroPanel);
        hero.addClassName("welcome-hero");

        Div sectionHeader = new Div();
        sectionHeader.addClassName("shop-shell");
        Span sectionKicker = new Span("Auswahl");
        sectionKicker.addClassName("section-kicker");
        H2 sectionTitle = new H2("Beliebte Produkte");
        sectionTitle.addClassName("section-title");
        sectionHeader.add(sectionKicker, sectionTitle);

        Div productGrid = new Div();
        productGrid.addClassName("product-grid");

        List<Products> randomProducts = controller.getRandom(4);
        for (Products product : randomProducts) {
            productGrid.add(createProductCard(product));
        }

        root.add(createHeader(), hero, sectionHeader, productGrid);
    }

    private HorizontalLayout createHeader() {
        Div mark = new Div(VaadinIcon.CART.create());
        mark.addClassName("brand-mark");

        Div brandText = new Div();
        H4 title = new H4("InnoShop");
        title.addClassName("brand-title");
        Span subtitle = new Span("Modern einkaufen");
        subtitle.addClassName("brand-subtitle");
        brandText.add(title, subtitle);

        HorizontalLayout inner = new HorizontalLayout(mark, brandText);
        inner.addClassName("shop-header-inner");
        inner.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(inner);
        header.addClassName("shop-header");
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        return header;
    }

    private Div createHeroStat(String value, String label) {
        Div stat = new Div();
        stat.addClassName("hero-stat");
        Span valueLabel = new Span(value);
        valueLabel.addClassName("stat-value");
        stat.add(valueLabel, new Span(label));
        return stat;
    }

    private Div createProductCard(Products product) {
        Div productCard = new Div();
        productCard.addClassName("product-card");

        Image image = createProductImage(product);

        H4 name = new H4(product.getProductsName());
        Span price = new Span(String.format("%.2f EUR", product.getPreis()));
        price.addClassName("product-price");

        Button buyButton = new Button("In den Einkaufswagen");
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buyButton.setWidthFull();
        buyButton.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute("pendingProductId", product.getProductsId());
            getUI().ifPresent(ui -> ui.navigate("anmelden"));
        });

        VerticalLayout body = new VerticalLayout(name, price, buyButton);
        body.addClassName("product-card-body");
        body.setPadding(false);
        body.setSpacing(true);
        body.setAlignItems(FlexComponent.Alignment.STRETCH);

        productCard.add(image, body);
        return productCard;
    }

    private Image createProductImage(Products product) {
        byte[] imageByte = product.getImage();
        if (imageByte != null && imageByte.length > 10) {
            String base64Image = Base64.getEncoder().encodeToString(imageByte);
            return new Image("data:image/jpg;base64," + base64Image, "Produktbild");
        }
        return new Image("/images/standard.png", "Kein Produktbild");
    }
}
