package pt.ipbeja.tp21.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import pt.ipbeja.tp21.databinding.FragmentCameraBinding
import pt.ipbeja.tp21.model.MainViewModel
import java.io.File

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCameraBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.camera.setLifecycleOwner(viewLifecycleOwner)


        binding.takePictureBtn.setOnClickListener {
            //Calls the method to take the picture
            binding.camera.takePicture()

        }

        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val folder = requireContext().filesDir
                //System.currentTimeMillis gets the absolute time
                val file = File(folder, "file${System.currentTimeMillis()}.jpg")

                mainViewModel.setCameraImage(file)

                result.toFile(file) {
                    findNavController().navigate(CameraFragmentDirections.actionCameraFragmentToClassificacaoFragment())
                }


            }
        })
    }



}