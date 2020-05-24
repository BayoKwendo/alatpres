package adil.dev.lib.materialnumberpicker.model;

/**
 * Created by Brian Kwendp on 13/02/2020.
 */


public class countyModel {
    private String  county;

    private boolean hasFocus;

    public countyModel(String county, boolean hasFocus) {
        this.hasFocus = hasFocus;
        this.county = county;
    }
    public String getCounty() {
        return county;
    }

    public void setcounty(String county) {
        this.county = county;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }
    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }


}
