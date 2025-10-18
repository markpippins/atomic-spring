package com.angrysurfer.atomic.vaadin.views;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.vaadin.layout.MainLayout;
import com.angrysurfer.atomic.vaadin.service.UserServiceClient;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;




@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final UserServiceClient userService;

    public ProfileView(UserServiceClient userService) {
        this.userService = userService;
        
        setupLayout();
        displayProfile();
    }

    private void setupLayout() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }

    private void displayProfile() {
        UserDTO user = userService.getCurrentUser();
        if (user == null) {
            return;
        }

        // Profile header
        VerticalLayout headerSection = createProfileHeader(user);
        
        // Profile information
        VerticalLayout infoSection = createProfileInfo(user);
        
        add(headerSection, infoSection);
    }

    private VerticalLayout createProfileHeader(UserDTO user) {
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setMaxWidth("600px");
        
        H2 title = new H2("Profile");
        title.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        Avatar avatar = new Avatar(user.getAlias());
        avatar.setWidth("80px");
        avatar.setHeight("80px");
        
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            avatar.setImage(user.getAvatarUrl());
        }
        
        H3 userName = new H3(user.getAlias());
        userName.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);
        
        Paragraph userAlias = new Paragraph("@" + user.getAlias());
        userAlias.addClassNames(LumoUtility.TextColor.TERTIARY, LumoUtility.FontSize.SMALL, LumoUtility.Margin.NONE);
        
        header.add(title, avatar, userName, userAlias);
        return header;
    }

    private VerticalLayout createProfileInfo(UserDTO user) {
        VerticalLayout section = new VerticalLayout();
        section.setAlignItems(FlexComponent.Alignment.CENTER);
        section.setSpacing(true);
        section.setMaxWidth("500px");
        
        // User information card
        VerticalLayout infoCard = new VerticalLayout();
        infoCard.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );
        infoCard.setWidth("100%");
        infoCard.setSpacing(true);
        
        H3 infoTitle = new H3("Account Information");
        infoTitle.addClassNames(LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);
        
        // Information rows
        HorizontalLayout idRow = createInfoRow("User ID", user.getId().toString());
        HorizontalLayout aliasRow = createInfoRow("Username", user.getAlias());
        HorizontalLayout nameRow = createInfoRow("Full Name", user.getAlias());
        HorizontalLayout emailRow = createInfoRow("Email", user.getEmail());
        
        infoCard.add(infoTitle, idRow, aliasRow, nameRow, emailRow);
        
        // Action buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);
        
        Button editButton = new Button("Edit Profile");
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClickListener(e -> showEditDialog(user));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshButton.addClickListener(e -> refreshProfile());
        
        buttonLayout.add(editButton, refreshButton);
        
        section.add(infoCard, buttonLayout);
        return section;
    }

    private HorizontalLayout createInfoRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        
        Paragraph labelParagraph = new Paragraph(label + ":");
        labelParagraph.addClassNames(LumoUtility.FontWeight.MEDIUM, LumoUtility.Margin.NONE);
        
        Paragraph valueParagraph = new Paragraph(value);
        valueParagraph.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);
        valueParagraph.getStyle().set("text-align", "right");
        
        row.add(labelParagraph, valueParagraph);
        return row;
    }

    private void showEditDialog(UserDTO user) {
        // Create a simple edit dialog - in a real app you might want a more sophisticated implementation
        com.vaadin.flow.component.dialog.Dialog editDialog = new com.vaadin.flow.component.dialog.Dialog();
        editDialog.setHeaderTitle("Edit Profile");
        editDialog.setWidth("400px");

        FormLayout form = new FormLayout();
        
        TextField nameField = new TextField("Full Name");
        nameField.setValue(user.getAlias());
        nameField.setRequired(true);
        
        EmailField emailField = new EmailField("Email");
        emailField.setValue(user.getEmail());
        emailField.setRequired(true);
        
        TextField avatarField = new TextField("Avatar URL");
        if (user.getAvatarUrl() != null) {
            avatarField.setValue(user.getAvatarUrl());
        }
        
        form.add(nameField, emailField, avatarField);

        Button saveBtn = new Button("Save Changes", e -> {
            // In a real implementation, you would call a service to update the user
            // For now, we'll just close the dialog
            com.vaadin.flow.component.notification.Notification.show(
                "Profile update functionality would be implemented here");
            editDialog.close();
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancel", e -> editDialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveBtn, cancelBtn);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(form, buttonLayout);
        editDialog.add(dialogLayout);

        editDialog.open();
    }

    private void refreshProfile() {
        try {
            UserDTO currentUser = userService.getCurrentUser();
            if (currentUser != null) {
                // Refresh user data from the service
                UserDTO refreshedUser = userService.getUserByAlias(currentUser.getAlias());
                // In a real implementation, you would update the session with the refreshed data
                com.vaadin.flow.component.notification.Notification.show(
                    "Profile refreshed successfully");
                // Refresh the UI
                removeAll();
                displayProfile();
            }
        } catch (Exception e) {
            // log.error("Failed to refresh profile", e);
            com.vaadin.flow.component.notification.Notification.show(
                "Failed to refresh profile: " + e.getMessage())
                .addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!userService.isLoggedIn()) {
            event.forwardTo(HomeView.class);
        }
    }
}