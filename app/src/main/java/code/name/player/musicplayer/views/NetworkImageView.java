package code.name.player.musicplayer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import code.name.player.musicplayer.R;
import code.name.player.musicplayer.glide.GlideApp;

/**
 * @author Hemanth S (h4h13).
 */
public class NetworkImageView extends CircularImageView {

    public NetworkImageView(Context context) {
        super(context);
        init(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setImageUrl(String imageUrl) {
        setImageUrl(getContext(), imageUrl);
    }

    public void setImageUrl(Context context, String imageUrl) {
        GlideApp.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_person_flat)
                .placeholder(R.drawable.ic_person_flat)
                .into(this);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray attributes = context.obtainStyledAttributes(attributeSet, R.styleable.NetworkImageView, 0, 0);
        String url = attributes.getString(R.styleable.NetworkImageView_url_link);
        setImageUrl(context, url);
        attributes.recycle();
    }
}
