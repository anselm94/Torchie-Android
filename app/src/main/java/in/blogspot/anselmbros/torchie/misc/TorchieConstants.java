/*
 *     Copyright (C) 2016  Merbin J Anselm <merbinjanselm@gmail.com>
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package in.blogspot.anselmbros.torchie.misc;

/**
 * All the constant values
 * Created by anselm94 on 1/12/15.
 */
public class TorchieConstants {

    public final static String INFO = "Torchie is 'free' as in 'freedom' and licensed under GNU GPL v2";

    public final static String APP_TAG = "in.blogspot.anselmbros.torchie";
    public final static String APP_NAME = "Torchie";
    public final static String PLAY_URI = "https://play.google.com/store/apps/details?id=in.blogspot.anselmbros.torchie";
    public final static String WEB_URI = "https://torchieapp.wordpress.com";
    public final static String ABOUTANSELM_URI = "http://anselmbros.blogspot.in/p/about-us.html";
    public final static String COMMUNITY_URI = "https://plus.google.com/communities/114100054385968340083";
    public final static String FACEBOOK_URI = "https://facebook.com/torchieapp";
    public final static String GOOGLEPLUS_URI = "https://plus.google.com/111668132285982978436";
    public final static String WEB_DONATE_URI = "https://torchieapp.wordpress.com/donate/";
    //Default Values
    public final static long DEFAULT_SCREENOFF_TIME = 10000; //in millis
    public final static long DEFAULT_VIBRATOR_TIME = 100; //in millis
    public final static long DEFAULT_FLASHOFF_TIME = 120000; //in millis
    public final static String  DEFAULT_LANG = "system"; //in millis
    //Preferences
    public final static String PREF_KEY_APP = "in.blogspot.anselmbros.torchie.PREF_KEY_TORCHIE";
    public final static String PREF_FIRST_TIME = "zFtLrXG";
    public final static String PREF_FUNC_SCREEN_OFF = "fFoSrkG";
    public final static String PREF_FUNC_SCREEN_LOCKED = "notQSrnU";
    public final static String PREF_FUNC_SCREEN_UNLOCKED = "loUnSrpr";
    public final static String PREF_FUNC_SCREEN_AMOLED = "gkLEdqt";
    public final static String PREF_FUNC_SCREEN_OFF_TIME = "joTnBrwz";
    public final static String PREF_FUNC_SCREEN_OFF_INDEFINITE = "tQcIdjel";
    public final static String PREF_FUNC_VIBRATE = "vTkOdxzH";
    public final static String PREF_FLASH_SOURCE = "dRuXsI";
    public final static String PREF_FUNC_FLASH_OFF_TIME = "moGsBrqz";
    public final static String PREF_FUNC_FLASH_OFF_INDEFINITE = "dExGhosH";
    public final static String PREF_FUNC_PROXIMITY = "xDrTjoY";
    public final static String PREF_LOCALE = "xKoRSeZ";
    //Broadcast Key
    public final static String BROADCAST_CLOSE_ACTIVITY = "in.blogspot.anselmbros.torchie.CLOSE_ACTIVITY";
    //Camera Errors
    public final static String ERR_CAMERA_UNAVAILABLE = "Camera not found!";
    public final static String ERR_FLASH_UNAVAILABLE = "Flash not found!";
    public final static String ERR_CAMERA_IMPERATIVE = "Cannot connect with camera!";
    public final static String ERR_CAMERA_VOID = "Camera is void!";
    public final static String ERR_CAMERA_PREVIEW = "Camera Preview is causing problem";
    public final static String ERR_CAMERA_OFF = "Camera error while turning off!";

    //Flash modes
    public final static int SOURCE_FLASH_CAMERA = 0;
    public final static int SOURCE_FLASH_SCREEN = 1;

    public enum ScreenState {
        SCREEN_OFF, //SCREEN_OFF
        SCREEN_LOCK, //SCREEN_LOCK
        SCREEN_UNLOCK //SCREEN_UNLOCK
    }
}
