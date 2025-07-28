package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.commercetron.beans.Admin;
import org.commercetron.beans.User;
import org.commercetron.controller.UserController;
import org.commercetron.dao.AdminDAO;
import org.commercetron.dao.UserDAO;

@Route("adminLoginView")
@PageTitle("AdminLoginView")
@AnonymousAllowed
public class AdminLoginView extends Composite<VerticalLayout> {
    private final AdminDAO dao = new AdminDAO();


    public AdminLoginView () {
        initLayout();
    }

    private void initLayout() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        LoginForm loginForm = new LoginForm();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getContent().setAlignItems(FlexComponent.Alignment.END);
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);
        loginForm.getStyle().set("width", "100%");
        getContent().add(layoutRow);
        layoutRow.add(layoutRow2);
        layoutRow2.add(loginForm);


        loginForm.addLoginListener(e -> {
            String username = e.getUsername();
            String password = e.getPassword();


            Admin admin = (Admin) dao.findByExactName(username);

            if (admin != null && admin.getPassword().equals(password)) {
                VaadinSession.getCurrent().setAttribute(Admin.class, admin);
                getUI().ifPresent(ui -> ui.navigate("adminView"));
                UI.getCurrent().navigate(AdminView.class);
            } else {
                loginForm.setError(true);
            }

        });
    }
}
