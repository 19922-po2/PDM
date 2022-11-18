package pt.ipbeja.tp21.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.maps.model.LatLng
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.FragmentResultadoBinding
import pt.ipbeja.tp21.model.MainViewModel

class resultadoFragment : Fragment() {

    //Location Coordinates with Default Coordinates in case of the person forgetting to put location on the map
    private var insectsCoordinates: String = LatLng(38.018178, -7.876109).toString()

    //View Model
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentResultadoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentResultadoBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Goes back
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            mainViewModel.goBack()
            findNavController().navigate(resultadoFragmentDirections.actionResultadoFragmentToClassificacaoFragment())
        }

        //Populates CameraView
        binding.pictureTakenImageView.load(mainViewModel.getCameraImage())

        //Populates ResultView
        binding.pictureResultImageView.load(
            mainViewModel.getNextAnswer(
                requireContext(),
                0
            )["image"]
        )
        //Description
        val insectDescription = mainViewModel.getNextAnswer(requireContext(), 0)["description"]
        binding.resultDescriptionTExtView.text = insectDescription
        //Order
        val insectOrder = mainViewModel.getNextAnswer(requireContext(), 0)["ordem"]
        binding.orderDescriptionTextView.text = insectOrder

        binding.questionsListTextView.text = mainViewModel.getUserQuestionsList().toString()

        // opens the map in a separate fragment
        binding.openMapaBtn.setOnClickListener() {
            findNavController().navigate(
                resultadoFragmentDirections.actionResultadoFragmentToMapFragment(
                    -1
                )
            )
        }

        /**
         * Asks the user if he wants to save the current observation to the database
         */
        binding.saveDataBtn.setOnClickListener {
            this.insectsCoordinates = mainViewModel.getInsectsCoordinates()
            //Sets Location Asked to False
            mainViewModel.setLocationAsked(false)

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.saveInsectQuestion))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    //calls viewmodel method to save data to database
                    mainViewModel.saveDataToDatabase(
                        this.insectsCoordinates,
                        insectDescription,
                        insectOrder,
                        requireContext()
                    )
                    //Goes Back to the Main Screen
                    findNavController().navigate(resultadoFragmentDirections.actionResultadoFragmentToMainFragment())
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                }
                .show()
        }

        /**
         * Cancels the current observation
         * Resets the user selected options list
         */
        binding.cancelSaveDataBtn.setOnClickListener {
            //Sets Location Asked to False
            mainViewModel.setLocationAsked(false)
            mainViewModel.resetUserQuestionsList()

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.cancelObservationSaveQuestion))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    //Goes Back to the Main Screen
                    findNavController().navigate(resultadoFragmentDirections.actionResultadoFragmentToMainFragment())
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                }
                .show()
        }
    }


}
