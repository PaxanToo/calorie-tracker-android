package com.example.fitness_app.feature.food

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.MainViewModel
import com.example.fitness_app.core.datastore.CalorieEntry
import com.example.fitness_app.core.datastore.PrefsKeys
import com.example.fitness_app.core.datastore.decodeEntries
import com.example.fitness_app.core.datastore.encodeEntries
import com.example.fitness_app.core.datastore.prefsDataStore
import com.example.fitness_app.data.local.entity.FoodProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun FoodScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var selectedProduct by remember { mutableStateOf<FoodProductEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureFoodProductsSeeded()
    }

    val products = viewModel.foodProducts.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Продукты",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            selectedProduct = product
                            showDialog = true
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "100г: ${product.kcal100} ккал | 200г: ${product.kcal200} | 300г: ${product.kcal300}"
                        )
                    }
                }
            }
        }
    }

    if (showDialog && selectedProduct != null) {
        val product = selectedProduct!!

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                selectedProduct = null
            },
            title = { Text(product.name) },
            text = { Text("Сколько съели?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        selectedProduct = null
                    }
                ) {
                    Text("Закрыть")
                }
            },
            dismissButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = product.kcal100,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("100г")
                    }

                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = product.kcal200,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("200г")
                    }

                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = product.kcal300,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("300г")
                    }
                }
            }
        )
    }
}

private fun addCaloriesFromProduct(
    kcalToAdd: Int,
    context: android.content.Context,
    scope: CoroutineScope,
    timeFormatter: DateTimeFormatter
) {
    scope.launch {
        val prefs = context.prefsDataStore().data.first()

        val currentEaten = prefs[PrefsKeys.CAL_EATEN] ?: 0
        val newEaten = currentEaten + kcalToAdd

        val savedEntriesRaw = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
        val entries = decodeEntries(savedEntriesRaw).toMutableList()

        entries.add(
            0,
            CalorieEntry(
                calories = kcalToAdd,
                time = LocalTime.now().format(timeFormatter)
            )
        )

        context.prefsDataStore().edit { editPrefs ->
            editPrefs[PrefsKeys.CAL_EATEN] = newEaten
            editPrefs[PrefsKeys.CAL_ENTRIES] = encodeEntries(entries)
        }
    }
}