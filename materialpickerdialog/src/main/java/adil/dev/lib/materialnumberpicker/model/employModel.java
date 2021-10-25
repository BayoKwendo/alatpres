package adil.dev.lib.materialnumberpicker.model;

/**
 * Created by Brian Kwendp on 13/02/2020.
 */


public class employModel {
    private String  employ;

    private boolean hasFocus;

    public employModel(String employ, boolean hasFocus) {
        this.hasFocus = hasFocus;
        this.employ = employ;
    }
    public String getEmploy() {
        return employ;
    }

    public void setEmploy(String employ) {
        this.employ = employ;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }
    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }


}
