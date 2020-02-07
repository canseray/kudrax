package tr.limonist.classes;

import java.io.Serializable;

public class ProfileItem implements Serializable {

    private String title;
    private String value;
    private String type;
    private String require;

    public ProfileItem(String value,String title,  String type, String require) {

        this.value = value;
        this.title = title;
        this.type = type;
        this.require = require;

    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getRequire() {
        return require;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
