package com.uncledavecode.notificator.ui;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class MainUI extends VerticalLayout{

    @PostConstruct
    private void init() {
        H2 title = new H2("Notificator Panel");

        this.add(title);
    }
}
