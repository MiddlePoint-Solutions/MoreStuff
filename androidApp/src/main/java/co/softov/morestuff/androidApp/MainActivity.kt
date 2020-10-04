package co.softov.morestuff.androidApp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import co.softov.morestuff.android.BuildConfig
import co.softov.morestuff.android.R
import co.softov.morestuff.androidApp.presentation.content.ContentFragment
import co.softov.morestuff.androidApp.presentation.list.ListsFragment
import co.softov.morestuff.androidApp.presentation.settings.MainSettings
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var contentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setContentView(R.layout.activity_main)

        initViews()
        addContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        contentFragment = null
    }

    private fun initViews() {
        if (BuildConfig.DEBUG) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setSupportActionBar(findViewById(R.id.my_toolbar))
    }

    private fun addContent() {

        contentFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
        }

        contentFragment = ContentFragment()
        // contentFragment = PriorityFragment()

        contentFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_content, it, it.javaClass.simpleName)
                .commit()
        }
    }

    private fun showMainSettings() {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.frame_settings,
                MainSettings()
            )
            .commit()
    }

    private fun showTaskList() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .add(R.id.frame_content, ListsFragment(), ListsFragment::class.java.simpleName)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.iterator()?.forEach { it.isVisible = true }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        closeKeyboard()
        item?.let {
            when (it.itemId) {
                R.id.option_settings -> {
                    showMainSettings()
                }
            }
        }
        return false
    }

    private fun closeKeyboard() {
        // Check if no view has focus:
        val view = this.currentFocus
        view?.let { v ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            v.clearFocus()
        }
    }
}