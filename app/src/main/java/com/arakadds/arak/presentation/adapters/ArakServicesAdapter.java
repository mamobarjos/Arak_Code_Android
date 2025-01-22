package com.arakadds.arak.presentation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.arak_services.ServiceDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ArakServicesAdapter extends RecyclerView.Adapter<ArakServicesAdapter.ArakServicesHolder> {

    private static final String TAG = "ArakServicesAdapter";
    private ArrayList<ServiceDetails> serviceDetailsArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private RequestArakServiceCallBack requestArakServiceCallBack;

    public interface RequestArakServiceCallBack {
        void onArakServiceSelected(ServiceDetails serviceDetails, int position);
    }

    public ArakServicesAdapter(ArrayList<ServiceDetails> serviceDetailsArrayList, Context context, String language, Resources resources, RequestArakServiceCallBack requestArakServiceCallBack) {
        this.serviceDetailsArrayList = serviceDetailsArrayList;
        this.context = context;
        this.language = language;
        this.resources = resources;
        this.requestArakServiceCallBack = requestArakServiceCallBack;
    }

    @NonNull
    @Override
    public ArakServicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.arak_services_row_item, parent, false);

        return new ArakServicesAdapter.ArakServicesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArakServicesHolder holder, @SuppressLint("RecyclerView") int position) {
        if (language.equals("ar")) {
            holder.serviceTitleTextView.setText(serviceDetailsArrayList.get(position).getTitleAr());
            holder.serviceDescTextView.setText(serviceDetailsArrayList.get(position).getLongDescAr());
        } else {
            holder.serviceTitleTextView.setText(serviceDetailsArrayList.get(position).getTitleEn());
            holder.serviceDescTextView.setText(serviceDetailsArrayList.get(position).getLongDescAr());
        }

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_placeholder);
        Glide.with(context).load(serviceDetailsArrayList.get(position).getImgUrl()).apply(options).into(holder.serviceImageView);
        holder.constraintLayout.setOnClickListener(v -> {
            requestArakServiceCallBack.onArakServiceSelected(serviceDetailsArrayList.get(position), position);

        });
    }
    @Override
    public int getItemCount() {
        return serviceDetailsArrayList.size();
    }
    class ArakServicesHolder extends RecyclerView.ViewHolder {
        private TextView serviceTitleTextView, serviceDescTextView;
        private ImageView serviceImageView;
        private ConstraintLayout constraintLayout;

        public ArakServicesHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleTextView = itemView.findViewById(R.id.arak_services_service_title_textView_id);
            serviceDescTextView = itemView.findViewById(R.id.arak_services_service_description_textView_id);
            serviceImageView = itemView.findViewById(R.id.arak_services_service_image_imageView_id);
            constraintLayout = itemView.findViewById(R.id.arak_services_item_row_ConstraintLayout_id);
        }
    }
}
