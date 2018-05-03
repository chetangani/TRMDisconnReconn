package com.transvision.trmdisconnreconn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.transvision.trmdisconnreconn.posting.SendingData;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.transvision.trmdisconnreconn.values.Constants.GETSET;
import static com.transvision.trmdisconnreconn.values.Constants.LOGIN_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.LOGIN_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.PREFS_NAME;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Login_date;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_MRCode;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_MRName;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Subdivision;

public class LoginActivity extends AppCompatActivity {

    private static final int RequestPermissionCode = 1;
    private static final int DLG_INTERNET_CONNECTION = 2;
    private static final int DLG_LOGIN_FAILURE = 3;

    TextInputEditText et_mrcode, et_password, et_date;
    Button btn_login;
    String selected_date="", mrcode="", password="", main_deviceid="";

    FunctionsCall functionsCall;
    SendingData sendingData;
    GetSetValues getSetValues;
    ProgressDialog progressDialog;

    SharedPreferences sPref;
    SharedPreferences.Editor editor;

    private Handler login_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
                    et_mrcode.setText("");
                    et_password.setText("");
                    et_mrcode.requestFocus();
                    editor.putString(sPref_MRCode, getSetValues.getLogin_mrcode());
                    editor.putString(sPref_MRName, getSetValues.getLogin_mrname());
                    editor.putString(sPref_Subdivision, getSetValues.getLogin_subdiv());
                    editor.putString(sPref_Login_date, selected_date);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(GETSET, getSetValues);
                    startActivity(intent);
                    finish();
                    break;

                case LOGIN_FAILURE:
                    progressDialog.dismiss();
                    showdialog(DLG_LOGIN_FAILURE);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissionsMandAbove();
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login_handler.removeCallbacksAndMessages(null);
    }

    private void initialize() {
        functionsCall = new FunctionsCall();
        sendingData = new SendingData();
        getSetValues = new GetSetValues();
        progressDialog = new ProgressDialog(this);
        sPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sPref.edit();
        editor.apply();

        et_mrcode = findViewById(R.id.et_login_mrcode);
        et_password = findViewById(R.id.et_login_password);
        et_date = findViewById(R.id.et_login_date);
        et_date.setText(functionsCall.dateSet());
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int date, month, year;
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                date = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dp = new DatePickerDialog(LoginActivity.this, dateSetListener, year, month, date);
                /*Calendar min_cal = Calendar.getInstance();
                min_cal.set(year, month, 1);
                dp.getDatePicker().setMinDate(min_cal.getTimeInMillis());*/
                dp.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dp.show();
            }
        });

        btn_login = findViewById(R.id.login_btn);
        btn_login.setEnabled(true);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (tm != null) {
//            main_deviceid = tm.getDeviceId();
            main_deviceid = "352514083077473";
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_details();
            }
        });
    }

    private void login_details() {
        if (functionsCall.checkInternetConnection(LoginActivity.this)) {
            if (!TextUtils.isEmpty(et_mrcode.getText())) {
                if (et_mrcode.getText().length() == 8) {
                    mrcode = et_mrcode.getText().toString();
                    if (!TextUtils.isEmpty(et_password.getText())) {
                        password = et_password.getText().toString();
                        functionsCall.showprogressdialog(progressDialog, getResources().getString(R.string.login), getResources().getString(R.string.login_details_validating));
                        sendingData.new MR_Login(login_handler, getSetValues).execute(mrcode, main_deviceid, password);
                    } else functionsCall.setEdittext_error(et_password, getResources().getString(R.string.password_enter));
                } else functionsCall.setEdittext_error(et_mrcode, getResources().getString(R.string.mrcode_valid));
            } else functionsCall.setEdittext_error(et_mrcode, getResources().getString(R.string.mrcode_enter));
        } else showdialog(DLG_INTERNET_CONNECTION);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Date Starttime = null;
            et_date.setText("");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            try {
                Starttime = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse((""+ dayOfMonth + "/" + ""+ (monthOfYear + 1) + "/" + ""+year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String dateselected = sdf.format(Starttime);
            et_date.setText(dateselected);
            et_date.setSelection(et_date.getText().length());
            selected_date = et_date.getText().toString();
        }
    };

    private void showdialog(int id) {
        final AlertDialog alertDialog;
        @SuppressLint("InflateParams")
        LinearLayout dialog_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_message_layout, null);
        TextView tv_msg = dialog_layout.findViewById(R.id.dialog_message);
        Button btn_positive = dialog_layout.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = dialog_layout.findViewById(R.id.dialog_negative_btn);
        switch (id) {
            case DLG_INTERNET_CONNECTION:
                AlertDialog.Builder internet_connection = new AlertDialog.Builder(this);
                internet_connection.setTitle(getResources().getString(R.string.internet_title));
                internet_connection.setCancelable(false);
                internet_connection.setView(dialog_layout);
                btn_negative.setVisibility(View.GONE);
                tv_msg.setText(getResources().getString(R.string.internet_connection));
                btn_positive.setText(getResources().getString(R.string.select_ok));
                alertDialog = internet_connection.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            case DLG_LOGIN_FAILURE:
                AlertDialog.Builder login_failure = new AlertDialog.Builder(LoginActivity.this);
                login_failure.setTitle(getResources().getString(R.string.login_failure_title));
                login_failure.setCancelable(false);
                login_failure.setView(dialog_layout);
                btn_negative.setVisibility(View.GONE);
                btn_positive.setText(getResources().getString(R.string.select_ok));
                tv_msg.setText(getResources().getString(R.string.login_failure_msg));
                alertDialog = login_failure.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
        }
    }

    @TargetApi(23)
    public void checkPermissionsMandAbove() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (checkPermission()) {
                initialize();
            } else requestPermission();
        } else initialize();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {
                        READ_PHONE_STATE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadPhoneStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (ReadPhoneStatePermission && ReadStoragePermission) {
                        initialize();
                    } else {
                        Toast.makeText(LoginActivity.this, "Required All Permissions to granted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }
}
