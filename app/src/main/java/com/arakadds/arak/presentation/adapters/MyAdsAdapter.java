package com.arakadds.arak.presentation.adapters;

import static com.arakadds.arak.utils.AppEnums.AdsStatus.APPROVED;
import static com.arakadds.arak.utils.AppEnums.AdsStatus.COMPLETED;
import static com.arakadds.arak.utils.AppEnums.AdsStatus.DECLINED;
import static com.arakadds.arak.utils.AppEnums.AdsStatus.PENDING;

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
import com.arakadds.arak.common.helper.TimeShow;
import com.arakadds.arak.common.preferaence.IPreferenceHelper;
import com.arakadds.arak.common.preferaence.PreferenceHelper;
import com.arakadds.arak.model.new_mapping_refactore.home.AdsData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.MyADSHolder> implements Serializable {

    private static final String TAG = "HomeAddsAdapter";
    private ArrayList<AdsData> adsDataArrayList;
    private Context context;
    private String language;
    private Resources resources;
    private IPreferenceHelper preferenceHelper;
    private AdsCallBacks adsCallBacks;
    private int currentPosition;
    TimeShow timeShow = new TimeShow();

    public MyAdsAdapter(ArrayList<AdsData> adsDataArrayList, Context context, String language, Resources resources,IPreferenceHelper preferenceHelper, AdsCallBacks adsCallBacks) {
        this.adsDataArrayList = adsDataArrayList;
        this.context = context;
        this.language = language;
        this.resources = resources;
        this.preferenceHelper = preferenceHelper;
        this.adsCallBacks = adsCallBacks;
    }

    public interface AdsCallBacks {
        void onNextPageRequired();

        void onAdSelected(AdsData adsData);
    }

    @NonNull
    @Override
    public MyADSHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ads_item_row, parent, false);

        return new MyADSHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyADSHolder holder, @SuppressLint("RecyclerView") int position) {
        switch (adsDataArrayList.get(position).getAdCategoryId()) {
            case 1:
                if (language.equals("ar")) {
                    holder.adTitleTextView.setText("صورة");
                    holder.adTypeBodyTextView.setText("اعلان صورة");
                } else {
                    holder.adTitleTextView.setText("Image");
                    holder.adTypeBodyTextView.setText("Image ad");
                    holder.adImagesNumberBodyTextView.setText(String.valueOf(adsDataArrayList.get(position).getAdPackage().getNumberOfImages()));
                }
                holder.adTypeImageView.setImageResource(R.drawable.my_ads_image_type_icon);
                break;
            case 2:
                if (language.equals("ar")) {
                    holder.adTitleTextView.setText("فيديو");
                    holder.adTypeBodyTextView.setText("اعلان فيديو");
                } else {
                    holder.adTitleTextView.setText("Video");
                    holder.adTypeBodyTextView.setText("Video ad");
                }
                holder.adTypeImageView.setImageResource(R.drawable.my_ads_video_type_icon);
                break;
            case 3:
                if (language.equals("ar")) {
                    holder.adTitleTextView.setText("ويب");
                    holder.adTypeBodyTextView.setText("اعلان ويب");
                } else {
                    holder.adTitleTextView.setText("Website");
                    holder.adTypeBodyTextView.setText("Website ad");
                    //holder.adImagesNumberBodyTextView.setText(currentPageDataArrayList.get(position).getAdsPackagesData().getPackageNumberOfImages());
                }
                holder.adTypeImageView.setImageResource(R.drawable.website_new_icon);
                break;
            case 4:
                if (language.equals("ar")) {
                    holder.adTitleTextView.setText("متجر");
                    holder.adTypeBodyTextView.setText("اعلان متجر");
                } else {
                    holder.adTitleTextView.setText("Store");
                    holder.adTypeBodyTextView.setText("Store ad");
                    //holder.adImagesNumberBodyTextView.setText(currentPageDataArrayList.get(position).getAdsPackagesData().getPackageNumberOfImages());
                }
                holder.adTypeImageView.setImageResource(R.drawable.store_ad_icon);
                break;
        }
        holder.adCratedDateTextView.setText(timeShow.parseDate(adsDataArrayList.get(position).getCreatedAt()));
        if (Objects.equals(adsDataArrayList.get(position).getStatus(), PENDING)) {
            holder.adStatusDotTextView.setTextColor(context.getResources().getColor(R.color.dark_blue));
            holder.adStatusTextView.setText(adsDataArrayList.get(position).getStatus());
           /* if (language.equals("ar")) {
                holder.adStatusTextView.setText("قيد المراجعة");
            } else {
                holder.adStatusTextView.setText("pending");
            }*/

        } else if (Objects.equals(adsDataArrayList.get(position).getStatus(), APPROVED)) {
            holder.adStatusDotTextView.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.adStatusTextView.setText(adsDataArrayList.get(position).getStatus());
            /*if (language.equals("ar")) {
                holder.adStatusTextView.setText("فعال");
            } else {
                holder.adStatusTextView.setText("approved");
            }*/
        } else if (Objects.equals(adsDataArrayList.get(position).getStatus(), COMPLETED)) {
            holder.adStatusDotTextView.setTextColor(context.getResources().getColor(R.color.dark_green));
            holder.adStatusTextView.setText(adsDataArrayList.get(position).getStatus());
            /*if (language.equals("ar")) {
                holder.adStatusTextView.setText("مكتمل");
            } else {
                holder.adStatusTextView.setText("completed");
            }*/
        } else if (Objects.equals(adsDataArrayList.get(position).getStatus(), DECLINED)) {
            holder.adStatusDotTextView.setTextColor(context.getResources().getColor(R.color.red_100));
            holder.adStatusTextView.setText(adsDataArrayList.get(position).getStatus());
           /* if (language.equals("ar")) {
                holder.adStatusTextView.setText("ملغي");
            } else {
                holder.adStatusTextView.setText("declined");
            }*/
        }

        holder.adTypeTitleTextView.setText(resources.getString(R.string.my_ads_activity_Ads_Type));
        holder.adReachTitleTextView.setText(resources.getString(R.string.my_ads_activity_Reach));
        holder.adImagesNumberTitleTextView.setText(resources.getString(R.string.my_ads_activity_Number));
        holder.adTimeTitleTextView.setText(resources.getString(R.string.my_ads_activity_Time));
        holder.adPriceTitleTextView.setText(resources.getString(R.string.my_ads_activity_Price));
        //----
        holder.adReachBodyTextView.setText(String.valueOf(adsDataArrayList.get(position).getAdPackage().getNumberOfImages()));
        holder.adUserNumberViewsBodyTextView.setText(String.valueOf(adsDataArrayList.get(position).getViews()));
        holder.adTimeBodyTextView.setText(String.valueOf(adsDataArrayList.get(position).getDuration()));
        holder.adPriceBodyTextView.setText(String.valueOf(adsDataArrayList.get(position).getAdPackage().getPrice()) + preferenceHelper.getCurrencySymbol());


        holder.packageTitleConstraintLayout.setOnClickListener(v -> {
            adsCallBacks.onAdSelected(adsDataArrayList.get(position));
        });

       /* if (currentPosition == position) {
            holder.packageDetailsConstraintLayout.setVisibility(View.VISIBLE);
        } else {
            holder.packageDetailsConstraintLayout.setVisibility(View.GONE);
        }*/


        if (adsDataArrayList.size() - 1 == position) {
            adsCallBacks.onNextPageRequired();
        }
    }

    @Override
    public int getItemCount() {
        return adsDataArrayList.size();
    }

    class MyADSHolder extends RecyclerView.ViewHolder {
        private TextView adCratedDateTextView, adTitleTextView, adStatusTextView, adStatusDotTextView;
        private TextView adTypeTitleTextView, adReachTitleTextView, adImagesNumberTitleTextView, adTimeTitleTextView, adPriceTitleTextView;
        private TextView adTypeBodyTextView, adReachBodyTextView, adUserNumberViewsBodyTextView, adImagesNumberBodyTextView, adTimeBodyTextView, adPriceBodyTextView;
        private ImageView adTypeImageView;
        private ConstraintLayout packageDetailsConstraintLayout, packageTitleConstraintLayout;
        //private ConstraintLayout adsConstraintLayout;

        public MyADSHolder(@NonNull View itemView) {
            super(itemView);
            adTitleTextView = itemView.findViewById(R.id.my_ads_ad_name_textView_id);
            adCratedDateTextView = itemView.findViewById(R.id.my_ads_ad_date_textView_id);
            adStatusTextView = itemView.findViewById(R.id.my_ads_ad_status_textView_id);
            adStatusDotTextView = itemView.findViewById(R.id.my_ads_ad_status_dot_textView_id);
            adTypeImageView = itemView.findViewById(R.id.my_ads_ad_type_icon_imageView_id);
            //------------
            adTypeTitleTextView = itemView.findViewById(R.id.my_ads_ad_type_title_textView_id);
            adTypeBodyTextView = itemView.findViewById(R.id.my_ads_ad_type_body_textView_id);
            adReachTitleTextView = itemView.findViewById(R.id.my_ads_ad_reach_title_textView_id);
            adReachBodyTextView = itemView.findViewById(R.id.my_ads_ad_reach_body_textView_id);
            adImagesNumberTitleTextView = itemView.findViewById(R.id.my_ads_ad_number_title_textView_id);
            adImagesNumberBodyTextView = itemView.findViewById(R.id.my_ads_ad_number_body_textView_id);
            adTimeTitleTextView = itemView.findViewById(R.id.my_ads_ad_time_title_textView_id);
            adTimeBodyTextView = itemView.findViewById(R.id.my_ads_ad_time_body_textView_id);
            adPriceTitleTextView = itemView.findViewById(R.id.my_ads_ad_price_title_textView_id);
            adPriceBodyTextView = itemView.findViewById(R.id.my_ads_ad_price_body_textView_id);
            adUserNumberViewsBodyTextView = itemView.findViewById(R.id.my_ads_ad_current_reach_body_textView_id);
            packageDetailsConstraintLayout = itemView.findViewById(R.id.my_ads_ad_details_constraintLayout_id);
            packageTitleConstraintLayout = itemView.findViewById(R.id.my_ads_ConstraintLayout_id);
        }
    }

}
