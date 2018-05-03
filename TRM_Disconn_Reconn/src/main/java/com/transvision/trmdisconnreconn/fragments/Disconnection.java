package com.transvision.trmdisconnreconn.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transvision.trmdisconnreconn.MainActivity;
import com.transvision.trmdisconnreconn.R;
import com.transvision.trmdisconnreconn.adapters.Disconnection_adapter;
import com.transvision.trmdisconnreconn.posting.SendingData;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_LIST_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_LIST_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.PREFS_NAME;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Login_date;

public class Disconnection extends Fragment {
    View view;

    FunctionsCall functionsCall;
    SendingData sendingData;
    GetSetValues getSetValues;

    RecyclerView disconnection_view;
    ArrayList<GetSetValues> disconnection_list;
    Disconnection_adapter disconnection_adapter;
    ProgressDialog progressDialog;
    Context context;

    SharedPreferences sPref;

    private Handler disconn_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISCONNECTION_LIST_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case DISCONNECTION_LIST_FAILURE:
                    progressDialog.dismiss();
                    break;
            }
            return false;
        }
    });

    public Disconnection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_disconnection, container, false);

        initialize();

        functionsCall.showprogressdialog(progressDialog, context.getResources().getString(R.string.disconnection_dlg_title),
                context.getResources().getString(R.string.disconnection_dlg_msg));
        sendingData.new Disconnection_List(disconn_handler, disconnection_list, disconnection_adapter).execute("14000521",
                functionsCall.convertdateview(sPref.getString(sPref_Login_date, ""), "yy", "-"));

        return view;
    }

    private void initialize() {
        functionsCall = new FunctionsCall();
        sendingData = new SendingData();
        getSetValues = ((MainActivity) Objects.requireNonNull(getActivity())).getSetValues();
        progressDialog = new ProgressDialog(getActivity());
        context = getActivity();
        sPref = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        disconnection_view = view.findViewById(R.id.disconnection_recycler_view);
        disconnection_list = new ArrayList<>();
        disconnection_adapter = new Disconnection_adapter(disconnection_list, context);
        disconnection_view.setHasFixedSize(true);
        disconnection_view.setLayoutManager(new LinearLayoutManager(context));
        disconnection_view.setAdapter(disconnection_adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconn_handler.removeCallbacksAndMessages(null);
    }
}
