package com.arakadds.arak.presentation.activities.ads

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityDisplayFullScreenImageBinding
import com.arakadds.arak.model.new_mapping_refactore.store.StoreProductFile
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.other.NoInternetConnectionActivity
import com.arakadds.arak.presentation.adapters.SelectedProductImagesViewPagerAdapter
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.POSITION
import com.arakadds.arak.utils.Constants.PRODUCT_FILES_INFORMATION
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ortiz.touchview.TouchImageView
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.android.AndroidInjection
import io.paperdb.Paper

class DisplayFullScreenImageActivity : BaseActivity() {

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityDisplayFullScreenImageBinding
    private lateinit var activityContext: Context

    private var connectionLiveData: ConnectionLiveData? = null

    private var viewPager: ViewPager? = null
    private var dotsIndicator: DotsIndicator? = null
    private var exitImageView: ImageView? = null
    private var selectedImageImageView: TouchImageView? = null
    private var selectedProductImagesViewPagerAdapter: SelectedProductImagesViewPagerAdapter? = null
    private var language: String? = null
    private var imageStringUri: String? = null
    private var imagePathStringUrl: String? = null
    private var myUri: Uri? = null
    private val adFilesList = ArrayList<StoreProductFile>()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityDisplayFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()

        //updateAdsStoryView(Paper.book().read<Any>("language") as String)
        checkNetworkConnection()

        getIntants()
        initUi()
        initViews()
        setListeners()
    }

    private fun initUi() {

        viewPager = findViewById(R.id.display_full_screen_image_ViewPager_id)
        dotsIndicator =
            findViewById(R.id.display_full_screen_image_DotsIndicator_id)
        exitImageView =
            findViewById(R.id.display_full_screen_image_exit_imageView_id)
        selectedImageImageView =
            findViewById(R.id.display_full_screen_image_imageView_id)

    }

    private fun initViews() {
        if (imageStringUri != null) {
            try {
                selectedImageImageView!!.visibility = View.VISIBLE
                viewPager!!.visibility = View.GONE
                dotsIndicator!!.visibility = View.GONE
                val bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    myUri
                )
                selectedImageImageView!!.setImageBitmap(bitmap)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (imagePathStringUrl != null) {
            selectedImageImageView!!.visibility = View.VISIBLE
            viewPager!!.visibility = View.GONE
            dotsIndicator!!.visibility = View.GONE
            selectedImageImageView!!.scaleType = ScaleType.FIT_CENTER
            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
            Glide.with(this).load(imagePathStringUrl).apply(options)
                .into(selectedImageImageView!!)
        } else {
            setDataToViews()
        }
    }

    private fun getIntants() {
        try {
            imageStringUri = intent.getStringExtra("uri")
            imagePathStringUrl = intent.getStringExtra("image_path")
            myUri = Uri.parse(imageStringUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        exitImageView!!.setOnClickListener { v: View? -> finish() }
    }

    private fun setDataToViews() {
        try {
            val position = intent.getIntExtra(POSITION, 0)
            val bundle = intent.extras
            var productFilesInformationArrayList: ArrayList<StoreProductFile?>? =
                java.util.ArrayList()
            productFilesInformationArrayList =
                bundle!!.getSerializable(PRODUCT_FILES_INFORMATION) as ArrayList<StoreProductFile?>?

            selectedProductImagesViewPagerAdapter = SelectedProductImagesViewPagerAdapter(
                productFilesInformationArrayList, this, true, position, false
            )
            viewPager!!.adapter = selectedProductImagesViewPagerAdapter
            dotsIndicator!!.setViewPager(viewPager!!)
            selectedProductImagesViewPagerAdapter!!.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkNetworkConnection() {
        connectionLiveData?.observe(this) { isNetworkAvailable ->
            //update ui
            if (!isNetworkAvailable) {
                startActivity(Intent(this, NoInternetConnectionActivity::class.java))
            } else {
                //Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}