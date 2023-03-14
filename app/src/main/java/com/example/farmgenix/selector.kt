package com.example.farmgenix

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_selector.*

abstract class SelectorActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        private val breedsMap = mapOf(
            "Jersey" to Breed("Jersey", "Small", "High butterfat milk"),
            "Angus" to Breed("Angus", "Large", "Meat production"),
            "Holstein" to Breed("Holstein", "Large", "High milk production")
        )
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)

        // Set up spinner for selecting cow state
        ArrayAdapter.createFromResource(
            this,
            R.array.cow_states,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            state_spinner.adapter = adapter
        }
        state_spinner.onItemSelectedListener = this

        // Set up button for insemination recommendation
        inseminate_button.setOnClickListener {
            val selectedBreed = breed_edit_text.text.toString()
            val selectedState = state_spinner.selectedItem.toString()
            val recommendedBreed = recommendInseminationBreed(selectedBreed, selectedState)
            insemination_text_view.text = recommendedBreed?.name ?: "Unknown"
        }
        // Create local variable for breed_details_text_view
        val breedDetailsTextView = findViewById<TextView>(R.id.breed_details_text_view)

        // Show default breed details
        breedDetailsTextView.text = recommendInseminationBreed(null, "Milk production")?.toString() ?: ""
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Show breed details when cow state is selected
        val selectedState = parent?.getItemAtPosition(position).toString()
        val breed = recommendInseminationBreed(null, selectedState)
        breed_details_text_view.text = breed?.toString() ?: ""
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun recommendInseminationBreed(breedName: String?, cowState: String): Breed? {
        val matchingBreeds = breedsMap.filterValues { it.isSuitableForState(cowState) }
        return when {
            breedName != null && matchingBreeds.containsKey(breedName) -> matchingBreeds[breedName]
            matchingBreeds.isNotEmpty() -> matchingBreeds.values.first()
            else -> null
        }
    }
}

data class Breed(
    val name: String,
    val size: String,
    val purpose: String
) {
    fun isSuitableForState(cowState: String): Boolean {
        return when (cowState) {
            "Milk production" -> purpose == "High milk production"
            "Meat production" -> purpose == "Meat production"
            "Dual-purpose" -> purpose == "High milk production" && size == "Large"
            else -> false
        }
    }
}
