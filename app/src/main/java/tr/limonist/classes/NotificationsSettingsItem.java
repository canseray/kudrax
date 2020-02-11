package tr.limonist.classes;

import java.io.Serializable;

public class NotificationsSettingsItem implements Serializable {

    private String id;
    private String title;
    private String value;

    public NotificationsSettingsItem(String id, String title, String value) {
        this.id = id;
        this.title = title;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
