package com.abhishek.parkingsystemapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.Dialogs.BookingDialog;
import com.abhishek.parkingsystemapp.Dialogs.CheckoutDialog;
import com.abhishek.parkingsystemapp.Dialogs.WalletDialog;
import com.abhishek.parkingsystemapp.Fragments.BookingFragment;
import com.abhishek.parkingsystemapp.Fragments.HistoryFragment;
import com.abhishek.parkingsystemapp.Fragments.ProfileFragment;
import com.abhishek.parkingsystemapp.Fragments.WalletFragment;
import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.Models.ParkingSlot;
import com.abhishek.parkingsystemapp.Models.UserHistory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity
        implements WalletDialog.WalletDialogListener,
        BookingDialog.BookingDialogListener,
        CheckoutDialog.CheckoutDialogListener {

    private BottomNavigationView bnNav;
    private Fragment selectorFragment;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.toobar_signout) { //SignOut with Firebase
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    startActivity(new Intent(MainActivity.this,
                            com.abhishek.parkingsystemapp.StartActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
                return true;
            }
        });

        bnNav = findViewById(R.id.bnNav);
        bnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_profile: selectorFragment = new ProfileFragment();
                                            break;

                    case R.id.nav_booking: selectorFragment = new BookingFragment();
                                            break;

                    case R.id.nav_wallet: selectorFragment = new WalletFragment();
                                            break;

                    case R.id.nav_history: selectorFragment = new HistoryFragment();
                                            break;

                }

                if (selectorFragment != null)
                {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.fragment_container, selectorFragment)
                            .commit();
                }

                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BookingFragment()).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_signout, menu);
        return true;
    }

    @Override
    public void recharge(double amount, AppUser user) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .update("wallet", user.getWallet())
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if(task.isSuccessful()){
                            //tvAmount.setText(user.getWallet());
                            Toast.makeText(MainActivity.this, "Recharge successful for ₹" + amount, Toast.LENGTH_SHORT).show();
                        }
                        //progressBar.setVisibility(View.INVISIBLE);
                        selectorFragment = new WalletFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectorFragment)
                                .commit();
                    }
                });
    }

    @Override
    public void bookParking(ParkingSlot slot, AppUser user) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user.setSlotNumber(slot.getSlotId());
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .update("slotNumber", user.getSlotNumber())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        UserHistory history = new UserHistory("", Timestamp.now(), null, 50);
                        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                                .collection("HISTORY")
                                .add(history)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        history.setTransactionId(documentReference.getId());
                                        updateHistory(history);
                                        updateSlot(history, slot);

                                        selectorFragment = new BookingFragment();
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, selectorFragment)
                                                .commit();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });
    }


    private void updateHistory(UserHistory history) {
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .collection("HISTORY").document(history.getTransactionId())
                .update("transactionId", history.getTransactionId())
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if(task.isSuccessful()){

                            //Updating Transaction Id in user document
                            firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                                    .update("transactionId", history.getTransactionId())
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task task) {
                                            if(task.isSuccessful()){
                                                Log.d("User update :: ", "True : " + history.getTransactionId());
                                            }
                                        }
                                    });

                            Log.d("History update :: ", "True : " + history.getTransactionId());
                        }
                        else {
                            Log.e("History update :: ", "False : " + history.getTransactionId());
                        }
                    }
                });
    }

    private void updateSlot(UserHistory history, ParkingSlot slot) {
        slot.setUserId(firebaseAuth.getCurrentUser().getUid());
        slot.setBooked(true);
        slot.setTransactionId(history.getTransactionId());
        firestore.collection("PARKING").document(slot.getSlotId())
                .set(slot)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Successfully booked Parking!!", Toast.LENGTH_SHORT).show();
                            Log.d("Parking update :: ", "True : " + slot.getSlotId());
                        }
                        else {
                            Log.d("Parking update :: ", "False : " + slot.getSlotId());
                        }
                    }
                });
    }

    @Override
    public void payment(UserHistory history, AppUser user) {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //Update History document
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .collection("HISTORY").document(history.getTransactionId())
                .set(history)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task task) {
                        if(task.isSuccessful()){
                            
                            //Update Parking slot document
                            firestore.collection("PARKING").document(user.getSlotNumber())
                                    .update("booked", false,
                                            "transactionId", "",
                                            "userId", "")
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task task) {
                                            if(task.isSuccessful()){

                                                //Update User document
                                                user.setSlotNumber("");
                                                user.setWallet(user.getWallet() - history.getAmount());
                                                user.setTransactionId("");
                                                firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                                                        .update("slotNumber", user.getSlotNumber(),
                                                                "wallet", user.getWallet(),
                                                                "transactionId", user.getTransactionId())
                                                        .addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task task) {
                                                                if(task.isSuccessful()){

                                                                    Toast.makeText(MainActivity.this,
                                                                            "Thank you!! Visit again " + user.getName() + "!! :)",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    Log.d("Successfully Checkout", "Please visit again" + user.getName());

                                                                    selectorFragment = new BookingFragment();
                                                                    getSupportFragmentManager().beginTransaction()
                                                                            .replace(R.id.fragment_container, selectorFragment)
                                                                            .commit();

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}