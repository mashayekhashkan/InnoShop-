package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.pro.licensechecker.Product;
import org.commercetron.beans.User;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.beans.Products;
import org.commercetron.dao.UserDAO;

import java.util.Base64;
import java.util.List;


@PageTitle("")
@Route("")

@AnonymousAllowed
public class WelcomeView extends Composite<VerticalLayout> {

    private ProductsDAO dao;


    public WelcomeView() {
        this.dao = new ProductsDAO() {

        };
        initLayout();
    }
    private void initLayout() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        H1 h1 = new H1("Welcome to InnoShop");
        VerticalLayout layoutColumn2 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Button buttonPrimary = new Button("Anmelden");
        Button buttonPrimary2 = new Button("Registrieren");
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        HorizontalLayout layoutRow4 = new HorizontalLayout();

        // Layout-Styles
        getContent().setWidth("100%");
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("200px");
        layoutRow.setAlignItems(FlexComponent.Alignment.END);
        layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        layoutColumn2.setWidth("100%");
        layoutRow2.setWidthFull();
        layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow2.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutRow2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutRow2.add(buttonPrimary, buttonPrimary2);


        buttonPrimary.addClickListener(event -> {

            getUI().ifPresent(ui -> ui.navigate("anmelden"));
        });

        buttonPrimary2.addClickListener(event -> {
            // Hier kannst du zur Registrierungsseite navigieren
            getUI().ifPresent(ui -> ui.navigate("registrieren"));
        });

        layoutRow.add(h1);
        layoutColumn2.add(layoutRow2);
        getContent().add(layoutRow, layoutColumn2);

        List<Products> randomProducts = dao.getRandomProducts(4);
        for (Products product :randomProducts){
            VerticalLayout productCard = new VerticalLayout();
            productCard.setHeight("250px");
            productCard.setAlignItems(FlexComponent.Alignment.CENTER);
            productCard.getStyle().set("border", "1px solid lightgray").set("border-radius", "8px").set("padding", "10px");

            byte[] imageByte = product.getImage();
            String base64Image = Base64.getEncoder().encodeToString(imageByte);
            Image image = new Image("data:image/jpg;base64," + base64Image, "Produktbild");
            image.setWidth("100%");
            image.setHeight("200px");
            image.getStyle().set("object-fit", "cover").set("border-radius", "4px");
            H4 name = new H4(product.getProductsName());
            name.getStyle().set("font-size", "1.2em").set("margin", "10px 0 5px 0");

            H4 preis = new H4(product.getPreis() + " €");
            preis.getStyle().set("font-size", "1em").set("color", "gray").set("margin", "0");

            productCard.add(image, name, preis);
            layoutRow4.add(productCard);
        }

//        Avatare hinzufügen
//        for (int i = 0; i < 4; i++) {
//            Avatar avatar = new Avatar("Name Preis");
//            avatar.setWidth("280px");
//            avatar.setHeight("280px");
//            layoutRow4.add(avatar);
//        }
//
        layoutRow3.add(layoutRow4);
        getContent().add(layoutRow3);
    }
}


