package com.abhishek.parkingsystemapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.parkingsystemapp.Models.UserHistory;
import com.abhishek.parkingsystemapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    Context context;
    List<UserHistory> historyList;

    public HistoryAdapter(Context context, List<UserHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.ViewHolder holder, int position) {

        UserHistory history = historyList.get(position);

        String arrival = history.getArrival().toDate().toString();
        arrival = arrival.substring(0, arrival.length() - 14) + arrival.substring(arrival.length() - 4);
        String exit = "---";
        if (history.getExit() != null){
            exit = history.getExit().toDate().toString();
            exit = exit.substring(0, exit.length() - 14) + exit.substring(exit.length() - 4);
        }

        holder.tvArrival.setText(arrival);
        holder.tvExit.setText(exit);
        holder.tvAmount.setText("₹".concat(String.valueOf(history.getAmount())));
        holder.tvTransaction.setText(history.getTransactionId());

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvArrival, tvExit, tvAmount, tvTransaction;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvArrival = itemView.findViewById(R.id.tvArrival_historyList);
            tvExit = itemView.findViewById(R.id.tvCheckout_historyList);
            tvAmount = itemView.findViewById(R.id.tvAmount_historyList);
            tvTransaction = itemView.findViewById(R.id.tvTransaction_historyList);

        }
    }

}
