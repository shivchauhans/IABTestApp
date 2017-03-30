package incube8.iabtestapp.view.adapter;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import incube8.iabtestapp.R;
import incube8.iabtestapp.model.MemberDetailsBean;


/**
 * Created by shiv on 29/3/17.
 */
public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder> {
    private Context context;
    ArrayList<MemberDetailsBean> memberDetailsBeanArrayList;

    public MembersListAdapter(Context context, ArrayList<MemberDetailsBean> memberDetailsBeanArrayList) {
        this.context = context;
        this.memberDetailsBeanArrayList = memberDetailsBeanArrayList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         final int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_list_item, parent, false);
        // create ViewHolder

        final ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }
    //Binds the data to views
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.tv_name.setTag(position);
        viewHolder.tv_name.setText(memberDetailsBeanArrayList.get(position).getName());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tv_name = (TextView) itemLayoutView.findViewById(R.id.tv_name);
        }
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return memberDetailsBeanArrayList.size();
    }
}