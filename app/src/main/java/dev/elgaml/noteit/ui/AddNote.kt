package dev.elgaml.noteit.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import dev.elgaml.noteit.R
import dev.elgaml.noteit.data.Note
import dev.elgaml.noteit.databinding.ActivityAddNoteBinding

const val ADMOB_AD_UNIT_ID = "ca-app-pub-7819737441034557/6268214004"

class AddNote : AppCompatActivity() {

    private var binding: ActivityAddNoteBinding? = null
    lateinit var viewModel: NoteViewModel
    lateinit var adLoader: AdLoader
    var currentNativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        //back button functionality
        binding?.ivBackButton?.setOnClickListener {
            finish()
        }
  //      showAdMix()

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}
        refreshAd()

        binding?.btnAdd?.setOnClickListener {
            if (binding?.etNoteTitle?.text.toString().isNotEmpty() && binding?.etNoteDes?.text.toString().isNotEmpty()) {

                //getting a random color for background
                val colorsArray = resources.getIntArray(R.array.cardColors)
                val randomInt = (0..9).random()
                Log.d("TAGYOYO", "RANDOM COLOR $randomInt")
                val randomColor =  colorsArray[randomInt]
                Log.d("TAGYOYO", "RANDOM COLOR $randomColor")
                viewModel.addNote(Note(0, binding?.etNoteTitle?.text.toString(), binding?.etNoteDes?.text.toString(), randomColor))
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }else {
                Snackbar.make(binding?.root!!, "Add title and description of the note to be added", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun showAdMix(){

        val adLoader = AdLoader.Builder(this, "/6499/example/native")

            .forNativeAd { ad : NativeAd ->
                // Show the ad.
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, and so on.
                    Log.e("aaaaa",adError.message)

                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build())
            .build()
        if (adLoader.isLoading) {
            // The AdLoader is still loading ads.
            // Expect more adLoaded or onAdFailedToLoad callbacks.
            Log.e("aaaaa","adsLoading")
        } else {
            // The AdLoader has finished loading ads.
            Log.e("aaaaa","adsNotLoading")
        }
        adLoader.loadAds(AdRequest.Builder().build(), 5)

    }

    private fun loadNativeAd() {
        // Creating  an Ad Request
        val adRequest = AdRequest.Builder().build()

        // load Native Ad with the Request
        adLoader.loadAd(adRequest)

        // Showing a simple Toast message to user when Native an ad is Loading
        Toast.makeText(this, "Native Ad is loading ", Toast.LENGTH_LONG).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView!!.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent!!.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                 //  refresh_button.isEnabled = true
                //    videostatus_text.text = "Video status: Video playback has ended."
                    super.onVideoEnd()
                }
            }
        } else {
      //      videostatus_text.text = "Video status: Ad does not contain a video asset."
      //      refresh_button.isEnabled = true
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private fun refreshAd() {

        val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

        builder.forNativeAd { nativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = isDestroyed
            }
            if (activityDestroyed || isFinishing || isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            val adView = layoutInflater
                .inflate(R.layout.adunit, null) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            binding!!.adFrame.removeAllViews()
            binding!!.adFrame.addView(adView)
        }

        val videoOptions = VideoOptions.Builder()
      //      .setStartMuted(start_muted_checkbox.isChecked)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Log.e("ddddd",error)
               // refresh_button.isEnabled = true
                Toast.makeText(applicationContext, "Failed to load native ad with error $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

      //  videostatus_text.text = ""
    }







}