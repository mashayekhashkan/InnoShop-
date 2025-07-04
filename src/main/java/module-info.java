module InnoShop {
    requires flow.html.components;
    requires flow.server;
    requires vaadin.button.flow;
    requires vaadin.ordered.layout.flow;
    requires jakarta.persistence;
    requires static lombok;
    requires jakarta.validation;
    requires com.sun.jna.platform;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires vaadin.lumo.theme;
    requires license.checker;
    requires org.apache.commons.io;
    requires java.desktop;
    requires vaadin.avatar.flow;
    opens org.commercetron.beans to org.hibernate.orm.core; // <-- Hier öffnen
    exports org.commercetron.cli;
    exports org.commercetron.dao;
    exports org.commercetron.beans;
}