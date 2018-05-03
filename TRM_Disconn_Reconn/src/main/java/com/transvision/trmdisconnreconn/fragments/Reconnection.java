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
import com.transvision.trmdisconnreconn.adapters.Reconnection_adapter;
import com.transvision.trmdisconnreconn.posting.SendingData;
import com.transvision.trmdisconnreconn.values.FunctionsCall;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.transvision.trmdisconnreconn.values.Constants.PREFS_NAME;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_LIST_FAILURE;
import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_LIST_SUCCESS;
import static com.transvision.trmdisconnreconn.values.Constants.sPref_Login_date;

public class Reconnection extends Fragment {
    View view;

    FunctionsCall functionsCall;
    SendingData sendingData;
    GetSetValues getSetValues;

    RecyclerView reconnection_view;
    ArrayList<GetSetValues> reconnection_list;
    Reconnection_adapter reconnection_adapter;
    ProgressDialog progressDialog;
    Context context;

    SharedPreferences sPref;

    private Handler reconn_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case RECONNECTION_LIST_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case RECONNECTION_LIST_FAILURE:
                    progressDialog.dismiss();
                    break;
            }
            return false;
        }
    });

    public Reconnection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reconnection, container, false);

        initialize();

        functionsCall.showprogressdialog(progressDialog, context.getResources().getString(R.string.reconnection_dlg_title),
                context.getResources().getString(R.string.reconnection_dlg_msg));
        sendingData.new Reconnection_List(reconn_handler, reconnection_list, reconnection_adapter).execute("14001137",
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

        reconnection_view = view.findViewById(R.id.reconnection_recycler_view);
        reconnection_list = new ArrayList<>();
        reconnection_adapter = new Reconnection_adapter(reconnection_list, context);
        reconnection_view.setHasFixedSize(true);
        reconnection_view.setLayoutManager(new LinearLayoutManager(context));
        reconnection_view.setAdapter(reconnection_adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reconn_handler.removeCallbacksAndMessages(null);
    }
}
