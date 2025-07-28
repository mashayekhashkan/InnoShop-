package org.commercetron.gui;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.commercetron.beans.Kategorie;
import org.commercetron.beans.Products;
import org.commercetron.dao.KategorieDAO;
import org.commercetron.dao.ProductsDAO;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@PageTitle("AdminView")
@Route("adminView")

public class AdminView extends Composite<VerticalLayout> {
    private KategorieDAO katDAO = new KategorieDAO();
    private ProductsDAO productsDAO = new ProductsDAO();
    ;

    public AdminView() {
        this.productsDAO = new ProductsDAO();
        initLayout();
    }

    public void initLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.removeAll();
        layout.setWidth("100%");
        layout.setMaxWidth("800px");
        layout.setAlignItems(Alignment.CENTER);

        H3 h3 = new H3("Admin");
        VerticalLayout katBlock = new VerticalLayout();
        katBlock.setSpacing(true);
        katBlock.setPadding(false);
        katBlock.setWidthFull();

        TextField neueKategorieField = new TextField("Neue Kategorie ");

        ComboBox<Kategorie> kategorieComboBox = new ComboBox<>("Vorhandene Kategorie");
        aktualisiereKategorieComboBox(kategorieComboBox);

        Button abbrechenButton1 = new Button("Abbrechen", event -> {
            neueKategorieField.clear();
        });
        Button speichernButton1 = new Button("Kategorie speichern", event -> {
            String katName = neueKategorieField.getValue();

            if (katName != null && !katName.trim().isEmpty()) {
                Kategorie neuekategorie = new Kategorie();
                neuekategorie.setName(katName.trim());
                katDAO.save(neuekategorie);
                Notification.show("Kategorie gespeichert!");
                neueKategorieField.clear();
                aktualisiereKategorieComboBox(kategorieComboBox);
            } else {
                Notification.show("Bitte einen gültigen Kategorienamen eingeben.");
            }
        });
        HorizontalLayout katButtonL = new HorizontalLayout(speichernButton1, abbrechenButton1);
        katBlock.add(new H4("Kategorie hinzufügen"), neueKategorieField, katButtonL, new Hr());

        TextField produktnameField = new TextField("Produktname");
        TextField statusField = new TextField("Status");
        TextField preisField = new TextField("Preis");
        TextField mengeField = new TextField("Menge");

        //  Paragraph statt Label verwenden
        Paragraph uploadInfo = new Paragraph("Noch keine Datei hochgeladen.");
        uploadInfo.getStyle().set("font-size", "small").set("color", "gray");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".png", ".jpg", ".jpeg", "jfif");
        upload.setMaxFiles(1);
        upload.setUploadButton(new Button("Datei auswählen"));

        upload.addSucceededListener(event -> {
            uploadInfo.setText("Hochgeladene Datei: " + event.getFileName());
        });

        Button speichernButton = new Button("Produkt speichern", event -> {
            try {

                byte[] imageBytes = null;
                try {
                    imageBytes = buffer.getInputStream().readAllBytes();
                } catch (IOException e) {
                    Notification.show("Fehler beim Einlesen des Bildes.");
                    return;
                }

                Products produkt = new Products();
                produkt.setProductsName(produktnameField.getValue());
                produkt.setStatus(statusField.getValue());
                produkt.setPreis(Double.parseDouble(preisField.getValue()));
                produkt.setBestand(Integer.parseInt(mengeField.getValue()));
                produkt.setImage(imageBytes);


                productsDAO.save(produkt); // Produkt speichern
                Notification.show("Produkt gespeichert!");

                // Felder zurücksetzen
                produktnameField.clear();
                statusField.clear();
                preisField.clear();
                mengeField.clear();
                kategorieComboBox.clear();
                uploadInfo.setText("Noch keine Datei hochgeladen.");

            } catch (Exception e) {
                Notification.show("Fehler beim Speichern: " + e.getMessage());
                e.printStackTrace();
            }
        });

        layout.add(
                h3,
                neueKategorieField,
                katBlock,
                kategorieComboBox,
                produktnameField,
                statusField,
                preisField,
                mengeField,
                upload,
                uploadInfo,
                speichernButton
        );

        getContent().add(layout);
    }

    private void aktualisiereKategorieComboBox(ComboBox<Kategorie> comboBox) {
        List<Kategorie> kategorien = katDAO.findAll();
        comboBox.setItems(kategorien);
        comboBox.setItemLabelGenerator(Kategorie::getName);
    }
}
