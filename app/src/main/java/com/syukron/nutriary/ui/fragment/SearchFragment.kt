package com.syukron.nutriary.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.syukron.nutriary.R
import com.syukron.nutriary.data.model.Food
import com.syukron.nutriary.data.model.ListType
import com.syukron.nutriary.databinding.FragmentSearchBinding
import com.syukron.nutriary.imagesearch.ImageSearchActivity
import com.syukron.nutriary.ui.ModType
import com.syukron.nutriary.ui.adapter.FoodListAdapter
import kotlinx.coroutines.launch

class SearchFragment :
    BaseFragment<FragmentSearchBinding>(
        FragmentSearchBinding::inflate,
        lockDrawer = true,
        hasOptionsMenu = true
    ) {


    private var searchQuery = MutableLiveData<String?>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedViewModel.modType = ModType.ADD
        super.onViewCreated(view, savedInstanceState)

        val fabSearch: FloatingActionButton = binding.fabSearch
        fabSearch.setOnClickListener {
            navigateToCamera()
        }

        sharedViewModel.predictionResult.observe(viewLifecycleOwner) { prediction ->
            if (!prediction.isNullOrEmpty()) {
                searchQuery.value = prediction
            }
        }


    }

    override fun applyBinding(v: View): ApplyTo<FragmentSearchBinding> = {
        historyList.apply {
            sharedViewModel.getList(ListType.HISTORY)
                .observe(viewLifecycleOwner) { list ->
                    val adapter = this.adapter as FoodListAdapter
                    adapter.submitList(list)
                }
            adapter = FoodListAdapter(clickListener, longClickListener)
        }
        searchQuery.observe(viewLifecycleOwner) { query ->
            if (query != null) {
                // Set loading state
                searchLoading.show()
                historyList.alpha = 0.5f
                lifecycleScope.launch {
                    try {
                        sharedViewModel.searchFoodsWithQuery(query)
                        this@SearchFragment.findNavController()
                            .navigate(R.id.action_searchFragment_to_trackerFragment)
                    } catch (e: Exception) {
                        com.google.android.material.snackbar.Snackbar.make(
                            requireView(),
                            e.toString(),
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        ).show()
                        Log.d("TAG", "Terjadi kesalahan: " + Log.getStackTraceString(e))
                        searchLoading.hide()
                        historyList.alpha = 1.0f
                    }
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_bar_menu, menu)
        val searchIcon = menu.findItem(R.id.search_icon)
        val searchView = searchIcon.actionView as SearchView
        searchView.apply {
            queryHint = "Search your food here"
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchQuery.value = query
                    searchIcon.collapseActionView()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_button -> {
                sharedViewModel.clearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val clickListener: (Food) -> (Unit) = { food ->
        sharedViewModel.selectedFood = food
        val argument = bundleOf(
            "foodName" to food.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        )
        this@SearchFragment.findNavController()
            .navigate(R.id.action_searchFragment_to_foodFragment, argument)
    }

    private val longClickListener: (PopupMenu, Food, View) -> (Boolean) =
        { menu, food, _ ->
            menu.inflate(R.menu.search_options_menu)
            menu.setOnMenuItemClickListener {
                sharedViewModel.deleteFood(food)
                true
            }
            true
        }


    private fun navigateToCamera() {
        val argument = bundleOf("upButtonNeeded" to true)
        this@SearchFragment
            .findNavController()
            .navigate(
                R.id.action_searchFragment_to_cameraFragment,
                argument
            )
    }
}
