package xyz.santtu.materialcarrotwear;

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.ambient.AmbientModeSupport
import xyz.santtu.materialcarrotrepository.Profile
import xyz.santtu.materialcarrotrepository.ProfileViewModel
import xyz.santtu.materialcarrotwear.databinding.SelectProfileBinding

class SelectProfileWearActivity() : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    private lateinit var binding: SelectProfileBinding
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var allProfiles: List<Profile> = emptyList()
    val profileModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = SelectProfileBinding.inflate(layoutInflater)
        val recyclerView = binding.root
        profileModel.allProfiles.observe(this, {
                profileList -> allProfiles = profileList
        })
        profileModel.insert(Profile(0,"Jekkuprofiili", "0123456789abcdef".toByteArray()))
        viewManager = LinearLayoutManager(this)
        viewAdapter = ProfileAdapter(allProfiles)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        TODO("Not yet implemented")
    }

}
