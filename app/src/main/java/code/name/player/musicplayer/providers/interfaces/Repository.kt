package code.name.player.musicplayer.providers.interfaces

import code.name.player.musicplayer.model.*
import code.name.player.musicplayer.model.smartplaylist.AbsSmartPlaylist
import io.reactivex.Observable
import kotlin.collections.ArrayList


interface Repository {

    val allSongs: Observable<ArrayList<Song>>

    val suggestionSongs: Observable<ArrayList<Song>>

    val allAlbums: Observable<ArrayList<Album>>

    val recentAlbums: Observable<ArrayList<Album>>

    val topAlbums: Observable<ArrayList<Album>>

    val allArtists: Observable<ArrayList<Artist>>

    val recentArtists: Observable<ArrayList<Artist>>

    val topArtists: Observable<ArrayList<Artist>>

    val allPlaylists: Observable<ArrayList<Playlist>>

    val homeList: Observable<ArrayList<Playlist>>

    val allThings: Observable<ArrayList<AbsSmartPlaylist>>

    val allGenres: Observable<ArrayList<Genre>>

    fun getSong(id: Int): Observable<Song>

    fun getAlbum(albumId: Int): Observable<Album>

    fun getArtistById(artistId: Long): Observable<Artist>

    fun search(query: String?): Observable<ArrayList<Any>>

    fun getPlaylistSongs(playlist: Playlist): Observable<ArrayList<Song>>

    fun getGenre(genreId: Int): Observable<ArrayList<Song>>

    val favoritePlaylist: Observable<ArrayList<Playlist>>

}