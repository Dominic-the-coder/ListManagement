package com.example.listmanagement.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmanagement.R
import com.example.listmanagement.RecyclerViewAdapter.TodoRecycleViewAdapter
import com.example.listmanagement.database.TodoDatabase
import com.example.listmanagement.model.TodoEntity

class NewTodoFragment : Fragment(R.layout.new_todo_fragment) {

    private var sortOrder = "ascending"
    private var sortBy = "title"

    private var searchQuery: String = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    private var data: List<TodoEntity> = emptyList()

    private lateinit var dao: com.example.listmanagement.database.TodoDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dao = TodoDatabase.getDatabase(requireContext()).todoDao()

        // SEARCH FIX (this is what you were missing)
        val searchEditText = view.findViewById<EditText>(R.id.editTextText2)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                applySorting()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        observeData()

        view.findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            val dialog = SortDialogFragment(sortOrder, sortBy) { newSortOrder, newSortBy ->
                sortOrder = newSortOrder
                sortBy = newSortBy
                applySorting()
            }
            dialog.show(parentFragmentManager, "SortDialog")
        }

        view.findViewById<View>(R.id.button).setOnClickListener {
            requireActivity().findViewById<View>(R.id.detailsContainer).visibility = View.VISIBLE
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.detailsContainer, AddWordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeData() {
        dao.getByStatus("NEW").observe(viewLifecycleOwner) { list ->
            data = list
            applySorting()
        }
    }

    private fun applySorting() {

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