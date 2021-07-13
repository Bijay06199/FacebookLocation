package com.example.facebooklocation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.facebooklocation.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginResult
import org.json.JSONObject
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var callbackManager: CallbackManager
    var name: String? = null
    var email: String? = null
    var profilePic: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   generateHashKey()
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        callbackManager = CallbackManager.Factory.create()
        mainBinding.loginButton.setReadPermissions(listOf("email", "public_profile"))

        mainBinding.loginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {

                    mainBinding.trackOnMap.visibility = View.VISIBLE
                    mainBinding.clInformation.visibility = View.VISIBLE
                    mainBinding.tvDownload.visibility = View.GONE


                    val graphRequest =
                        GraphRequest.newMeRequest(result?.accessToken) { `object`, response ->
                            getFacebookData(`object`)
                        }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,email,name")
                    graphRequest.parameters = parameters
                    graphRequest.executeAsync()
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }


            })


        mainBinding.trackOnMap.setOnClickListener {
            var intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("profilePic", profilePic)
            startActivity(intent)
        }

    }

    private fun getFacebookData(obj: JSONObject?) {
        profilePic =
            "https://graph.facebook.com/${obj?.getString("id")}/picture?width=100&height=100"


        Glide.with(this)
            .load(profilePic)
            .into(mainBinding.imageViewProfile)

        name = obj?.getString("name")
        email = obj?.getString("email")


        mainBinding.tvName.text = name
        mainBinding.tvEmail.text = email

    }


    fun generateHashKey() {
        val info = packageManager.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)

        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.i("HashKey :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}