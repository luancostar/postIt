package com.example.meuapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {
    
    private final Context context;
    private final List<String> imageUris;
    
    public ImageGalleryAdapter(Context context, List<String> imageUris) {
        this.context = context;
        this.imageUris = imageUris;
    }
    
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new ImageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUri = imageUris.get(position);
        
        Glide.with(context)
                .load(Uri.parse(imageUri))
                .fitCenter()
                .into(holder.photoView);
    }
    
    @Override
    public int getItemCount() {
        return imageUris.size();
    }
    
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;
        
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}