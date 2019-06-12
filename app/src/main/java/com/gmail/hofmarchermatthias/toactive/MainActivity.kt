package com.gmail.hofmarchermatthias.toactive

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.User
import com.gmail.hofmarchermatthias.toactive.about.AboutFragment
import com.gmail.hofmarchermatthias.toactive.edit.EditSampleFragment
import com.gmail.hofmarchermatthias.toactive.list.ListFragment
import com.gmail.hofmarchermatthias.toactive.map.MapFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ListFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener, AboutFragment.OnFragmentInteractionListener,EditSampleFragment.OnFragmentInteractionListener,
    FirebaseAuth.AuthStateListener {
    private lateinit var navView: NavigationView




    companion object{
        const val RC_SIGN_IN = 501
        const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        this.navView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    private fun handleAuthentication() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                ).build(), RC_SIGN_IN
        )
    }

    /**
     * Gets called at the Beginning, therefore we don't need explicit handleAuth call in onCreate
     */
    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if(p0.currentUser != null){
            navView.getHeaderView(0).tv_nav_email.text = p0.currentUser!!.email
            navView.getHeaderView(0).tv_nav_user.text = p0.currentUser!!.displayName
            Glide.with(this).load(p0.currentUser?.photoUrl.toString()).into(navView.getHeaderView(0).imgv_nav_picture)
        }else{
            handleAuthentication()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            RC_SIGN_IN-> onAuthResult(resultCode, data)
        }
    }

    private fun onAuthResult(resultCode: Int, data: Intent?) {
        //val response = IdpResponse.fromResultIntent(data)
        if(resultCode== Activity.RESULT_OK){
            Log.d(TAG, "Signed in")
        }else{
            Log.e(TAG, "Could not log in User")
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_listFragment)
            }
            R.id.nav_map->{
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_mapFragment)
            }

            R.id.nav_about -> {
                Navigation.findNavController(nav_host_fragment.view!!).navigate(R.id.action_global_aboutFragment)
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
