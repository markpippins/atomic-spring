package com.angrysurfer.atomic.vaadin.views;

import com.angrysurfer.atomic.vaadin.layout.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    public HomeView() {
        add(new H2("Welcome to Nucleus Client"));
        add(new Paragraph("Please log in to access the full features of the application."));
    }
}
