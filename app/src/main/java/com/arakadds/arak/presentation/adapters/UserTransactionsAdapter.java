package com.arakadds.arak.presentation.adapters;

import static com.arakadds.arak.utils.AppEnums.TransactionType.INCOME;
import static com.arakadds.arak.utils.AppEnums.TransactionType.OUTCOME;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.arakadds.arak.R;
import com.arakadds.arak.model.new_mapping_refactore.response.banners.transactions.TransactionsObject;

import java.util.ArrayList;

public class UserTransactionsAdapter extends RecyclerView.Adapter<UserTransactionsAdapter.UserTransactionsHolder>{
    private static final String TAG = "UserTransactionsAdapter";

    private ArrayList<TransactionsObject> userTransactionsDetailsArrayList;
    private UserTransactionsClickEvents userTransactionsClickEvents;
    private Context context;

    public UserTransactionsAdapter(ArrayList<TransactionsObject> userTransactionsDetailsArrayList, Context context,UserTransactionsClickEvents userTransactionsClickEvents) {
        this.context=context;
        this.userTransactionsClickEvents=userTransactionsClickEvents;
        this.userTransactionsDetailsArrayList=userTransactionsDetailsArrayList;
    }

    public interface UserTransactionsClickEvents {
        void onNextPageRequired();
    }

    @NonNull
    @Override
    public UserTransactionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_wallet_activities, parent, false);
        return new UserTransactionsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTransactionsHolder holder, int position) {
        if (userTransactionsDetailsArrayList.get(position).getType().equals(INCOME)){
            //incoming
            holder.transactionImageView.setImageResource(R.drawable.wallet_up_icon);
            holder.transactionRevenueTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_plus_icon, 0, 0, 0);
            holder.walletConstraintLayout.setBackgroundColor(context.getResources().getColor(R.color.gray_50));
        }else if (userTransactionsDetailsArrayList.get(position).getType().equals(OUTCOME)){
            //outgoing
            holder.transactionImageView.setImageResource(R.drawable.wallet_down_icon);
            holder.transactionRevenueTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_minus_icon, 0, 0, 0);
            holder.walletConstraintLayout.setBackgroundResource(0);
        }
        holder.transactionTitleTextView.setText(userTransactionsDetailsArrayList.get(position).getDescription());
        holder.transactionDateTextView.setText(userTransactionsDetailsArrayList.get(position).getCreatedAt().substring(1,10));
        holder.transactionRevenueTextView.setText(String.valueOf(userTransactionsDetailsArrayList.get(position).getAmount())+" JOD");
        if (userTransactionsDetailsArrayList.size() - 1 == position) {
            userTransactionsClickEvents.onNextPageRequired();
        }

    }

    @Override
    public int getItemCount() {
        if (userTransactionsDetailsArrayList!=null){
            return userTransactionsDetailsArrayList.size();
        }
        return 0;
    }

    class UserTransactionsHolder extends RecyclerView.ViewHolder{

        private ImageView transactionImageView;
        private TextView transactionTitleTextView;
        private TextView transactionDateTextView;
        private TextView transactionRevenueTextView;
        private ConstraintLayout walletConstraintLayout;

        public UserTransactionsHolder(@NonNull View itemView) {
            super(itemView);
            transactionImageView=itemView.findViewById(R.id.wallet_item_up_down_imageView_id);
            transactionTitleTextView=itemView.findViewById(R.id.wallet_item_action_name_textView_id);
            transactionDateTextView=itemView.findViewById(R.id.wallet_item_action_date_textView_id);
            transactionRevenueTextView=itemView.findViewById(R.id.wallet_item_revenue_textView_id);
            walletConstraintLayout=itemView.findViewById(R.id.wallet_item_ConstraintLayout_id);
        }
    }
}
