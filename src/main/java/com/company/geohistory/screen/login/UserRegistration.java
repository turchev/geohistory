package com.company.geohistory.screen.login;

import com.company.geohistory.entity.User;
import com.company.geohistory.security.UserRole;
import com.company.geohistory.service.RoleAssignmentService;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.securityui.role.UiMinimalRole;
import io.jmix.ui.Notifications;
import io.jmix.ui.component.FileStorageResource;
import io.jmix.ui.component.Image;
import io.jmix.ui.component.PasswordField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@UiController("geo_UserRegistration")
@UiDescriptor("user-registration.xml")
@EditedEntityContainer("userDc")
public class UserRegistration extends StandardEditor<User> {

    private static final String APP_LOGO_IMAGE_NAME = "app_logo.png";
    private String registeredUserPasswd;

    @Autowired
    @Qualifier("systemFileStorage")
    private FileStorage systemFileFs;
    @Autowired
    private Image<FileRef> logoImage;
    @Autowired
    private TextField<String> usernameField;
    @Autowired
    private PasswordField passwordField;
    @Autowired
    private PasswordField confirmPasswordField;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleAssignmentService roleAssignmentService;

    public String getRegisteredUserPasswd() {
        return this.registeredUserPasswd;
    }

    @Subscribe
    public void onInit(InitEvent event) {
        initLogoImage();
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<User> event) {
        usernameField.setEditable(true);
        passwordField.setVisible(true);
        confirmPasswordField.setVisible(true);
    }

    private void initLogoImage() {
        FileRef logoImageFile = new FileRef(systemFileFs.getStorageName(),
                String.format("icons//%s//", APP_LOGO_IMAGE_NAME),
                APP_LOGO_IMAGE_NAME);
        logoImage.setSource(FileStorageResource.class)
                .setFileReference(logoImageFile);

    }

    @Subscribe
    public void onBeforeCommitChanges(BeforeCommitChangesEvent event) {
        if (!Objects.equals(passwordField.getValue(), confirmPasswordField.getValue())) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messageBundle.getMessage("passwordsDoNotMatch"))
                    .show();
            event.preventCommit();
        }
        getEditedEntity().setPassword(passwordEncoder.encode(passwordField.getValue()));
        this.registeredUserPasswd = passwordField.getValue();
    }

    @Subscribe
    public void onAfterCommitChanges(AfterCommitChangesEvent event) {
        roleAssignmentService.createRoleAssignmentByCode(getEditedEntity().getUsername(), UiMinimalRole.CODE);
        roleAssignmentService.createRoleAssignmentByCode(getEditedEntity().getUsername(), UserRole.CODE);
    }

}