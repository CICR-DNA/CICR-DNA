package fr.insalyon.mxyns.icrc.dna.sync;

/**
 * A synchronizer is a way to send data to any case data file to a receiver and then mark the file as sync-ed.
 */
public interface Sync {

    boolean send(String filePath);
}
