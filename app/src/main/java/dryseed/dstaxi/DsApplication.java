package dryseed.dstaxi;

import android.app.Application;

/**
 * Created by caiminming on 2017/11/2.
 */

public class DsApplication extends Application {
    private static DsApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static DsApplication getInstance() {
        return instance;
    }
}
