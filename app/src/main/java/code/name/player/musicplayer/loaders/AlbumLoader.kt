package code.name.player.musicplayer.loaders

import android.content.Context
import android.provider.MediaStore.Audio.AudioColumns
import code.name.player.musicplayer.model.Album
import code.name.player.musicplayer.model.Song
import code.name.player.musicplayer.util.PreferenceUtil
import io.reactivex.Observable
import java.util.*


open class AlbumLoader {
    companion object {
        fun getAllAlbums(context: Context): Observable<ArrayList<Album>> {
            val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                    context, null, null,
                    getSongLoaderSortOrder())
            )

            return splitIntoAlbums(songs)
        }

        fun getAlbums(context: Context,
                      query: String): Observable<ArrayList<Album>> {
            val songs = SongLoader.getSongs(SongLoader.makeSongCursor(
                    context,
                    AudioColumns.ALBUM + " LIKE ?",
                    arrayOf("%$query%"),
                    getSongLoaderSortOrder())
            )
            return splitIntoAlbums(songs)
        }

        fun getAlbum(context: Context, albumId: Int): Observable<Album> {
            return Observable.create { e ->
                val songs = SongLoader.getSongs(SongLoader
                        .makeSongCursor(context, AudioColumns.ALBUM_ID + "=?",
                                arrayOf(albumId.toString()), getSongLoaderSortOrder()))
                songs.subscribe { songs1 ->
                    e.onNext(Album(songs1))
                    e.onComplete()
                }
            }
        }

        fun splitIntoAlbums(
                songs: Observable<ArrayList<Song>>?): Observable<ArrayList<Album>> {
            return Observable.create { e ->
                val albums = ArrayList<Album>()
                songs?.subscribe { songs1 ->
                    for (song in songs1) {
                        getOrCreateAlbum(albums, song.albumId).subscribe { album -> album.songs!!.add(song) }
                    }
                }
                e.onNext(albums)
                e.onComplete()
            }
        }

        fun splitIntoAlbums(songs: ArrayList<Song>?): ArrayList<Album> {
            val albums = ArrayList<Album>()
            if (songs != null) {
                for (song in songs) {
                    getOrCreateAlbum(albums, song.albumId).subscribe { album -> album.songs!!.add(song) }
                }
            }
            return albums
        }

        private fun getOrCreateAlbum(albums: ArrayList<Album>, albumId: Int): Observable<Album> {
            return Observable.create { e ->
                for (album in albums) {
                    if (!album.songs!!.isEmpty() && album.songs[0].albumId == albumId) {
                        e.onNext(album)
                        e.onComplete()
                        return@create
                    }
                }
                val album = Album()
                albums.add(album)
                e.onNext(album)
                e.onComplete()
            }
        }

        private fun getSongLoaderSortOrder(): String {
            return PreferenceUtil.getInstance().albumSortOrder + ", " +
                    //PreferenceUtil.getInstance().getAlbumSongSortOrder() + "," +
                    PreferenceUtil.getInstance().albumDetailSongSortOrder
        }
    }
}
