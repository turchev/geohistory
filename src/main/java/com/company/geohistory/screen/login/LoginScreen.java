package com.company.geohistory.screen.login;

import com.company.geohistory.entity.User;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.MessageTools;
import io.jmix.core.Messages;
import io.jmix.securityui.authentication.AuthDetails;
import io.jmix.securityui.authentication.LoginScreenSupport;
import io.jmix.ui.JmixApp;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.*;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import io.jmix.ui.security.UiLoginProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.util.Locale;
import java.util.TimeZone;

@UiController("geo_LoginScreen")
@UiDescriptor("login-screen.xml")
@Route(path = "login", root = true)
public class LoginScreen extends Screen {
    @Autowired
    private Image<FileRef> logoImage;
    @Autowired
    private Image<FileRef> captchaImage;
    @Autowired
    @Qualifier("systemFileStorage")
    private FileStorage systemFileFs;
    @Autowired
    private TextField<String> usernameField;
    @Autowired
    private PasswordField passwordField;
    @Autowired
    private ComboBox<Locale> localesField;
    @Autowired
    private Notifications notifications;
    @Autowired
    private Messages messages;
    @Autowired
    private MessageTools messageTools;
    @Autowired
    private LoginScreenSupport loginScreenSupport;
    @Autowired
    private UiLoginProperties loginProperties;
    @Autowired
    private JmixApp app;
    @Autowired
    private Button loginButton;
    @Autowired
    private ScreenBuilders screenBuilders;
    @Autowired
    private LinkButton registerLink;
    @Autowired
    private CheckBox captchaCheckBox;

    private final Logger log = LoggerFactory.getLogger(LoginScreen.class);
    private static final String APP_LOGO_IMAGE_NAME = "app_logo.png";
    private static final String APP_CAPTCHA_IMAGE_NAME = "captcha.png";

    @Subscribe
    private void onInit(InitEvent event) {
        initImages();
        initDefaultCredentials(loginProperties);
        initLocalesField(localesField);
    }

    private void initImages() {
        FileRef logoImageFile = new FileRef(systemFileFs.getStorageName(),
                String.format("icons//%s//", APP_LOGO_IMAGE_NAME),
                APP_LOGO_IMAGE_NAME);
        logoImage.setSource(FileStorageResource.class)
                .setFileReference(logoImageFile);
        FileRef captchaImageFile = new FileRef(systemFileFs.getStorageName(),
                String.format("icons//%s//", APP_CAPTCHA_IMAGE_NAME),
                APP_LOGO_IMAGE_NAME);
        captchaImage.setSource(FileStorageResource.class)
                .setFileReference(captchaImageFile);
    }

    @Subscribe("captchaCheckBox")
    public void onCaptchaCheckBoxValueChange(HasValue.ValueChangeEvent<Boolean> event) {
        loginButton.setEnabled(Boolean.TRUE.equals(event.getValue()));
        registerLink.setEnabled(Boolean.TRUE.equals(event.getValue()));
    }

    @Subscribe("submit")
    private void onSubmitActionPerformed(Action.ActionPerformedEvent event) {
        login();
    }

    @Subscribe("registerLink")
    public void onRegisterLinkClick(Button.ClickEvent event) {
        screenBuilders.editor(User.class, this)
                .newEntity()
                .withInitializer(user -> {
                    user.setUsername(usernameField.getValue());
                    user.setActive(true);
                    user.setTimeZoneId(TimeZone.getDefault().getID());
                })
                .withScreenClass(UserRegistration.class)
                .withAfterCloseListener(afterCloseEvent -> {
                    UserRegistration registrationScreen = afterCloseEvent.getSource();
                    if (afterCloseEvent.closedWith(StandardOutcome.COMMIT)) {
                        usernameField.setValue(registrationScreen.getEditedEntity().getUsername());
                        passwordField.setValue(registrationScreen.getRegisteredUserPasswd());
                        login();
                    }
                })
                .withOpenMode(OpenMode.DIALOG)
                .build()
                .show();
    }

    private void initLocalesField(ComboBox<Locale> localesField) {
        localesField.setOptionsMap(messageTools.getAvailableLocalesMap());
        localesField.setValue(app.getLocale());
        localesField.addValueChangeListener(this::onLocalesFieldValueChangeEvent);
    }

    private void onLocalesFieldValueChangeEvent(HasValue.ValueChangeEvent<Locale> event) {
        boolean stateCaptchaCheckBox = captchaCheckBox.isChecked();
        //noinspection ConstantConditions
        app.setLocale(event.getValue());
        LoginScreen recreatedScreen = UiControllerUtils.getScreenContext(this).getScreens()
                .create(this.getClass(), OpenMode.ROOT);
        recreatedScreen.captchaCheckBox.setValue(stateCaptchaCheckBox);
        recreatedScreen.show();
    }

    private void initDefaultCredentials(UiLoginProperties loginProperties) {
        String defaultUsername = loginProperties.getDefaultUsername();
        if (!defaultUsername.isBlank() && !"<disabled>".equals(defaultUsername)) {
            usernameField.setValue(defaultUsername);
        } else {
            usernameField.setValue("");
        }

        String defaultPassword = loginProperties.getDefaultPassword();
        if (!defaultPassword.isBlank() && !"<disabled>".equals(defaultPassword)) {
            passwordField.setValue(defaultPassword);
        } else {
            passwordField.setValue("");
        }
    }

    private void login() {
        if (captchaCheckBox.isChecked()) {
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                notifications.create(Notifications.NotificationType.WARNING)
                        .withCaption(messages.getMessage(getClass(), "emptyUsernameOrPassword"))
                        .show();
                return;
            }

            try {
                loginScreenSupport.authenticate(
                        AuthDetails.of(username, password)
                                .withLocale(localesField.getValue())
                                .withRememberMe(false), this);
            } catch (BadCredentialsException | DisabledException | LockedException e) {
                log.warn("Login failed for user '{}': {}", username, e.toString());
                notifications.create(Notifications.NotificationType.ERROR)
                        .withCaption(messages.getMessage(getClass(), "loginFailed"))
                        .withDescription(messages.getMessage(getClass(), "badCredentials"))
                        .withHideDelayMs(2000)
                        .show();
            }
        }
    }
}