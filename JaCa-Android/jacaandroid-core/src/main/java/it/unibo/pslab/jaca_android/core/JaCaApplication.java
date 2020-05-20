package it.unibo.pslab.jaca_android.core;

import android.app.Application;
import android.content.res.Resources;

import java.io.File;
import java.io.IOException;

public class JaCaApplication extends Application {

    protected static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();

        resources = getResources();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     *
     * @param dir
     * @param fileName
     * @throws IOException
     */
    protected void activateLogOnFileMode(File dir, String fileName) throws IOException{
        Runtime.getRuntime().exec("logcat -c");
        Runtime.getRuntime().exec("logcat -f " + new File(dir, fileName));
    }

    /**
     *
     * @return
     */
    public static Resources getAppResources() {
        return resources;
    }
}
