package com.example.listmanagement.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmanagement.R
import com.example.listmanagement.RecyclerViewAdapter.TodoRecycleViewAdapter
import com.example.listmanagement.database.TodoDatabase
import com.example.listmanagement.model.TodoEntity

class CompletedWordFragment : Fragment(R.layout.completed_word_fragment) {

    private var sortOrder = "ascending"
    private var sortBy = "title"
    private var searchQuery = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    private var data: List<TodoEntity> = emptyList()

    private lateinit var dao: com.example.listmanagement.database.TodoDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        dao = TodoDatabase.getDatabase(requireContext()).todoDao()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val searchInput = view.findViewById<EditText>(R.id.editTextText2)
        searchInput.addTextChangedListener { text ->
            searchQuery = text.toString().trim()
            applyFilterAndSort()
        }

        val btnSort = view.findViewById<ImageButton>(R.id.imageButton3)
        btnSort.setOnClickListener {
            val dialog = SortDialogFragment(sortOrder, sortBy) { newSortOrder, newSortBy ->
                sortOrder = newSortOrder
                sortBy = newSortBy
                applyFilterAndSort()
            }
            dialog.show(parentFragmentManager, "SortDialog")
        }

        observeData()
    }

    private fun observeData() {
        dao.getByStatus("DONE").observe(viewLifecycleOwner) { list ->
            data = list
            applyFilterAndSort()
        }
    }

    private fun applyFilterAndSort() {

        val filtered = if (searchQuery.isEmpty()) {
            data
        } else {
            data.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }

        val sorted = if (sortBy == "title") {
            filtered.sortedBy { it.title.lowercase() }
        } else {
            filtered.sortedBy { it.title }
        }

        val finalList =
            if (sortOrder == "ascending") sorted else sorted.reversed()

        updateList(finalList)
    }

    private fun updateList(list: List<TodoEntity>) {

        if (list.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE

            recyclerView.adapter = TodoRecycleViewAdapter(list) { item ->

                val detailFragment = WordDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putInt("id", item.id)
                        putString("title", item.title)
                        putString("meaning", item.meaning)
                        putString("synonyms", item.synonyms)
                        putString("details", item.details)
                        putString("status", item.status)
                    }
                }

                requireActivity().findViewById<View>(R.id.detailsContainer).visibility =
                    View.VISIBLE

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.detailsContainer, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}