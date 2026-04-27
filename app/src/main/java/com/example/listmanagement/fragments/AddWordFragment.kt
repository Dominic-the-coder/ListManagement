package com.example.listmanagement.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.listmanagement.R
import com.example.listmanagement.database.TodoDatabase
import com.example.listmanagement.model.TodoEntity
import kotlinx.coroutines.launch

class AddWordFragment : Fragment(R.layout.add_word_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Get all input fields from layout
        val AddTitle    = view.findViewById<EditText>(R.id.AddTitle)
        val AddMeaning  = view.findViewById<EditText>(R.id.AddMeaning)
        val AddSynonyms = view.findViewById<EditText>(R.id.AddSynonyms)
        val AddDetails  = view.findViewById<EditText>(R.id.AddDetails)
        val AddBtnAdd   = view.findViewById<Button>(R.id.AddBtnSubmit)
        val tvHeader    = view.findViewById<TextView>(R.id.tvHeader)

        // 2. Initialize database
        val dao = TodoDatabase.getDatabase(requireContext()).todoDao()

        // 3. Check if opened in UPDATE mode
        val originalId    = arguments?.getInt("id", -1) ?: -1
        val originalTitle = arguments?.getString("title")
        val isEditMode    = originalTitle != null

        // 4. Pre-fill fields if UPDATE mode
        if (isEditMode) {
            tvHeader.text  = "Update Word"
            AddBtnAdd.text = "Update"
            AddTitle.setText(originalTitle)
            AddMeaning.setText(arguments?.getString("meaning"))
            AddSynonyms.setText(arguments?.getString("synonyms"))
            AddDetails.setText(arguments?.getString("details"))
        }

        // 5. Handle submit button click
        AddBtnAdd.setOnClickListener {

            val title    = AddTitle.text.toString().trim()
            val meaning  = AddMeaning.text.toString().trim()
            val synonyms = AddSynonyms.text.toString().trim()
            val details  = AddDetails.text.toString().trim()

            if (title.isEmpty()) {
                AddTitle.error = "Title is required"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (isEditMode) {
                    // UPDATE: modify existing data
                    dao.update(
                        TodoEntity(
                            id       = originalId,
                            title    = title,
                            meaning  = meaning.ifEmpty { null },
                            synonyms = synonyms.ifEmpty { null },
                            details  = details,
                            status   = "NEW"
                        )
                    )
                } else {
                    // ADD: insert new data
                    dao.insert(
                        TodoEntity(
                            title = title,
                            meaning = meaning.ifEmpty { null },
                            synonyms = synonyms.ifEmpty { null },
                            details = details,
                            status = "NEW"
                        )
                    )
                }

                // 6. Return to previous screen after saving
                requireActivity().runOnUiThread {
                    requireActivity().findViewById<View>(R.id.detailsContainer).visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}