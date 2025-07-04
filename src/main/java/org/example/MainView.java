package org.example;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.awt.*;



    @PageTitle("MainView")
    @Route("")

    @AnonymousAllowed
    public class MainView extends Composite<VerticalLayout> {

        public MainView() {
            HorizontalLayout layoutRow = new HorizontalLayout();
            H1 h1 = new H1();
            VerticalLayout layoutColumn2 = new VerticalLayout();
            HorizontalLayout layoutRow2 = new HorizontalLayout();
            Button buttonPrimary = new Button();
            Button buttonPrimary2 = new Button();
            HorizontalLayout layoutRow3 = new HorizontalLayout();
            HorizontalLayout layoutRow4 = new HorizontalLayout();
            Avatar avatar = new Avatar();
            Avatar avatar2 = new Avatar();
            Avatar avatar3 = new Avatar();
            Avatar avatar4 = new Avatar();
            getContent().setWidth("100%");
            getContent().getStyle().set("flex-grow", "1");
            layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.setHeight("200px");
            layoutRow.setAlignItems(FlexComponent.Alignment.END);
            layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            h1.setText("Welcome to InnoShop");
            layoutRow.setAlignSelf(FlexComponent.Alignment.START, h1);
            h1.setWidth("max-content");
            layoutColumn2.addClassName(LumoUtility.Gap.XSMALL);
            layoutColumn2.setWidth("100%");
            layoutColumn2.getStyle().set("flex-grow", "1");
            layoutRow2.setWidthFull();
            layoutColumn2.setFlexGrow(1.0, layoutRow2);
            layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow2.addClassName(LumoUtility.Padding.XSMALL);
            layoutRow2.setWidth("1156px");
            layoutRow2.setHeight("100px");
            layoutRow2.setAlignItems(FlexComponent.Alignment.START);
            layoutRow2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            buttonPrimary.setText("Anmelden");
            buttonPrimary.setWidth("115px");
            buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            buttonPrimary2.setText("Registrieren");
            buttonPrimary2.setWidth("min-content");
            buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            layoutRow3.addClassName(LumoUtility.Gap.LARGE);
            layoutRow3.setWidth("100%");
            layoutRow3.setHeight("360px");
            layoutRow4.setHeightFull();
            layoutRow3.setFlexGrow(1.0, layoutRow4);
            layoutRow4.addClassName(LumoUtility.Gap.MEDIUM);
            layoutRow4.addClassName(LumoUtility.Padding.LARGE);
            layoutRow4.setWidth("100%");
            layoutRow4.getStyle().set("flex-grow", "1");
            avatar.setName("Firstname Lastname");
            avatar.setWidth("280px");
            avatar.setHeight("280px");
            avatar2.setName("Firstname Lastname");
            avatar2.setWidth("280px");
            avatar2.setHeight("280px");
            avatar3.setName("Firstname Lastname");
            avatar3.setWidth("280px");
            avatar3.setHeight("280px");
            avatar4.setName("Firstname Lastname");
            avatar4.setWidth("280px");
            avatar4.setHeight("280px");
            getContent().add(layoutRow);
            layoutRow.add(h1);
            getContent().add(layoutColumn2);
            layoutColumn2.add(layoutRow2);
            layoutRow2.add(buttonPrimary);
            layoutRow2.add(buttonPrimary2);
            getContent().add(layoutRow3);
            layoutRow3.add(layoutRow4);
            layoutRow4.add(avatar);
            layoutRow4.add(avatar2);
            layoutRow4.add(avatar3);
            layoutRow4.add(avatar4);
        }
    }
