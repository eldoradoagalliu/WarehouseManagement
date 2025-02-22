package com.warehousemanagement.constant;

public class Constants {
    // API
    public static final String API_PATH = "/api/v1";
    public static final String REDIRECT_USER_API_PATH = "/api/v1/redirect";
    public static final String INVENTORY_API_PATH = "/api/v1/item/inventory";
    public static final String ORDER_API_PATH = "/api/v1/order/";
    public static final String TRUCK_API_PATH = "/api/v1/truck";

    // Attributes
    public static final String ID = "id";
    public static final String USER = "user";
    public static final String CURRENT_USER = "currentUser";
    public static final String USERS = "users";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String ROLE = "role";
    public static final String ROLES = "roles";
    public static final String ITEM = "item";
    public static final String ITEMS = "items";
    public static final String ORDER = "order";
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String ORDERS = "orders";
    public static final String ORDER_ITEMS = "orderItems";
    public static final String QUANTITY = "quantity";
    public static final String LOW_QUANTITY = "lowQuantity";
    public static final String STATUS = "status";
    public static final String REASON = "reason";
    public static final String TRUCK = "truck";
    public static final String TRUCKS = "trucks";
    public static final String LICENSE_PLATE = "licensePlate";
    public static final String TODAY_DATE = "now";
    public static final String DATE = "date";

    // Authorization
    public static final String USER_NOT_FOUND = "User not found";
    public static final String CLIENT = "CLIENT";
    public static final String WAREHOUSE_MANAGER = "WAREHOUSE_MANAGER";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";

    // Login
    public static final String INVALID_CREDENTIALS = "Invalid Credentials, Please try again.";
    public static final String SUCCESSFUL_LOGOUT = "Successful Logout!";

    // Registration & User Edit
    public static final String EMAIL_EXISTS = "This email has been used by another user!";
    public static final String SUCCESSFUL_PASSWORD_CHANGE = "User Password changed successfully!";
    public static final String REUSED_OLD_PASSWORD = "The user is using the old account password! User needs to try with another password.";
    public static final String OLD_PASSWORD_REUSE = "You are using the old password! Please, try a new one.";

    // Order
    public static final String LOW_QUANTITY_ITEM = "The item required is in low quantity in our inventory!";
    public static final String NOT_CORRECT_STATUS = "The order can't be cancelled in the current status!";
    public static final String NOT_CORRECT_SUBMISSION_STATUS = "The order is not in the correct submission status!";
    public static final long DEFAULT_VALUE = 1;

    // Truck
    public static final String CHASSIS_NUMBER_EXISTS = "This chassis number already exists!";
    public static final String BUSY_TRUCK = "This truck has already a order to deliver!";
    public static final String TRUCK_DRIVER_OFF_DAY = "The day you chose is a off day for truck drivers";
    public static final String MAX_TRUCK_ITEM_AMOUNT = "The truck can not carry more than 10 items!";
}
