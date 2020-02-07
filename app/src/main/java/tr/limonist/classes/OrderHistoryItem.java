package tr.limonist.classes;

import java.io.Serializable;

public class OrderHistoryItem implements Serializable {

    private String id;
    private String date;
    private String order_id;
    private String total;
    private String status;
    private String detail;

    private String status_id;
    private String address_id;
    private String driver_id;

    public OrderHistoryItem(String id, String date, String order_id, String total, String status, String detail, String status_id, String address_id, String driver_id) {

        this.id = id;
        this.date = date;
        this.order_id = order_id;
        this.total = total;
        this.status = status;
        this.detail = detail;

        this.status_id = status_id;
        this.address_id = address_id;
        this.driver_id = driver_id;

    }

    public String getStatus_id() {
        return status_id;
    }

    public String getAddress_id() {
        return address_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }
}