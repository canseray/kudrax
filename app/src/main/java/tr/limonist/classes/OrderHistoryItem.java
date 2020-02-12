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
    private String order_payment_method;


    private String address_id;
    private String driver_id;

    public OrderHistoryItem(String id, String date, String order_id, String total, String status, String detail, String status_id, String order_payment_method, String address_id, String driver_id) {
        this.id = id;
        this.date = date;
        this.order_id = order_id;
        this.total = total;
        this.status = status;
        this.detail = detail;
        this.status_id = status_id;
        this.order_payment_method = order_payment_method;
        this.address_id = address_id;
        this.driver_id = driver_id;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getOrder_payment_method() {
        return order_payment_method;
    }

    public void setOrder_payment_method(String order_payment_method) {
        this.order_payment_method = order_payment_method;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }
}