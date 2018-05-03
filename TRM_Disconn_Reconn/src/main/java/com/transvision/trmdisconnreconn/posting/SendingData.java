package com.transvision.trmdisconnreconn.posting;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;

import com.transvision.trmdisconnreconn.adapters.Disconnection_adapter;
import com.transvision.trmdisconnreconn.adapters.Reconnection_adapter;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.transvision.trmdisconnreconn.values.Constants.LOGIN_SERVICE;
import static com.transvision.trmdisconnreconn.values.Constants.SERVICE;
import static com.transvision.trmdisconnreconn.values.Constants.TIME_OUT_EXCEPTION;
import static com.transvision.trmdisconnreconn.values.Constants.TRM_URL;

public class SendingData {
    private ReceivingData receivingData = new ReceivingData();
    private FunctionsCall functionCalls = new FunctionsCall();
    private String BASE_URL = TRM_URL + SERVICE;
    private String LOGIN_URL = TRM_URL + LOGIN_SERVICE;

    private String urlPostConnection(String Post_Url, HashMap<String, String> datamap, Handler handler) throws IOException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(Post_Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(datamap));
        writer.flush();
        writer.close();
        os.close();
        int responseCode= conn.getResponseCode();
        try {
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
            } else response = new StringBuilder();
        } catch (InterruptedIOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(TIME_OUT_EXCEPTION);
        }
        return response.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /*----------------------------------------LOGIN----------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    public class MR_Login extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        GetSetValues getSetValues;

        public MR_Login(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues = getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("MRCode", params[0]);
            datamap.put("DeviceId", params[1]);
            datamap.put("PASSWORD", params[2]);
            functionCalls.logStatus("MRCode: "+params[0] + "\n" + "DeviceID: "+params[1] + "\n" + "Password: "+params[2]);
            try {
                response = urlPostConnection(LOGIN_URL+"MRDetails", datamap, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getMR_Login_status(result, handler, getSetValues);
        }
    }

    /*----------------------------------------Disconnection Result----------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    public class Disconnection_List extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        ArrayList<GetSetValues> arrayList;
        Disconnection_adapter adapter;

        public Disconnection_List(Handler handler, ArrayList<GetSetValues> arrayList, Disconnection_adapter adapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("MRCode", strings[0]);
            datamap.put("Date", strings[1]);
            functionCalls.logStatus("MRCode: "+strings[0] + "\n" + "Date: "+strings[1]);
            try {
                response = urlPostConnection(BASE_URL+"DisConList", datamap, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getDisconnection_status(result, handler, arrayList, adapter);
        }
    }

    /*----------------------------------------Disconnection Update----------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    public class Disconnect_Update extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;

        public Disconnect_Update(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("Acc_id", params[0]);
            datamap.put("Dis_Date", params[1]);
            datamap.put("CURREAD", params[2]);
            functionCalls.logStatus("Acc_id: "+params[0] + "\n" + "Dis_Date: "+params[1] + "\n" + "CURREAD: "+params[2]);
            try {
                response = urlPostConnection(BASE_URL+"DisConUpdate", datamap, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getDisconnection_update_status(result, handler);
        }
    }

    /*----------------------------------------Reconnection Result----------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    public class Reconnection_List extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;
        ArrayList<GetSetValues> arrayList;
        Reconnection_adapter adapter;

        public Reconnection_List(Handler handler, ArrayList<GetSetValues> arrayList, Reconnection_adapter adapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("MRCode", strings[0]);
            datamap.put("Date", strings[1]);
            functionCalls.logStatus("MRCode: "+strings[0] + "\n" + "Date: "+strings[1]);
            try {
                response = urlPostConnection(BASE_URL+"ReConList", datamap, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getReconnection_status(result, handler, arrayList, adapter);
        }
    }

    /*----------------------------------------Reconnection Update----------------------------------------*/
    @SuppressLint("StaticFieldLeak")
    public class Reconnect_Update extends AsyncTask<String, String, String> {
        String response="";
        Handler handler;

        public Reconnect_Update(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("Acc_id", params[0]);
            datamap.put("Dis_Date", params[1]);
            datamap.put("CURREAD", params[2]);
            functionCalls.logStatus("Acc_id: "+params[0] + "\n" + "Dis_Date: "+params[1] + "\n" + "CURREAD: "+params[2]);
            try {
                response = urlPostConnection(BASE_URL+"ReConUpdate", datamap, handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getReconnection_update_status(result, handler);
        }
    }
}
