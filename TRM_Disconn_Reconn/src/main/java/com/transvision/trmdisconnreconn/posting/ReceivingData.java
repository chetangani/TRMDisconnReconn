package com.transvision.trmdisconnreconn.posting;

import android.os.Handler;

import com.transvision.trmdisconnreconn.adapters.Disconnection_adapter;
import com.transvision.trmdisconnreconn.adapters.Reconnection_adapter;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_LIST_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_LIST_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_UPDATE_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_UPDATE_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.LOGIN_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.LOGIN_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_LIST_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_LIST_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_UPDATE_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_UPDATE_SUCCESS;

public class ReceivingData {
    private FunctionsCall functionsCall = new FunctionsCall();

    private String parseServerXML(String result) {
        String value="";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "string":
                                value =  parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void getMR_Login_status(String result, Handler handler, GetSetValues getSetValues) {
        result = parseServerXML(result);
        functionsCall.logStatus("MR_Login: "+result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String message = jsonObject.getString("message");
                if (StringUtils.startsWithIgnoreCase(message, "Success!")) {
                    getSetValues.setLogin_mrcode(jsonObject.getString("MRCODE"));
                    getSetValues.setLogin_mrname(jsonObject.getString("MRNAME"));
                    getSetValues.setLogin_subdiv(jsonObject.getString("SUBDIVCODE"));
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } else handler.sendEmptyMessage(LOGIN_FAILURE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(LOGIN_FAILURE);
        }
    }

    public void getDisconnection_status(String result, Handler handler, ArrayList<GetSetValues> arrayList, Disconnection_adapter adapter) {
        result = parseServerXML(result);
        functionsCall.logStatus("Disconnection: "+result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GetSetValues getSetValues = new GetSetValues();
                getSetValues.setDisconn_Account_id(jsonObject.getString("ACCT_ID"));
                getSetValues.setDisconn_arrears(jsonObject.getString("ARREARS"));
                String dis_date = jsonObject.getString("DIS_DATE");
                getSetValues.setDisconn_date(functionsCall.convertdateview(dis_date.substring(0, dis_date.indexOf(' ')), "dd", "/"));
                getSetValues.setDisconn_prev_read(jsonObject.getString("PREVREAD"));
                getSetValues.setDisconn_name(jsonObject.getString("CONSUMER_NAME"));
                getSetValues.setDisconn_address(jsonObject.getString("ADD1"));
                getSetValues.setDisconn_lat(jsonObject.getString("LAT"));
                getSetValues.setDisconn_long(jsonObject.getString("LON"));
                arrayList.add(getSetValues);
                adapter.notifyDataSetChanged();
            }
            if (arrayList.size() > 0)
                handler.sendEmptyMessage(DISCONNECTION_LIST_SUCCESS);
            else handler.sendEmptyMessage(DISCONNECTION_LIST_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(DISCONNECTION_LIST_FAILURE);
        }
    }

    public void getDisconnection_update_status(String result, Handler handler) {
        result = parseServerXML(result);
        functionsCall.logStatus("Disconnection Update: "+result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (StringUtils.startsWithIgnoreCase(jsonObject.getString("message"), "Success"))
                handler.sendEmptyMessage(DISCONNECTION_UPDATE_SUCCESS);
            else handler.sendEmptyMessage(DISCONNECTION_UPDATE_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getReconnection_status(String result, Handler handler, ArrayList<GetSetValues> arrayList, Reconnection_adapter adapter) {
        result = parseServerXML(result);
        functionsCall.logStatus("Reconnection: "+result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GetSetValues getSetValues = new GetSetValues();
                getSetValues.setReconn_Account_id(jsonObject.getString("ACCT_ID"));
                String re_date = jsonObject.getString("RE_DATE");
                getSetValues.setReconn_date(functionsCall.convertdateview(re_date.substring(0, re_date.indexOf(' ')), "dd", "/"));
                getSetValues.setReconn_prev_read(jsonObject.getString("PREVREAD"));
                getSetValues.setReconn_name(jsonObject.getString("CONSUMER_NAME"));
                getSetValues.setReconn_address(jsonObject.getString("ADD1"));
                getSetValues.setReconn_lat(jsonObject.getString("LAT"));
                getSetValues.setReconn_long(jsonObject.getString("LON"));
                arrayList.add(getSetValues);
                adapter.notifyDataSetChanged();
            }
            if (arrayList.size() > 0)
                handler.sendEmptyMessage(RECONNECTION_LIST_SUCCESS);
            else handler.sendEmptyMessage(RECONNECTION_LIST_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(RECONNECTION_LIST_FAILURE);
        }
    }

    public void getReconnection_update_status(String result, Handler handler) {
        result = parseServerXML(result);
        functionsCall.logStatus("Reconnection Update: "+result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (StringUtils.startsWithIgnoreCase(jsonObject.getString("message"), "Success"))
                handler.sendEmptyMessage(RECONNECTION_UPDATE_SUCCESS);
            else handler.sendEmptyMessage(RECONNECTION_UPDATE_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
