package uz.akra.valyutakurswithvolley

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import uz.akra.valyutakurswithvolley.adapters.MyRvAdapter
import uz.akra.valyutakurswithvolley.databinding.ActivityMainBinding
import uz.akra.valyutakurswithvolley.databinding.ItemDialogBinding
import uz.akra.valyutakurswithvolley.models.MyCurrency

class MainActivity : AppCompatActivity(), MyRvAdapter.RvAction {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var requestQueue: RequestQueue
    private var link2 = "https://cbu.uz/uzc/arkhiv-kursov-valyut/json/"
    lateinit var myRvAdapter: MyRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestQueue = Volley.newRequestQueue(this)

        var arrayRequest = JsonArrayRequest(
            Request.Method.GET,
            link2,
            null,
            object : Response.Listener<JSONArray> {
                override fun onResponse(response: JSONArray?) {
                    val typeToken = object : TypeToken<ArrayList<MyCurrency>>() {}.type
                    val list =
                        Gson().fromJson<ArrayList<MyCurrency>>(response.toString(), typeToken)
                    myRvAdapter = MyRvAdapter(list, this@MainActivity)
                    binding.myContainer.adapter = myRvAdapter

                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    Toast.makeText(this@MainActivity, "Array Errorga tushdi", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

        requestQueue.add(arrayRequest)


    }

    override fun onClick(myCurrency: MyCurrency, position: Int) {
        val dialog = AlertDialog.Builder(this).create()
        val itemDialogBinding = ItemDialogBinding.inflate(layoutInflater)
        itemDialogBinding.tvName.text = myCurrency.CcyNm_EN

        itemDialogBinding.edtChetel.addTextChangedListener {

            if (it.toString() != "" && itemDialogBinding.edtChetel.hasFocus()) {
                itemDialogBinding.edtUzbSom.setText(
                    toUzs(it.toString().toDouble(), myCurrency.Rate.toDouble())
                )
            }
        }

        itemDialogBinding.edtUzbSom.addTextChangedListener {

            if (it.toString() != "" && itemDialogBinding.edtUzbSom.hasFocus()) {
                itemDialogBinding.edtChetel.setText(
                    toValyuta( myCurrency.Rate.toDouble(), it.toString().toDouble())
                )

            }
        }

        dialog.setView(itemDialogBinding.root)
        dialog.show()
    }

    private fun toUzs(valyuta:Double, uzSom:Double):String{
        return (valyuta*uzSom).toString()
    }

    private fun toValyuta(valyuta: Double, uzSom: Double):String{
        return (uzSom/valyuta).toString()
    }
}