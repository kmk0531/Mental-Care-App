package com.example.maps;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private final List<Hospital> hospitalList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Hospital hospital);
    }

    public HospitalAdapter(List<Hospital> hospitalList, OnItemClickListener listener) {
        this.hospitalList = hospitalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hospital_card, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital h = hospitalList.get(position);
        holder.name.setText(h.name);
        holder.rating.setText("⭐ 평점: " + h.rating);
        holder.phone.setText("📞 " + h.phone);
        holder.address.setText("📍 " + h.address);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(h));
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, phone, address;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hospital_name);
            rating = itemView.findViewById(R.id.hospital_rating);
            phone = itemView.findViewById(R.id.hospital_phone);
            address = itemView.findViewById(R.id.hospital_address);
        }
    }
}
