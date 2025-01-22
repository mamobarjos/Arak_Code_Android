package com.arakadds.arak.presentation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arakadds.arak.R;
import com.arakadds.arak.common.preferaence.IPreferenceHelper;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.auth.UserObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingHolder> {

    private ArrayList<UserObject> userObjectDataArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private IPreferenceHelper preferenceHelper;
    private RankingCallBacks rankingCallBacks;

    public RankingAdapter(ArrayList<UserObject> userObjectDataArrayList, Context context, Resources resources, String language,IPreferenceHelper preferenceHelper,RankingCallBacks rankingCallBacks) {
        this.userObjectDataArrayList = userObjectDataArrayList;
        this.context = context;
        this.resources = resources;
        this.language = language;
        this.preferenceHelper = preferenceHelper;
        this.rankingCallBacks = rankingCallBacks;
    }
    public interface RankingCallBacks {
        void onNextPageRequired();
    }
    @NonNull
    @Override
    public RankingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_ranking, parent, false);

        return new RankingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingHolder holder, int position) {
        holder.userNameTextView.setText(userObjectDataArrayList.get(position).getFullname());
        holder.userCountryTextView.setText(userObjectDataArrayList.get(position).getCountryId());
        holder.rankLevelTextView.setText(position+1+"\n"+resources.getString(R.string.Ranking_activity_Ranking));
        holder.imagesNumberTextView.setText(userObjectDataArrayList.get(position).getAdsImgsViews()+"\n"+resources.getString(R.string.Ranking_activity_Arak_Image));
        holder.videosNumberTextView.setText(userObjectDataArrayList.get(position).getAdsVideosViews()+"\n"+resources.getString(R.string.Ranking_activity_Arak_Video));
        holder.websiteNumberTextView.setText(userObjectDataArrayList.get(position).getAdsWebsiteViews()+"\n"+resources.getString(R.string.Ranking_activity_Arak_Website));
        holder.userBalanceTextView.setText(preferenceHelper.getCurrencySymbol()
                +""+String.valueOf(userObjectDataArrayList.get(position).getBalance())
                +"\n"+resources.getString(R.string.Ranking_activity_Arak_Total));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder);
        Glide.with(context).load(userObjectDataArrayList.get(position).getImgAvatar()).apply(options).into(holder.userImageView);

        if (userObjectDataArrayList.size() - 1 == position) {
            rankingCallBacks.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        return userObjectDataArrayList.size();
    }

    static class RankingHolder extends RecyclerView.ViewHolder {
        private TextView userNameTextView, userCountryTextView;
        private TextView rankLevelTextView, videosNumberTextView, imagesNumberTextView, websiteNumberTextView, userBalanceTextView;
        private ImageView userImageView;

        public RankingHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.ranking_user_image_circleImageView_id);
            userNameTextView = itemView.findViewById(R.id.ranking_userName_textView_id);
            userCountryTextView = itemView.findViewById(R.id.ranking_user_country_textView_id);
            rankLevelTextView = itemView.findViewById(R.id.ranking_user_rank_level_textView_id);
            videosNumberTextView = itemView.findViewById(R.id.ranking_videos_views_textView_id);
            imagesNumberTextView = itemView.findViewById(R.id.ranking_images_views_textView_id);
            websiteNumberTextView = itemView.findViewById(R.id.ranking_website_views_textView_id);
            userBalanceTextView = itemView.findViewById(R.id.ranking_total_revenue_textView_id);

        }
    }
}
