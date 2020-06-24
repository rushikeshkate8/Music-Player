package code.name.player.musicplayer.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.MediaStoreSignature;

import code.name.player.musicplayer.App;
import code.name.player.musicplayer.R;
import code.name.player.musicplayer.glide.artistimage.ArtistImage;
import code.name.player.musicplayer.glide.audiocover.AudioFileCover;
import code.name.player.musicplayer.glide.palette.BitmapPaletteWrapper;
import code.name.player.musicplayer.model.Artist;
import code.name.player.musicplayer.model.Song;
import code.name.player.musicplayer.util.ArtistSignatureUtil;
import code.name.player.musicplayer.util.CustomArtistImageUtil;
import code.name.player.musicplayer.util.MusicUtil;
import code.name.player.musicplayer.util.PreferenceUtil;

@GlideExtension
public final class RetroGlideExtension {
    private RetroGlideExtension() {
    }

    @GlideType(BitmapPaletteWrapper.class)
    public static void asBitmapPalette(RequestBuilder<BitmapPaletteWrapper> requestBuilder) {
    }

    @NonNull
    @GlideOption
    public static RequestOptions artistOptions(@NonNull RequestOptions requestOptions, Artist artist) {
        return requestOptions
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.default_artist_art)
                .placeholder(R.drawable.default_artist_art)
                .priority(Priority.LOW)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .signature(createSignature(artist));
    }

    @GlideOption
    @NonNull
    public static RequestOptions songOptions(@NonNull RequestOptions requestOptions, Song song) {
        return requestOptions
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.default_album_art)
                //.placeholder(R.drawable.default_album_art)
                .signature(createSignature(song));
    }

    public static Key createSignature(Artist artist) {
        return ArtistSignatureUtil.getInstance().getArtistSignature(artist.getName());
    }

    public static Key createSignature(Song song) {
        return new MediaStoreSignature("", song.dateModified, 0);
    }

    public static Object getArtistModel(Artist artist) {
        return getArtistModel(artist, CustomArtistImageUtil.Companion.getInstance(App.Companion.getContext()).hasCustomArtistImage(artist), false);
    }

    public static Object getArtistModel(Artist artist, boolean forceDownload) {
        return getArtistModel(artist, CustomArtistImageUtil.Companion.getInstance(App.Companion.getContext()).hasCustomArtistImage(artist), forceDownload);
    }

    public static Object getArtistModel(Artist artist, boolean hasCustomImage, boolean forceDownload) {
        if (!hasCustomImage) {
            return new ArtistImage(artist.getName(), forceDownload);
        } else {
            return CustomArtistImageUtil.getFile(artist);
        }
    }

    public static Object getSongModel(Song song) {
        return getSongModel(song, PreferenceUtil.getInstance().ignoreMediaStoreArtwork());
    }

    public static Object getSongModel(Song song, boolean ignoreMediaStore) {
        if (ignoreMediaStore) {
            return new AudioFileCover(song.data);
        } else {
            return MusicUtil.getMediaStoreAlbumCoverUri(song.albumId);
        }
    }

    public static <TranscodeType> GenericTransitionOptions<TranscodeType> getDefaultTransition() {
        return new GenericTransitionOptions<TranscodeType>().transition(android.R.anim.fade_in);
    }

}