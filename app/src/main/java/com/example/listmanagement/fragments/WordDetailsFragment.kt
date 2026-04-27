package com.example.listmanagement.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.listmanagement.R
import com.example.listmanagement.database.TodoDatabase
import com.example.listmanagement.model.TodoEntity
import kotlinx.coroutines.launch

class WordDetailsFragment : Fragment(R.layout.word_details_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = TodoDatabase.getDatabase(requireContext()).todoDao()

        val id = arguments?.getInt("id", -1) ?: -1
        val title = arguments?.getString("title")
        val meaning = arguments?.getString("meaning")
        val synonyms = arguments?.getString("synonyms")
        val details = arguments?.getString("details")

        view.findViewById<TextView>(R.id.tvTitle).text = title
        view.findViewById<TextView>(R.id.tvMeaning).text = meaning
        view.findViewById<TextView>(R.id.tvSynonyms).text = synonyms
        view.findViewById<TextView>(R.id.tvDetails).text = details

        // BACK
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            requireActivity().findViewById<View>(R.id.detailsContainer).visibility = View.GONE
            requireActivity().supportFragmentManager.popBackStack()
        }

        // UPDATE
        view.findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            val updateFragment = AddWordFragment().apply {
                arguments = Bundle().apply {
                    putInt("id", id)
                    putString("title", title)
                    putString("meaning", meaning)
                    putString("synonyms", synonyms)
                    putString("details", details)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.detailsContainer, updateFragment)
                .addToBackStack(null)
                .commit()
        }

        // DELETE
        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Delete this word permanently?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->

                    lifecycleScope.launch {
                        dao.delete(
                            TodoEntity(
                                id = id,
                                title = title ?: "",
                                meaning = meaning,
                                synonyms = synonyms,
                                details = details ?: "",
                                status = "DONE"
                            )
                        )
                    }

                    requireActivity().findViewById<View>(R.id.detailsContainer).visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .show()
        }

        // DONE
        view.findViewById<Button>(R.id.btnDone).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("Mark this word as DONE?")
                .setPositiveButton("Yes") { _, _ ->

                    lifecycleScope.launch {
                        dao.update(
                            TodoEntity(
                                id = id,
                                title = title ?: "",
                                meaning = meaning,
                                synonyms = synonyms,
                                details = details ?: "",
                                status = "DONE"
                            )
                        )
                    }

                    requireActivity().findViewById<View>(R.id.detailsContainer).visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}