package com.arakadds.arak.presentation.activities.ads.CustomPackage

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityCustomPackageBinding
import com.arakadds.arak.model.CustomAges
import com.arakadds.arak.model.CustomCities
import com.arakadds.arak.model.CustomCountries
import com.arakadds.arak.model.CustomGenders
import com.arakadds.arak.model.CustomImgs
import com.arakadds.arak.model.CustomPackage
import com.arakadds.arak.model.CustomReachs
import com.arakadds.arak.model.CustomSeconds
import com.arakadds.arak.model.new_mapping_refactore.response.banners.packages.AdPackages
import com.arakadds.arak.presentation.activities.ads.CreateNewAdActivity
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.activities.payments.PaymentOptionsActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ArakStoreViewModel
import com.arakadds.arak.utils.AppEnums
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.AD_PACKAGE_ID
import com.arakadds.arak.utils.Constants.CATEGORY_ID
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject


class CustomPackageActivity : BaseActivity(), ApplicationDialogs.AlertDialogCallbacks {

    lateinit var binding: ActivityCustomPackageBinding

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakStoreViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView

    var NOOFIMAGES: String? =""
    var NOOFSECONDS: String? =""
    var NOOFReaches: String? =""

    private lateinit var token: String
    private var language: String = "ar"

    var noOfImgs: Int? =0
    var gendersPrice: Double? =0.0
    var secondsValue: Double =0.0
    var agesPrice: Double = 0.0
    var countriesPrice: Double =0.0
    var citiesPrice: Double = 0.0
    var reachSecondPrice: Double =0.0

    var catID=""
    private var adPackages = AdPackages()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)

        language = preferenceHelper.getLanguage()

        initToolbar()

        initData()

//        adPackages = intent.getParcelableExtra(AD_PACKAGE_ID)!!

        catID=intent.getStringExtra(CATEGORY_ID).toString()


        CustomPackageData(this,binding)

        binding.saveButton.setOnClickListener {
            if (catID.equals(AppEnums.AdsTypeCategories.STORES.toString())){
                startActivity(Intent(this, PaymentOptionsActivity::class.java).putExtra(CATEGORY_ID, catID).putExtra("NOOFReaches",NOOFReaches).putExtra("NOOFIMAGES",NOOFIMAGES).putExtra("NOOFSECONDS",NOOFSECONDS).putExtra("Price",binding.totalAmountValue.text.toString()))
            }else{
                val intent=Intent(this@CustomPackageActivity, CreateNewAdActivity::class.java)
                intent.putExtra(CATEGORY_ID, catID)
               // intent.putExtra(AD_PACKAGE_ID, adPackages)
                startActivity(intent)
            }
        }
    }

    private fun initData() {
        token = preferenceHelper.getToken()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)
        pageTitle.text = resources.getString(R.string.Category_Packages_activity_Custom_Package)
        backImageView.setOnClickListener { finish() }
    }

    fun CustomPackageData(context: Context,binding: ActivityCustomPackageBinding) {

        viewModel.CustomPackageData(
            "Bearer $token",
            language
        )
        this?.let {
            viewModel.baseResponseCustomPackageModel.observe(
                it,
                Observer(function = fun(customPackage: CustomPackage?) {
                    customPackage?.let {
                        if (customPackage.statusCode == 200||customPackage.statusCode == 201) {

                            fillSpinnersReaches(context,binding.numberOfReach,customPackage.data!!.customReachs,Color.GRAY)
                            fillSpinnersimages(context,binding.numberOfImage,customPackage.data!!.customImgs,Color.GRAY)
                            fillSpinnerssecound(context,binding.numberOfSeconds,customPackage.data!!.customSeconds,Color.GRAY)
                            fillSpinnersGender(context,binding.gender,customPackage.data!!.customGenders,Color.GRAY)
                            fillSpinnersAges(context,binding.age,customPackage.data!!.customAges,Color.GRAY)
                            fillSpinnerscountries(context,binding.country,customPackage.data!!.customCountries,Color.GRAY)
                            fillSpinnersCity(context,binding.city,customPackage.data!!.customCities,Color.GRAY)


                        } else {
                            this?.let { it1 ->
                                ApplicationDialogs.openAlertDialog(
                                    it1,
                                    this.resources.getString(com.arakadds.arak.R.string.dialogs_error),
                                    customPackage.message,
                                    false,
                                    AppEnums.DialogActionTypes.DISMISS,
                                    this
                                )
                            }
                            // hideView(binding.loginProgressBarId*//*progressBar*//*)

                        }
                    }
                    hideLoadingDialog()
                })
            )
        }

        /*  this?.let {
              viewModel.throwableResponse.observe(
                  it,
                  Observer(function = fun(throwable: Throwable?) {
                      throwable?.let {
                          throwable?.let { it1 ->
                              ApplicationDialogs.openAlertDialog(
                                  this,
                                  this.resources.getString(R.string.dialogs_error),
                                  throwable.message,
                                  false,
                                  AppEnums.DialogActionTypes.DISMISS,
                                  this
                              )
                          }
                      }
                      hideLoadingDialog()
                  })
              )
          }*/
    }



    fun  fillSpinnersReaches(context: Context, spinner: Spinner, arrayData: ArrayList<CustomReachs>, hintColor: Int){

        var hintText ="Select Number of Reaches"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }
        for (array in arrayData) {
            arrayDatasimple.add(array.value.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                   // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    gendersPrice = arrayData.get(position-1).secondPrice?.toDouble()
                    NOOFReaches= arrayData.get(position-1).id.toString()!!

                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun  fillSpinnersimages(context: Context, spinner: Spinner, arrayData: ArrayList<CustomImgs>, hintColor: Int){

        var hintText ="Select Number Of Images"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {
            arrayDatasimple.add(array.city.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    noOfImgs = arrayData.get(position-1).price?.toInt()
                    NOOFIMAGES= arrayData.get(position-1).id.toString()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun  fillSpinnerssecound(context: Context, spinner: Spinner, arrayData: ArrayList<CustomSeconds>, hintColor: Int){

        var hintText ="Select Number Of Seconds"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {
            arrayDatasimple.add(array.value.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text =arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    secondsValue = arrayData.get(position-1).price?.toDouble()!!
                    NOOFSECONDS= arrayData.get(position-1).id.toString()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun  fillSpinnersAges(context: Context, spinner: Spinner, arrayData: ArrayList<CustomAges>, hintColor: Int){

        var hintText ="Ages"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {
            arrayDatasimple.add(array.age.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    agesPrice = arrayData.get(position-1).price?.toDouble()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }


    fun  fillSpinnersGender(context: Context, spinner: Spinner, arrayData: ArrayList<CustomGenders>, hintColor: Int){

        var hintText ="Select Gender"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {

            arrayDatasimple.add(array.gender.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    gendersPrice = arrayData.get(position-1).price?.toDouble()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun  fillSpinnerscountries(context: Context, spinner: Spinner, arrayData: ArrayList<CustomCountries>, hintColor: Int){

        var hintText ="Select Countries"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {
            arrayDatasimple.add(array.country!!.nameEn.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    countriesPrice = arrayData.get(position-1).price?.toDouble()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun  fillSpinnersCity(context: Context, spinner: Spinner, arrayData: ArrayList<CustomCities>, hintColor: Int){

        var hintText ="Select City"
        // Add a dummy GenderItem for the hint, if hintText is provided


        var arrayDatasimple = ArrayList<String>()
        if (hintText != null && arrayData.isNotEmpty()) {
            arrayDatasimple.add(0, hintText)
        }

        for (array in arrayData) {
            arrayDatasimple.add(array.city!!.nameEn.toString())
        }

        val adapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayDatasimple) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item if it's the hint
                return if (hintText != null) position != 0 else true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                // Change the color for hint and normal items
                if (position == 0 && hintText != null) {
                    tv.setTextColor(hintColor) // Color for the hint text
                } else {
                    // tv.setTextColor(context.resources.getColor()) // Normal items color
                }
                tv.text = arrayDatasimple.get(position).toString() // Set gender as the displayed text
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the item selection listener to call the price calculation
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0 || hintText == null) {
                    // Trigger the callback for price calculation when a valid item is selected
                    citiesPrice = arrayData.get(position-1).price?.toDouble()!!
                    calculatePrice(noOfImgs, gendersPrice, secondsValue, agesPrice, countriesPrice, citiesPrice, reachSecondPrice)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing if no item is selected
            }
        }
    }

    fun calculatePrice(noOfImgs: Int?, gendersPrice: Double?, secondsValue: Double, agesPrice: Double, countriesPrice: Double, citiesPrice: Double, reachSecondPrice: Double) {
        val imgsFactor = noOfImgs?.toDouble() ?: 0.0
        val genderFactor = gendersPrice ?: 1.0
        val secondsFactor = secondsValue
        val ageFactor = agesPrice
        val countryFactor = countriesPrice
        val cityFactor = citiesPrice
        val reachFactor = reachSecondPrice

        val finalFactor = (reachFactor + imgsFactor + secondsFactor + ageFactor) * countryFactor * cityFactor * genderFactor

        binding.totalAmountValue.text= "%.2f".format(finalFactor)
    }

    override fun onClose() {
        TODO("Not yet implemented")
    }

    override fun onConfirm(actionType: Int) {
        TODO("Not yet implemented")
    }

}