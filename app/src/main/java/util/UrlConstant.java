package util;

/**
 * Created by riya on 13/2/18.
 */

public class UrlConstant {
    //    http://webservicestrkus.tarule.com/
    public static String base_url = "http://dguruserver.website/taxiadmin/api/";
    public static String REGISTRATION_URL = base_url + "register_customer.php";
    public static String OTP_GENERATE = base_url + "opt_create_login.php";
    public static String VERIFY_OTP = base_url + "otp_verify.php";
    public static String LOGIN_URL = base_url + "login_customer.php";
    public static String FETCH_CAR_URL = base_url + "get_car_type.php";

    public static String Book_CAR_URL = base_url + "get_driver_for_book.php";
    public static String customer_profile=base_url+"profile_customer.php";
    public static String GET_DISTANCE_TIME=base_url+"get_distance_time.php";
    public static String POST_CANCEL_BOOKING=base_url+"cancle_booking_user.php";
    public static String GET_Ride_History= base_url +"get_booking_history.php";
//    http://mobdevelopertracker.com/taxiadmin/api/createFCM.php
//    http://dguruserver.website/taxiadmin/api/get_distance_time.php
}
