package com.transvision.trmdisconnreconn.values;

public class Constants {
    /*------------------------Server Connection URL------------------------*/
    public static String TRM_URL = "http://www.bc_service.hescomtrm.com/";
    public static String SERVICE = "ReadFile.asmx/";
    public static String LOGIN_SERVICE = "Service.asmx/";

    public static final String PREFS_NAME = "TRM_Disconnection_Reconnection";
    public static final String GETSET = "GetSetValues";

    /*------------------------Dialog Variable------------------------*/
    public static final int DISCONNECTION_DIALOG = 1001;
    public static final int RECONNECTION_DIALOG = 1002;

    /*------------------------Shared Preference Constants------------------------*/
    public static final String sPref_MRCode = "disconn_mrcode";
    public static final String sPref_MRName = "disconn_mrname";
    public static final String sPref_Subdivision = "disconn_subdiv";
    public static final String sPref_Login_date = "login_date";

    /*----------------------Handler Connection's------------------------*/
    public static final int TIME_OUT_EXCEPTION = 1;
    public static final int LOGIN_SUCCESS = 2;
    public static final int LOGIN_FAILURE = 3;
    public static final int DISCONNECTION_LIST_SUCCESS = 4;
    public static final int DISCONNECTION_LIST_FAILURE = 5;
    public static final int DISCONNECTION_UPDATE_SUCCESS = 6;
    public static final int DISCONNECTION_UPDATE_FAILURE = 7;
    public static final int RECONNECTION_LIST_SUCCESS = 8;
    public static final int RECONNECTION_LIST_FAILURE = 9;
    public static final int RECONNECTION_UPDATE_SUCCESS = 10;
    public static final int RECONNECTION_UPDATE_FAILURE = 11;
}
