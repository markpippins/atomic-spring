package com.angrysurfer.atomic.vaadin.layout;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.vaadin.service.UserServiceClient;
import com.angrysurfer.atomic.vaadin.views.FileUploadView;
import com.angrysurfer.atomic.vaadin.views.HomeView;
import com.angrysurfer.atomic.vaadin.views.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainLayout extends AppLayout {
    
    private final UserServiceClient userService;
    private HorizontalLayout toolbar;
    private Button loginButton;
    private HorizontalLayout userSection;
    private SideNavItem profileItem;
    private SideNavItem filesItem;

    public MainLayout(UserServiceClient userService) {
        this.userService = userService;
        
        createHeader();
        createDrawer();
        updateUserSection();
    }

    private void createHeader() {
        H1 logo = new H1("Nucleus Client");
        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        );

        // Create login button
        loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        loginButton.addClickListener(e -> showLoginDialog());

        // Create user section (shown when logged in)
        userSection = new HorizontalLayout();
        userSection.setVisible(false);
        userSection.setAlignItems(FlexComponent.Alignment.CENTER);
        userSection.setSpacing(true);

        // Right side of toolbar
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.add(loginButton, userSection);
        rightSection.setAlignItems(FlexComponent.Alignment.CENTER);

        toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.add(logo);
        toolbar.add(rightSection);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(true, toolbar);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();
        
        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        
        profileItem = new SideNavItem("Profile", ProfileView.class, VaadinIcon.USER.create());
        filesItem = new SideNavItem("File Upload", FileUploadView.class, VaadinIcon.UPLOAD.create());
        
        nav.addItem(profileItem);
        nav.addItem(filesItem);
        
        addToDrawer(nav);
    }

    private void showLoginDialog() {
        log.info("Showing login dialog");
        Dialog loginDialog = new Dialog();
        loginDialog.setHeaderTitle("Login");
        loginDialog.setWidth("400px");

        FormLayout form = new FormLayout();
        
        TextField aliasField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        
        form.add(aliasField, passwordField);

        Button loginBtn = new Button("Login", e -> {
            try {
                UserDTO user = userService.login(aliasField.getValue(), passwordField.getValue());
                loginDialog.close();
                updateUserSection();
                Notification.show("Welcome, " + user.getAlias() + "!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(ProfileView.class);
            } catch (Exception ex) {
                Notification.show("Login failed: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> loginDialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(loginBtn, cancelBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(form, buttonLayout);
        loginDialog.add(dialogLayout);

        loginDialog.open();
    }

    private void updateUserSection() {
        boolean isLoggedIn = userService.isLoggedIn();
        
        loginButton.setVisible(!isLoggedIn);
        userSection.setVisible(isLoggedIn);
        profileItem.setVisible(isLoggedIn);
        filesItem.setVisible(isLoggedIn);

        if (isLoggedIn) {
            UserDTO user = userService.getCurrentUser();
            
            // Create user avatar and name
            Avatar avatar = new Avatar(user.getAlias());
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                avatar.setImage(user.getAvatarUrl());
            }
            
            Span userName = new Span(user.getAlias());
            userName.addClassNames(LumoUtility.FontWeight.MEDIUM);
            
            Button logoutBtn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
            logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            logoutBtn.addClickListener(e -> {
                userService.logout();
                updateUserSection();
                Notification.show("Logged out successfully");
                UI.getCurrent().navigate(HomeView.class);
            });

            userSection.removeAll();
            userSection.add(avatar, userName, logoutBtn);
        }
    }

    public void refreshUserSection() {
        updateUserSection();
    }
}