package tr.limonist.classes;

import java.io.Serializable;

public class ProductAllPricaData implements Serializable {
    private String id;
    private String gramaj;
    private String price;
    private String old_price;

    public ProductAllPricaData(String id, String gramaj, String price, String old_price) {
        this.id = id;
        this.gramaj = gramaj;
        this.price = price;
        this.old_price = old_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGramaj() {
        return gramaj;
    }

    public void setGramaj(String gramaj) {
        this.gramaj = gramaj;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }
}
