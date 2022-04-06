package jp.techacademy.keita.doi.apiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.android.synthetic.main.recycler_favorite.view.*

class WebViewFragment: Fragment() {

    // FavoriteFragment -> MainActivity に削除を通知する
    private var fragmentCallback : FragmentCallback? = null

    private lateinit var backPressedCallback : OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentCallback) {
            fragmentCallback = context
        }

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                } else {
                    this.isEnabled = false
                    activity?.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // fragment_favorite.xmlが反映されたViewを作成して、returnします
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: お気に入りマークの画像を変える
        fab.setOnClickListener {
            arguments?.getString(KEY_ID)?.let {
                val favoriteShop = FavoriteShop.findBy(it)
                val isFavorite = favoriteShop != null
                fab.setImageResource((if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border))
                if (isFavorite) {
                    fragmentCallback?.onDeleteFavorite(favoriteShop!!.id)
                } else {
                    R.drawable.ic_star_border
                    // TODO: ShopをFragmentに渡す
//                    fragmentCallback?.onAddFavorite(Shop())
                }
            }
        }

        // クーポンURLを開く
        arguments?.getString(KEY_URL)?.let { webView.loadUrl(it) }
    }

    override fun onDestroy() {
        backPressedCallback.remove()
        super.onDestroy()
    }

    companion object {
        private const val KEY_ID = "key_id"
        private const val KEY_URL = "key_url"
        fun new(id: String, url: String): WebViewFragment =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ID, id)
                    putString(KEY_URL, url)
                }
            }
    }
}