package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import lombok.Setter;
import org.commercetron.beans.*;
import org.commercetron.controller.WarenkorbController;
import org.commercetron.dao.BestellungDAO;
import org.commercetron.dao.KategorieDAO;
import org.commercetron.dao.ProductsDAO;
import org.commercetron.dao.WarenkorbDAO;


@PageTitle("Home")
@Route("home")

public class HomeView extends Composite<VerticalLayout> {

    private final ProductsDAO dao;
    private final KategorieDAO katDao;
    private MultiSelectListBox<Products> avatarItems = new MultiSelectListBox<>();
    private VerticalLayout einkaufswagenLayout;
    private final List<Products> einkaufswagen = new ArrayList<>();
    private VerticalLayout kassaLayout;
    private final List<List<Products>> bestellverlauf = new ArrayList<>();
    private VerticalLayout bestellungLayout;
    private final BestellungDAO bestellungDAO;
    private Warenkorb warenkorb;
    private final WarenkorbDAO warenkorbDAO = new WarenkorbDAO(Warenkorb.class);
    // Setze den aktuellen Benutzer
    @Setter
    private User currentUser;


    //    private Avatar avatar = new Avatar();
    private Image productImage = new Image();

    public HomeView() {
        this.bestellungDAO = new BestellungDAO();
        this.katDao = new KategorieDAO();

        this.dao = new ProductsDAO() {
        };
        this.currentUser = (User) VaadinSession.getCurrent().getAttribute(User.class);

        initLayout();

    }

    private void initLayout() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        TabSheet tabSheet = new TabSheet();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        ComboBox comboBox = new ComboBox();
//        MultiSelectListBox avatarItems = new MultiSelectListBox();
//        loadOrCreateWarenkorb();

        Button buttonPrimary = new Button();
        Button buttonPrimary2 = new Button();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("300px");
        layoutColumn2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
        layoutColumn2.setAlignItems(Alignment.CENTER);
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.START, tabSheet);
        tabSheet.setWidth("100%");
        tabSheet.setHeight("400px");
        setTabSheetSampleData(tabSheet);
        layoutColumn3.setWidth("100%");
        layoutColumn3.setHeight("400px");
        layoutRow2.setWidthFull();
        layoutColumn3.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");
        layoutRow2.getStyle().set("flex-grow", "1");
        comboBox.setLabel("Combo Box");
        comboBox.setWidth("min-content");
        setComboBoxSampleData(comboBox);
        productImage.setWidth("300px");
        productImage.setHeight("400px");
        buttonPrimary.setText("In den Einkaufswagen");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary);
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Merkliste Hinzufügen");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary2);
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.setHeight("min-content");
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(tabSheet);
        if (currentUser != null) {
            updateEinkaufswagenFromDatabase();
        }
        getContent().add(layoutColumn3);
        layoutColumn3.add(layoutRow2);
        layoutRow2.add(avatarItems);
        layoutRow2.add(comboBox);
        layoutRow2.add(productImage);
//        layoutRow2.add(avatar);
        layoutRow2.add(buttonPrimary);
        layoutRow2.add(buttonPrimary2);
        getContent().add(layoutRow3);

//        avatarItems.addValueChangeListener(event -> {
//            List<Products> selectedProducts = (List<Products>) event.getValue();
//            if (!selectedProducts.isEmpty()) {
//                Products selectedProduct = selectedProducts.get(0);
//                updateAvatarWithProductInfo(selectedProduct);
//            } else {
//                avatar.setImage("path/to/default/image.png");
//                avatar.setName("Kein Produkt ausgewählt");
//            }
//        });



        avatarItems.addValueChangeListener(event -> {
            List<Products> selectedProducts = (List<Products>) event.getValue();
            if (!selectedProducts.isEmpty()) {
                Products selectedProduct = selectedProducts.get(0);
                updateProductImageWithProductInfo(selectedProduct);
            } else {
                productImage.setSrc("path/to/default/image.jfif");
            }
        });

        buttonPrimary.addClickListener(event -> {
            List<Products> selectedProducts = new ArrayList<>(avatarItems.getSelectedItems());
            if (!selectedProducts.isEmpty() ) {
                for (Products product : selectedProducts) {
                    warenkorbDAO.fuegeProduktHinzu(currentUser, product, 1);
                }

                updateEinkaufswagenFromDatabase();
//                einkaufswagen.addAll(selectedProducts);
                Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");
                avatarItems.clear();

                Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");// Optional: Auswahl leeren
            }
        });
        if (currentUser != null) {
            updateEinkaufswagenFromDatabase();
        }

    }

//    private void loadOrCreateWarenkorb(){
//        warenkorb = warenkorbDAO.findByUserId(currentUser);
//        if (warenkorb == null){
//            warenkorb =new Warenkorb();
//            warenkorb.setUser(currentUser);
//            warenkorb.setProdukteMitMenge(new HashMap<>());
//            warenkorb.setGesamtPreis(0.0);
//            warenkorb.setVersandPreis(0.0);
//            warenkorbDAO.save(warenkorb);
//        }
//    }

    private void setTabSheetSampleData(TabSheet tabSheet) {
        // Erstellen der Inhalte für die Tabs
        tabSheet.add("Einkaufwagen", createEinkaufswagenContent());
        tabSheet.add("Merkliste", createMerklisteContent());
        tabSheet.add("Bestellung", createBestellungContent());
        tabSheet.add("Kassa", createKassaContent());
    }

    private void setComboBoxSampleData(ComboBox<Kategorie> comboBox) {
        List<Kategorie> kategories = katDao.findAll();
        comboBox.setItems(kategories);
        comboBox.setItemLabelGenerator(Kategorie::getName);
        comboBox.addValueChangeListener(event -> {
            Kategorie selectedCategory = event.getValue();
            if (selectedCategory != null) {
                List<Products> products = dao.findByKategorie(selectedCategory); // Methode zum Abrufen der Produkte
                setAvatarItemsSampleData(avatarItems, products);
            }
        });
    }

    private Div createMerklisteContent() {
        Div merklisteContent = new Div();
        MultiSelectListBox<Products> merkliste = new MultiSelectListBox<>();
        List<Products> products = dao.findAll();
        merkliste.setItems(products);
        merkliste.setRenderer(new ComponentRenderer(item -> {
            return new Text(item.toString());
        }));

        Button addToCartButton = new Button("In den Einkaufswagen", event -> {
            List<Products> selectedProducts = (List<Products>) merkliste.getValue();

            addToCart(selectedProducts);
        });

        merklisteContent.add(merkliste, addToCartButton);
        return merklisteContent;
    }

    private Div createEinkaufswagenContent() {

        Div einkaufswagenContent = new Div();
        einkaufswagenLayout = new VerticalLayout();
        updateEinkaufswagenView(); // Initial anzeigen
        Button clearCartButton = new Button("Löchen", event -> {
            einkaufswagen.clear();
            updateEinkaufswagenView();
        });

        Button goToCheckoutButton = new Button("Zur Kassa gehen", event -> {
            Notification.show("Zur Kassa geleitet");
            prepareCheckoutView();
        });

        clearCartButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        goToCheckoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttons = new HorizontalLayout(clearCartButton, goToCheckoutButton);
        buttons.setSpacing(true);
        einkaufswagenContent.add(einkaufswagenLayout, buttons);
        return einkaufswagenContent;
    }

    private void updateEinkaufswagenFromDatabase() {
        einkaufswagen.clear();
        einkaufswagenLayout.removeAll();

        Warenkorb warenkorb = warenkorbDAO.findeWarenkorbVonUser(currentUser);
        if (warenkorb != null && !warenkorb.getProdukteMitMenge().isEmpty()) {
            for (Map.Entry<Products, Integer> entry : warenkorb.getProdukteMitMenge().entrySet()) {
                Products p = entry.getKey();
                int menge = entry.getValue();
                einkaufswagen.add(p);
                einkaufswagenLayout.add(new Text(p.getProductsName() + " x " + menge + " - " + (p.getPreis() * menge) + " €"));
            }
        } else {
            einkaufswagenLayout.add(new Text("Warenkorb ist leer."));
        }

        updateEinkaufswagenView();
    }


    public void updateEinkaufswagenView() {
        einkaufswagenLayout.removeAll();
        for (Products p : einkaufswagen) {
            HorizontalLayout productLayout = new HorizontalLayout();
            productLayout.add(new Text(p.getProductsName() + " - " + p.getPreis() + " €"));
            einkaufswagenLayout.add(productLayout);
        }
    }

    private Div createBestellungContent() {
        Div bestellungContent = new Div();
        bestellungLayout = new VerticalLayout();
        updateBestellungView();
        bestellungContent.add(bestellungLayout);
        return bestellungContent;
    }

    private void updateBestellungView() {
        bestellungLayout.removeAll();
        if (bestellverlauf.isEmpty()) {
            bestellungLayout.add(new Text("Keine Bestellungen vorhanden."));
            return;
        }
        int bestellungNr = 1;
        for (List<Products> bestellung : bestellverlauf) {
            bestellungLayout.add(new Hr(), new Text("Bestellung #" + bestellungNr++));
            for (Products p : bestellung) {
                bestellungLayout.add(new Text(p.getProductsName() + " - " + p.getPreis() + " €"));
            }
        }
    }

    private Div createKassaContent() {
        Div kassaContent = new Div();
        kassaLayout = new VerticalLayout();
        Button payButton = new Button("Zahlen", event -> {
            // Logik zur Zahlungsabwicklung
            processPayment();
        });

        kassaContent.add(kassaLayout, payButton);
        return kassaContent;
    }

    private void prepareCheckoutView() {
        kassaLayout.removeAll();
        if (einkaufswagen.isEmpty()) {
            kassaLayout.add(new Text("Keine Produkte im Einkaufswagen."));
        }
        double gesamtpreis = 0.0;
        for (Products p : einkaufswagen) {
            kassaLayout.add(new Text(p.getProductsName() + " - " + p.getPreis() + " €"));
            gesamtpreis += p.getPreis();
            kassaLayout.add(new Hr());
            kassaLayout.add(new Text("Gesamtpreis: " + gesamtpreis + " €"));
        }
    }

    private void addToCart(List<Products> products) {
        if (products != null && !products.isEmpty() && currentUser != null) {
            for (Products p : products) {
                warenkorbDAO.fuegeProduktHinzu(currentUser, p, 1);
            }
            updateEinkaufswagenFromDatabase();
            Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt");
        } else {
            Notification.show("Keine Produkte ausgewählt oder Benutzer nicht eingeloggt");
        }
    }

    private void processPayment() {
        if (currentUser == null) {
            Notification.show("Kein Benutzer eingeloggt.");
            return;
        }


        Warenkorb warenkorb = warenkorbDAO.findeWarenkorbVonUser(currentUser);
        if (warenkorb == null || warenkorb.getProdukteMitMenge().isEmpty()) {
            Notification.show("Der Einkaufswagen ist leer.");
            return;
        }


        Bestellung bestellung = bestellungDAO.erstelleBestellung(
                currentUser,
                warenkorb.getProdukteMitMenge(),
                true
        );

        // Verlauf speichern
        bestellverlauf.add(new ArrayList<>(warenkorb.getProdukteMitMenge().keySet()));

        // Warenkorb leeren
        warenkorbDAO.leereWarenkorb(currentUser);

        // UI aktualisieren
        einkaufswagen.clear();
        updateEinkaufswagenFromDatabase();
        prepareCheckoutView();
        updateBestellungView();

        Notification.show("Bezahlung abgeschlossen. Bestellung gespeichert!");
    }

    private void setAvatarItemsSampleData(MultiSelectListBox<Products> multiSelectListBox, List<Products> products) {
        multiSelectListBox.setItems(products);
        multiSelectListBox.setRenderer(new ComponentRenderer<>(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            Avatar avatar = new Avatar();
            if (item.getImage() != null && item.getImage().length > 0) {
                StreamResource resource = new StreamResource(item.getProductsName() + "jfif",
                        () -> new ByteArrayInputStream(item.getImage()));
                avatar.setImage(String.valueOf(resource));
            } else {

                avatar.setImage("path/to/default/image.png");
            }

            layout.add(avatar, new Text(item.getProductsName() + " - " + item.getPreis() + " €"));
            return layout;


        }));
    }
//
//    private void updateAvatarWithProductInfo(Products products) {
//        if (products.getImage() != null && products.getImage().length > 0) {
//            StreamResource resource = new StreamResource(products.getProductsName() + "jfif",
//                    () -> new ByteArrayInputStream(products.getImage()));
//            avatar.setImage("images/default-avatar.jfif");
//        } else {
//            // Optional: Setze ein Standardbild oder lasse das Bild leer
//            avatar.setImage("path/to/default/image.png"); // Beispiel für ein Standardbild
//        }
//
//        avatar.setName(products.getProductsName() + " - " + products.getPreis() + " €");
//
//    }

    private void updateProductImageWithProductInfo(Products product) {
        System.out.println("Aktualisiere Bild mit Produkt: " + product.getProductsName());
        if (product.getImage() != null && product.getImage().length > 0) {
            StreamResource resource = new StreamResource(product.getProductsName() + "jfif",
                    () -> new ByteArrayInputStream(product.getImage()));
            productImage.setSrc(resource); // Setze die Quelle des Bildes
        } else {
            productImage.setSrc("path/to/default/image.png"); // Beispiel für ein Standardbild
        }
    }
}


