package com.example.fitness_app.food

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.MainViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import com.example.fitness_app.DATA.PrefsKeys
import com.example.fitness_app.DATA.prefsDataStore
import com.example.fitness_app.DATA.decodeEntries
import com.example.fitness_app.DATA.encodeEntries
import com.example.fitness_app.DATA.CalorieEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.LocalIndication


@Composable
fun ScreenFood(
    viewModel: MainViewModel = viewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }


    var selectedProduct by remember { mutableStateOf<FoodProductDb?>(null) }
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


        Text("Продукты", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))



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


                    Column(Modifier.padding(14.dp)) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("100г: ${product.kcal100} ккал | 200г: ${product.kcal200} | 300г: ${product.kcal300}")


                    }
                }
            }
        }
    }

    if (showDialog && selectedProduct != null) {
        val p = selectedProduct!!

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                selectedProduct = null
            },
            title = { Text(p.name) },
            text = { Text("Сколько съели?") },
            confirmButton = {
                // тут можно оставить пустым, потому что всё через кнопки ниже
                TextButton(onClick = {
                    showDialog = false
                    selectedProduct = null
                }) { Text("Закрыть") }
            },
            dismissButton = {
                // ТРИ КНОПКИ 100/200/300
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = p.kcal100,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("100г") }

                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = p.kcal200,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("200г") }

                    Button(
                        onClick = {
                            addCaloriesFromProduct(
                                kcalToAdd = p.kcal300,
                                context = context,
                                scope = scope,
                                timeFormatter = timeFormatter
                            )
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("300г") }
                }
            }
        )
    }


}

private fun addCaloriesFromProduct(
    kcalToAdd: Int,
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
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
