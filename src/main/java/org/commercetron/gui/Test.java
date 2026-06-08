package org.commercetron.gui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.commercetron.beans.Products;
import org.commercetron.beans.Warenkorb;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

//public class Test {
//    public class WelcomeView {
//        //    @PageTitle("Welcome")
////    @Route("") // Hauptseite
//        public class Welcome extends Composite<VerticalLayout> {
//
//            public Welcome() {
//                VerticalLayout content = getContent();
//                content.setWidthFull();
//                content.getStyle().set("flex-grow", "1");
//
//                // Header: Welcome
//                HorizontalLayout layoutRow = new HorizontalLayout();
//                layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
//                layoutRow.setWidth("100%");
//                layoutRow.setHeight("200px");
//                layoutRow.setAlignItems(FlexComponent.Alignment.END);
//                layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//                H1 h12 = new H1("Welcome to InnoShop");
//                h12.setWidth("max-content");
//                layoutRow.setAlignSelf(FlexComponent.Alignment.START, h12);
//                layoutRow.add(h12);
//
//                // Buttons
//                VerticalLayout layoutColumn2 = new VerticalLayout();
//                layoutColumn2.addClassName(LumoUtility.Gap.XSMALL);
//                layoutColumn2.setWidth("100%");
//                layoutColumn2.getStyle().set("flex-grow", "1");
//
//                HorizontalLayout layoutRow2 = new HorizontalLayout();
//                layoutRow2.setWidthFull();
//                layoutRow2.setWidth("1156px");
//                layoutRow2.setHeight("100px");
//                layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
//                layoutRow2.addClassName(LumoUtility.Padding.XSMALL);
//                layoutRow2.setAlignItems(FlexComponent.Alignment.START);
//                layoutRow2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
//
//                Button buttonPrimary3 = new Button("Anmelden");
//                buttonPrimary3.setWidth("115px");
//                buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//                Button buttonPrimary4 = new Button("Registrieren");
//                buttonPrimary4.setWidth("min-content");
//                buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//
//                layoutRow2.add(buttonPrimary3, buttonPrimary4);
//                layoutColumn2.add(layoutRow2);
//
//                // Produktbilder statt Avatare
//                HorizontalLayout layoutRow3 = new HorizontalLayout();
//                layoutRow3.addClassName(LumoUtility.Gap.LARGE);
//                layoutRow3.setWidth("100%");
//                layoutRow3.setHeight("360px");
//
//                HorizontalLayout layoutRow4 = new HorizontalLayout();
//                layoutRow4.setHeightFull();
//                layoutRow4.addClassName(LumoUtility.Gap.MEDIUM);
//                layoutRow4.addClassName(LumoUtility.Padding.LARGE);
//                layoutRow4.setWidth("100%");
//                layoutRow4.getStyle().set("flex-grow", "1");
//                layoutRow3.setFlexGrow(1.0, layoutRow4);
//
//                // Beispielbilder
//                layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+1"));
//                layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+2"));
//                layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+3"));
//                layoutRow4.add(createProductImage("https://via.placeholder.com/280x280?text=Produkt+4"));
//
//                // Aufbau der Seite
//                content.add(layoutRow, layoutColumn2, layoutRow3);
//                layoutRow3.add(layoutRow4);
//            }
//
//            private Image createProductImage(String url) {
//                Image image = new Image(url, "Produktbild");
//                image.setWidth("280px");
//                image.setHeight("280px");
//                image.getStyle().set("object-fit", "cover");
//                return image;
//            }
//            private Image createProductImageFromBytes(byte[] imageBytes, String altText) {
//                if (imageBytes == null || imageBytes.length == 0) {
//                    // Fallback-Bild
//                    return new Image("https://via.placeholder.com/280x280?text=Kein+Bild", altText);
//                }
//                String base64 = Base64.getEncoder().encodeToString(imageBytes);
//                String src = "data:image/png;base64," + base64; // png anpassen, falls anderes Format
//                Image image = new Image(src, altText);
//                image.setWidth("280px");
//                image.setHeight("280px");
//                image.getStyle().set("object-fit", "cover");
//                return image;
//            }
//        }
//    }
//
//}




//private void initLayout() {
//    HorizontalLayout layoutRow = new HorizontalLayout();
//    VerticalLayout layoutColumn2 = new VerticalLayout();
//    TabSheet tabSheet = new TabSheet();
//    VerticalLayout layoutColumn3 = new VerticalLayout();
//    HorizontalLayout layoutRow2 = new HorizontalLayout();
//    ComboBox comboBox = new ComboBox();
////        MultiSelectListBox avatarItems = new MultiSelectListBox();
////        loadOrCreateWarenkorb();
//
//    Button buttonPrimary = new Button();
//    Button buttonPrimary2 = new Button();
//    HorizontalLayout layoutRow3 = new HorizontalLayout();
//    getContent().setWidth("100%");
//    getContent().getStyle().set("flex-grow", "1");
//    layoutRow.addClassName(LumoUtility.Gap.MEDIUM);
//    layoutRow.setWidth("100%");
//    layoutRow.setHeight("300px");
//    layoutColumn2.setHeightFull();
//    layoutRow.setFlexGrow(1.0, layoutColumn2);
//    layoutColumn2.setWidth("100%");
//    layoutColumn2.getStyle().set("flex-grow", "1");
//    layoutColumn2.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
//    layoutColumn2.setAlignItems(FlexComponent.Alignment.CENTER);
//    layoutColumn2.setAlignSelf(FlexComponent.Alignment.START, tabSheet);
//    tabSheet.setWidth("100%");
//    tabSheet.setHeight("400px");
//    setTabSheetSampleData(tabSheet);
//    layoutColumn3.setWidth("100%");
//    layoutColumn3.setHeight("400px");
//    layoutRow2.setWidthFull();
//    layoutColumn3.setFlexGrow(1.0, layoutRow2);
//    layoutRow2.addClassName(LumoUtility.Gap.MEDIUM);
//    layoutRow2.setWidth("100%");
//    layoutRow2.getStyle().set("flex-grow", "1");
//    layoutRow2.getStyle().set("flex-grow", "1");
//    comboBox.setLabel("Combo Box");
//    comboBox.setWidth("min-content");
//    setComboBoxSampleData(comboBox);
//    productImage.setWidth("300px");
//    productImage.setHeight("400px");
//    buttonPrimary.setText("In den Einkaufswagen");
//    layoutRow2.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary);
//    buttonPrimary.setWidth("min-content");
//    buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//    buttonPrimary2.setText("Merkliste Hinzufügen");
//    layoutRow2.setAlignSelf(FlexComponent.Alignment.END, buttonPrimary2);
//    buttonPrimary2.setWidth("min-content");
//    buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//    layoutRow3.addClassName(LumoUtility.Gap.MEDIUM);
//    layoutRow3.setWidth("100%");
//    layoutRow3.setHeight("min-content");
//    getContent().add(layoutRow);
//    layoutRow.add(layoutColumn2);
//    layoutColumn2.add(tabSheet);
//
//
//    if (currentUser != null) {
//        updateEinkaufswagenFromDatabase();
//    }
//    getContent().add(layoutColumn3);
//    layoutColumn3.add(layoutRow2);
//    layoutRow2.add(avatarItems);
//    layoutRow2.add(comboBox);
//    layoutRow2.add(productImage);
////        layoutRow2.add(avatar);
//    layoutRow2.add(buttonPrimary);
//    layoutRow2.add(buttonPrimary2);
////        getContent().add(layoutRow3);
////        currentUser.getBestellung();
////        currentUser.getWarenkorb();
//
////        avatarItems.addValueChangeListener(event -> {
////            List<Products> selectedProducts = (List<Products>) event.getValue();
////            if (!selectedProducts.isEmpty()) {
////                Products selectedProduct = selectedProducts.get(0);
////                updateAvatarWithProductInfo(selectedProduct);
////            } else {
////                avatar.setImage("path/to/default/image.png");
////                avatar.setName("Kein Produkt ausgewählt");
////            }
////        });
//
//
//    avatarItems.addValueChangeListener(event -> {
//        List<Products> selectedProducts = (List<Products>) event.getValue();
//        if (!selectedProducts.isEmpty()) {
//            Products selectedProduct = selectedProducts.get(0);
//            updateProductImageWithProductInfo(selectedProduct);
//        } else {
//            productImage.setSrc("path/to/default/image.jfif");
//        }
//    });
//
//    buttonPrimary.addClickListener(event -> {
//        List<Products> selectedProducts = new ArrayList<>(avatarItems.getSelectedItems());
//        if (!selectedProducts.isEmpty()) {
//            for (Products product : selectedProducts) {
//                warenkorbDAO.fuegeProduktHinzu(currentUser, product, 1);
//            }
//
//            updateEinkaufswagenFromDatabase();
////                einkaufswagen.addAll(selectedProducts);
//            Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");
//            avatarItems.clear();
//
//            Notification.show("Produkte wurden zum Einkaufswagen hinzugefügt!");// Optional: Auswahl leeren
//        }
//    });
//    if (currentUser != null) {
//        updateEinkaufswagenFromDatabase();
//    }
//
//    buttonPrimary2.addClickListener(event -> {
//        List<Products> selectedProducts = new ArrayList<>(avatarItems.getSelectedItems());
//        if (!selectedProducts.isEmpty()) {
//            for (Products p : selectedProducts) {
//                wunschlisteDAO.fuegeProduktHinzu(currentUser, p);
//            }
//            updateWunschlisteFromDatabase();
//            Notification.show("Zur Merkliste hinzugefügt.");
//            avatarItems.clear();
//        }
//    });
//
//    VerticalLayout mainLayout = getContent();
//    mainLayout.removeAll();
//
//    HorizontalLayout produktAuswahlLayout = new HorizontalLayout();
//    produktAuswahlLayout.setWidthFull();
//    produktAuswahlLayout.setAlignItems(FlexComponent.Alignment.CENTER);
//    produktAuswahlLayout.add(comboBox, avatarItems, productImage, buttonPrimary, buttonPrimary2);
//
//
//    tabSheet.setWidthFull();
//    tabSheet.setHeight("400px");
//
//    setTabSheetSampleData(tabSheet); // Tabs: Warenkorb, Merkliste, etc.
//    mainLayout.add(tabSheet);
//
//}


//private void updateEinkaufswagenFromDatabase() {
//    einkaufswagen.clear();
//    einkaufswagenLayout.removeAll();
//
//    Warenkorb warenkorb = warenkorbDAO.findeWarenkorbVonUser(currentUser);
//    if (warenkorb != null && !warenkorb.getProdukteMitMenge().isEmpty()) {
//        for (Map.Entry<Products, Integer> entry : warenkorb.getProdukteMitMenge().entrySet()) {
//            Products p = entry.getKey();
//            int menge = entry.getValue();
//            einkaufswagen.add(p);
////                einkaufswagenLayout.add(new Text(p.getProductsName() + " x " + menge + " - " + (p.getPreis() * menge) + " €"));
//            HorizontalLayout zeile = new HorizontalLayout();
//            zeile.setAlignItems(FlexComponent.Alignment.CENTER);
//
//            Image img;
//
//            if (p.getImage() != null &&  p.getImage().length > 0) {
//                StreamResource resource = new StreamResource(
//                        p.getProductsName() + ".jfif",
//                        () -> new ByteArrayInputStream(p.getImage())
//                );
//                img = new Image(resource, "Produktbild");
//            } else {
//                img = new Image("path/to/default/image.png", "Standardbild");
//            }
//            img.setWidth("80px");
//            img.setHeight("100px");
//
//            // Produktname und Menge
//            Span text = new Span(p.getProductsName() + " x " + menge + " - " + (p.getPreis() * menge) + " €");
//
//            zeile.add(img, text);
//            einkaufswagenLayout.add(zeile);
//        }
//
//    } else {
//        einkaufswagenLayout.add(new Text("Warenkorb ist leer."));
//    }
//
//    updateEinkaufswagenView();
//}










//39.99
//SONGMICS
//Kleiderschrank Stoffschrank
//mit Überzug aus Vliesstoff, Kleiderstange, 12 Ablagen
//7ec20618-8a74-4ace-8b51-96f55acdd966


