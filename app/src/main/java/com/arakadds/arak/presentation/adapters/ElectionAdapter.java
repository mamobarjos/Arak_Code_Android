package com.arakadds.arak.presentation.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.elected.ElectedPerson;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

public class ElectionAdapter extends RecyclerView.Adapter<ElectionAdapter.ElectionHolder> {

    private ArrayList<ElectedPerson> electionPeopleDataArrayList;
    private ElectionClickEventCallBack electionClickEventCallBack;
    private Activity activity;

    public ElectionAdapter(ArrayList<ElectedPerson> electionPeopleDataArrayList, ElectionClickEventCallBack electionClickEventCallBack, Activity activity) {
        this.electionPeopleDataArrayList = electionPeopleDataArrayList;
        this.electionClickEventCallBack = electionClickEventCallBack;
        this.activity = activity;
    }

    public interface ElectionClickEventCallBack {
        void onElectionSelectedCallBack(ElectedPerson electionPeopleData,int productId);
    }

    @NonNull
    @Override
    public ElectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_products_stories, parent, false);
        return new ElectionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ElectionHolder viewHolder, int position) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_holder_virtical)
                .error(R.drawable.image_holder_virtical);

        Glide.with(activity).load(electionPeopleDataArrayList.get(position).getImg())
                .apply(options).into(viewHolder.productStoryImageView);

        viewHolder.productStoryImageView.setOnClickListener(view -> electionClickEventCallBack.onElectionSelectedCallBack(electionPeopleDataArrayList.get(position),position));

        viewHolder.productStoryTitleTextView.setText(electionPeopleDataArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (electionPeopleDataArrayList != null) {
            return electionPeopleDataArrayList.size();
        }
        return 0;
    }

    public class ElectionHolder extends RecyclerView.ViewHolder {

        ImageView productStoryImageView;
        TextView productStoryTitleTextView;

        public ElectionHolder(View itemView) {
            super(itemView);
            productStoryImageView = itemView.findViewById(R.id.product_story_imageView_id);
            productStoryTitleTextView = itemView.findViewById(R.id.product_story_title_textView_id);
        }
    }
}