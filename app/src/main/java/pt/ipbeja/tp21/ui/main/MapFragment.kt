package pt.ipbeja.tp21.ui.main

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.FragmentMapBinding
import pt.ipbeja.tp21.model.MainViewModel

class MapFragment : Fragment(), OnMapReadyCallback {

    //View Model
    //mapview object
    private lateinit var mMapView: MapView
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentMapBinding
    //Location Coordinates with Default Coordinates in case of the person forguetting to put location on the map
    private var insectsCoordinates: (String) = LatLng(38.018178, -7.876109).toString()
    private val args : MapFragmentArgs by navArgs()
    //Locations
    private lateinit var task: Task<Location>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentMapBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        task = LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
        //Shows Data from Google Maps
        mMapView = view.findViewById(R.id.map_view)
        mMapView.onCreate(savedInstanceState?.getBundle("MapViewBundleKey"))
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {

        var latLng = LatLng(38.018178, -7.876109)

        //If authorization to locate user is granted then modifies location and show's it
        task.addOnSuccessListener {
            //Checks if the location is null or not
            if(it.longitude != null) {
                latLng = LatLng(it.latitude, it.longitude)
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
        task.addOnFailureListener {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }

        /**
         * Checks if it is called by "resultadoFragment" or "ObservationDetailsInsectFragment"
         *
         */
        if(args.insectId.toInt() != -1) {
            val coordinates = mainViewModel.getObservationLocationByID(requireContext(), args.insectId)
            latLng = LatLng(
                coordinates.substring(coordinates.indexOf("(") + 1, coordinates.indexOf(","))
                    .toDouble(),
                coordinates.substring(coordinates.indexOf(",") + 1, coordinates.indexOf(")")).toDouble()
            )
            map.addMarker(MarkerOptions().position(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
        //getClientLocation(latLng)

        map.setOnMapClickListener {
            map.clear()
            val marker = map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(getString(R.string.newInsect))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            this.insectsCoordinates = marker?.position.toString()
            mainViewModel.setInsectsCoordinates(this.insectsCoordinates)
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

}