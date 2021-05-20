package com.abhishek.parkingsystemapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.MainActivity;
import com.abhishek.parkingsystemapp.Models.AppUser;
import com.abhishek.parkingsystemapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    TextView tvName, tvEmail, tvPhone, tvDlNumber, tvLicense;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    AppUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvusername);
        tvEmail = view.findViewById(R.id.tvemail);
        tvPhone = view.findViewById(R.id.tvphoneNumber);
        tvDlNumber = view.findViewById(R.id.tvdriverLicense);
        tvLicense = view.findViewById(R.id.tvlicensePlate);

        getUser();

        return view;
    }

    private void getUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){

                            user = task.getResult().toObject(AppUser.class);
                            if(user != null) {
                                tvName.setText(user.getName());
                                tvEmail.setText(user.getEmail());
                                tvPhone.setText(user.getPhone());
                                tvDlNumber.setText(user.getDlNumber());
                                tvLicense.setText(user.getLicense());
                            }
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
}