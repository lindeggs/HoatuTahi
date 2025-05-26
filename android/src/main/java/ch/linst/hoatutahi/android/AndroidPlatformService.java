package ch.linst.hoatutahi.android;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import ch.linst.hoatutahi.BuildConfig;
import ch.linst.hoatutahi.components.PlatformService;

public class AndroidPlatformService implements PlatformService {

    private Context context;

    public AndroidPlatformService(Context contextArg) {
        context = contextArg;
    }

    /**
     * Reads the Android app version and returns it as a String
     * @return Android app version
     */
    public String getAppVerison(){

        String appVersion = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appVersion;
    }

    public String getBuildConfigBuildType(){
        return BuildConfig.BUILD_TYPE;
    }
}
