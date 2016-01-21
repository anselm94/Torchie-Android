package in.blogspot.anselmbros.torchie.misc;

/**
 * Created by anselm94 on 1/12/15.
 */
public class TorchieConstants {

    public final static String INFO = "Torchie will soon become Open-Source! But it takes time until it get 100,000+ installs";

    public final static String APP_TAG = "in.blogspot.anselmbros.torchie";
    public final static String APP_NAME = "Torchie";
    public final static String PLAY_URI = "https://play.google.com/store/apps/details?id=in.blogspot.anselmbros.torchie";
    public final static String WEB_URI = "https://torchieapp.wordpress.com";
    public final static String ABOUTANSELM_URI = "http://anselmbros.blogspot.in/p/about-us.html";
    public final static String COMMUNITY_URI = "https://plus.google.com/communities/114100054385968340083";
    public final static String FACEBOOK_URI = "https://facebook.com/torchieapp";
    public final static String GOOGLEPLUS_URI = "https://plus.google.com/111668132285982978436";
    public final static String WEB_DONATE_URI = "https://torchieapp.wordpress.com/donate/";
    //Accessibility Event Announcement
    public final static String ACC_VOLUME_CHANGE = "AzVfCd"; //ACCESSIBLITY_VOLUME_CHANGE_EVENT out own event
    //Preferences
    public final static String PREF_KEY_APP = "in.blogspot.anselmbros.torchie.PREF_KEY_TORCHIE";
    public final static String PREF_FIRST_TIME = "zFtLrXG";
    public final static String PREF_FUNC_SCREEN_OFF = "fFoSrkG";
    public final static String PREF_FUNC_SCREEN_LOCKED = "notQSrnU";
    public final static String PREF_FUNC_SCREEN_UNLOCKED = "loUnSrpr";
    public final static String PREF_FUNC_SCREEN_AMOLED = "gkLEdqt";
    //Camera Errors
    public final static String ERR_CAMERA_UNAVAILABLE = "Camera not found!";
    public final static String ERR_FLASH_UNAVAILABLE = "Flash not found!";
    public final static String ERR_CAMERA_IMPERATIVE = "Cannot connect with camera!";
    public final static String ERR_CAMERA_VOID = "Camera is void!";
    public final static String ERR_CAMERA_PREVIEW = "Camera Preview is causing problem";
    public final static String ERR_CAMERA_OFF = "Camera error while turning off!";

    public enum ScreenState {
        SCREEN_OFF, //SCREEN_OFF AhOFj Lt
        SCREEN_LOCK, //SCREEN_LOCK XgLOC tk
        SCREEN_UNLOCK //SCREEN_UNLOCK KrULoC dw
    }

    public enum TorchieFlashMode {
        TOGGLE,
        PTO //Push To On
    }

}
