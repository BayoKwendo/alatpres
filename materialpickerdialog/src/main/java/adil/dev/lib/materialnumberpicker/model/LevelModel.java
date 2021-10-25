package adil.dev.lib.materialnumberpicker.model;

/**
 * Created by Brian Kwendp on 13/02/2020.
 */


public class LevelModel {
    private String  level;

    private boolean hasFocus;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public LevelModel(String level, boolean hasFocus) {
        this.hasFocus = hasFocus;
        this.level = level;
    }

}
