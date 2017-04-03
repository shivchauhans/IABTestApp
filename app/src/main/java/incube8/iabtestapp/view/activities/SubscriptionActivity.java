package incube8.iabtestapp.view.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import incube8.iabtestapp.R;
import incube8.iabtestapp.model.MemberDetailsBean;
import incube8.iabtestapp.sqlitehelper.SqliteMemberDetails;
import incube8.iabtestapp.util.CommonUtils;
import incube8.iabtestapp.util.Constants;
import incube8.iabtestapp.util.IabBroadcastReceiver;
import incube8.iabtestapp.util.IabHelper;
import incube8.iabtestapp.util.IabResult;
import incube8.iabtestapp.util.Inventory;
import incube8.iabtestapp.util.Purchase;

/**
 * Created by shiv on 28/3/17.
 */
public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener {

    private TextView mTvLoggedInUserName, mTvLogout;
    private Button mBtOneMonthSub, mBtThreeMonthSub;
    private CircleImageView mCivUserPic;
    private RelativeLayout mLayParent;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    SqliteMemberDetails sqliteMemberDetails;

    private Context mContext;
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);
        mContext = this;
        init();
    }


    /**
     * This method initialize the views and Sqlite helper class
     */
    private void init() {
        sqliteMemberDetails = new SqliteMemberDetails(mContext);
        mTvLoggedInUserName = (TextView) findViewById(R.id.tv_login_user_name);
        mTvLogout = (TextView) findViewById(R.id.tv_logout);
        mBtOneMonthSub = (Button) findViewById(R.id.bt_sub_one);
        mBtThreeMonthSub = (Button) findViewById(R.id.bt_sub_three);
        mLayParent = (RelativeLayout) findViewById(R.id.lay_parent);
        mCivUserPic = (CircleImageView) findViewById(R.id.iv_user_pic);
        setListeners();
        getDataAndUpdateUi();
        insertDummyMembersToSqlite();
        if (CommonUtils.isNetworkAvailable(mContext))
            initSubscriptions();
        else
            CommonUtils.showSnackBar(mLayParent, Constants.NO_INTERNET_MESSAGE);
    }

    /**
     * This method fetch the data as bundle from intent and update the corresponding UI
     */
    private void getDataAndUpdateUi() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("user_info");
        mTvLoggedInUserName.setText("Welcome " + bundle.getString("name"));
        Picasso.with(this).load(bundle.getString("img_url")).into(mCivUserPic);
    }


    /**
     * This method used to set click listeners on necessary views to perform click events on them
     */
    private void setListeners() {
        mBtThreeMonthSub.setOnClickListener(this);
        mBtOneMonthSub.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sub_one:
                onClickPurchaseSubs(Constants.SKU_SUB_ONE_MONTH);
                break;
            case R.id.bt_sub_three:
                onClickPurchaseSubs(Constants.SKU_SUB_THREE_MONTH);
                break;
            case R.id.tv_logout:
                fbLogout();
                break;
        }
    }


    /**
     * This method used to destroy the user session and logout user from facebook
     */
    private void fbLogout() {
        if (CommonUtils.isNetworkAvailable(mContext))
            LoginManager.getInstance().logOut();
        else
            CommonUtils.showSnackBar(mLayParent, Constants.NO_INTERNET_MESSAGE);
        finish();
    }


    /**
     * This method runs if there are no members stored inside databse and insert some dummy members using loop  in Sqlite databse
     */
    private void insertDummyMembersToSqlite() {
        if (getSize() == 0)
            for (int i = 1; i < 12; i++) {
                String name = "Dummy Member ".concat(String.valueOf(i));
                String email = "dummyemail".concat(String.valueOf(i).concat("@gmail.com"));
                sqliteMemberDetails.insertDetails(name, email);
            }

    }

    /**
     * This method checks the device database and returns the number of members stored
     */
    private int getSize() {
        ArrayList<MemberDetailsBean> arrayList = sqliteMemberDetails.getMemberDetails();
        return arrayList.size();
    }


   /**
     * This method initiates IAB helper classes and query inventory to check whether you have already subscribed 
     */
    private void initSubscriptions() {

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, Constants.BASE64ENCODEDSTRING);
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                CommonUtils.showSnackBar(mLayParent, Constants.IAB_SETUP_SUCESS);

                if (!result.isSuccess()) {
                    // Oops, there was a problem.
                    CommonUtils.showSnackBar(mLayParent, Constants.IAB_SETUP_ERROR + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(SubscriptionActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    CommonUtils.showSnackBar(mLayParent, Constants.IAB_INVENTORY_ERROR);
                }
            }
        });


    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;
            // Is it a failure?
            if (result.isFailure()) {
                CommonUtils.showSnackBar(mLayParent, Constants.IAB_INVENTORY_ERROR + result);
                return;
            }

        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            CommonUtils.showSnackBar(mLayParent, Constants.IAB_INVENTORY_ERROR);
        }
    }

    /**
     * This method initiates the purchase flow for selected subscription type
     *
     * @param sku A variable of type String represents product id (called as sku by google IAB terms)
     */
    private void onClickPurchaseSubs(String sku) {
        String payload = "";
        List<String> oldSkus = null;
        oldSkus = new ArrayList();
        CommonUtils.showSnackBar(mLayParent, Constants.IAB_PURCHASE_SUCESS);
        try {
            mHelper.launchPurchaseFlow(this, sku, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            CommonUtils.showSnackBar(mLayParent, Constants.IAB_INVENTORY_ERROR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("OAR", "onActivityResult handled by IABUtil.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                CommonUtils.showSnackBar(mLayParent, "Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                CommonUtils.showSnackBar(mLayParent, Constants.IAB_ERROR_PURCHASE);
                return;
            }

            CommonUtils.showSnackBar(mLayParent, Constants.IAB_COMPLETE);
            //navigates to member's list activity after sucessfully purchasing subscription

            startActivity(new Intent(mContext, MembersListActivity.class));

        }
    };

    // We're being destroyed. It's important to dispose of the helper and receiver here!
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }


}
