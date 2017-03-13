package dev.nohasmith.venya_android_app;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import static dev.nohasmith.venya_android_app.MainActivity.appLanguage;
import static dev.nohasmith.venya_android_app.MainActivity.locale_from_language;

/**
 * Created by arturo on 12/03/2017.
 */

public class MyContextWrapper extends ContextWrapper {
    public MyContextWrapper(Context base){
        super(base);
    }

    @SuppressWarnings("deprecation")
    public static ContextWrapper wrapContext(Context context, String language) {
        Configuration config = context.getResources().getConfiguration();
        Locale sysLocale = null;

        if ( language == null || language.equals("") ) {
            language = appLanguage;
        }

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }

        if ( ! sysLocale.equals(locale_from_language.get(language)) ) {
            Locale newLocale = new Locale(locale_from_language.get(language));
            Locale.setDefault(newLocale);

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                config.setLocale(newLocale);
            } else {
                config.locale = newLocale;
            }

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
                context = context.createConfigurationContext(config);
            } else {
                context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
            }
        }

        return new ContextWrapper(context);
    }
}
