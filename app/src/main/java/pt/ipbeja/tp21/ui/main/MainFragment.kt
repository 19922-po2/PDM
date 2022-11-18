package pt.ipbeja.tp21.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pt.ipbeja.tp21.databinding.MainFragmentBinding
import pt.ipbeja.tp21.model.MainViewModel


class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding

    //MainViewModel has the lyfecycle of the main activity
    //https://youtu.be/THt9QISnIMQ
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = MainFragmentBinding.inflate(inflater).let {
        this.binding = it
        it.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // "go back" button
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            //do nothing
        }

        binding.classifyInsectsBtn.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToCameraFragment())
        }

        binding.myInsectsBtn.setOnClickListener{
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToMyInsectsFragment())
        }
    }
}