package bankmanagement;

public class Customer {
    private String id;        // unique id (used as username)
    private String name;
    private String phone;
    private String pin;       // 4-6 digit PIN for actions

    public Customer(String id, String name, String phone, String pin) {
        this.id = id.trim();
        this.name = name.trim();
        this.phone = phone.trim();
        this.pin = pin.trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPin() { return pin; }

    @Override
    public String toString() {
        // CSV: id,name,phone,pin
        return id + "," + name + "," + phone + "," + pin;
    }
}
