package fr.insalyon.mxyns.icrc.dna.sync;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * A synchronizer is a way to send data to any case data file to a receiver and then mark the file as sync-ed.
 */
public abstract class Sync {

    /**
     * Data synchronizers, used to send the cases data to any type of receiver, sorted in order of use.
     * If the first one fails the next one will be used until one is etc.
     * @see Sync#attemptFileSync
     */
    public static LinkedList<Sync> syncs = new LinkedList<>(Arrays.asList(new RestSync(), new EmailSync("edezimmall-6736@yopmail.com")));

    public abstract boolean send(Context context, String filePath);

    /**
     * Attempts to send a list of files using any synchronizer after zipping them.
     * @param filePaths list of paths to the files to send.
     * @return true if synchronization was successful
     * @see Sync#syncs
     */
    public static boolean attemptFileSync(Context context, List<String> filePaths) {

        File zipFile;
        try {
            zipFile = FileUtils.randomFileInDir(context.getFilesDir(), "tmp", "zip");
        } catch (IOException ex) {
            return false;
        }
        zipFile = FileUtils.zip(filePaths, zipFile.getPath());

        boolean result = attemptFileSync(context, zipFile.getPath()) != null;

        // FIXME wait until email activity is closed
        //  zipFile.delete();
        Log.d("email-zip-delete", "deleted zip");

        return result;
    }
    /**
     * Attempts to send a single file using any synchronizer.
     * @param filePath path of the file to send
     * @return true if synchronization was successful
     * @see Sync#syncs
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
