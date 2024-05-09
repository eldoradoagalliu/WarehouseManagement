package com.warehousemanagement.models.constants;

public class Constants {
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
