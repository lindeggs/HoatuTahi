package ch.linst.hoatutahi.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ch.linst.hoatutahi.HoatuTahi;
import ch.linst.hoatutahi.components.PlatformService;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlatformService platformService = new AndroidPlatformService(getContext());

        // Initialize HoatuTahi game view
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();

        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new HoatuTahi(platformService), configuration);
    }
}
