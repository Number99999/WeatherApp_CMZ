package com.cmzsoft.weather.Model;

public class FakeGlobal {
    private static FakeGlobal instance;

    //region public field
    public LocationWeatherModel curLocation;
    public boolean userAcceptRequestLocation;

    public boolean isFirstLoadActivity = true;
    public boolean userAcceptRequestNotifi;
    //endregion


    public static FakeGlobal getInstance() {
        if (instance == null) {
            instance = new FakeGlobal();
            instance.curLocation = new LocationWeatherModel(0, "Hà Nội", 21.0266468, 105.7703291, "", "", 0);
        }

        return instance;
    }

}
