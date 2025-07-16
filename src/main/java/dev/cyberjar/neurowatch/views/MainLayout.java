package dev.cyberjar.neurowatch.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;

@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();

        H1 logo = new H1("NeuroWatch");
        logo.getStyle().set("font-size", "1.5em").set("margin", "0");

        // top bar
        addToNavbar(toggle, logo);

        // drawer with navigation links
        RouterLink civLink = new RouterLink("Civilians", CivilianView.class);
        RouterLink logLink = new RouterLink("Implant Logs", ImplantLogView.class);
        addToDrawer(new VerticalLayout(civLink, logLink));
    }

}
