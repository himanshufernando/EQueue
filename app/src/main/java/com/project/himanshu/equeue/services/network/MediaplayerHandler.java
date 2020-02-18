package com.project.himanshu.equeue.services.network;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * @author Manish Kumar
 *         <p>
 *         MediaplayerHandler
 *         <p>
 *         <p>
 *         This class is use for  handle {@link MediaPlayer} in application
 *         <p>
 */
public class MediaplayerHandler implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    static MediaplayerHandler mediaplayerHandler;

    /**
     * {@link MediaPlayer} object
     */
    MediaPlayer mediaPlayer;
    Context _context;


    /**
     * @param _context
     */
    private MediaplayerHandler(Context _context) {
        this._context = _context;
    }

    /**
     * use for get instance of {@link MediaplayerHandler}
     *
     * @param _context
     * @return
     */
    public static MediaplayerHandler getInstance (Context _context) {
        if (mediaplayerHandler == null) {
            mediaplayerHandler = new MediaplayerHandler(_context);
        }
        return mediaplayerHandler;
    }

    /**
     * use for stop {@link #mediaPlayer}
     */
    public void stopPlayer () {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * use for play source in {@link #mediaPlayer}
     *
     * @param source
     * @param repeate
     */
    public void playSound (Uri source, boolean repeate) {
        stopPlayer();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        try {
            setPlayer(source, repeate);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * use for setup {@link #mediaPlayer}
     *
     * @param source
     * @param repeate
     * @throws IOException
     */
    private void setPlayer (Uri source, boolean repeate) throws IOException, IllegalStateException, IllegalArgumentException, SecurityException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(_context, source);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        mediaPlayer.setLooping(repeate);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnCompletionListener(this);
    }


    /**
     * This method is call when {@link #mediaPlayer} play completed
     *
     * @param mp
     */
    @Override
    public void onCompletion (MediaPlayer mp) {
        if (!mediaPlayer.isLooping()) {
            stopPlayer();
        }
    }

    /**
     * This method is call when Error in {@link #mediaPlayer}
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError (MediaPlayer mp, int what, int extra) {
        stopPlayer();
        return false;
    }

    /**
     * This method is call when {@link #mediaPlayer} Prepared successfully
     *
     * @param mp
     */
    @Override
    public void onPrepared (MediaPlayer mp) {
        try {
            mp.start();
        } catch (IllegalStateException e) {
            stopPlayer();

        }
    }
}
