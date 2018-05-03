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

import static com.transvision.trmdisconnreconn.values.Constants.DISCONNECTION_DIALOG;

public class Disconnection_adapter extends RecyclerView.Adapter<Disconnection_adapter.Disconnection_ViewHolder> {
    private ArrayList<GetSetValues> arrayList;
    private Context context;

    public Disconnection_adapter(ArrayList<GetSetValues> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Disconnection_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disconnection_card_view, parent, false);
        return new Disconnection_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Disconnection_ViewHolder holder, int position) {
        GetSetValues details = arrayList.get(position);
        holder.tv_account_id.setText(details.getDisconn_Account_id());
        holder.tv_arrears.setText(String.format("%s %s", context.getResources().getString(R.string.rupee), details.getDisconn_arrears()));
        holder.tv_date.setText(details.getDisconn_date());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Disconnection_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_account_id, tv_arrears, tv_date;

        Disconnection_ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_account_id = itemView.findViewById(R.id.disconnection_account_id);
            tv_arrears = itemView.findViewById(R.id.disconnection_arrears);
            tv_date = itemView.findViewById(R.id.disconnection_date);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ((MainActivity) context).show_DIS_RE_connection_dialog(DISCONNECTION_DIALOG, position, arrayList);
        }
    }
}
