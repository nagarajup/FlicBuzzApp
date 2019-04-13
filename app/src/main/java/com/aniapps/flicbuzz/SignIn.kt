package com.aniapps.flicbuzz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.aniapps.flicbuzz.networkcall.APIResponse
import com.aniapps.flicbuzz.networkcall.RetrofitClient
import org.json.JSONObject
import org.json.JSONArray



class SignIn : AppCompatActivity() {
    internal lateinit var btn_login: Button
    internal lateinit var btn_signup: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        btn_login = findViewById<View>(R.id.btn_login) as Button
        btn_signup = findViewById<View>(R.id.btn_signup) as Button
        btn_login.setOnClickListener {
             val i = Intent(this@SignIn, Login::class.java)
              startActivity(i)
           // LoginApi()
        }

        btn_signup.setOnClickListener {
          /*  val i = Intent(this@SignIn, SignUp::class.java)
            startActivity(i)*/

            val player_in = Intent(it.context, MyPlayer::class.java)
            player_in.putExtra("url", "https://www.flicbuzz.com/vendor_videos/converted/vendor_3/video_56_20190401_1539.mp4.m3u8")
            player_in.putExtra("title", "Test Test Test")
            player_in.putExtra("desc", "Test Test Test TestTest Test Test TestTest Test Test TestTest Test Test TestTest Test Test TestTest Test Test TestTest Test Test TestTest Test Test Test ")
            player_in.putExtra("id", "10")

            it.context.startActivity(player_in)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
           // LoginApi2()
        }
    }

    private fun getParams(): Map<String, String> {
        val params = HashMap<String, String>()
        params["action"] = "home"
        params["plan"] = "free"
        params["device_name"] = "abcd"
        return params
    }

    private fun getParams2(): Map<String, String> {
        val params = HashMap<String, String>()
        params["action"] = "home2"
        params["plan"] = "free"
        params["page_number"] = "1"
        params["device_name"] = "abcd"
        return params
    }


    private fun LoginApi() {
        RetrofitClient.getInstance()
            .doBackProcess(this@SignIn, getParams(), "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array",""+jsonArray.length())

                            val i = Intent(this@SignIn, Base::class.java)
                            i.putExtra("jsonArray", jsonArray.toString());
                            startActivity(i)

                        } else {
                            Toast.makeText(this@SignIn, "status" + status, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@SignIn, "status" + res, Toast.LENGTH_LONG).show()
                }
            })
    }


    private fun LoginApi2() {
        RetrofitClient.getInstance()
            .doBackProcess(this@SignIn, getParams2(), "", object : APIResponse {
                override fun onSuccess(res: String?) {
                    try {
                        val jobj = JSONObject(res)
                        val status = jobj.getInt("status")
                        val details = jobj.getString("details")

                        if (status == 1) {
                            Log.e("RES", res)
                            val jsonArray = jobj.getJSONArray("data")
                            Log.e("RES my Array",""+jsonArray.length())

                            val i = Intent(this@SignIn, MyPlaerList::class.java)
                            i.putExtra("jsonArray", jsonArray.toString());
                            startActivity(i)

                        } else {
                            Toast.makeText(this@SignIn, "status" + status, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(res: String?) {
                    Toast.makeText(this@SignIn, "status" + res, Toast.LENGTH_LONG).show()
                }
            })
    }

}