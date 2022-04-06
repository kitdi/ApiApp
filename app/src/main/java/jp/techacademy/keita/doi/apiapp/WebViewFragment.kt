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

    private lateinit var viewShop: Shop

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

        viewShop = Shop(
            id = arguments?.getString(KEY_ID, "").toString(),
            couponUrls = CouponUrls(arguments?.getString(KEY_URL, "").toString(), arguments?.getString(KEY_URL, "").toString()),
            name = arguments?.getString(KEY_NAME, "").toString(),
            logoImage = arguments?.getString(KEY_LOGO_IMAGE, "").toString(),
            address = ""
        )

        // fragment_favorite.xmlが反映されたViewを作成して、returnします
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setImageResource((if (isFavorite()) R.drawable.ic_star else R.drawable.ic_star_border))

        // TODO: アイコンの画像を変えるタイミングを他のFragmentと合わせる
        fab.setOnClickListener {
            if (isFavorite()) {
                fab.setImageResource(R.drawable.ic_star_border)
                fragmentCallback?.onDeleteFavorite(viewShop.id)
            } else {
                fab.setImageResource(R.drawable.ic_star_border)
                fragmentCallback?.onAddFavorite(viewShop)
            }
        }

        // クーポンURLを開く
        webView.loadUrl(viewShop.couponUrls.sp)
    }

    private fun isFavorite(): Boolean {
        return FavoriteShop.findBy(viewShop.id) != null
    }

    override fun onDestroy() {
        backPressedCallback.remove()
        super.onDestroy()
    }

    companion object {
        private const val KEY_ID = "key_id"
        private const val KEY_URL = "key_url"
        private const val KEY_NAME = "key_name"
        private const val KEY_LOGO_IMAGE = "key_logo_image"
        fun new(id: String, url: String, name: String, logoImage: String): WebViewFragment =
            WebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ID, id)
                    putString(KEY_URL, url)
                    putString(KEY_NAME, name)
                    putString(KEY_LOGO_IMAGE, logoImage)
                }
            }
    }
}