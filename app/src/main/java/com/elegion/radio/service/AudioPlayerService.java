package com.elegion.radio.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.elegion.radio.AppDelegate;
import com.elegion.radio.MainActivity;
import com.elegion.radio.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import okhttp3.OkHttpClient;

public class AudioPlayerService extends Service {

    //todo разница между simple player и обычным
    private SimpleExoPlayer mExoPlayer;
    private ExtractorsFactory extractorsFactory;
    private DataSource.Factory dataSourceFactory;
    private String mStreamResources = "";

    private MediaSessionCompat mMediaSession;

    final MediaMetadataCompat.Builder mMediaMetadataBuilder = new MediaMetadataCompat.Builder();

    private void updateMetadata(String stationName) {
        //todo засетить иконку, как засетить иконку для окна блокировки?
        mMediaMetadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, stationName);
        mMediaSession.setMetadata(mMediaMetadataBuilder.build());
    }

    // ...состояния плеера
    // Здесь мы указываем действия, которые собираемся обрабатывать в коллбэках.
    final PlaybackStateCompat.Builder mActions = new PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE);


    @Override
    public void onCreate() {
        super.onCreate();
        initExoPlayer();
        initMediaSession();

    }

    private void initExoPlayer() {
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        mExoPlayer.addListener(exoPlayerListener);
        DataSource.Factory httpDataSourceFactory = new OkHttpDataSourceFactory(new OkHttpClient(), Util.getUserAgent(this, getString(R.string.app_name)), null);
        Cache cache = new SimpleCache(new File(this.getCacheDir().getAbsolutePath() + "/exoplayer"), new LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100)); // 100 Mb max
        this.dataSourceFactory = new CacheDataSourceFactory(cache, httpDataSourceFactory, CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        this.extractorsFactory = new DefaultExtractorsFactory();

    }

    public void initMediaSession() {

        mMediaSession = new MediaSessionCompat(this, "AudioPlayerService");

        // FLAG_HANDLES_MEDIA_BUTTONS - хотим получать события от аппаратных кнопок(например, гарнитуры)
        // FLAG_HANDLES_TRANSPORT_CONTROLS - хотим получать события от кнопок на окне блокировки
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Отдаем наши коллбэки
        mMediaSession.setCallback(mediaSessionCallback);

        // Укажем activity, которую запустит система, если пользователь заинтересуется подробностями данной сессии
        Intent activityIntent = new Intent(AppDelegate.getInstance(), MainActivity.class);
        mMediaSession.setSessionActivity(PendingIntent.getActivity(AppDelegate.getInstance(), 0, activityIntent, 0));

    }

    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        private Uri currentUri;
        int currentState = PlaybackStateCompat.STATE_STOPPED;

        @Override
        public void onPlay() {
            if (!mExoPlayer.getPlayWhenReady()) {
                startService(new Intent(AppDelegate.getInstance(), AudioPlayerService.class));

/*
                StreamBean streamBean = new StreamBean("http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio4fm_mf_p");
                List<StreamBean> mStreamBeans = new ArrayList<>();
                mStreamBeans.set(0, streamBean);
                Station station = new Station("Radio name", mStreamBeans);

*/

                updateMetadata("Radio station name");

                // Указываем, что наше приложение теперь активный плеер и кнопки
                // на окне блокировки должны управлять именно нами
                mMediaSession.setActive(true); // Сразу после получения фокуса

                // Сообщаем новое состояние
                mMediaSession.setPlaybackState(mActions.setState(PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());

                // Загружаем URL аудио-файла в ExoPlayer
                prepareToPlay(Uri.parse(mStreamResources));

                // Запускаем воспроизведение
                mExoPlayer.setPlayWhenReady(true);
            }

        }

        @Override
        public void onPause() {
            if (mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(false);
            }

            mMediaSession.setPlaybackState(mActions.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_PAUSED;

        }

        @Override
        public void onStop() {
            if (mExoPlayer.getPlayWhenReady()) {
                mExoPlayer.setPlayWhenReady(false);
            }

            // Все, больше мы не "главный" плеер, уходим со сцены
            mMediaSession.setActive(false);

            mMediaSession.setPlaybackState(mActions.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_STOPPED;
        }


        private void prepareToPlay(Uri uri) {
            if (!uri.equals(currentUri)) {
                currentUri = uri;
                ExtractorMediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                mExoPlayer.prepare(mediaSource);
            }
        }


    };

    private ExoPlayer.EventListener exoPlayerListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady && playbackState == ExoPlayer.STATE_ENDED) {
                mediaSessionCallback.onSkipToNext();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
        }

        @Override
        public void onPositionDiscontinuity() {
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AudioPlayerBinder();
    }

    public class AudioPlayerBinder extends Binder {
        public MediaSessionCompat.Token getMediaSessionToken() {
            return mMediaSession.getSessionToken();
        }

        public void setStreamResources(String streamResources) {
            mStreamResources = streamResources;
        }

        public String getCurrentStreamResources() {
            return mStreamResources;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaSession.release();
    }
}
