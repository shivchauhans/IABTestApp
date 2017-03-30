package incube8.iabtestapp.util;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;


/**
 * Created by shiv on 28/3/17.
 */
public class CommonUtils {

    /**
     * Show snackbar view to notify the user for various events
     *
     * @param parent  parent layout
     * @param message message to display
     */
    public static void showSnackBar(View parent, String message) {
        try {
            Snackbar snack = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check if internet is present or not
     *
     * @param activity activity
     */
    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
