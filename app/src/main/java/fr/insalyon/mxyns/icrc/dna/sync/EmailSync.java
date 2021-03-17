package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

import fr.insalyon.mxyns.icrc.dna.MainActivity;
import fr.insalyon.mxyns.icrc.dna.R;

/**
 * Synchronizer that uses emails to send the cases data
 */
public class EmailSync extends Sync {

    /**
     * Target email to which email will be sent
     */
    private final String target_address;

    public EmailSync(String target_address) {

        this.target_address = target_address;
        this.showDialog = false;
    }

    @Override
    public boolean send(Context context, String filePath) {

        Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
        emailSelectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{ target_address });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, makeSubject(context.getResources(), filePath));
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        emailIntent.setSelector( emailSelectorIntent );

        Uri attachment = FileProvider.getUriForFile(context, MainActivity.class.getPackage().getName() + ".fileprovider", new File(filePath));
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment);

        if( emailIntent.resolveActivity(context.getPackageManager()) != null )
            context.startActivity(emailIntent);

        return true;
    }

    public String makeSubject(Resources resources, String filePath) {

        return resources.getString(R.string.app_name) + " - " + new File(filePath).getName();
    }
}
