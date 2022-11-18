package pt.ipbeja.tp21.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.FragmentMyInsectsBinding
import pt.ipbeja.tp21.databinding.MyInsectsItemBinding
import pt.ipbeja.tp21.model.MainViewModel
import pt.ipbeja.tp21.model.db.Insects
import java.io.File

class MyInsectsFragment : Fragment() {

    //View Model
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentMyInsectsBinding

    private val adapter: InsectsAdapter = InsectsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.binding = FragmentMyInsectsBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        var myInsects = mainViewModel.getMyInsects(requireContext())

        adapter.insectsList = myInsects.toMutableList()

        //Checks if the List is Empty and shows a message in case of being true
        checkIfListIsEmpty()
        adapter.notifyDataSetChanged()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Fufills the adapter
        binding.insectsRecyclerView.adapter = adapter

        // "go back" button
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            //calls main fragment
            findNavController().navigate(MyInsectsFragmentDirections.actionMyInsectsFragmentToMainFragment())
        }

        /**
         * Sorts MyInsects list by Order
         */
        var sortOderder = 1
        var sortTime = 1
        binding.sortByOrderBtn.setOnClickListener {
            if (sortOderder == 1) {
                sortOderder = 0
                adapter.insectsList.sortBy { it.insectOrder } //ascendente
            } else {
                sortOderder = 1
                adapter.insectsList.sortByDescending { it.insectOrder } //descendente
            }
            adapter.notifyDataSetChanged()
        }
        /**
         * Sorts MyInsects list by Time
         */
        binding.sortByTimeBtn.setOnClickListener {
            if (sortTime == 1) {
                sortTime = 0
                adapter.insectsList.sortBy {
                    mainViewModel.getObservationTime(requireContext(), it.insectId)
                } //ascendente
            } else {
                sortTime = 1
                adapter.insectsList.sortByDescending {
                    mainViewModel.getObservationTime(requireContext(), it.insectId)
                } //descendente
            }
            adapter.notifyDataSetChanged()
        }

    }

    inner class InsectsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var insects: Insects
        private val binding = MyInsectsItemBinding.bind(view)

        init {
            binding.root.setOnClickListener {
                //Toast.makeText(requireContext(), "ID = " + insects.insectId, Toast.LENGTH_SHORT).show()
                findNavController().navigate(
                    MyInsectsFragmentDirections.actionMyInsectsFragmentToInsectDetailFragment(
                        insects.insectId
                    )
                )
            }

            binding.deleteInsectBtn.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.deleteInsectQuestion))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->

                        mainViewModel.deleteInsectById(requireContext(), insects.insectId)
                        mainViewModel.deleteAllObservationsByInsectId(
                            requireContext(),
                            insects.insectId
                        )

                        adapter.insectsList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        //Checks if the List is Empty and shows a message in case of being true
                        checkIfListIsEmpty()
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ ->

                    }
                    .show()
            }
        }

        fun bind(insects: Insects) {
            this.insects = insects

            binding.insectOrderTextView.text = insects.insectOrder
            binding.insectThumbnail.load(
                File(
                    mainViewModel.getInsectThumbnail(
                        requireContext(),
                        insects.insectId
                    )
                )
            )

            binding.insectTimeTextView.text = mainViewModel.getObservationTime(
                requireContext(),
                insects.insectId
            ).toString()
        }
    }

    inner class InsectsAdapter : RecyclerView.Adapter<InsectsViewHolder>() {

        var insectsList: MutableList<Insects> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsectsViewHolder {
            val v: View =
                LayoutInflater.from(parent.context).inflate(R.layout.my_insects_item, parent, false)
            return InsectsViewHolder(v)
        }

        override fun onBindViewHolder(holder: InsectsViewHolder, position: Int) {
            val insect = insectsList[position]
            holder.bind(insect)
        }

        override fun getItemCount() = insectsList.size

    }

    /**
     * Check if List is Empty and shows a message
     */
    fun checkIfListIsEmpty() {
        if (adapter.insectsList.size == 0) {
            binding.showMessage.visibility = View.VISIBLE
            binding.sortByOrderBtn.visibility = View.INVISIBLE
            binding.sortByTimeBtn.visibility = View.INVISIBLE
        } else {
            binding.showMessage.visibility = View.INVISIBLE
            binding.sortByOrderBtn.visibility = View.VISIBLE
            binding.sortByTimeBtn.visibility = View.VISIBLE
        }
    }


}

