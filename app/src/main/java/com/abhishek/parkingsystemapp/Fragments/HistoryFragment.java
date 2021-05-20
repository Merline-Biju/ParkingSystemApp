package com.abhishek.parkingsystemapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abhishek.parkingsystemapp.Adapters.HistoryAdapter;
import com.abhishek.parkingsystemapp.Models.UserHistory;
import com.abhishek.parkingsystemapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    RecyclerView rvHistory;
    HistoryAdapter historyAdapter;
    ProgressBar progressBar;

    List<UserHistory> historyList;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        rvHistory.setHasFixedSize(true);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        getHistoryList();

        return view;
    }

    private void getHistoryList() {

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid())
                .collection("HISTORY")
                .orderBy("arrival", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){

                            historyList = new ArrayList<>();
                            historyList = task.getResult().toObjects(UserHistory.class);
                            if(historyList.size() > 0){
                                historyAdapter = new HistoryAdapter(getActivity(), historyList);
                                rvHistory.setAdapter(historyAdapter);
                                historyAdapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(getActivity(), "You haven't made a transaction with us yet!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}