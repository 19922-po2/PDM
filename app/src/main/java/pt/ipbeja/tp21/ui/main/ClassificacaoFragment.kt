package pt.ipbeja.tp21.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import pt.ipbeja.tp21.databinding.FragmentClassificacaoBinding
import pt.ipbeja.tp21.model.MainViewModel

class ClassificacaoFragment : Fragment() {

    //private val args: ClassificacaoFragmentArgs by navArgs()
    private lateinit var binding: FragmentClassificacaoBinding

    //View Model
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentClassificacaoBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Asks Location just once
        if (!mainViewModel.getLocationAsked()) {
            //Checks if the user wants to save current location
            getClientLocation()
            mainViewModel.setLocationAsked(true)
        }

        // "go back" button
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {

            if (mainViewModel.getUserQuestionsListSize() == 1) {
                //if its the first question the "go back" btn returns to the initial screen
                findNavController().navigate(ClassificacaoFragmentDirections.actionClassificacaoFragmentToMainFragment())
            } else {
                findNavController().navigate(ClassificacaoFragmentDirections.actionClassificacaoFragmentSelf())
            }
            mainViewModel.goBack()
        }

        /**
         * checks if the classification has ended or not
         * When it ends, opens the final fragment with the result
         */
        if (this.mainViewModel.getEndOfClassification() == 1) {
            findNavController().navigate(ClassificacaoFragmentDirections.actionClassificacaoFragmentToResultadoFragment())
        } else {
            //Load CameraImage From ViewModel
            binding.cameraImageView.load(this.mainViewModel.getCameraImage())

            //changes answers textViews
            binding.questionTextView.text = mainViewModel.getQuestion(requireContext())
            binding.answer1TextView.text = mainViewModel.getAnswers1(requireContext())
            binding.answer2TextView.text = mainViewModel.getAnswers2(requireContext())

            //changes answers imageviews
            binding.answer1ImageView.load(mainViewModel.getImageAnswers1(requireContext()))
            binding.answer2ImageView.load(mainViewModel.getImageAnswers2(requireContext()))

            //answer 1
            binding.question1FrameLayout.setOnClickListener {
                val nq = mainViewModel.getNextAnswer(requireContext(), 0)
                findNavController().navigate(ClassificacaoFragmentDirections.actionClassificacaoFragmentSelf())
            }
            //answer 2
            binding.question2FrameLayout.setOnClickListener {
                val nq = mainViewModel.getNextAnswer(requireContext(), 1)
                findNavController().navigate(ClassificacaoFragmentDirections.actionClassificacaoFragmentSelf())
            }
        }
    }

    /**
     * Function that asks if the user wants to save his location
     * https://www.youtube.com/watch?v=Iq9yQmVOThE
     */
    private fun getClientLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
            return
        }
    }
}