package adil.dev.lib.materialnumberpicker.model;

/**
 * Created by Brian Kwendp on 13/02/2020.
 */


public class genderModel {
    private String gender;

    private boolean hasFocus;

    public genderModel(String gender, boolean hasFocus) {
        this.hasFocus = hasFocus;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }
    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }


}
