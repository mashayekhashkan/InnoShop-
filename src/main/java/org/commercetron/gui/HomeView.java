package org.commercetron.gui;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import java.util.ArrayList;
import java.util.List;



    @Route("home")
    @PageTitle("Home")
    @AnonymousAllowed
    public class HomeView extends Composite<VerticalLayout> {

        public HomeView(){
            initLayout();
        }


        public void initLayout() {
            HorizontalLayout layoutRow = new HorizontalLayout();
            HorizontalLayout layoutRow2 = new HorizontalLayout();
            ComboBox comboBox = new ComboBox();
            ComboBox comboBox2 = new ComboBox();
            ComboBox comboBox3 = new ComboBox();
            VerticalLayout layoutColumn2 = new VerticalLayout();
            HorizontalLayout layoutRow3 = new HorizontalLayout();
            Avatar avatar = new Avatar();
            Avatar avatar2 = new Avatar();
            Avatar avatar3 = new Avatar();
            Avatar avatar4 = new Avatar();
            Avatar avatar5 = new Avatar();
            HorizontalLayout layoutRow4 = new HorizontalLayout();
            MessageInput messageInput = new MessageInput();
            getContent().setWidth("100%");
            getContent().getStyle().set("flex-grow", "1");
            layoutRow.addClassName(Gap.MEDIUM);
            layoutRow.setWidth("100%");
            layoutRow.setHeight("150px");
            layoutRow2.setHeightFull();
            layoutRow.setFlexGrow(1.0, layoutRow2);
            layoutRow2.addClassName(Gap.XLARGE);
            layoutRow2.setWidth("100%");
            layoutRow2.getStyle().set("flex-grow", "1");
            layoutRow2.setAlignItems(Alignment.CENTER);
            layoutRow2.setJustifyContentMode(JustifyContentMode.CENTER);
            comboBox.setLabel("Kategorie");
            comboBox.setWidth("min-content");
            setComboBoxSampleData(comboBox);
            comboBox2.setLabel("Products");
            comboBox2.setWidth("min-content");
            setComboBoxSampleData(comboBox2);
            comboBox3.setLabel("Bewertung");
            comboBox3.setWidth("min-content");
            setComboBoxSampleData(comboBox3);
            layoutColumn2.addClassName(Gap.SMALL);
            layoutColumn2.addClassName(Padding.XLARGE);
            layoutColumn2.setWidth("100%");
            layoutColumn2.getStyle().set("flex-grow", "1");
            layoutColumn2.setJustifyContentMode(JustifyContentMode.START);
            layoutColumn2.setAlignItems(Alignment.START);
            layoutRow3.setWidthFull();
            layoutColumn2.setFlexGrow(1.0, layoutRow3);
            layoutRow3.addClassName(Gap.MEDIUM);
            layoutRow3.setWidth("147px");
            layoutRow3.getStyle().set("flex-grow", "1");
            avatar.setName("Firstname Lastname");
            layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, avatar);
            avatar.setWidth("140px");
            avatar.setHeight("140px");
            avatar2.setName("Firstname Lastname");
            layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, avatar2);
            avatar2.setWidth("140px");
            avatar2.setHeight("140px");
            avatar3.setName("Firstname Lastname");
            layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, avatar3);
            avatar3.setWidth("140px");
            avatar3.setHeight("140px");
            avatar4.setName("Firstname Lastname");
            layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, avatar4);
            avatar4.setWidth("140px");
            avatar4.setHeight("140px");
            avatar5.setName("Firstname Lastname");
            layoutRow3.setAlignSelf(FlexComponent.Alignment.CENTER, avatar5);
            avatar5.setWidth("140px");
            avatar5.setHeight("140px");
            layoutRow4.addClassName(Gap.MEDIUM);
            layoutRow4.setWidth("100%");
            layoutRow4.setHeight("min-content");
            messageInput.setWidth("min-content");
            getContent().add(layoutRow);
            layoutRow.add(layoutRow2);
            layoutRow2.add(comboBox);
            layoutRow2.add(comboBox2);
            layoutRow2.add(comboBox3);
            getContent().add(layoutColumn2);
            layoutColumn2.add(layoutRow3);
            layoutRow3.add(avatar);
            layoutRow3.add(avatar2);
            layoutRow3.add(avatar3);
            layoutRow3.add(avatar4);
            layoutRow3.add(avatar5);
            getContent().add(layoutRow4);
            layoutRow4.add(messageInput);
        }

        record SampleItem(String value, String label, Boolean disabled) {
        }

        private void setComboBoxSampleData(ComboBox comboBox) {
            List<SampleItem> sampleItems = new ArrayList<>();
            sampleItems.add(new SampleItem("first", "First", null));
            sampleItems.add(new SampleItem("second", "Second", null));
            sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
            sampleItems.add(new SampleItem("fourth", "Fourth", null));
            comboBox.setItems(sampleItems);
            comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
        }
    }


