package com.transvision.trmdisconnreconn.values;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FunctionsCall {

    public void logStatus(String message) {
        Log.d("debug", message);
    }

    private String Appfoldername() {
    return "TRM_Disconnection";
}

    public String filepath(String value) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory(), Appfoldername()
                + File.separator + value);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.toString();
    }

    public void setEdittext_error(TextInputEditText editText, String error_msg) {
        editText.setError(error_msg);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
    }

    public String dateSet() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int mnth2 = month + 1;
        String present_date1 = day + "/" + mnth2 + "/" + "" + year;
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(present_date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        c.setTime(date);
        return sdf.format(c.getTime());
    }

    public boolean checkInternetConnection(Context context) {
        CheckInternetConnection cd = new CheckInternetConnection(context.getApplicationContext());
        return cd.isConnectingToInternet();
    }

    public void showprogressdialog(ProgressDialog dialog, String Title, String Message) {
        dialog.setTitle(Title);
        dialog.setMessage(Message);
        dialog.setCancelable(false);
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    public String convertdateview(String date, String format, String separation) {
        String s1, s2, s3, s4, s5="";
        if (date.length() == 10) {
            s1 = date.substring(0, 2);
            s2 = date.substring(3, 5);
            s3 = date.substring(6, 10);
            if (format.equals("DD") || format.equals("dd")) {
                s5 = s1 + separation + s2 + separation + s3;
            } else {
                s5 = s3 + separation + s2 + separation + s1;
            }
        } else if (date.length() == 9) {
            s4 = date.substring(1, 2);
            try {
                int i1 = Integer.parseInt(s4);
                s1 = date.substring(0, 2);
                s2 = date.substring(3, 4);
                s3 = date.substring(5, 9);
                if (format.equals("DD") || format.equals("dd")) {
                    s5 = s1 + separation + "0"+s2 + separation + s3;
                } else {
                    s5 = s3 + separation + "0"+s2 + separation + s1;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                s1 = date.substring(0, 1);
                s2 = date.substring(2, 4);
                s3 = date.substring(5, 9);
                if (format.equals("DD") || format.equals("dd")) {
                    s5 = "0"+s1 + separation + s2 + separation + s3;
                } else {
                    s5 = s3 + separation + s2 + separation + "0"+s1;
                }
            }
        } else if (date.length() == 8) {
            s1 = date.substring(0, 1);
            s2 = date.substring(2, 3);
            s3 = date.substring(4, 8);
            if (format.equals("DD") || format.equals("dd")) {
                s5 = "0"+s1 + separation + "0"+s2 + separation + s3;
            } else {
                s5 = s3 + separation + "0"+s2 + separation + "0"+s1;
            }
        }
        return s5;
    }
}
