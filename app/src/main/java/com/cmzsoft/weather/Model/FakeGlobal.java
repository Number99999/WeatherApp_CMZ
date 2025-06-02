package com.cmzsoft.weather.Model;

public class FakeGlobal {
    private static FakeGlobal instance;

    //region public field
    public CurLocationModel curLocation;
    public boolean userAcceptRequestLocation;
    public boolean userAcceptRequestNotifi;
    //endregion


    public static FakeGlobal getInstance() {
        if (instance == null) instance = new FakeGlobal();
        return instance;
    }

}
