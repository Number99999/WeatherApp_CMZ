package com.cmzsoft.weather.Model;

import com.google.android.gms.maps.model.LatLng;

public class FakeGlobal {
    private static FakeGlobal instance;

    //region public field
    public CurLocationModel curLocation;
    public boolean userAcceptRequestLocation;
    public boolean userAcceptRequestNotifi;
    //endregion


    public static FakeGlobal getInstance() {
        if (instance == null) {
            instance = new FakeGlobal();
            instance.curLocation = new CurLocationModel(new LatLng(21.0266468, 105.7703291), "Hanoi");
        }

        return instance;
    }

}
