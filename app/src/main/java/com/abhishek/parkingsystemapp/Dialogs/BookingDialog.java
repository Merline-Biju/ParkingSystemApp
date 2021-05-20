package com.abhishek.parkingsystemapp.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.Models.ParkingSlot;
import com.abhishek.parkingsystemapp.R;

import org.jetbrains.annotations.NotNull;

public class BookingDialog extends AppCompatDialogFragment {

    ProgressBar progressBar;
    AppUser user;
    ParkingSlot slot;

    BookingDialogListener listener;

    public BookingDialog(ParkingSlot slot, AppUser user, ProgressBar progressBar){
        this.slot = slot;
        this.user = user;
        this.progressBar = progressBar;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_booking, null);

        builder.setView(view)
                .setTitle("Confirm")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.bookParking(slot, user);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (BookingDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the bookParking method!!");
        }

    }

    public interface BookingDialogListener{
        void bookParking(ParkingSlot slot, AppUser user);
    }

}
