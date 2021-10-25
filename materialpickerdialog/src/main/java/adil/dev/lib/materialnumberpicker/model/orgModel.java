package adil.dev.lib.materialnumberpicker.model;

/**
 * Created by Brian Kwendp on 13/02/2020.
 */


public class orgModel {
    private String  org;

    private boolean hasFocus;

    public orgModel(String org, boolean hasFocus) {
        this.hasFocus = hasFocus;
        this.org = org;
    }
    public String getOrg() {
        return org;
    }

    public void setOrg(String county) {
        this.org = org;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }
    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }


}
