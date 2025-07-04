package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.pro.licensechecker.Product;

import java.util.Base64;

public class WelcomeView {
//    @PageTitle("Welcome")
//    @Route("") // Hauptseite
    public class Welcome extends Composite<VerticalLayout> {

        public Welcome() {
            VerticalLayout content = getContent();
            content.setWidthFull();
            content.getStyle().set("flex-grow", "1");

            // Header: Welcome
            HorizontalLayout layoutRow = new HorizontalLayout();
            layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.setHeight("200px");
            layoutRow.setAlignItems(FlexComponent.Alignment.END);
            layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            H1 h12 = new H1("Welcome to InnoShop");
            h12.setWidth("max-content");
            layoutRow.setAlignSelf(FlexComponent.Alignment.START, h12);
            layoutRow.add(h12);

            // Buttons
            VerticalLayout layoutColumn2 = new VerticalLayout();
            layoutColumn2.addClassName(LumoUtility.Gap.XSMALL);
            layoutColumn2.setWidth("100%");
            layoutColumn2.getStyle().set("flex-grow", "1");

            HorizontalLayout layoutRow2 = new HorizontalLayout();
            layoutRow2.setWidthFull();
            layoutRow2.setWidth("1156px");
            layoutRow2.setHeight("100px");
            layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow2.addClassName(LumoUtility.Padding.XSMALL);
            layoutRow2.setAlignItems(FlexComponent.Alignment.START);
            layoutRow2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            Button buttonPrimary3 = new Button("Anmelden");
            buttonPrimary3.setWidth("115px");
            buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Button buttonPrimary4 = new Button("Registrieren");
            buttonPrimary4.setWidth("min-content");
            buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            layoutRow2.add(buttonPrimary3, buttonPrimary4);
            layoutColumn2.add(layoutRow2);

            // Produktbilder statt Avatare
            HorizontalLayout layoutRow3 = new HorizontalLayout();
            layoutRow3.addClassName(LumoUtility.Gap.LARGE);
            layoutRow3.setWidth("100%");
            layoutRow3.setHeight("360px");

            HorizontalLayout layoutRow4 = new HorizontalLayout();
            layoutRow4.setHeightFull();
            layoutRow4.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow4.addClassName(LumoUtility.Padding.LARGE);
            layoutRow4.setWidth("100%");
            layoutRow4.getStyle().set("flex-grow", "1");
            layoutRow3.setFlexGrow(1.0, layoutRow4);

            // Beispielbilder
            layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+1"));
            layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+2"));
            layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+3"));
            layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+4"));

            // Aufbau der Seite
            content.add(layoutRow, layoutColumn2, layoutRow3);
            layoutRow3.add(layoutRow4);
        }

        private Image createProductImage(String url) {
            Image image = new Image(url, "Produktbild");
            image.setWidth("280px");
            image.setHeight("280px");
            image.getStyle().set("object-fit", "cover");
            return image;
        }
        private Image createProductImageFromBytes(byte[] imageBytes, String altText) {
            if (imageBytes == null || imageBytes.length == 0) {
                // Fallback-Bild
                return new Image("https://via.placeholder.com/280x280?text=Kein+Bild", altText);
            }
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String src = "data:image/png;base64," + base64; // png anpassen, falls anderes Format
            Image image = new Image(src, altText);
            image.setWidth("280px");
            image.setHeight("280px");
            image.getStyle().set("object-fit", "cover");
            return image;
        }
    }
}
