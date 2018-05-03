package com.transvision.trmdisconnreconn.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transvision.trmdisconnreconn.MainActivity;
import com.transvision.trmdisconnreconn.R;
import com.transvision.trmdisconnreconn.values.GetSetValues;

import java.util.ArrayList;

import static com.transvision.trmdisconnreconn.values.Constants.RECONNECTION_DIALOG;

public class Reconnection_adapter extends RecyclerView.Adapter<Reconnection_adapter.Reconnection_ViewHolder> {
    private ArrayList<GetSetValues> arrayList;
    private Context context;

    public Reconnection_adapter(ArrayList<GetSetValues> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Reconnection_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reconnection_card_view, parent, false);
        return new Reconnection_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Reconnection_ViewHolder holder, int position) {
        GetSetValues details = arrayList.get(position);
        holder.tv_account_id.setText(details.getReconn_Account_id());
        holder.tv_date.setText(details.getReconn_date());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Reconnection_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_account_id, tv_date;

        Reconnection_ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_account_id = itemView.findViewById(R.id.reconnection_account_id);
            tv_date = itemView.findViewById(R.id.reconnection_date);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ((MainActivity) context).show_DIS_RE_connection_dialog(RECONNECTION_DIALOG, position, arrayList);
        }
    }
}
