package com.uncledavecode.notificator.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.PostConstruct;

import com.uncledavecode.notificator.model.AccessRequest;
import com.uncledavecode.notificator.model.UserAccount;
import com.uncledavecode.notificator.services.AccessRequestService;
import com.uncledavecode.notificator.services.UserAccountService;
import com.uncledavecode.notificator.ui.components.UncleTab;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@CssImport("./styles/app-style.css")
public class MainUI extends VerticalLayout {

    private final AccessRequestService accessRequestService;
    private final UserAccountService userAccountService;

    public MainUI(AccessRequestService accessRequestService, UserAccountService userAccountService) {
        this.accessRequestService = accessRequestService;
        this.userAccountService = userAccountService;
    }

    private Grid<AccessRequest> dgdAccessRequest;
    private Grid<UserAccount> dgdUserAccount;

    @PostConstruct
    private void init() {
        H2 title = new H2("Notificator Panel");

        UncleTab tabMain = new UncleTab();
        tabMain.addTab("Access Requests", this.getRequestAccessPanel());
        tabMain.addTab("Users List", this.getUserAccountPanel());

        this.add(title, tabMain);

        this.setSizeFull();

        this.refreshAccessRequestData();
        this.refreshUserAccountData();
    }

    private VerticalLayout getRequestAccessPanel() {
        this.dgdAccessRequest = new Grid<>();
        this.dgdAccessRequest.setSizeFull();
        this.dgdAccessRequest.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES);
        this.dgdAccessRequest.setSelectionMode(SelectionMode.NONE);
        this.dgdAccessRequest.addColumn(AccessRequest::getLogid).setHeader("Log Id");
        this.dgdAccessRequest.addColumn(AccessRequest::getEmail).setHeader("Email");
        this.dgdAccessRequest.addColumn(AccessRequest::getName).setHeader("Name");
        this.dgdAccessRequest.addColumn(AccessRequest::getLastname).setHeader("Last Name");
        this.dgdAccessRequest
                .addColumn(item -> DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm").format(item.getRequestDate()))
                .setHeader("Request Date");
        this.dgdAccessRequest.addComponentColumn(item -> getAccessRequestColumnRenderer(item)).setHeader("Actions")
                .setWidth("100px");
        VerticalLayout result = new VerticalLayout();
        result.add(dgdAccessRequest);
        result.setSizeFull();
        return result;
    }

    private VerticalLayout getUserAccountPanel() {
        this.dgdUserAccount = new Grid<>();
        this.dgdUserAccount.setSizeFull();
        this.dgdUserAccount.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_ROW_STRIPES);
        this.dgdUserAccount.setSelectionMode(SelectionMode.MULTI);
        this.dgdUserAccount.addColumn(UserAccount::getEmail).setHeader("Email");
        this.dgdUserAccount.addColumn(UserAccount::getName).setHeader("Name");
        this.dgdUserAccount.addColumn(UserAccount::getLastname).setHeader("Last Name");

        this.dgdUserAccount.addComponentColumn(item -> getUserAccountColumnRenderer(item)).setHeader("Actions")
                .setWidth("100px");
        VerticalLayout result = new VerticalLayout();
        result.add(dgdUserAccount);
        result.setSizeFull();
        return result;
    }

    private HorizontalLayout getAccessRequestColumnRenderer(AccessRequest request) {
        HorizontalLayout result = new HorizontalLayout();

        Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        btnDelete.addClassName("action-button");

        result.add(btnDelete);

        return result;
    }

    private HorizontalLayout getUserAccountColumnRenderer(UserAccount userAccount) {
        HorizontalLayout result = new HorizontalLayout();

        if (!userAccount.getActive()) {
            Button btnAccept = new Button(new Icon(VaadinIcon.CHECK));
            btnAccept.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SUCCESS,
                    ButtonVariant.LUMO_TERTIARY);
            btnAccept.addClassName("action-button");

            Button btnReject = new Button(new Icon(VaadinIcon.CLOSE));
            btnReject.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnReject.addClassName("action-button");

            result.add(btnAccept, btnReject);
        } else {
            Button btnDelete = new Button(new Icon(VaadinIcon.TRASH));
            btnDelete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            btnDelete.addClassName("action-button");
            result.add(btnDelete);
        }

        return result;
    }

    private void refreshAccessRequestData() {
        List<AccessRequest> lstAccessRequest = this.accessRequestService.getAllAccessRequests();
        dgdAccessRequest.setItems(lstAccessRequest);
    }

    private void refreshUserAccountData() {
        List<UserAccount> lstUserAccount = this.userAccountService.getAllUserAccounts();
        dgdUserAccount.setItems(lstUserAccount);
    }
}
