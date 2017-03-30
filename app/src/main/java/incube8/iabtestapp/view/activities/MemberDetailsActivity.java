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
import android.widget.TextView;

import incube8.iabtestapp.R;

/**
 * Created by shiv on 29/3/17.
 */
public class MemberDetailsActivity extends AppCompatActivity {

    private Context mContext;
    private TextView mTvName, mTvEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);
        mContext = this;
        init();
    }

    /**
     * This method initialize the views
     */
    private void init() {
        mTvName = (TextView) findViewById(R.id.tv_user_name);
        mTvEmail = (TextView) findViewById(R.id.tv_user_email);
        getDataAndUpdateUi();
    }

    /**
     * This method fetch the data as bundle from intent and update the corresponding UI and generates a styled local notification
     */
    private void getDataAndUpdateUi() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("user_info");
        String name = bundle.getString("name");
        String email = bundle.getString("email");
        String from = bundle.getString("from");
        mTvName.setText(name);
        mTvEmail.setText(email);
        if (from.equalsIgnoreCase("memberlisting"))
            showLocalNotification("you have viewed the profile of ".concat(name).concat(".The viewed member has been get a notification regards this."), name, email);
    }


    /**
     * This function generates notification in big style expandable view to notify who has viewed your profile
     *
     * @param message A variable of type String holding the message to show in local generated notifications
     * @param name    A variable of type String holding the name of viewed member
     * @param email   A variable of type String holding the email address of viewed member
     */
    private void showLocalNotification(String message, String name, String email) {
        Intent intent = new Intent(mContext, MemberDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("from", "notification");
        intent.putExtra("user_info", bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getString(R.string.app_name)).setStyle
                        (new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message).setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true).setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
