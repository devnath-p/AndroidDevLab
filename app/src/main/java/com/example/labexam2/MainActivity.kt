package com.example.labexam2

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var tripList: LinearLayout
    private lateinit var listView: ScrollView
    private lateinit var mapContainer: FrameLayout
    private lateinit var btnList: MaterialButton
    private lateinit var btnMap: MaterialButton

    private var mapView: MapView? = null

    private var selectedTripId = -1
    private var pendingPhotoId = -1

    // Callback when full screen photo or detail photo is replaced
    private var onPhotoUpdatedCallback: (() -> Unit)? = null

    // Select multiple photos
    private val addPhotoPicker =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            if (selectedTripId != -1 && uris.isNotEmpty()) {
                uris.forEach { uri ->
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {}
                    db.addPhoto(selectedTripId, uri.toString())
                }

                refreshTrips()
                val currentTripId = selectedTripId
                db.getTrip(currentTripId)?.let {
                    showTripDetails(it)
                }
            }
        }

    // Select one photo for replacement
    private val editPhotoPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null && pendingPhotoId != -1) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {}

                db.updatePhoto(pendingPhotoId, uri.toString())
                pendingPhotoId = -1

                refreshTrips()

                val currentTripId = selectedTripId
                onPhotoUpdatedCallback?.invoke() ?: run {
                    if (currentTripId != -1) {
                        db.getTrip(currentTripId)?.let { showTripDetails(it) }
                    }
                }
            } else {
                pendingPhotoId = -1
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "TripJournal/1.0 (Android)"

        setContentView(R.layout.activity_main)

        // Apply WindowInsets padding to avoid overlap with status/notification bar and navigation bar
        val rootLayout = findViewById<View>(R.id.rootLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.setPadding(
                view.paddingLeft,
                statusBarInsets.top,
                view.paddingRight,
                navBarInsets.bottom
            )
            insets
        }

        db = DatabaseHelper(this)

        tripList = findViewById(R.id.tripList)
        listView = findViewById(R.id.listView)
        mapContainer = findViewById(R.id.mapContainer)
        btnList = findViewById(R.id.btnList)
        btnMap = findViewById(R.id.btnMap)

        btnList.setOnClickListener { showList() }
        btnMap.setOnClickListener { showMap() }

        showList()
    }

    // ---------------- NAVIGATION SWITCHER ----------------

    private fun showList() {
        listView.visibility = View.VISIBLE
        mapContainer.visibility = View.GONE

        // Update active tab styles
        btnList.setStyle(isPrimary = true)
        btnMap.setStyle(isPrimary = false)

        refreshTrips()
    }

    private fun showMap() {
        listView.visibility = View.GONE
        mapContainer.visibility = View.VISIBLE

        // Update active tab styles
        btnList.setStyle(isPrimary = false)
        btnMap.setStyle(isPrimary = true)

        if (mapView == null) {
            val osmMap = MapView(this).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(20.5937, 78.9629))
            }

            mapView = osmMap
            mapContainer.addView(
                osmMap,
                0,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        mapView?.let { displayMarkers(it) }
    }

    private fun MaterialButton.setStyle(isPrimary: Boolean) {
        if (isPrimary) {
            setBackgroundColor(getColor(R.color.accent_light))
            setTextColor(getColor(R.color.accent_dark))
            iconTint = getColorStateList(R.color.accent_dark)
        } else {
            setBackgroundColor(getColor(R.color.transparent))
            setTextColor(getColor(R.color.text_secondary))
            iconTint = getColorStateList(R.color.text_secondary)
        }
    }

    // ---------------- TRIP LIST ----------------

    private fun refreshTrips() {
        tripList.removeAllViews()
        val trips = db.getAllTrips()

        if (trips.isEmpty()) {
            val emptyLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(32, 120, 32, 120)

                val emptyIcon = ImageView(this@MainActivity).apply {
                    setImageResource(R.drawable.ic_map_view)
                    setColorFilter(getColor(R.color.accent))
                    layoutParams = LinearLayout.LayoutParams(120, 120)
                }

                val title = TextView(this@MainActivity).apply {
                    text = "No Trips Recorded Yet"
                    textSize = 20f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(getColor(R.color.text_primary))
                    gravity = android.view.Gravity.CENTER
                    setPadding(0, 24, 0, 8)
                }

                val subtitle = TextView(this@MainActivity).apply {
                    text = "Switch to Map View and long-press anywhere to add your first adventure!"
                    textSize = 14f
                    setTextColor(getColor(R.color.text_secondary))
                    gravity = android.view.Gravity.CENTER
                }

                val mapBtn = MaterialButton(this@MainActivity, null, com.google.android.material.R.attr.materialButtonStyle).apply {
                    text = "Go to Map View"
                    setIconResource(R.drawable.ic_map_view)
                    cornerRadius = (20 * resources.displayMetrics.density).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (32 * resources.displayMetrics.density).toInt()
                    }
                    setOnClickListener { showMap() }
                }

                addView(emptyIcon)
                addView(title)
                addView(subtitle)
                addView(mapBtn)
            }
            tripList.addView(emptyLayout)
            return
        }

        for (trip in trips) {
            val cardView = LayoutInflater.from(this).inflate(
                R.layout.item_trip_card,
                tripList,
                false
            )

            val imgCover = cardView.findViewById<ShapeableImageView>(R.id.imgTripCover)
            val tvTitle = cardView.findViewById<TextView>(R.id.tvTripTitle)
            val tvDate = cardView.findViewById<TextView>(R.id.tvTripDate)
            val tvLocation = cardView.findViewById<TextView>(R.id.tvTripLocation)
            val tvPhotoCount = cardView.findViewById<TextView>(R.id.tvPhotoCount)
            val tvNotes = cardView.findViewById<TextView>(R.id.tvTripNotes)

            tvTitle.text = trip.title
            tvDate.text = "Date: ${trip.date}"
            tvLocation.text = String.format(Locale.getDefault(), "Location: %.4f, %.4f", trip.latitude, trip.longitude)

            if (trip.notes.isNotBlank()) {
                tvNotes.text = trip.notes
                tvNotes.visibility = View.VISIBLE
            } else {
                tvNotes.visibility = View.GONE
            }

            val photos = db.getPhotos(trip.id)
            tvPhotoCount.text = "${photos.size} Photo${if (photos.size == 1) "" else "s"}"

            if (photos.isNotEmpty()) {
                try {
                    imgCover.setImageURI(Uri.parse(photos[0].uri))
                } catch (_: Exception) {
                    imgCover.setImageResource(R.drawable.ic_photo)
                }
            } else {
                imgCover.setImageResource(R.drawable.ic_photo)
            }

            cardView.setOnClickListener {
                showTripDetails(trip)
            }

            tripList.addView(cardView)
        }
    }

    // ---------------- MAP VIEW ----------------

    private fun displayMarkers(map: MapView) {
        map.overlays.clear()
        val trips = db.getAllTrips()

        for (trip in trips) {
            val marker = Marker(map).apply {
                position = GeoPoint(trip.latitude, trip.longitude)
                title = trip.title
                snippet = "Date: ${trip.date}\n${trip.notes}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                setOnMarkerClickListener { _, _ ->
                    db.getTrip(trip.id)?.let {
                        showTripDetails(it)
                    }
                    true
                }
            }
            map.overlays.add(marker)
        }

        val mapEventsOverlay = MapEventsOverlay(
            object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(point: GeoPoint): Boolean = false

                override fun longPressHelper(point: GeoPoint): Boolean {
                    showAddTripDialog(point)
                    return true
                }
            }
        )
        map.overlays.add(mapEventsOverlay)

        val copyrightOverlay = CopyrightOverlay(this).apply {
            setCopyrightNotice(map.tileProvider.tileSource.copyrightNotice)
        }
        map.overlays.add(copyrightOverlay)

        if (trips.size == 1) {
            val trip = trips.first()
            map.controller.setZoom(12.0)
            map.controller.animateTo(GeoPoint(trip.latitude, trip.longitude))
        } else if (trips.size > 1) {
            val north = trips.maxOf { it.latitude }
            val south = trips.minOf { it.latitude }
            val east = trips.maxOf { it.longitude }
            val west = trips.minOf { it.longitude }

            val latPad = if (north - south < 0.001) 0.01 else 0.0
            val lonPad = if (east - west < 0.001) 0.01 else 0.0

            val bounds = BoundingBox(
                north + latPad,
                east + lonPad,
                south - latPad,
                west - lonPad
            )

            map.post {
                map.zoomToBoundingBox(bounds, true, 100)
            }
        }

        map.invalidate()
    }

    // ---------------- ADD TRIP ----------------

    private fun showAddTripDialog(location: GeoPoint) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_trip, null)

        val tvHeader = dialogView.findViewById<TextView>(R.id.tvDialogHeader)
        val tvSubtext = dialogView.findViewById<TextView>(R.id.tvLocationSubtext)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTripTitle)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etTripDate)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etTripNotes)

        tvHeader.text = "Add New Trip"
        tvSubtext.text = String.format(Locale.getDefault(), "Location: %.4f, %.4f", location.latitude, location.longitude)
        etDate.setText(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()))

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save Trip", null)
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val title = etTitle.text.toString().trim()
                        val date = etDate.text.toString().trim()
                        val notes = etNotes.text.toString().trim()

                        if (title.isEmpty() || date.isEmpty()) {
                            Toast.makeText(this@MainActivity, "Title and date are required", Toast.LENGTH_SHORT).show()
                        } else {
                            val id = db.addTrip(
                                Trip(0, title, date, notes, location.latitude, location.longitude)
                            )
                            if (id != -1L) {
                                Toast.makeText(this@MainActivity, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
                                refreshTrips()
                                mapView?.let { displayMarkers(it) }
                                dismiss()
                            } else {
                                Toast.makeText(this@MainActivity, "Unable to save trip", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                show()
            }
    }

    // ---------------- TRIP DETAILS ----------------

    private fun showTripDetails(trip: Trip) {
        selectedTripId = trip.id

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_trip_details, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDetailsTitle)
        val tvDate = dialogView.findViewById<TextView>(R.id.tvDetailsDate)
        val tvLocation = dialogView.findViewById<TextView>(R.id.tvDetailsLocation)
        val tvNotes = dialogView.findViewById<TextView>(R.id.tvDetailsNotes)
        val tvGalleryHeader = dialogView.findViewById<TextView>(R.id.tvGalleryHeader)
        val tvNoPhotosHint = dialogView.findViewById<TextView>(R.id.tvNoPhotosHint)
        val galleryContainer = dialogView.findViewById<LinearLayout>(R.id.photoGalleryContainer)
        val btnAddPhotos = dialogView.findViewById<MaterialButton>(R.id.btnAddPhotos)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDetails)
        val btnEditTrip = dialogView.findViewById<MaterialButton>(R.id.btnEditTrip)
        val btnDone = dialogView.findViewById<MaterialButton>(R.id.btnDoneDetails)

        tvTitle.text = trip.title
        tvDate.text = "Date: ${trip.date}"
        tvLocation.text = String.format(Locale.getDefault(), "Location: %.4f, %.4f", trip.latitude, trip.longitude)
        tvNotes.text = if (trip.notes.isNotBlank()) "Notes: ${trip.notes}" else "No notes added."

        val photos = db.getPhotos(trip.id)
        tvGalleryHeader.text = "Photo Gallery (${photos.size})"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        fun populatePhotos() {
            galleryContainer.removeAllViews()
            val updatedPhotos = db.getPhotos(trip.id)
            tvGalleryHeader.text = "Photo Gallery (${updatedPhotos.size})"

            if (updatedPhotos.isEmpty()) {
                tvNoPhotosHint.visibility = View.VISIBLE
            } else {
                tvNoPhotosHint.visibility = View.GONE
                for ((index, photo) in updatedPhotos.withIndex()) {
                    val thumbView = LayoutInflater.from(this).inflate(R.layout.item_photo_thumbnail, galleryContainer, false)
                    val imgThumb = thumbView.findViewById<ImageView>(R.id.imgThumbnail)
                    val btnReplace = thumbView.findViewById<ImageButton>(R.id.btnReplacePhoto)
                    val btnDelete = thumbView.findViewById<ImageButton>(R.id.btnDeletePhoto)

                    try {
                        imgThumb.setImageURI(Uri.parse(photo.uri))
                    } catch (_: Exception) {
                        imgThumb.setImageResource(R.drawable.ic_photo)
                    }

                    // Click thumbnail to open Full-Screen Swipeable Viewer!
                    imgThumb.setOnClickListener {
                        showFullScreenGallery(updatedPhotos, index, trip) {
                            populatePhotos()
                            refreshTrips()
                        }
                    }

                    btnReplace.setOnClickListener {
                        selectedTripId = trip.id
                        pendingPhotoId = photo.id
                        onPhotoUpdatedCallback = {
                            populatePhotos()
                            refreshTrips()
                            onPhotoUpdatedCallback = null
                        }
                        editPhotoPicker.launch(arrayOf("image/*"))
                    }

                    btnDelete.setOnClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("Delete Photo")
                            .setMessage("Are you sure you want to delete this photo?")
                            .setPositiveButton("Delete") { _, _ ->
                                db.deletePhoto(photo.id)
                                populatePhotos()
                                refreshTrips()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }

                    galleryContainer.addView(thumbView)
                }
            }
        }

        populatePhotos()

        btnAddPhotos.setOnClickListener {
            selectedTripId = trip.id
            dialog.dismiss()
            addPhotoPicker.launch(arrayOf("image/*"))
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        btnDone.setOnClickListener { dialog.dismiss() }

        btnEditTrip.setOnClickListener {
            dialog.dismiss()
            showEditTripDialog(trip)
        }

        dialog.show()
    }

    // ---------------- FULL SCREEN SWIPEABLE GALLERY VIEWER ----------------

    private fun showFullScreenGallery(
        initialPhotos: ArrayList<TripPhoto>,
        startIndex: Int,
        trip: Trip,
        onGalleryChanged: () -> Unit
    ) {
        val photosList = ArrayList(initialPhotos)
        if (photosList.isEmpty()) return

        val viewerDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_full_screen_gallery, null)
        viewerDialog.setContentView(dialogView)

        val vpPhotos = dialogView.findViewById<ViewPager2>(R.id.vpPhotos)
        val tvCounter = dialogView.findViewById<TextView>(R.id.tvImageCounter)
        val tvTripTitle = dialogView.findViewById<TextView>(R.id.tvGalleryTripTitle)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseGallery)
        val btnReplace = dialogView.findViewById<ImageButton>(R.id.btnReplaceInViewer)
        val btnDelete = dialogView.findViewById<ImageButton>(R.id.btnDeleteInViewer)
        val btnPrev = dialogView.findViewById<ImageButton>(R.id.btnPrevPhoto)
        val btnNext = dialogView.findViewById<ImageButton>(R.id.btnNextPhoto)

        tvTripTitle.text = "${trip.title} • ${trip.date}"

        val adapter = FullScreenPhotoAdapter(photosList)
        vpPhotos.adapter = adapter

        fun updateNavigationUI(position: Int) {
            tvCounter.text = "${position + 1} / ${photosList.size}"
            btnPrev.visibility = if (position > 0) View.VISIBLE else View.GONE
            btnNext.visibility = if (position < photosList.size - 1) View.VISIBLE else View.GONE
        }

        vpPhotos.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavigationUI(position)
            }
        })

        if (startIndex in photosList.indices) {
            vpPhotos.setCurrentItem(startIndex, false)
            updateNavigationUI(startIndex)
        }

        btnPrev.setOnClickListener {
            if (vpPhotos.currentItem > 0) {
                vpPhotos.currentItem = vpPhotos.currentItem - 1
            }
        }

        btnNext.setOnClickListener {
            if (vpPhotos.currentItem < photosList.size - 1) {
                vpPhotos.currentItem = vpPhotos.currentItem + 1
            }
        }

        btnClose.setOnClickListener {
            viewerDialog.dismiss()
        }

        btnReplace.setOnClickListener {
            val currentPos = vpPhotos.currentItem
            if (currentPos in photosList.indices) {
                val photoToEdit = photosList[currentPos]
                selectedTripId = trip.id
                pendingPhotoId = photoToEdit.id
                onPhotoUpdatedCallback = {
                    val freshPhotos = db.getPhotos(trip.id)
                    photosList.clear()
                    photosList.addAll(freshPhotos)
                    adapter.notifyDataSetChanged()
                    if (photosList.isNotEmpty()) {
                        val newPos = currentPos.coerceAtMost(photosList.size - 1)
                        vpPhotos.setCurrentItem(newPos, false)
                        updateNavigationUI(newPos)
                    } else {
                        viewerDialog.dismiss()
                    }
                    onGalleryChanged()
                    onPhotoUpdatedCallback = null
                }
                editPhotoPicker.launch(arrayOf("image/*"))
            }
        }

        btnDelete.setOnClickListener {
            val currentPos = vpPhotos.currentItem
            if (currentPos in photosList.indices) {
                val photoToDelete = photosList[currentPos]
                AlertDialog.Builder(this)
                    .setTitle("Delete Photo")
                    .setMessage("Delete this photo from ${trip.title}?")
                    .setPositiveButton("Delete") { _, _ ->
                        db.deletePhoto(photoToDelete.id)
                        photosList.removeAt(currentPos)
                        adapter.notifyItemRemoved(currentPos)
                        if (photosList.isNotEmpty()) {
                            val nextPos = currentPos.coerceAtMost(photosList.size - 1)
                            vpPhotos.setCurrentItem(nextPos, true)
                            updateNavigationUI(nextPos)
                        } else {
                            viewerDialog.dismiss()
                        }
                        onGalleryChanged()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }

        viewerDialog.show()
    }

    // Adapter for ViewPager2 full-screen photo sliding
    private inner class FullScreenPhotoAdapter(private val photos: List<TripPhoto>) :
        RecyclerView.Adapter<FullScreenPhotoAdapter.PhotoViewHolder>() {

        inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgView: ImageView = view.findViewById(R.id.imgFullScreen)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_full_screen_photo, parent, false)
            return PhotoViewHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photo = photos[position]
            try {
                holder.imgView.setImageURI(Uri.parse(photo.uri))
            } catch (_: Exception) {
                holder.imgView.setImageResource(R.drawable.ic_photo)
            }
        }

        override fun getItemCount(): Int = photos.size
    }

    // ---------------- EDIT TRIP ----------------

    private fun showEditTripDialog(trip: Trip) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_trip, null)

        val tvHeader = dialogView.findViewById<TextView>(R.id.tvDialogHeader)
        val tvSubtext = dialogView.findViewById<TextView>(R.id.tvLocationSubtext)
        val etTitle = dialogView.findViewById<TextInputEditText>(R.id.etTripTitle)
        val etDate = dialogView.findViewById<TextInputEditText>(R.id.etTripDate)
        val etNotes = dialogView.findViewById<TextInputEditText>(R.id.etTripNotes)

        tvHeader.text = "Edit Trip"
        tvSubtext.text = String.format(Locale.getDefault(), "Location: %.4f, %.4f", trip.latitude, trip.longitude)

        etTitle.setText(trip.title)
        etDate.setText(trip.date)
        etNotes.setText(trip.notes)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete Trip", null)
            .create()
            .apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val title = etTitle.text.toString().trim()
                        val date = etDate.text.toString().trim()
                        val notes = etNotes.text.toString().trim()

                        if (title.isEmpty() || date.isEmpty()) {
                            Toast.makeText(this@MainActivity, "Title and date are required", Toast.LENGTH_SHORT).show()
                        } else {
                            db.updateTrip(trip.copy(title = title, date = date, notes = notes))
                            refreshTrips()
                            mapView?.let { displayMarkers(it) }
                            Toast.makeText(this@MainActivity, "Trip updated", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }

                    getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Delete Trip")
                            .setMessage("Are you sure you want to delete '${trip.title}' and all its photos?")
                            .setPositiveButton("Delete") { _, _ ->
                                db.deleteTrip(trip.id)
                                selectedTripId = -1
                                refreshTrips()
                                mapView?.let { displayMarkers(it) }
                                Toast.makeText(this@MainActivity, "Trip deleted", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
                show()
            }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }
}