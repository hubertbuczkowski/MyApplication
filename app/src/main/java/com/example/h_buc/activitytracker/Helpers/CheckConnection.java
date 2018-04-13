package com.example.h_buc.activitytracker.Helpers;

import java.net.InetAddress;

/**
 * Created by h_buc on 11/04/2018.
 */

//This class just check if phone has connection with internet
public class CheckConnection {

    public static boolean InternetConnection(){
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
