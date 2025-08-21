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
    requires vaadin.form.layout.flow;
    requires vaadin.text.field.flow;
    requires vaadin.date.picker.flow;
    requires vaadin.login.flow;
    requires vaadin.notification.flow;
    requires vaadin.combo.box.flow;
    requires vaadin.messages.flow;
    requires vaadin.tabs.flow;
    requires vaadin.list.box.flow;
    requires vaadin.renderer.flow;
    requires flow.data;
    requires vaadin.upload.flow;
    requires vaadin.icons.flow;
    requires org.checkerframework.checker.qual;
    requires vaadin.radio.button.flow;
    opens org.commercetron.beans to org.hibernate.orm.core; // <-- Hier öffnen
    exports org.commercetron.cli;
    exports org.commercetron.dao;
    exports org.commercetron.beans;
}