package pt.ipbeja.tp21.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.model.LatLng
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.FragmentObservationDetailsInsectBinding
import pt.ipbeja.tp21.model.MainViewModel
import java.io.File


class ObservationDetailsInsectFragment : Fragment() {

    private lateinit var binding: FragmentObservationDetailsInsectBinding
    private val args: ObservationDetailsInsectFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()

    //Location Coordinates with Default Coordinates in case of the person forguetting to put location on the map
    private var insectsCoordinates: String = LatLng(38.018178, -7.876109).toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentObservationDetailsInsectBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.observationDetailsImageView.load(
            File(
                mainViewModel.getObservationThumbnail(
                    requireContext(),
                    args.insectId
                )
            )
        )

        binding.insectOrderTextViewObservationDetails.text =
            mainViewModel.getObservationOrder(requireContext(), args.insectId)

        binding.insectDescriptionTextViewObservationDetails.text =
            mainViewModel.getObservationDescription(requireContext(), args.insectId)

        binding.insectTimeTextViewObservationDetails.text =
            mainViewModel.getObservationTimebyID(requireContext(), args.insectId).toString()

        binding.openMapBtn.setOnClickListener() {
            findNavController().navigate(
                ObservationDetailsInsectFragmentDirections.actionObservationDetailsInsectFragmentToMapFragment(
                    args.insectId
                )
            )
        }

        binding.updateDataBtn.setOnClickListener {

            this.insectsCoordinates = mainViewModel.getInsectsCoordinates()

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.updateInsectQuestion))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->

                    //Calls viewmodel method to save data to database
                    mainViewModel.updateObservationLocation(
                        requireContext(),
                        args.insectId,
                        this.insectsCoordinates
                    )

                    //Goes Back to the Main Screen
                    findNavController().navigate(ObservationDetailsInsectFragmentDirections.actionObservationDetailsInsectFragmentToMyInsectsFragment())
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->

                }
                .show()
        }

        binding.observationDetailsCancelDataBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.cancelObservationUpdateQuestion))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    //Goes Back to the Main Screen
                    findNavController().navigate(ObservationDetailsInsectFragmentDirections.actionObservationDetailsInsectFragmentToMyInsectsFragment())
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                }
                .show()
        }
    }
}