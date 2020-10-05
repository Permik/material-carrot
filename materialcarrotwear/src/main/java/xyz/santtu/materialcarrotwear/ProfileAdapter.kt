package xyz.santtu.materialcarrotwear

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.santtu.materialcarrotrepository.Profile

class ProfileAdapter(private val profileList: List<Profile>):
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val textView = LayoutInflater
            .from(parent.context).inflate(R.layout.profile_list_text_view, parent, false ) as TextView
        return ProfileViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.textView.text = profileList[position].profileName
    }

    override fun getItemCount() = profileList.size
}