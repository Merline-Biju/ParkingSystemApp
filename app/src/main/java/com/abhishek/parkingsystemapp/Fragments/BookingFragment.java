package com.abhishek.parkingsystemapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.Dialogs.BookingDialog;
import com.abhishek.parkingsystemapp.Dialogs.CheckoutDialog;
import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.Models.ParkingSlot;
import com.abhishek.parkingsystemapp.Models.UserHistory;
import com.abhishek.parkingsystemapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookingFragment extends Fragment {

    ImageView ivNumber;
    Button btnCheckAvailability, btnBook;
    RelativeLayout rlHidden;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    List<ParkingSlot> slotList;
    AppUser user;
    UserHistory history;

    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        ivNumber = view.findViewById(R.id.ivNumber);
        btnCheckAvailability = view.findViewById(R.id.btnCheckAvailability);
        btnBook = view.findViewById(R.id.btnBookSlot);
        rlHidden = view.findViewById(R.id.rlHidden);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        rlHidden.setClickable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        getUser();

        btnCheckAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAvailability(false);
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAvailability(true);
            }
        });

        rlHidden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutSlot();
            }
        });

        return view;
    }

    private void getUser() {
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            user = task.getResult().toObject(AppUser.class);
                            getAvailability(false);
                            if (!user.getTransactionId().isEmpty())
                                getUserHistory();
                            rlHidden.setClickable(true);
                        }
                        else {
                            Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(getActivity(), com.abhishek.parkingsystemapp.StartActivity.class));
                            getActivity().finish();
                        }
                    }
                });
    }

    private void getUserHistory() {
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .collection("HISTORY").document(user.getTransactionId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            history = task.getResult().toObject(UserHistory.class);
                        }
                    }
                });
    }

    private void getAvailability(boolean bookSlot) {

        progressBar.setVisibility(View.VISIBLE);

        firestore.collection("PARKING")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()){
                            slotList = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                ParkingSlot slot = doc.toObject(ParkingSlot.class);
                                if(!slot.isBooked()){
                                    slotList.add(slot);
                                }
                            }
                            switch(slotList.size()){
                                case 0: ivNumber.setImageResource(R.drawable.number_0); break;
                                case 1: ivNumber.setImageResource(R.drawable.number_1); break;
                                case 2: ivNumber.setImageResource(R.drawable.number_2); break;
                                case 3: ivNumber.setImageResource(R.drawable.number_3); break;
                                case 4: ivNumber.setImageResource(R.drawable.number_4); break;
                            }

                            /*if(slotList.size() > 1){
                                Toast.makeText(getActivity(), slotList.size() + " slots are available!!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getActivity(), slotList.size() + " slot available!!", Toast.LENGTH_SHORT).show();
                            }*/

                            if(bookSlot && slotList.size() > 0 && user.getSlotNumber().isEmpty()){
                                //Book slot only if btnBook is clicked and if user hasn't booked any other slot;
                                bookSlot();
                            }
                            else {
                                if (slotList.size() == 0)
                                    Toast.makeText(getActivity(), "Sorry, No slots available!! :(", Toast.LENGTH_SHORT).show();
                                else if(bookSlot && !user.getSlotNumber().isEmpty()){
                                    Toast.makeText(getActivity(), "You have already booked a slot!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    private void bookSlot() {
       if(user.getWallet() < 50){ //Minimum balance
           Toast.makeText(getActivity(), "Insufficient balance to book a slot!! Please recharge wallet", Toast.LENGTH_SHORT).show();
       }
       else{
            ParkingSlot slot = slotList.remove(0);
            openBookingDialog(slot);
       }
    }

    private void openBookingDialog(ParkingSlot slot) {
        progressBar.setVisibility(View.VISIBLE);
        BookingDialog booking = new BookingDialog(slot, user, progressBar);
        booking.show(getActivity().getSupportFragmentManager(), "Booking Dialog");
    }

    private void checkoutSlot() {
        //Confirm if user is exiting
        //Calculate Fair
        //If wallet has sufficient balance ://Update history
                                            //Update Parking Slot document
                                            //Set user slot number to empty and transationId to empty and deduct amount from wallet
        //Else : Do nothing!!

        if(!user.getSlotNumber().isEmpty())
            openCheckoutDialog();
    }

    private void openCheckoutDialog() {
        progressBar.setVisibility(View.VISIBLE);
        CheckoutDialog checkout = new CheckoutDialog(user, history, progressBar);
        checkout.show(getActivity().getSupportFragmentManager(), "Checkout Dialog");
    }


}