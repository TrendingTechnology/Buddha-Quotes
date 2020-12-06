package org.bandev.buddhaquotes

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.bandev.buddhaquotes.core.Colours
import org.bandev.buddhaquotes.core.Compatibility
import org.bandev.buddhaquotes.core.Languages
import org.bandev.buddhaquotes.databinding.AddlistContentBinding
import java.util.*

class AddQuote : AppCompatActivity() {

    private lateinit var binding: AddlistContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Colours().setAccentColor(this, window, resources)
        Compatibility().setNavigationBar(this, window, resources)
        Languages().setLanguage(this)

        //Setup view binding & force portrait mode
        binding = AddlistContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Setup toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val list = (intent.getStringExtra("list") ?: return).toString()

        val names = genList()

        val adapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.quotes_search, names)

        binding.listView.adapter = adapter

        binding.listView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->

                val pref = getSharedPreferences("List_system", 0)
                val editor = pref.edit()
                val listArr = pref.getString(list, "")
                val listArrFinal = LinkedList(listArr?.split("//"))
                val intent2 = Intent(this, ScrollingActivity::class.java)
                val mBundle = Bundle()
                mBundle.putString("list", list)
                intent2.putExtras(mBundle)
                if (listArrFinal.contains(binding.listView.getItemAtPosition(position) as String?)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        binding.root.performHapticFeedback(HapticFeedbackConstants.REJECT)
                    } else {
                        binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                    Snackbar.make(view, "This quote is already in the list!", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        binding.root.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                    listArrFinal.push(binding.listView.getItemAtPosition(position) as String?)
                    this.startActivity(intent2)
                }

                val stringOut = listArrFinal.joinToString(separator = "//")

                editor.putString(list, stringOut)
                editor.apply()
            }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })
    }

    private fun genList(): ArrayList<String> {
        val list = ArrayList<String>()
        val max = 237
        var i = 1
        while (i != max) {
            list.add(Quotes().random(i, this))
            i++
        }

        return list
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
