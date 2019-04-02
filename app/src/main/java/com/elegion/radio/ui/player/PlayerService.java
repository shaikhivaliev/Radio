package com.elegion.radio.ui.player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.elegion.radio.R;

import java.io.IOException;


public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private final int NOTIFICATION_ID = 1;
    private final String CHANNEL_ID = "default_channel";
    private final String CHANNEL_NAME = "Radio";
    private String mStreamResource = "";

    public void setStreamResource(String mStreamResource) {
        this.mStreamResource = mStreamResource;
    }

    // 1 - метаданные станции
    private final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

    // 2 - состояния плеера, действия, которые собираемся обрабатывать в коллбэках.
    private final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PAUSE
    );

    private MediaSessionCompat mSession;
    private MediaPlayer mMediaPlayer;
    boolean isPlayerReady = false;


    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private boolean audioFocusRequested = false;

    @Override
    public void onCreate() {
        super.onCreate();


        // 3 - инициализируем сессию
        mSession = new MediaSessionCompat(this, "RadioService");
        // 4 - получаем события от аппаратных кнопок, получаем события от кнопок на окне блокировки
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // 5 - прикручиваем коллбеки
        mSession.setCallback(mediaSessionCallback);

        // 6 - activity, которую запустит система, если пользователь, заинтересуется подробностями данной сессии
        Context appContext = getApplicationContext();
        Intent activityIntent = new Intent(appContext, PlayerActivity.class);
        mSession.setSessionActivity(PendingIntent.getActivity(appContext, 0, activityIntent, 0));


        // ???
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            //Объект инкапсулирует свойства аудио-потока
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            //работа с аудио-фокусом, с api26
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }

        // 33 - для api>=21. Добавляем к сессии приемник, куда можно слать бродкасты для пробуждения
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null, appContext, MediaButtonReceiver.class);
        mSession.setMediaButtonReceiver(PendingIntent.getBroadcast(appContext, 0, mediaButtonIntent, 0));

        // 34 - получаем audiomanager (обеспечивает доступ к управлению громкостью и режимом звонка)
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 12 - инициализируем непосредственно плеер
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPlayerReady = true;
    }


    // 8 - коллбеки
    private MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        // состояние сессии
        int currentState = PlaybackStateCompat.STATE_STOPPED;

        @Override
        public void onPlay() {

            //todo получить данные из фрагменты

            // 13 - Загружаем URL станции в плеер
            try {
                mMediaPlayer.setDataSource(mStreamResource);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }



            // 9 - Заполняем данные о станции
            MediaMetadataCompat metadata = metadataBuilder
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.drawable.radio))
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Radio")
                    .build();
            mSession.setMetadata(metadata);

            // 11 - Сообщаем новое состояние
            mSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());



            // ???
            currentState = PlaybackStateCompat.STATE_PLAYING;


            // 35 - запрашиваем аудио-фокус (надо получить до вызова setActive)
            if (isPlayerReady) {
                if (!audioFocusRequested) {
                    audioFocusRequested = true;

                    int audioFocusResult;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        audioFocusResult = mAudioManager.requestAudioFocus(mAudioFocusRequest);
                    } else {
                        audioFocusResult = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    }
                    if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                        return;

                }
                // 14 - Запускаем воспроизведение
                mMediaPlayer.start();

                // 41.1 - наушники
                registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

            }

            // 10 - наше приложение теперь активный плеер и кнопки окне блокировки должны управлять именно нами
            mSession.setActive(true);


            // 40.1 - обновляем уведомление к оллбеках сессии
            refreshNotificationAndForegroundStatus(currentState);
        }

        @Override
        public void onPause() {

            // 15 - останавливаем плеер
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();

                // 41.2 - наушники
                unregisterReceiver(becomingNoisyReceiver);
            }

            // 16 - Сообщаем новое состояние
            mSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_PAUSED;

            // 40.2 - обновляем уведомление к оллбеках сессии
            refreshNotificationAndForegroundStatus(currentState);
        }

        @Override
        public void onStop() {

            // 17 - останавливем воспроизведение
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();

                // 41.3 - наушники
                unregisterReceiver(becomingNoisyReceiver);
            }

            // 18 - теперь мы не главый плеер
            mSession.setActive(false);

            // 19 - Сообщаем новое состояние
            mSession.setPlaybackState(stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            currentState = PlaybackStateCompat.STATE_STOPPED;


            // 36 - освобождаем аудио-фокус
            if (audioFocusRequested) {
                audioFocusRequested = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                } else {
                    mAudioManager.abandonAudioFocus(audioFocusChangeListener);
                }
            }

            // 40.3 - обновляем уведомление к оллбеках сессии
            refreshNotificationAndForegroundStatus(currentState);

        }
    };

    // 20 - Для доступа извне к MediaSession требуется токен. Для этого научим сервис его отдавать
    // 21 - прописываем в манифесте сервис ---> рисуем плеер во фрагменте...
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerServiceBinder();
    }


    public class PlayerServiceBinder extends Binder {

        public MediaSessionCompat.Token getMediaSessionToken() {
            return mSession.getSessionToken();
        }

        // 33 - передаем во фрагмент текущий экземпляр сервиса
        public PlayerService getService() {
            return PlayerService.this;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 7 - освобождаем ресурсы
        mSession.release();
        mMediaPlayer.release();
    }


    // 37 - коллбэк для аудио фокуса, отрабатываем различные ситуации, например звонок или звук уведомления  (TRANSIENT_CAN_DUCK)
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lower the volume while ducking.
                    mMediaPlayer.setVolume(0.2f, 0.2f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mMediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mMediaPlayer.stop();
                    mAudioManager.abandonAudioFocus(this);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Return the volume to normal and resume if paused.
                    mMediaPlayer.setVolume(1f, 1f);
                    mMediaPlayer.start();
                    break;
                default:
                    break;
            }
        }
    };


    // 38 - ??????
    private void refreshNotificationAndForegroundStatus(int playbackState) {
        switch (playbackState) {
            case PlaybackStateCompat.STATE_PLAYING: {
                startForeground(NOTIFICATION_ID, getNotification(playbackState));
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                // На паузе мы перестаем быть foreground, однако оставляем уведомление,
                // чтобы пользователь мог play нажать
                NotificationManagerCompat.from(PlayerService.this).notify(NOTIFICATION_ID, getNotification(playbackState));
                stopForeground(false);
                break;
            }
            default: {
                stopForeground(true);
                break;
            }
        }
    }

    // 39 - рисуем уведомление
    private Notification getNotification(int playbackState) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, getString(R.string.pause), MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        else
            builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, getString(R.string.play), MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));

        builder.setStyle(new MediaStyle()
                .setShowActionsInCompactView(1)
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)));

        builder.setSmallIcon(R.drawable.radio);
        builder.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        builder.setShowWhen(false);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        // Не надо каждый раз вываливать уведомление на пользователя
        builder.setOnlyAlertOnce(true);
        builder.setChannelId(CHANNEL_ID);

        return builder.build();
    }

    // 41 - отрабатываем выдергивание наушников
    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                mediaSessionCallback.onPause();
            }
        }
    };


}
