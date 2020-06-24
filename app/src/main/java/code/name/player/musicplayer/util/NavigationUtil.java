package code.name.player.musicplayer.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import code.name.player.musicplayer.R;
import code.name.player.musicplayer.helper.MusicPlayerRemote;
import code.name.player.musicplayer.model.Genre;
import code.name.player.musicplayer.model.Playlist;
import code.name.player.musicplayer.ui.activities.AboutActivity;
import code.name.player.musicplayer.ui.activities.AlbumDetailsActivity;
import code.name.player.musicplayer.ui.activities.ArtistDetailActivity;
import code.name.player.musicplayer.ui.activities.EqualizerActivity;
import code.name.player.musicplayer.ui.activities.GenreDetailsActivity;
import code.name.player.musicplayer.ui.activities.LicenseActivity;
import code.name.player.musicplayer.ui.activities.LyricsActivity;
import code.name.player.musicplayer.ui.activities.PlayingQueueActivity;
import code.name.player.musicplayer.ui.activities.PlaylistDetailActivity;
import code.name.player.musicplayer.ui.activities.PurchaseActivity;
import code.name.player.musicplayer.ui.activities.SearchActivity;
import code.name.player.musicplayer.ui.activities.SettingsActivity;
import code.name.player.musicplayer.ui.activities.SupportDevelopmentActivity;
import code.name.player.musicplayer.ui.activities.UserInfoActivity;
import code.name.player.musicplayer.ui.activities.WhatsNewActivity;

import static code.name.player.musicplayer.Constants.RATE_ON_GOOGLE_PLAY;
import static code.name.player.musicplayer.util.RetroUtil.openUrl;


public class NavigationUtil {

    public static void goToAlbum(@NonNull Activity activity, int i,
                                 @Nullable Pair... sharedElements) {
        Intent intent = new Intent(activity, AlbumDetailsActivity.class);
        intent.putExtra(AlbumDetailsActivity.EXTRA_ALBUM_ID, i);
        //noinspection unchecked
        ActivityCompat.startActivity(activity, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
    }

    public static void goToArtist(@NonNull Activity activity, int i,
                                  @Nullable Pair... sharedElements) {
        Intent intent = new Intent(activity, ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, i);
        //noinspection unchecked
        ActivityCompat.startActivity(activity, intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedElements).toBundle());
    }

    public static void goToPlaylistNew(@NonNull Activity activity, Playlist playlist) {
        Intent intent = new Intent(activity, PlaylistDetailActivity.class);
        intent.putExtra(PlaylistDetailActivity.Companion.getEXTRA_PLAYLIST(), playlist);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void openEqualizer(@NonNull final Activity activity) {
        if (PreferenceUtil.getInstance().getSelectedEqualizer().equals("system")) {
            stockEqalizer(activity);
        } else {
            ActivityCompat.startActivity(activity, new Intent(activity, EqualizerActivity.class), null);
        }
    }

    private static void stockEqalizer(@NonNull Activity activity) {
        final int sessionId = MusicPlayerRemote.INSTANCE.getAudioSessionId();
        if (sessionId == AudioEffect.ERROR_BAD_VALUE) {
            Toast.makeText(activity, activity.getResources().getString(R.string.no_audio_ID),
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, sessionId);
                effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                activity.startActivityForResult(effects, 0);
            } catch (@NonNull final ActivityNotFoundException notFound) {
                Toast.makeText(activity, activity.getResources().getString(R.string.no_equalizer),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void goToPlayingQueue(@NonNull Activity activity) {
        Intent intent = new Intent(activity, PlayingQueueActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToLyrics(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LyricsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToGenre(@NonNull Activity activity, @NonNull Genre genre) {
        Intent intent = new Intent(activity, GenreDetailsActivity.class);
        intent.putExtra(GenreDetailsActivity.EXTRA_GENRE_ID, genre);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void goToProVersion(@NonNull Context context) {
        ActivityCompat.startActivity(context, new Intent(context, PurchaseActivity.class), null);
    }

    public static void goToSettings(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SettingsActivity.class), null);
    }

    public static void goToAbout(@NonNull Activity activity) {
        //ActivityCompat.startActivity(activity, new Intent(activity, AboutActivity.class), null);
    }

    public static void goToUserInfo(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, UserInfoActivity.class), null);
    }

    public static void goToOpenSource(@NonNull Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, LicenseActivity.class), null);
    }

    public static void goToSearch(Activity activity) {
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class), null);
    }

    public static void goToSupportDevelopment(Activity activity) {
        //ActivityCompat.startActivity(activity, new Intent(activity, SupportDevelopmentActivity.class), null);
    }

    public static void goToPlayStore(Activity activity) {
        openUrl(activity, RATE_ON_GOOGLE_PLAY);
    }

    public static void gotoWhatNews(Activity activity) {
        //ActivityCompat.startActivity(activity, new Intent(activity, WhatsNewActivity.class), null);
    }
}
