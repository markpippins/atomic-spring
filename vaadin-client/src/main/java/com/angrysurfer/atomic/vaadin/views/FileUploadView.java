package com.angrysurfer.atomic.vaadin.views;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.springframework.core.ParameterizedTypeReference;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.vaadin.layout.MainLayout;
import com.angrysurfer.atomic.vaadin.service.BrokerClient;
import com.angrysurfer.atomic.vaadin.service.UserServiceClient;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import lombok.extern.slf4j.Slf4j;

@Route(value = "upload", layout = MainLayout.class)
@Slf4j
public class FileUploadView extends VerticalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;

    private final UserServiceClient userService;
    private final BrokerClient brokerClient;
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);
    private final ProgressBar progressBar = new ProgressBar();
    private final Div uploadStatus = new Div();

    public FileUploadView(UserServiceClient userService, BrokerClient brokerClient) {
        this.userService = userService;
        this.brokerClient = brokerClient;

        setupLayout();
        setupUploadComponent();
        setupStatusArea();
    }

    private void setupLayout() {
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("File Upload");
        title.addClassNames(LumoUtility.TextColor.PRIMARY);

        Paragraph description = new Paragraph(
                "Upload files using the Nucleus service broker. "
                + "Files will be processed and stored securely through our backend services.");
        description.addClassNames(LumoUtility.TextAlignment.CENTER);
        description.setMaxWidth("600px");

        add(title, description);
    }

    private void setupUploadComponent() {
        upload.setMaxFiles(1);
        upload.setMaxFileSize(50 * 1024 * 1024); // 50MB
        upload.setAcceptedFileTypes("image/*", ".pdf", ".doc", ".docx", ".txt", ".zip", ".tar", ".gz");

        upload.addStartedListener(event -> {
            log.info("Upload started for file: {}", event.getFileName());
            progressBar.setVisible(true);
            progressBar.setValue(0);
            uploadStatus.removeAll();
            uploadStatus.add(new Paragraph("Uploading " + event.getFileName() + "..."));
        });

        upload.addProgressListener(event -> {
            long readBytes = event.getContentLength(); // This is a workaround, as getBytesRead() is not directly available in ProgressUpdateEvent
            long contentLength = event.getContentLength();
            if (contentLength > 0) {
                double progress = (double) readBytes / contentLength;
                progressBar.setValue(progress);
            }
        });

        upload.addSucceededListener(event -> {
            log.info("Upload succeeded for file: {}", event.getFileName());
            processUploadedFile(event.getFileName());
        });

        upload.addFailedListener(event -> {
            log.error("Upload failed for file: {}", event.getFileName(), event.getReason());
            progressBar.setVisible(false);
            uploadStatus.removeAll();
            uploadStatus.add(createErrorMessage("Upload failed: " + event.getReason().getMessage()));
        });

        // Style the upload component
        upload.getStyle()
                .set("box-sizing", "border-box")
                .set("border", "2px dashed var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("padding", "var(--lumo-space-l)")
                .set("text-align", "center")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("min-height", "200px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "center")
                .set("width", "100%")
                .set("max-width", "500px");

        add(upload);
    }

    private void setupStatusArea() {
        progressBar.setVisible(false);
        progressBar.setWidth("300px");

        uploadStatus.setWidth("100%");
        uploadStatus.getStyle().set("text-align", "center");

        add(progressBar, uploadStatus);
    }

    private void processUploadedFile(String fileName) {
        log.info("Processing uploaded file: {}", fileName);
        try {
            InputStream inputStream = buffer.getInputStream();
            byte[] bytes = inputStream.readAllBytes();

            if (bytes.length == 0) {
                throw new IOException("File appears to be empty");
            }

            ServiceResponse<String> response = brokerClient.submitRequestWithFile(
                    "uploadService",
                    "processFile",
                    Collections.emptyMap(),
                    bytes,
                    fileName,
                    "application/octet-stream",
                    new ParameterizedTypeReference<ServiceResponse<String>>() {
            }
            );

            progressBar.setVisible(false);
            uploadStatus.removeAll();

            if (response.isOk() && response.getData() != null) {
                uploadStatus.add(createSuccessMessage(
                        "File uploaded successfully! Server filename: " + response.getData()));
                Notification.show("File '" + fileName + "' uploaded successfully!")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                String errorMessage = response.getErrors() != null && !response.getErrors().isEmpty()
                        ? response.getErrors().get(0).get("message").toString()
                        : "Unknown error occurred";
                uploadStatus.add(createErrorMessage("Upload failed: " + errorMessage));
                Notification.show("Upload failed: " + errorMessage)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            log.error("Error processing uploaded file", e);
            progressBar.setVisible(false);
            uploadStatus.removeAll();
            uploadStatus.add(createErrorMessage("Error processing file: " + e.getMessage()));
            Notification.show("Error processing file: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private Div createSuccessMessage(String message) {
        Div div = new Div();
        div.add(new Paragraph(message));
        div.addClassNames(
                LumoUtility.Background.SUCCESS_10,
                LumoUtility.TextColor.SUCCESS,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        div.getStyle().set("margin-top", "var(--lumo-space-m)");
        return div;
    }

    private Div createErrorMessage(String message) {
        Div div = new Div();
        div.add(new Paragraph(message));
        div.addClassNames(
                LumoUtility.Background.ERROR_10,
                LumoUtility.TextColor.ERROR,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        div.getStyle().set("margin-top", "var(--lumo-space-m)");
        return div;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!userService.isLoggedIn()) {
            event.forwardTo(HomeView.class);
        }
    }
}
