package it.unibo.pslab.jaca_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import it.unibo.pslab.jaca_android.core.Utils;

/**
 * Utility class for launching a JaCa-Android app. 
 * It's the entry point (main) of every Jaca-Android application.
 */
public class LauncherActivity extends Activity {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ApplicationInfo appInfo;

        try {
            appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(ErrorMessage.MISSING_META_DATA);
        }

        startMasService(appInfo.metaData.getString(Utils.MAS2J, null));
    }

    private void startMasService(final String mas2j){
        if (mas2j != null ){
            Intent service = new Intent(getApplicationContext(), MasService.class);
            service.putExtra(Utils.MAS2J, mas2j);
            startService(service);
        } else {
            throw new RuntimeException(ErrorMessage.MISSING_META_DATA_MAS2J);
        }
	}
}