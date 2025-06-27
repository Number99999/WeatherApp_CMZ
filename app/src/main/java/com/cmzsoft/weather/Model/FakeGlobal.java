package com.cmzsoft.weather.Model;

import org.json.JSONObject;
public class FakeGlobal {
    private static FakeGlobal instance;

    //region public field
    public LocationWeatherModel curLocation;
    public boolean isCurrentLocation;
    public boolean flagIsChooseDefaultLocation = false;
    public boolean userAcceptRequestLocation;

    public boolean isFirstLoadActivity = true;
    public boolean isShowConfirmDefault = false;
    public boolean userAcceptRequestNotifi;
    public JSONObject responseAPI;
    //endregion

    public static FakeGlobal getInstance() {
        if (instance == null) {
            instance = new FakeGlobal();
            System.out.println("????????????????? create instance");
            instance.curLocation = new LocationWeatherModel(0, "Hà Nội", 21.0266468, 105.7703291, "", "", 0);
        }

        return instance;
    }

}
