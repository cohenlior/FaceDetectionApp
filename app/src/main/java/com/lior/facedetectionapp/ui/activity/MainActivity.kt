package com.lior.facedetectionapp.ui.activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.lior.facedetectionapp.databinding.ActivityMainBinding
import com.lior.facedetectionapp.repository.FaceDetectionRepository
import com.lior.facedetectionapp.service.ForegroundDetectionService
import com.lior.facedetectionapp.ui.fragments.BottomNavFragmentAll
import com.lior.facedetectionapp.ui.fragments.BottomNavFragmentFaces
import com.lior.facedetectionapp.ui.fragments.BottomNavFragmentNoFaces
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModel
import com.lior.facedetectionapp.ui.viewmodel.FaceDetectionViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import android.content.IntentFilter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lior.facedetectionapp.R
import com.lior.facedetectionapp.utils.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageListPagerAdapter: ImageListPagerAdapter
    private lateinit var repository: FaceDetectionRepository
    private lateinit var viewModel: FaceDetectionViewModel
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var updateUIReciver: BroadcastReceiver
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val application = requireNotNull(this).application

        repository = FaceDetectionRepository.getInstance(application)

        val viewModelFactory = FaceDetectionViewModelFactory(repository)

        viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(FaceDetectionViewModel::class.java)

        binding.viewModel = viewModel


        initBroadcastReceiver()

        viewModel.isDetectionAllowed.observe(this, Observer {
            when (it) {
                true -> {
                    Intent(applicationContext, ForegroundDetectionService::class.java).also { intent ->
                        ContextCompat.startForegroundService(this, intent)
                    }
                }
                else -> Toast.makeText(
                    this, "Failed to load detection!",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        })

        imageListPagerAdapter =
            ImageListPagerAdapter(supportFragmentManager)
        pager.adapter = imageListPagerAdapter
        tab_layout.setupWithViewPager(pager)

        setSupportActionBar(toolbar)
    }

    private fun initBroadcastReceiver() {

        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        val filter = IntentFilter()

        filter.addAction(LOCAL_BROADCAST_ACTION)

        updateUIReciver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                val totalsImages = intent.getIntExtra(getString(R.string.total_images), -1)
                val facesDetected = intent.getIntExtra(getString(R.string.faces_detected), -1)

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(
                    getString(R.string.detect_result_msg, facesDetected, totalsImages)
                )
                    ?.setTitle(R.string.detection_complete)
                builder.show()
            }
        }
        localBroadcastManager.registerReceiver(updateUIReciver, filter)
    }

    class ImageListPagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val tabs = arrayListOf(
            ALL,
            FACES,
            NO_FACES
        )

        override fun getItem(position: Int): Fragment {
            return when (tabs[position]) {
                ALL -> BottomNavFragmentAll()
                FACES -> BottomNavFragmentFaces()
                else -> BottomNavFragmentNoFaces()
            }
        }

        override fun getCount(): Int {
            return tabs.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabs[position]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.menuItem = item
        when (item.itemId) {
            R.id.action_detect -> {
                viewModel.onDetectClicked()
                menuItem.isEnabled = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        localBroadcastManager.unregisterReceiver(this.updateUIReciver)
    }
}
