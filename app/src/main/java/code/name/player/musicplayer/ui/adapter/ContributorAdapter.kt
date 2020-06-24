package code.name.player.musicplayer.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import code.name.player.musicplayer.R
import code.name.player.musicplayer.model.Contributor
import code.name.player.musicplayer.ui.adapter.base.MediaEntryViewHolder
import code.name.player.musicplayer.util.RetroUtil.openUrl
import code.name.player.musicplayer.views.NetworkImageView

class ContributorAdapter(private var contributors: ArrayList<Contributor>) : RecyclerView.Adapter<ContributorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contributor, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contributor = contributors[position]
        holder.bindData(contributor)
    }

    override fun getItemCount(): Int {
        return contributors.size
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        internal fun bindData(contributor: Contributor) {
            if (title != null) {
                title!!.text = contributor.name
            }
            if (text != null) {
                text!!.text = contributor.summary
            }
            if (image is NetworkImageView) {
                (image as NetworkImageView).setImageUrl(contributor.profileImage)
            }
        }

        override fun onClick(v: View?) {
            super.onClick(v)
            openUrl(v!!.context as Activity, contributors[adapterPosition].link)
        }
    }
}
