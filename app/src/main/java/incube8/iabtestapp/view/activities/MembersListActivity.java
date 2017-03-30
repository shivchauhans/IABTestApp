package incube8.iabtestapp.view.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import incube8.iabtestapp.R;
import incube8.iabtestapp.model.MemberDetailsBean;
import incube8.iabtestapp.sqlitehelper.SqliteMemberDetails;
import incube8.iabtestapp.util.ItemClickSupport;
import incube8.iabtestapp.view.adapter.MembersListAdapter;

/**
 * Created by shiv on 29/3/17.
 */
public class MembersListActivity extends AppCompatActivity {
    RecyclerView mRvMembersList;
    private Context mContext;
    SqliteMemberDetails sqliteMemberDetails;
    private ArrayList<MemberDetailsBean> memberDetailsBeanArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        mContext = this;
        init();
    }

    /**
     * This method initialize the views and Sqlite helper class
     */
    private void init() {
        sqliteMemberDetails = new SqliteMemberDetails(mContext);
        mRvMembersList = (RecyclerView) findViewById(R.id.rv_members);
        fetchDatFromSqliteAndsetAdapter();
        onRecyclerClick();
    }

    /**
     * This method fetch the members from database and stores them in an array list ,sets adapter to recycler list view
     */
    private void fetchDatFromSqliteAndsetAdapter() {
        memberDetailsBeanArrayList = sqliteMemberDetails.getMemberDetails();
        mRvMembersList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMembersList.setAdapter(new MembersListAdapter(mContext, sqliteMemberDetails.getMemberDetails()));
    }

    /**
     * This method used to perform click on the single item of recycler list view
     */
    private void onRecyclerClick() {
        ItemClickSupport.addTo(mRvMembersList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                getDatAndNavigateToDetailPage(position);
            }
        });
    }

    /**
     * This method used for storing user information fetched from Sqlite in bundle and then pass it to memberdetails activity via explicit intent
     *
     * @param position A variable of type int holding the position of item clicked in recycler list view
     */
    public void getDatAndNavigateToDetailPage(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", memberDetailsBeanArrayList.get(position).getName());
        bundle.putString("email", memberDetailsBeanArrayList.get(position).getEmail());
        bundle.putString("from", "memberlisting");
        Intent intent = new Intent(mContext, MemberDetailsActivity.class);
        intent.putExtra("user_info", bundle);
        startActivity(intent);

    }
}
