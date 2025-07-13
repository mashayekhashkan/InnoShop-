package org.example;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.commercetron.dao.UserDAO;
import org.commercetron.gui.AnmeldungView;
import org.commercetron.gui.HomeView;
import org.commercetron.gui.RegistrierungView;
import org.commercetron.gui.WelcomeView;


public class MainView extends Composite<VerticalLayout> {
        UserDAO dao ;

        public MainView(UserDAO dao) {
            this.dao = dao;
            WelcomeView welcomeView = new WelcomeView();
            getContent().add(welcomeView);
            RegistrierungView registrierungView = new RegistrierungView();
            getContent().add(registrierungView);
            AnmeldungView anmeldenView = new AnmeldungView();
            getContent().add(anmeldenView);
            HomeView homeView = new HomeView();
            getContent().add(homeView);
        }

    }
