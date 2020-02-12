package tr.limonist.classes;

import java.io.Serializable;

public class MyInvoicesItem implements Serializable {
    private String id;
    private String name;
    private String amount;
    private String date;
    private String invoiceData;
    private String totalTitle;

    public MyInvoicesItem(String id, String name, String amount, String date, String invoiceData, String totalTitle) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.invoiceData = invoiceData;
        this.totalTitle = totalTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceData() {
        return invoiceData;
    }

    public void setInvoiceData(String invoiceData) {
        this.invoiceData = invoiceData;
    }

    public String getTotalTitle() {
        return totalTitle;
    }

    public void setTotalTitle(String totalTitle) {
        this.totalTitle = totalTitle;
    }
}


