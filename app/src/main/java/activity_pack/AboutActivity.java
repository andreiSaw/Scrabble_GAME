package activity_pack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.tools.R;

public class AboutActivity extends AppCompatActivity {

    /**
     * Indicates whether the specified app ins installed and can used as an intent. This
     * method checks the package manager for installed packages that can
     * respond to an intent with the specified app. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param appName The name of the package you want to check
     * @return True if app is installed
     */
    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void instagramOnCLickListner(View view) {
        final String appName = "com.instagram.android";
        final boolean isAppInstalled = isAppAvailable(AboutActivity.this.getApplicationContext(), appName);
        if (isAppInstalled) {
            Intent inst = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/_u/sky_tmb"));
            startActivity(inst);
        } else {
            Toast.makeText(AboutActivity.this, "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void telegramOnCLickListner(View view) {
        final String appName = "org.telegram.messenger";
        final boolean isAppInstalled = isAppAvailable(AboutActivity.this.getApplicationContext(), appName);
        if (isAppInstalled) {
            Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/sky_tmb"));
            startActivity(telegram);
        } else {
            Toast.makeText(AboutActivity.this, "Telegram not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void githubOnCLickListner(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.github.com/andreisaw"));
        startActivity(browserIntent);
    }

    public void hseOnCLickListner(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hse.ru"));
        startActivity(browserIntent);
    }
}