package tr.limonist.classes;

import java.io.Serializable;

public class WelcomeItem implements Serializable {

    private String title;
    private String desc;
    private String image;
    private String back;

    public WelcomeItem(String title, String desc, String image, String back) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.back = back;
    }

    public String getBack() {
        return back;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

}
