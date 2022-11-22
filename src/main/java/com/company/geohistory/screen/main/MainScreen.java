package com.company.geohistory.screen.main;

import com.company.geohistory.entity.User;
import com.company.geohistory.entity.geo.History;
import com.company.geohistory.service.GeolocationService;
import com.company.geohistory.service.HistoryService;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.mapsui.component.GeoMap;
import io.jmix.ui.Notifications;
import io.jmix.ui.ScreenTools;
import io.jmix.ui.component.AppWorkArea;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.TextField;
import io.jmix.ui.component.Window;
import io.jmix.ui.component.mainwindow.Drawer;
import io.jmix.ui.icon.JmixIcon;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("geo_MainScreen")
@UiDescriptor("main-screen.xml")
@Route(path = "main", root = true)
public class MainScreen extends Screen implements Window.HasWorkArea {
    private final double CENTER_X = 49.29716;
    private final double CENTER_Y = 53.53349;

    @Autowired
    private ScreenTools screenTools;

    @Autowired
    private AppWorkArea workArea;
    @Autowired
    private Drawer drawer;
    @Autowired
    private Button collapseDrawerButton;
    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private TextField<String> streetField;
    @Autowired
    private TextField<String> cityField;
    @Autowired
    private TextField<String> houseField;
    @Autowired
    private GeoMap locationMap;
    @Autowired
    private Notifications notifications;
    @Autowired
    private MessageBundle messageBundle;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CollectionLoader<History> historiesDl;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Override
    public AppWorkArea getWorkArea() {
        return workArea;
    }

    @Subscribe("collapseDrawerButton")
    private void onCollapseDrawerButtonClick(Button.ClickEvent event) {
        drawer.toggle();
        if (drawer.isCollapsed()) {
            collapseDrawerButton.setIconFromSet(JmixIcon.CHEVRON_RIGHT);
        } else {
            collapseDrawerButton.setIconFromSet(JmixIcon.CHEVRON_LEFT);
        }
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        User currentUser = (User) currentAuthentication.getUser();
        if (currentUser != null) {
            historiesDl.setParameter("user", currentUser);
        }
        historiesDl.load();
    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {
        screenTools.openDefaultScreen(
                UiControllerUtils.getScreenContext(this).getScreens());

        screenTools.handleRedirect();
    }

    @Subscribe("geocodingBtn")
    public void onGeocodingBtnClick(Button.ClickEvent event) {
        Point locationPoint = geolocationService.findLocationByAddress(
                houseField.getValue(),
                streetField.getValue(),
                cityField.getValue()
        );
        if (locationPoint == null) {
            locationMap.setCenter(CENTER_X, CENTER_Y);
            locationMap.setZoomLevel(12);
            notifications.create()
                    .withPosition(Notifications.Position.BOTTOM_RIGHT)
                    .withCaption(messageBundle
                            .getMessage("onGeocodingBtnClick.notifications"))
                    .show();
            return;
        }
        History history = dataManager.create(History.class);
        history.setLocation(locationPoint);
        historyService.saveWithUserAndTime(history);
        historiesDl.load();

        locationMap.setZoomLevel(14);
        locationMap.setCenter(locationPoint.getX(), locationPoint.getY());
    }

}
