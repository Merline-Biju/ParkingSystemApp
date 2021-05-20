package com.abhishek.parkingsystemapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.Models.UserHistory;
import com.abhishek.parkingsystemapp.R;
import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class CheckoutDialog extends AppCompatDialogFragment {

    TextView tvFair, tvArrival, tvExit;
    ProgressBar progressBar;
    AppUser user;
    UserHistory history;
    double amount = 0;

    CheckoutDialogListener listener;

    public CheckoutDialog(AppUser user, UserHistory history, ProgressBar progressBar) {
        this.user = user;
        this.history = history;
        this.progressBar = progressBar;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_checkout, null);

        tvFair = view.findViewById(R.id.tvFair_dialog);
        tvArrival = view.findViewById(R.id.tvArrival);
        tvExit = view.findViewById(R.id.tvExit);
        history.setExit(Timestamp.now());
        
        String arrival = history.getArrival().toDate().toString();
        arrival = arrival.substring(0, arrival.length() - 14) + arrival.substring(arrival.length() - 4);
        String exit = history.getExit().toDate().toString();
        exit = exit.substring(0, exit.length() - 14) + exit.substring(exit.length() - 4);


        //Calculate Fair
        double time = (double) ((history.getExit().getSeconds() - history.getArrival().getSeconds()) / 3600.0); //Seconds to hour conversion
        if(time < 1.00){ //Minimum 50/-
            amount = 50;
        }
        else{ //Above 1hr, its 50/- per hour
            amount = time * 50;
        }

        amount = Double.parseDouble(String.format(Locale.US,"%.2f",amount));
        history.setAmount(amount);

        Log.d("Time :;", history.getArrival().toDate() + ", " + history.getExit().toDate() + ", " + amount);

        tvFair.setText("₹".concat(String.valueOf(amount)));
        tvArrival.setText(arrival);
        tvExit.setText(exit);

        builder.setView(view)
                .setTitle("Confirm")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .setPositiveButton("Checkout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(user.getWallet() > amount){ //Allow
                            listener.payment(history, user);
                        }
                        else{
                            dialog.cancel();
                            Toast.makeText(getActivity(), "Insufficient balance!! Please recharge wallet", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (CheckoutDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the payment method!!");
        }

    }

    public interface CheckoutDialogListener{
        void payment(UserHistory history, AppUser user);
    }

}
