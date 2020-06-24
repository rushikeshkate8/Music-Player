package code.name.player.musicplayer.rest;

import android.content.Context;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import code.name.player.musicplayer.App;
import code.name.player.musicplayer.rest.service.KuGouApiService;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static code.name.player.musicplayer.Constants.BASE_API_URL_KUGOU;

/**
 * Created by hemanths on 23/08/17.
 */

public class KogouClient {

    private static final String BASE_URL = BASE_API_URL_KUGOU;

    private KuGouApiService apiService;

    public KogouClient() {
        this(createDefaultOkHttpClientBuilder().build());
    }

    private KogouClient(@NonNull Call.Factory client) {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callFactory(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = restAdapter.create(KuGouApiService.class);
    }

    @Nullable
    private static Cache createDefaultCache(Context context) {
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "/okhttp-lastfm/");
        if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
            return new Cache(cacheDir, 1024 * 1024 * 10);
        }
        return null;
    }

    private static Interceptor createCacheControlInterceptor() {
        return chain -> {
            Request modifiedRequest = chain.request().newBuilder()
                    .addHeader("Cache-Control", String.format("max-age=%d, max-stale=%d", 31536000, 31536000))
                    .build();
            return chain.proceed(modifiedRequest);
        };
    }

    private static OkHttpClient.Builder createDefaultOkHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .cache(createDefaultCache(App.Companion.getInstance()))
                .addInterceptor(createCacheControlInterceptor());
    }

    public KuGouApiService getApiService() {
        return apiService;
    }
}
