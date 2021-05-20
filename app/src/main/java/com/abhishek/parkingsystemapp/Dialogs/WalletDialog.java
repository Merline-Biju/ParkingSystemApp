package com.abhishek.parkingsystemapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.R;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class WalletDialog extends AppCompatDialogFragment {

    TextView tvAmount;
    ProgressBar progressBar;
    double amount;
    AppUser user;

    WalletDialogListener listener;

    public WalletDialog(double amount, AppUser user, ProgressBar progressBar) {
        this.amount = amount;
        this.user = user;
        this.progressBar = progressBar;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_wallet, null);
        amount = Double.parseDouble(String.format(Locale.US,"%.2f",amount));

        builder.setView(view)
                .setTitle("Confirm")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .setPositiveButton("Recharge", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setWallet(user.getWallet() + amount);
                        listener.recharge(amount, user);
                    }
                });

//        Button positive = builder.create().getButton(AlertDialog.BUTTON_POSITIVE);
//        positive.setBackground(getResources().getDrawable(R.drawable.button_background));
//        positive.setTextColor(getResources().getColor(R.color.white));
//        Button negative = builder.create().getButton(AlertDialog.BUTTON_NEGATIVE);
//        negative.setBackground(getResources().getDrawable(R.drawable.button_background));
//        negative.setTextColor(getResources().getColor(R.color.white));

        tvAmount = view.findViewById(R.id.tvAmount_dialog);
        tvAmount.setText("₹".concat(String.valueOf(amount)));

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (WalletDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the recharge method!!");
        }

    }

    public interface WalletDialogListener{
        void recharge(double amount, AppUser user);
    }

}
