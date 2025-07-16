package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import dev.cyberjar.neurowatch.entity.Civilian;
import dev.cyberjar.neurowatch.entity.Implant;
import dev.cyberjar.neurowatch.service.CivilianService;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class CivilianView extends VerticalLayout {

    private final CivilianService civilianService;
    private final Grid<Civilian> grid = new Grid<>(Civilian.class, false);

    private final IntegerField lotGte = new IntegerField("Lot ≥");
    private final IntegerField lotLte = new IntegerField("Lot ≤");
    private final IntegerField lotN = new IntegerField("Lot #");
    private final TextField nationalId = new TextField("National ID");
    private final Button clear = new Button("Clear");

    public CivilianView(CivilianService civilianService) {
        this.civilianService = civilianService;
        configureGrid();
        configureFilters();

        HorizontalLayout filters = new HorizontalLayout(lotGte, lotLte, lotN, nationalId, clear);
        filters.setDefaultVerticalComponentAlignment(Alignment.END);


        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL, LumoUtility.FlexWrap.WRAP);

        add(filters, grid);

    }

    private void configureGrid() {

        grid.setItems(buildProvider());

        grid.addColumn(Civilian::getNationalId).setHeader("National ID");
        grid.addColumn(Civilian::getLegalName).setHeader("Legal Name");

        grid.asSingleSelect().addValueChangeListener(e -> {
            Optional.ofNullable(e.getValue()).ifPresent(this::openCivilianDialog);
        });

        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        setSizeFull();

    }

    private void openCivilianDialog(Civilian civilian) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(civilian.getLegalName());
        dialog.setWidth("48rem");

        Tab detailsTab = new Tab("Details");
        Tab editTab = new Tab("Edit");
        Tab addTab = new Tab("Add Implant");
        Tabs tabs = new Tabs(detailsTab);
        if (hasRole("ROLE_ADMIN")) tabs.add(editTab, addTab);


        Component detailsPanel = buildDetailsPanel(civilian);
        Component editPanel    = buildEditForm(civilian, dialog);
        Component addPanel     = buildAddImplantForm(civilian, dialog);

        Map<Tab, Component> map = Map.of(
                detailsTab, detailsPanel,
                editTab,    editPanel,
                addTab,     addPanel
        );

        Div pages = new Div(detailsPanel, editPanel, addPanel);
        pages.getStyle().set("position", "relative");
        map.values().forEach(p -> p.setVisible(false));
        detailsPanel.setVisible(true);

        tabs.addSelectedChangeListener(e -> {
            map.values().forEach(p -> p.setVisible(false));
            map.get(tabs.getSelectedTab()).setVisible(true);
        });

        dialog.add(tabs, pages);
        dialog.getFooter().add(new Button("Close", ev -> dialog.close()));
        dialog.open();

    }

    private Component buildAddImplantForm(Civilian civilian, Dialog dialog) {

        TextField type = new TextField("Type");
        TextField model = new TextField("Model");
        TextField version = new TextField("Version");
        TextField maker = new TextField("Manufacturer");
        TextField serial = new TextField("Serial #");
        IntegerField lot = new IntegerField("Lot #");
        DatePicker installed = new DatePicker("Installed at");

        Binder<Implant> binder = new Binder<>(Implant.class);
        Implant newImpl = new Implant();

        binder.forField(type).bind(Implant::getType, Implant::setType);
        binder.forField(model).bind(Implant::getModel, Implant::setModel);
        binder.forField(version).bind(Implant::getVersion, Implant::setVersion);
        binder.forField(maker).bind(Implant::getManufacturer, Implant::setManufacturer);
        binder.forField(serial).bind(Implant::getSerialNumber, Implant::setSerialNumber);
        binder.forField(lot).bind(Implant::getLotNumber, Implant::setLotNumber);
        binder.forField(installed).bind(Implant::getInstalledAt, Implant::setInstalledAt);

        Button save = new Button("Add", e -> {
            if (binder.writeBeanIfValid(newImpl)) {
                civilianService.addImplantToCivilian(civilian, newImpl);
                refresh();
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        return new VerticalLayout(type, model, version, maker, serial, lot, installed, save, cancel);

    }

    private Component buildEditForm(Civilian civilian, Dialog dialog) {

        TextField name = new TextField("Legal name");
        name.setValue(civilian.getLegalName());

        Checkbox criminal = new Checkbox("Criminal record", civilian.isCriminalRecord());
        Checkbox surveil = new Checkbox("Under surveillance", civilian.isUnderSurveillance());

        Binder<Civilian> binder = new Binder<>(Civilian.class);
        binder.bind(name, Civilian::getLegalName, Civilian::setLegalName);
        binder.bind(criminal, Civilian::isCriminalRecord, Civilian::setCriminalRecord);
        binder.bind(surveil, Civilian::isUnderSurveillance, Civilian::setUnderSurveillance);
        binder.readBean(civilian);

        Button save = new Button("Save", e -> {
            if (binder.writeBeanIfValid(civilian)) {
                civilianService.updateCivilian(civilian);
                refresh();
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());

        return new VerticalLayout(name, criminal, surveil, save, cancel);

    }

    private Component buildDetailsPanel(Civilian civilian) {

        /* CIVILIAN meta */
        FormLayout civForm = new FormLayout();
        civForm.addFormItem(new Span(civilian.getNationalId()), "National ID");
        civForm.addFormItem(new Span(civilian.getBirthDate().toString()), "Birth date");
        civForm.addFormItem(new Span(civilian.isCriminalRecord() ? "Yes" : "No"), "Criminal record");
        civForm.addFormItem(new Span(civilian.isUnderSurveillance() ? "Yes" : "No"), "Under surveillance");

        /* IMPLANT list */
        Grid<Implant> implantGrid = new Grid<>(Implant.class, false);
        implantGrid.addColumn(Implant::getType).setHeader("Type").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getModel).setHeader("Model").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getVersion).setHeader("Ver").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getManufacturer).setHeader("Made by").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getSerialNumber).setHeader("Serial #").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getLotNumber).setHeader("Lot #").setAutoWidth(true).setFlexGrow(1);
        implantGrid.addColumn(Implant::getInstalledAt).setHeader("Installed").setAutoWidth(true).setFlexGrow(1);
        implantGrid.setItems(civilian.getImplants());
        implantGrid.setHeight("200px");                 // small scroll area
        implantGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        VerticalLayout content = new VerticalLayout(civForm, new H5("Implants"), implantGrid);
        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();

        return content;

    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    private void configureFilters() {

        HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<?, ?>> listener = e -> refresh();


        lotGte.addValueChangeListener(listener);
        lotLte.addValueChangeListener(listener);
        lotN.addValueChangeListener(listener);
        nationalId.addValueChangeListener(listener);
        clear.addClickListener(e -> {
            lotGte.clear();
            lotLte.clear();
            lotN.clear();
            nationalId.clear();
        });

    }

    private CallbackDataProvider<Civilian, Void> buildProvider() {
        return DataProvider.fromCallbacks(

                /* fetch callback  (lazy paging) */
                query -> {
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    return serviceQuery()
                            .stream()
                            .skip(offset)
                            .limit(limit);
                },

                /* count callback */
                query -> serviceQuery().size()
        );
    }

    private Collection<Civilian> serviceQuery() {

        if (!lotN.isEmpty()) {
            return civilianService.getCiviliansByLotNumber(lotN.getValue());
        }
        if (!lotGte.isEmpty()) {
            return civilianService.getCiviliansByLotNumberGreaterOrEqual(lotGte.getValue());
        }
        if (!lotLte.isEmpty()) {
            return civilianService.getCiviliansByLotNumberLessOrEqual(lotLte.getValue());
        }
        if (!nationalId.isEmpty()) {
            return civilianService.getCivilianByNationalId(nationalId.getValue());
        }
        // default: everything (but pageable)
        return civilianService.getAllCivilians();
    }

    private void refresh() {
        grid.setItems(buildProvider());
    }


}
