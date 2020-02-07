package tr.limonist.classes;

public class USER {

    public String id;
    public String name;
    public String surname;
    public String email;
    public String image;
    public String mobile;
    public String pass;

    public USER() {}

    public USER(String id, String name, String surname, String email, String image, String mobile, String pass) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.image = image;
        this.email = email;
        this.mobile = mobile;
        this.pass = pass;

    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImage() {
        return image;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
