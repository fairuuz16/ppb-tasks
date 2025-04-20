package com.example.currencyconverter

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.currencyconverter.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale // For formatting currency

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var exchangeRates: Map<String, Double> = emptyMap()
    private var currencyCodes: List<String> = emptyList()
    private var lastUpdated: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners() // Initial setup (empty for now)
        fetchExchangeRates()

        binding.btnConvert.setOnClickListener {
            performConversion()
        }
    }

    private fun setupSpinners() {
        // Create dummy adapters initially, they will be updated when data arrives
        val dummyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Loading..."))
        dummyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFromCurrency.adapter = dummyAdapter
        binding.spinnerToCurrency.adapter = dummyAdapter
    }

    private fun updateSpinners() {
        if (currencyCodes.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyCodes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerFromCurrency.adapter = adapter
            binding.spinnerToCurrency.adapter = adapter


            val defaultFrom = "USD"
            val defaultTo = "EUR"

            val fromPosition = currencyCodes.indexOf(defaultFrom)
            val toPosition = currencyCodes.indexOf(defaultTo)

            if (fromPosition != -1) {
                binding.spinnerFromCurrency.setSelection(fromPosition)
            }
            if (toPosition != -1) {
                binding.spinnerToCurrency.setSelection(toPosition)
            } else if (currencyCodes.isNotEmpty()) {
                // Fallback to the first currency if the default 'to' is not found
                binding.spinnerToCurrency.setSelection(0)
            }

        } else {
            Log.e("MainActivity", "Currency codes list is empty.")
            Toast.makeText(this, "Could not load currency list", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchExchangeRates() {
        binding.progressBar.visibility = View.VISIBLE // Show progress bar
        binding.btnConvert.isEnabled = false // Disable button while loading

        // Use lifecycleScope for coroutine tied to Activity lifecycle
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getLatestRates()

                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.result == "success") {
                        exchangeRates = apiResponse.rates
                        currencyCodes = exchangeRates.keys.toList().sorted() // Get and sort currency codes
                        lastUpdated = apiResponse.lastUpdateUtc
                        Log.d("MainActivity", "Rates fetched successfully: ${exchangeRates.size} currencies")
                        updateSpinners() // Update spinners with fetched data
                        updateLastUpdatedTimestamp()
                    } else {
                        showError("API returned an error: ${apiResponse.result}")
                    }
                } else {
                    showError("Failed to fetch rates. Code: ${response.code()}")
                }

            } catch (e: IOException) {
                // Network error (no internet, server unreachable)
                Log.e("MainActivity", "Network error: ${e.message}", e)
                showError("Network error. Please check your connection.")
            } catch (e: HttpException) {
                // HTTP error (4xx, 5xx)
                Log.e("MainActivity", "HTTP error: ${e.message}", e)
                showError("Could not retrieve data from server.")
            } catch (e: Exception) {
                // Other errors (parsing, etc.)
                Log.e("MainActivity", "Error fetching rates: ${e.message}", e)
                showError("An unexpected error occurred.")
            } finally {
                binding.progressBar.visibility = View.GONE // Hide progress bar
                binding.btnConvert.isEnabled = true // Re-enable button
            }
        }
    }

    private fun updateLastUpdatedTimestamp() {
        if (lastUpdated != null) {
            binding.tvLastUpdated.text = "Last Updated: $lastUpdated"
            binding.tvLastUpdated.visibility = View.VISIBLE
        } else {
            binding.tvLastUpdated.visibility = View.GONE
        }
    }

    private fun performConversion() {
        val amountString = binding.etAmount.text.toString()
        val fromCurrency = binding.spinnerFromCurrency.selectedItem as? String
        val toCurrency = binding.spinnerToCurrency.selectedItem as? String

        if (amountString.isBlank()) {
            binding.etAmount.error = "Please enter an amount"
            return
        }
        binding.etAmount.error = null // Clear error

        val amount = amountString.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Please enter a valid positive amount"
            return
        }
        binding.etAmount.error = null // Clear error


        if (fromCurrency == null || toCurrency == null || exchangeRates.isEmpty()) {
            showError("Currency data not loaded yet. Please wait or check connection.")
            // Optionally, try fetching rates again here
            // fetchExchangeRates()
            return
        }

        // --- Conversion Logic ---
        // The API provides rates relative to USD (the base_code in the example).
        // Formula: amountInTarget = (amountInBase / rateOfSource) * rateOfTarget
        // Since our base is USD:
        // amountInUSD = amount / rateOfFromCurrency (relative to USD)
        // result = amountInUSD * rateOfToCurrency (relative to USD)
        // Simplified: result = amount * (rateOfToCurrency / rateOfFromCurrency)

        val rateFrom = exchangeRates[fromCurrency]
        val rateTo = exchangeRates[toCurrency]

        if (rateFrom == null || rateTo == null || rateFrom == 0.0) {
            Log.e("MainActivity", "Rate not found for $fromCurrency or $toCurrency or fromRate is zero.")
            showError("Could not find exchange rate for the selected currencies.")
            return
        }

        val resultValue = amount * (rateTo / rateFrom)

        // Format the result nicely
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()) // Use default locale formatting
        numberFormat.maximumFractionDigits = 2 // Show up to 2 decimal places
        val formattedResult = numberFormat.format(resultValue)

        binding.tvResult.text = "$formattedResult $toCurrency"
        binding.tvResult.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        binding.tvResult.text = "" // Clear previous result on error
        binding.tvResult.visibility = View.INVISIBLE
    }
}