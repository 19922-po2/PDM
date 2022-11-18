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
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.FragmentInsectDetailBinding
import pt.ipbeja.tp21.databinding.MyInsectDetailItemBinding
import pt.ipbeja.tp21.model.MainViewModel
import pt.ipbeja.tp21.model.db.Observations
import java.io.File

class InsectDetailFragment : Fragment() {

    private lateinit var binding: FragmentInsectDetailBinding
    private val args: InsectDetailFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter: ObservationsAdapter = ObservationsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentInsectDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.observationsRecyclerView.adapter = adapter

        binding.insectDetailsThumbnailImageView.load(
            File(
                mainViewModel.getInsectThumbnail(
                    requireContext(),
                    args.insectId
                )
            )
        )

        binding.insectDetailsLocationTextView.text =
            mainViewModel.getObservationLocation(requireContext(), args.insectId)

        binding.insectDetailsTimeTextView.text =
            mainViewModel.getObservationTime(requireContext(), args.insectId).toString()

        binding.insectDetailsOrderTextView.text =
            mainViewModel.getInsectsOrder(requireContext(), args.insectId)

        binding.insectDetailsDescriptionTextView.text =
            mainViewModel.getInsectsDescription(requireContext(), args.insectId)
    }

    override fun onResume() {
        super.onResume()

        var myObservations = mainViewModel.getMyObservationsById(requireContext(), args.insectId)
        adapter.observationsList = myObservations.toMutableList()
        adapter.notifyDataSetChanged()
    }

    inner class ObservationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var observations: Observations

        private val binding = MyInsectDetailItemBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                findNavController().navigate(
                    InsectDetailFragmentDirections.actionInsectDetailFragmentToObservationDetailsInsectFragment(
                        observations.insectObservationsId
                    )
                )
            }

            binding.deleteInsectDetailBtn.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.deleteObservationQuestion)
                    .setPositiveButton(R.string.yes) { _, _ ->

                        mainViewModel.deleteObservationById(
                            requireContext(),
                            observations.insectObservationsId
                        )
                        //Verifica se é a última observação que existe, caso seja
                        if (adapter.observationsList.size == 1) {
                            //Apaga a categoria de Insetos que não tem nada associado
                            mainViewModel.deleteInsectById(
                                requireContext(),
                                observations.insectIdForeingKey
                            )
                        }

                        //refresh adaptor
                        adapter.observationsList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                    }
                    .show()
            }
        }

        fun bind(observations: Observations) {
            this.observations = observations
            binding.insectDetailThumbnailImgageView.load(
                File(
                    mainViewModel.getObservationThumbnail(
                        requireContext(),
                        this.observations.insectObservationsId
                    )
                )
            )
            binding.insectDetailLocationTextView.text = mainViewModel.getObservationLocationByID(
                requireContext(),
                this.observations.insectObservationsId
            )
            binding.insectIDetailtemTimeTextView.text = mainViewModel.getObservationTimebyID(
                requireContext(),
                this.observations.insectObservationsId
            ).toString()
        }
    }

    inner class ObservationsAdapter : RecyclerView.Adapter<ObservationsViewHolder>() {

        var observationsList: MutableList<Observations> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationsViewHolder {
            val v: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_insect_detail_item, parent, false)
            return ObservationsViewHolder(v)
        }

        override fun onBindViewHolder(holder: ObservationsViewHolder, position: Int) {
            holder.bind(observationsList[position])
        }

        override fun getItemCount() = observationsList.size

    }


}

