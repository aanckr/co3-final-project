package com.example.nirvana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.amadeus.Amadeus
import com.amadeus.Params
import com.example.nirvana.databinding.ActivityHomeBinding
import com.example.nirvana.databinding.ActivityScrollViewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import java.util.*
import java.math.BigDecimal
import java.math.RoundingMode


class HomeActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        updateScrollView()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_collections -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Collections", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_contact_us -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Contact Us", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_log_out -> {
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // Profile
        val profileImg: View = findViewById(R.id.profile_img)
        profileImg.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.bikeIcon.setOnClickListener {
            val intent = Intent(this, ExploringActivity::class.java).apply {
                putExtra("category", "RESTAURANT")
            }
            startActivity(intent)
        }
        binding.hikingIcon.setOnClickListener {
            val intent = Intent(this, ExploringActivity::class.java).apply {
                putExtra("category", "SHOPPING")
            }
            startActivity(intent)
        }
        binding.sightseeingIcon.setOnClickListener {
            val intent = Intent(this, ExploringActivity::class.java).apply {
                putExtra("category", "SIGHTS")
            }
            startActivity(intent)
        }
        binding.viewAll.setOnClickListener {
            val intent = Intent(this, ExploringActivity::class.java).apply {
                putExtra("category", "ALL")
            }
            startActivity(intent)
        }

        binding.myLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)

            } else {
                getLocation()
            }
        }

        // Bottom navigation bar
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    //Already in home, do nothing
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.explore -> {
                    startActivity(Intent(this, ExploringActivity::class.java))
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        val geocoder = Geocoder(this, Locale.getDefault())

                        try {
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val cityName = addresses[0].locality
                                binding.textViewLocation.text = Editable.Factory.getInstance().newEditable(cityName)

                                Toast.makeText(this, "Stadt: $cityName, Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
                                getActivities(latitude, longitude)
                            } else {
                                Toast.makeText(this, "Adresse nicht gefunden", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: IOException) {
                            Toast.makeText(this, "Geocoder-Dienst nicht verfügbar", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Standort nicht verfügbar", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Fehler beim Abrufen des Standorts", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getActivities(latitude: Double, longitude: Double) {
        val amadeus = Amadeus.builder("HDFV1svGcpeg0kMymEmPVGkU4JNf5h7v", "dWVYpb8M8wIm1hzc").build()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    amadeus.referenceData.locations.pointsOfInterest.get(
                        Params.with("latitude", latitude)
                            .and("longitude", longitude)
                            .and("radius", 20)
                    )
                }
                saveActivitiesInDB(response, latitude, longitude)
                updateScrollView()
            }catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Fehler beim Abrufen der Aktivitäten", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveActivitiesInDB(response: Array<com.amadeus.resources.PointOfInterest>, latitude: Double, longitude: Double) {
        Toast.makeText(this, "size: ${response.size}", Toast.LENGTH_LONG).show()
        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.setValue(null).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Activities deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        for (activity in response) {
            val name = activity.name
            val category = activity.category
            val rank = Random().nextInt(5) + 1
            val tags = activity.tags.asList()
            val activityLatitude = activity.geoCode.latitude
            val activityLongitude = activity.geoCode.longitude
            val id = UUID.randomUUID().toString()
            val distance = calculateDistance(latitude, longitude, activityLatitude, activityLongitude)

            database = FirebaseDatabase.getInstance().getReference("Activities")
            val activity = Activities(name, category, rank, tags, activityLatitude, activityLongitude, id, distance)
            database.child(name).setValue(activity).addOnSuccessListener {
                Toast.makeText(this, "Activity added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val originLat = Math.toRadians(lat1)
        val destinationLat = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2) +
                sin(dLon / 2).pow(2) * cos(originLat) * cos(destinationLat)
        val c = 2 * asin(sqrt(a))

        val distance = earthRadius * c

        return BigDecimal(distance).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }
    private fun updateScrollView(){
        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.get().addOnSuccessListener {

            if (it.exists()) {
                val activities = it.children.map { it.getValue(Activities::class.java)!! }
                val sortedActivities = activities.sortedBy { it.distance }
                binding.linearHorizontallyActivities.removeAllViews()
                for (activity in sortedActivities) {
                    val activityBinding = ActivityScrollViewBinding.inflate(layoutInflater)
                    val text = activity.name?.let {
                        if (it.length > 23) it.substring(0, 23) + "..." else it
                    }
                    activityBinding.itemName.text = text
                    activityBinding.itemLocation.text = activity.distance.toString() + " km"
                    activityBinding.activityImg.setImageResource(R.drawable.mountains)
                    when (activity.category) {
                        "SIGHTS" -> activityBinding.activityImg.setImageResource(R.drawable.sight)
                        "RESTAURANT" -> activityBinding.activityImg.setImageResource(R.drawable.restaurant)
                        "SHOPPING" -> activityBinding.activityImg.setImageResource(R.drawable.shopping)
                        else -> activityBinding.activityImg.setImageResource(R.drawable.mountains)
                    }
                    activityBinding.root.setOnClickListener{
                        navigationToActivity(activity.name)
                    }

                    binding.linearHorizontallyActivities.addView(activityBinding.root)
                }
                Toast.makeText(this, "Activity added srollview", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigationToActivity(activityName: String?) {
        val intent = Intent(this, RecommendationActivity::class.java).apply {
            putExtra("activity", activityName)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if (toggle.onOptionsItemSelected(item)) {
           return true
       }
        return super.onOptionsItemSelected(item)
    }
}