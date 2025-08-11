package com.example.meuapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Usaremos Glide para carregar imagens
import java.util.List;

public class AnexoAdapter extends RecyclerView.Adapter<AnexoAdapter.AnexoViewHolder> {

    private final List<String> uris;
    private final Context context;
    private final OnAnexoClickListener listener;

    public interface OnAnexoClickListener {
        void onAnexoClick(String uri);
    }

    public AnexoAdapter(Context context, List<String> uris, OnAnexoClickListener listener) {
        this.context = context;
        this.uris = uris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnexoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_anexo, parent, false);
        return new AnexoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnexoViewHolder holder, int position) {
        String uriString = uris.get(position);
        Glide.with(context)
                .load(Uri.parse(uriString))
                .into(holder.imageViewAnexo);

        holder.itemView.setOnClickListener(v -> listener.onAnexoClick(uriString));
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    static class AnexoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAnexo;
        public AnexoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAnexo = itemView.findViewById(R.id.imageViewAnexo);
        }
    }
}