package incube8.iabtestapp.util;

/**
 * Created by shiv on 29/3/17.
 */
public class Constants {

    // Unique Public liscense key to verify signatures
    public static final String BASE64ENCODEDSTRING = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo6xjAy5MTt6LA3HbqB4WxY2pbl+9JgEr6dP3PhAAGkO1CQ8Onxi2laHPP1Z/LIfZALNn2a3cU9uLlLz/s+2/BjYsJihpNf1XI0RI6p5cmn3fVl6iCtHsEgdTcxDha501hLW5vNmhQeW1Xi1eb+pM24xGvrJGBc6H/8/KNgSIWorEmsZVdSt26aAhkgXoG0Id2llXOuigltF07+g8jKlwraQr+/hh6SVXVsXRJ4nPpglifcLVm8Ke4BNu3c/rfuRoINVtLwCcxqz6WVaxr8pl0vcH+FlnC8RY9xYEwK0BgSuwGfUYuDgCqsgLdMPmdcsewLmOaNp0R9tUQPW6xIx4KwIDAQAB";

    public static final String NO_INTERNET_MESSAGE = "No internet available, Please try again.";

    public static final String IAB_SETUP_SUCESS = "InAppBilling setup sucessfully.";

    public static final String IAB_SETUP_ERROR = "Problem setting up in-app billing: ";

    public static final String IAB_INVENTORY_ERROR = "Error querying inventory. Another async operation in progress.";

    public static final String IAB_PURCHASE_SUCESS = "Launching purchase flow for selected subscription.";

    public static final String IAB_PURCHASE_ERROR = "Error launching purchase flow. Another async operation in progress.";

    public static final String IAB_ERROR_PURCHASE = "Error purchasing. Authenticity verification failed.";

    public static final String IAB_COMPLETE = "You have purchased subscription sucessfully.";

    public static final String FACEBOOK_SUCESS = "Login via Facebook successfully.";

    public static final String FACEBOOK_CANCEL_ERROR = "Facebook login cancelled.";
    public static final String FACEBOOK_ERROR = "Error in Facebook login.";


    // SKU for our subscription (Used as product id on the playstore while adding subscriptions)
    public static final String SKU_SUB_ONE_MONTH = "sub_one_month";
    public static final String SKU_SUB_THREE_MONTH = "sub_three_month";


}
