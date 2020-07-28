package code.name.player.musicplayer.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import code.name.player.musicplayer.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()

                .withTargetActivity(MainActivity.class)

                .withSplashTimeOut(100)

                .withBackgroundColor(Color.parseColor("#FFFFFF"))
                .withAfterLogoText( "Music Player" )
                .withLogo( R.drawable.ic_music_note_dark_blue_24dp);
        config.getAfterLogoTextView().setTextColor( getResources().getColor( R.color.footer_text_color ));
        config.getAfterLogoTextView().setTextSize( 38f );
        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
    }
}