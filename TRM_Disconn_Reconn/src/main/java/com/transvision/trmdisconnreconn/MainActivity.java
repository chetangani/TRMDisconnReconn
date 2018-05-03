package com.transvision.trmdisconnreconn;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.transvision.trmdisconnreconn.fragments.Disconnection;
import com.transvision.trmdisconnreconn.fragments.Reconnection;
import com.transvision.trmdisconnreconn.posting.SendingData;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.util.ArrayList;

import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_DIALOG;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_UPDATE_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_UPDATE_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.GETSET;
import static com.transvision.trmdisconnreconn.values.Constants.PREFS_NAME;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_DIALOG;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_UPDATE_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_UPDATE_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Login_date;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_MRCode;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_MRName;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Subdivision;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int DLG_DISCONNECT_SUCCESS = 1;
    private static final int DLG_DISCONNECT_FAILURE = 2;
    private static final int DLG_RECONNECT_SUCCESS = 3;
    private static final int DLG_RECONNECT_FAILURE = 4;

    private Fragment fragment;
    private Toolbar toolbar;
    GetSetValues getSetValues;
    FunctionsCall functionsCall;
    SendingData sendingData;
    ProgressDialog progressDialog;

    SharedPreferences sPref;
    SharedPreferences.Editor editor;

    String Account_ID="";
    private TextView tv_name, tv_code;

    public enum Steps {
        FORM0(Disconnection.class),
        FORM1(Reconnection.class);

        private Class clazz;

        Steps(Class clazz) {
            this.clazz = clazz;
        }

        public Class getFragClass() {
            return clazz;
        }
    }

    private Handler main_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISCONNECTION_UPDATE_SUCCESS:
                    progressDialog.dismiss();
                    showdialog(DLG_DISCONNECT_SUCCESS);
                    break;

                case DISCONNECTION_UPDATE_FAILURE:
                    progressDialog.dismiss();
                    showdialog(DLG_DISCONNECT_FAILURE);
                    break;

                case RECONNECTION_UPDATE_SUCCESS:
                    progressDialog.dismiss();
                    showdialog(DLG_RECONNECT_SUCCESS);
                    break;

                case RECONNECTION_UPDATE_FAILURE:
                    progressDialog.dismiss();
                    showdialog(DLG_RECONNECT_FAILURE);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        getSetValues = (GetSetValues) intent.getSerializableExtra(GETSET);

        initialize();

        tv_name.setText(sPref.getString(sPref_MRName, ""));
        tv_code.setText(sPref.getString(sPref_MRCode, ""));

        switchContent(Steps.FORM0, getResources().getString(R.string.disconnection));
    }

    private void initialize() {
        functionsCall = new FunctionsCall();
        sendingData = new SendingData();
        progressDialog = new ProgressDialog(this);

        sPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = sPref.edit();
        editor.apply();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        tv_name = view.findViewById(R.id.login_name);
        tv_code = view.findViewById(R.id.login_code);
        NavigationView logout_navigationView = findViewById(R.id.nav_drawer_bottom);
        logout_navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        main_handler.removeCallbacksAndMessages(null);
    }

    public GetSetValues getSetValues() {
        return this.getSetValues;
    }

    public void switchContent(Steps currentForm, String title) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        toolbar.setTitle(title);
        ft.replace(R.id.container_main, fragment, currentForm.name());
        ft.commit();
    }

    public void switchPopBackContent(Steps currentForm, String title) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        FragmentTransaction ft = fm.beginTransaction();
        toolbar.setTitle(title);
        ft.replace(R.id.container_main, fragment, currentForm.name());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_disconnection:
                switchPopBackContent(Steps.FORM0, getResources().getString(R.string.disconnection));
                break;

            case R.id.nav_reconnection:
                switchPopBackContent(Steps.FORM1, getResources().getString(R.string.reconnection));
                break;

            case R.id.nav_logout:
                editor.putString(sPref_MRCode, "");
                editor.putString(sPref_MRName, "");
                editor.putString(sPref_Subdivision, "");
                editor.putString(sPref_Login_date, "");
                editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showdialog(int id) {
        final AlertDialog alertDialog;
        @SuppressLint("InflateParams")
        LinearLayout connection_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_message_layout, null);
        TextView tv_message = connection_layout.findViewById(R.id.dialog_message);
        Button btn_positive = connection_layout.findViewById(R.id.dialog_positive_btn);
        btn_positive.setText(getResources().getString(R.string.select_ok));
        Button btn_negative = connection_layout.findViewById(R.id.dialog_negative_btn);
        btn_negative.setVisibility(View.GONE);
        switch (id) {
            case DLG_DISCONNECT_SUCCESS:
                AlertDialog.Builder disconnect_success = new AlertDialog.Builder(this);
                disconnect_success.setTitle(getResources().getString(R.string.disconnection_dlg_title));
                disconnect_success.setCancelable(false);
                disconnect_success.setView(connection_layout);
                tv_message.setText(String.format("%s %s %s", getResources().getString(R.string.disconnection_msg), Account_ID,
                        getResources().getString(R.string.disconnection_success_msg_1)));
                alertDialog = disconnect_success.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        switchPopBackContent(Steps.FORM0, getResources().getString(R.string.disconnection));
                    }
                });
                alertDialog.show();
                break;

            case DLG_DISCONNECT_FAILURE:
                AlertDialog.Builder disconnect_failure = new AlertDialog.Builder(this);
                disconnect_failure.setTitle(getResources().getString(R.string.disconnection_dlg_title));
                disconnect_failure.setCancelable(false);
                disconnect_failure.setView(connection_layout);
                tv_message.setText(String.format("%s %s %s", getResources().getString(R.string.disconnection_msg), Account_ID,
                        getResources().getString(R.string.disconnection_failure_msg_1)));
                alertDialog = disconnect_failure.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            case DLG_RECONNECT_SUCCESS:
                AlertDialog.Builder reconnect_success = new AlertDialog.Builder(this);
                reconnect_success.setTitle(getResources().getString(R.string.reconnection_dlg_title));
                reconnect_success.setCancelable(false);
                reconnect_success.setView(connection_layout);
                tv_message.setText(String.format("%s %s %s", getResources().getString(R.string.reconnection_msg), Account_ID,
                        getResources().getString(R.string.reconnection_success_msg_1)));
                alertDialog = reconnect_success.create();
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        switchPopBackContent(Steps.FORM1, getResources().getString(R.string.reconnection));
                    }
                });
                alertDialog.show();
                break;

            case DLG_RECONNECT_FAILURE:
                AlertDialog.Builder reconnect_failure = new AlertDialog.Builder(this);
                reconnect_failure.setTitle(getResources().getString(R.string.reconnection_dlg_title));
                reconnect_failure.setCancelable(false);
                reconnect_failure.setView(connection_layout);
                tv_message.setText(String.format("%s %s %s", getResources().getString(R.string.reconnection_msg), Account_ID,
                        getResources().getString(R.string.reconnection_failure_msg_1)));
                alertDialog = reconnect_failure.create();
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

    public void show_DIS_RE_connection_dialog(int id, int position, ArrayList<GetSetValues> arrayList) {
        final AlertDialog alertDialog;
        final GetSetValues getSetValues = arrayList.get(position);
        switch (id) {
            case DISCONNECTION_DIALOG:
                AlertDialog.Builder disconnection = new AlertDialog.Builder(this);
                disconnection.setTitle(getResources().getString(R.string.disconnection));
                disconnection.setCancelable(false);
                @SuppressLint("InflateParams")
                LinearLayout disconn_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.disconnection_layout, null);
                disconnection.setView(disconn_layout);
                TextView tv_dis_account_id = disconn_layout.findViewById(R.id.disconn_dlg_account_id);
                TextView tv_dis_name = disconn_layout.findViewById(R.id.disconn_dlg_name);
                TextView tv_dis_address = disconn_layout.findViewById(R.id.disconn_dlg_address);
                TextView tv_dis_arrears = disconn_layout.findViewById(R.id.disconn_dlg_arrears);
                TextView tv_dis_prev_read = disconn_layout.findViewById(R.id.disconn_dlg_prev_read);
                final TextInputEditText et_dis_current_reading = disconn_layout.findViewById(R.id.disconn_current_read);
                Button disconnect_btn = disconn_layout.findViewById(R.id.dialog_positive_btn);
                disconnect_btn.setText(getResources().getString(R.string.disconnect));
                Button dis_cancel_btn = disconn_layout.findViewById(R.id.dialog_negative_btn);
                Account_ID = getSetValues.getDisconn_Account_id();
                tv_dis_account_id.setText(getSetValues.getDisconn_Account_id());
                tv_dis_name.setText(getSetValues.getDisconn_name());
                tv_dis_address.setText(getSetValues.getDisconn_address());
                tv_dis_arrears.setText(String.format("%s %s", getResources().getString(R.string.rupee), getSetValues.getDisconn_arrears()));
                tv_dis_prev_read.setText(getSetValues.getDisconn_prev_read());
                alertDialog = disconnection.create();
                disconnect_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(et_dis_current_reading.getText())) {
                            String disconnect_reading = et_dis_current_reading.getText().toString();
                            if (Double.parseDouble(disconnect_reading) >= Double.parseDouble(getSetValues.getDisconn_prev_read())) {
                                alertDialog.dismiss();
                                functionsCall.showprogressdialog(progressDialog, getResources().getString(R.string.disconnecting),
                                        getResources().getString(R.string.disconnecting_msg));
                                sendingData.new Disconnect_Update(main_handler).execute(getSetValues.getDisconn_Account_id(),
                                        functionsCall.convertdateview(sPref.getString(sPref_Login_date, ""), "yy", "-"),
                                        disconnect_reading);
                            } else functionsCall.setEdittext_error(et_dis_current_reading, getResources().getString(R.string.validate_disconnect_reading));
                        } else functionsCall.setEdittext_error(et_dis_current_reading, getResources().getString(R.string.disconnect_current_reading));
                    }
                });
                dis_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            case RECONNECTION_DIALOG:
                AlertDialog.Builder reconnection = new AlertDialog.Builder(this);
                reconnection.setTitle(getResources().getString(R.string.reconnection));
                reconnection.setCancelable(false);
                @SuppressLint("InflateParams")
                LinearLayout reconn_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.reconnection_layout, null);
                reconnection.setView(reconn_layout);
                TextView tv_re_account_id = reconn_layout.findViewById(R.id.reconn_dlg_account_id);
                TextView tv_re_name = reconn_layout.findViewById(R.id.reconn_dlg_name);
                TextView tv_re_address = reconn_layout.findViewById(R.id.reconn_dlg_address);
                TextView tv_re_prev_read = reconn_layout.findViewById(R.id.reconn_dlg_prev_read);
                final TextInputEditText et_re_current_reading = reconn_layout.findViewById(R.id.reconn_current_read);
                Button reconnect_btn = reconn_layout.findViewById(R.id.dialog_positive_btn);
                reconnect_btn.setText(getResources().getString(R.string.reconnect));
                Button re_cancel_btn = reconn_layout.findViewById(R.id.dialog_negative_btn);
                Account_ID = getSetValues.getReconn_Account_id();
                tv_re_account_id.setText(getSetValues.getReconn_Account_id());
                tv_re_name.setText(getSetValues.getReconn_name());
                tv_re_address.setText(getSetValues.getReconn_address());
                tv_re_prev_read.setText(getSetValues.getReconn_prev_read());
                alertDialog = reconnection.create();
                reconnect_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(et_re_current_reading.getText())) {
                            String reconnect_reading = et_re_current_reading.getText().toString();
                            if (Double.parseDouble(reconnect_reading) >= Double.parseDouble(getSetValues.getReconn_prev_read())) {
                                alertDialog.dismiss();
                                functionsCall.showprogressdialog(progressDialog, getResources().getString(R.string.reconnecting),
                                        getResources().getString(R.string.reconnecting_msg));
                                sendingData.new Reconnect_Update(main_handler).execute(getSetValues.getReconn_Account_id(),
                                        functionsCall.convertdateview(sPref.getString(sPref_Login_date, ""), "yy", "-"),
                                        reconnect_reading);
                            } else functionsCall.setEdittext_error(et_re_current_reading, getResources().getString(R.string.validate_disconnect_reading));
                        } else functionsCall.setEdittext_error(et_re_current_reading, getResources().getString(R.string.disconnect_current_reading));
                    }
                });
                re_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
        }
    }
}
