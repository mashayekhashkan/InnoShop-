package org.example;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

/**
 * This class is used to configure the generated html host page used by the app
 */
@CssImport("./styles/inno-shop.css")
@PWA(name = "InnoShop", shortName = "InnoShop")
public class AppShell implements AppShellConfigurator {
    
}
