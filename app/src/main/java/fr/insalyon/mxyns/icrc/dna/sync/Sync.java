package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

import static fr.insalyon.mxyns.icrc.dna.MainActivity.syncs;

/**
 * A synchronizer is a way to send data to any case data file to a receiver and then mark the file as sync-ed.
 */
public abstract class Sync {

    public abstract boolean send(Context context, String filePath);

    /**
     * Attempts to send a list of files using any synchronizer after zipping them.
     * @param filePaths list of paths to the files to send.
     * @return true if synchronization was successful
     * @see fr.insalyon.mxyns.icrc.dna.MainActivity#syncs
     */
    public static Sync attemptFileSync(Context context, List<String> filePaths) {

        File zipFile;
        try {
            zipFile = FileUtils.randomFileInDir(context.getCacheDir(), "tmp", "zip");
        } catch (IOException ex) {
            return null;
        }
        zipFile = FileUtils.zip(filePaths, zipFile.getPath());

        Sync usedSynchronizer = attemptFileSync(context, zipFile.getPath());

        Log.d("email-zip-delete", "deleted zip");

        return usedSynchronizer;
    }
    /**
     * Attempts to send a single file using any synchronizer.
     * @param filePath path of the file to send
     * @return true if synchronization was successful
     * @see fr.insalyon.mxyns.icrc.dna.MainActivity#syncs
     */
    public static Sync attemptFileSync(Context context, String filePath) {

        for (Sync sync : syncs) {
            if (sync.send(context, filePath)) {
                return sync;
            }
        }

        return null;
    }
}
