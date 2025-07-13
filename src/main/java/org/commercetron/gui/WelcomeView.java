package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
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
import org.commercetron.dao.UserDAO;

import java.util.Base64;


@PageTitle("WelcomeView")
@Route("")

@AnonymousAllowed
public class WelcomeView extends Composite<VerticalLayout> {

private UserDAO dao;

    public WelcomeView() {
        this.dao = new UserDAO() {

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

        // Avatare hinzufügen
        for (int i = 0; i < 4; i++) {
            Avatar avatar = new Avatar("Name Preis");
            avatar.setWidth("280px");
            avatar.setHeight("280px");
            layoutRow4.add(avatar);
        }

        layoutRow3.add(layoutRow4);
        getContent().add(layoutRow3);
    }
}

