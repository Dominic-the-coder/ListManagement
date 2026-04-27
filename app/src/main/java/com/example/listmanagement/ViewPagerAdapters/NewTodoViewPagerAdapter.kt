package com.example.listmanagement.ViewPagerAdapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.listmanagement.fragments.CompletedWordFragment
import com.example.listmanagement.fragments.NewTodoFragment

class NewTodoViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewTodoFragment()
            1 -> CompletedWordFragment()
            else -> NewTodoFragment()
        }
    }
}