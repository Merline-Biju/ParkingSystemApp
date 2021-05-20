package com.abhishek.parkingsystemapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.Dialogs.WalletDialog;
import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;


public class WalletFragment extends Fragment {

    EditText etEnterAmount;
    TextView tvAmount;
    Button btnAddAmount, btnAdd50, btnAdd100, btnAdd500;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    AppUser user;
    double amount = 0.0;

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        etEnterAmount = view.findViewById(R.id.etEnterAmount);
        tvAmount = view.findViewById(R.id.tvAmount);
        btnAddAmount = view.findViewById(R.id.btnAddAmount);
        btnAdd50 = view.findViewById(R.id.btnAdd50);
        btnAdd100 = view.findViewById(R.id.btnAdd100);
        btnAdd500 = view.findViewById(R.id.btnAdd500);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        getUser();

        btnAddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etEnterAmount.getText().toString().isEmpty()){
                    amount = Integer.parseInt(etEnterAmount.getText().toString().trim());
                    openDialog(amount);
                }
                else{
                    Toast.makeText(getActivity(), "Input a number!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAdd50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(50);
            }
        });

        btnAdd100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(100);
            }
        });

        btnAdd500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(500);
            }
        });

        return view;
    }

    private void openDialog(double amount) {

        progressBar.setVisibility(View.VISIBLE);
        WalletDialog walletDialog = new WalletDialog(amount, user, progressBar);
        walletDialog.show(getActivity().getSupportFragmentManager(), "Wallet Dialog");

    }

    private void getUser() {

        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            user = task.getResult().toObject(AppUser.class);
                            tvAmount.setText("₹".concat(String.format(Locale.US,"%.2f", user.getWallet())));
                        }
                        else {
                            Toast.makeText(getActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            startActivity(new Intent(getActivity(), com.abhishek.parkingsystemapp.StartActivity.class));
                            getActivity().finish();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}