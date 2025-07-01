package org.example;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;


@Route("")
@PageTitle("My View")
public class MainView extends VerticalLayout {

    public MainView() {
        addClassName(LumoUtility.Padding.XLARGE);
        setWidthFull();
        getStyle().set("flex-grow", "1");

        HorizontalLayout buttonRow = new HorizontalLayout();
        buttonRow.setWidthFull();
        buttonRow.setAlignItems(Alignment.CENTER);
        buttonRow.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonRow.addClassName(LumoUtility.Gap.MEDIUM);

        Button loginButton = new Button("Login");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("min-content");

        Button registerButton = new Button("Registrierung");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidth("min-content");

        buttonRow.add(loginButton, registerButton);

        add(buttonRow);
    }
}