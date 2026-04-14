package com.example.fitness_app.feature.food

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.MainViewModel
import com.example.fitness_app.core.datastore.CalorieEntry
import com.example.fitness_app.core.datastore.DailyProgress
import com.example.fitness_app.core.datastore.PrefsKeys
import com.example.fitness_app.core.datastore.decodeDailyProgressList
import com.example.fitness_app.core.datastore.decodeEntries
import com.example.fitness_app.core.datastore.encodeDailyProgressList
import com.example.fitness_app.core.datastore.encodeEntries
import com.example.fitness_app.core.datastore.prefsDataStore
import com.example.fitness_app.data.local.entity.FoodProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fitness_app.R
import kotlinx.coroutines.delay
import androidx.compose.material3.OutlinedTextField

@Composable
fun FoodScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var selectedProduct by remember { mutableStateOf<FoodProductEntity?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showAchievementAnimation by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.ensureFoodProductsSeeded()
    }

    val products = viewModel.foodProducts.collectAsState().value
    val filteredProducts = products.filter { product ->
        product.name.contains(searchQuery.trim(), ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Продукты",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Выберите продукт и порцию, чтобы добавить калории и БЖУ в дневной прогресс.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Поиск продукта") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))





            if (filteredProducts.isEmpty()) {
                Text(
                    text = "Ничего не найдено",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { product ->
                        FoodProductCard(
                            product = product,
                            onClick = {
                                selectedProduct = product
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showAchievementAnimation) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.lottie)
            )

            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(220.dp)
            )
        }
    }

    if (showDialog && selectedProduct != null) {
        val product = selectedProduct!!

        AlertDialog(
            onDismissRequest = {
                showDialog = false
                selectedProduct = null
            },
            title = {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Выберите порцию:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Будут добавлены калории, белки, жиры и углеводы.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PortionButton(
                            grams = 100,
                            kcal = calculateScaled(product.kcalPer100, 100),
                            onClick = {
                                addNutritionFromProduct(
                                    product = product,
                                    grams = 100,
                                    context = context,
                                    scope = scope,
                                    timeFormatter = timeFormatter,
                                    onFirstProductAchievementUnlocked = {
                                        showAchievementAnimation = true
                                        scope.launch {
                                            delay(1800)
                                            showAchievementAnimation = false
                                        }
                                    }
                                )
                                showDialog = false
                                selectedProduct = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        PortionButton(
                            grams = 200,
                            kcal = calculateScaled(product.kcalPer100, 200),
                            onClick = {
                                addNutritionFromProduct(
                                    product = product,
                                    grams = 200,
                                    context = context,
                                    scope = scope,
                                    timeFormatter = timeFormatter,
                                    onFirstProductAchievementUnlocked = {
                                        showAchievementAnimation = true
                                        scope.launch {
                                            delay(1800)
                                            showAchievementAnimation = false
                                        }
                                    }
                                )
                                showDialog = false
                                selectedProduct = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        PortionButton(
                            grams = 300,
                            kcal = calculateScaled(product.kcalPer100, 300),
                            onClick = {
                                addNutritionFromProduct(
                                    product = product,
                                    grams = 300,
                                    context = context,
                                    scope = scope,
                                    timeFormatter = timeFormatter,
                                    onFirstProductAchievementUnlocked = {
                                        showAchievementAnimation = true
                                        scope.launch {
                                            delay(1800)
                                            showAchievementAnimation = false
                                        }
                                    }
                                )
                                showDialog = false
                                selectedProduct = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    TextButton(
                        onClick = {
                            showDialog = false
                            selectedProduct = null
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Закрыть")
                    }
                }
            },
            dismissButton = {}
        )
    }
}

@Composable
private fun FoodProductCard(
    product: FoodProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImagePlaceholder()

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "На 100 г",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PortionChip(text = "${product.kcalPer100} ккал")
                    PortionChip(text = "Б ${product.proteinPer100}")
                    PortionChip(text = "Ж ${product.fatPer100}")
                    PortionChip(text = "У ${product.carbsPer100}")
                }
            }
        }
    }
}

@Composable
private fun ProductImagePlaceholder() {
    Box(
        modifier = Modifier
            .size(82.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        ) {
            Text(
                text = "Фото",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PortionChip(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PortionButton(
    grams: Int,
    kcal: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = "${grams}г\n${kcal} ккал",
            style = LocalTextStyle.current
        )
    }
}

private fun calculateScaled(per100: Int, grams: Int): Int {
    return ((per100 / 100f) * grams).roundToInt()
}

private fun addNutritionFromProduct(
    product: FoodProductEntity,
    grams: Int,
    context: Context,
    scope: CoroutineScope,
    timeFormatter: DateTimeFormatter,
    onFirstProductAchievementUnlocked: () -> Unit
) {
    scope.launch {
        val prefs = context.prefsDataStore().data.first()

        val wasFirstProductAchievementUnlocked =
            prefs[PrefsKeys.ACH_FIRST_PRODUCT_ADDED] ?: false

        val kcalToAdd = calculateScaled(product.kcalPer100, grams)
        val proteinToAdd = calculateScaled(product.proteinPer100, grams)
        val fatToAdd = calculateScaled(product.fatPer100, grams)
        val carbsToAdd = calculateScaled(product.carbsPer100, grams)

        val currentEaten = prefs[PrefsKeys.CAL_EATEN] ?: 0
        val currentProtein = prefs[PrefsKeys.PROTEIN_EATEN] ?: 0
        val currentFat = prefs[PrefsKeys.FAT_EATEN] ?: 0
        val currentCarbs = prefs[PrefsKeys.CARBS_EATEN] ?: 0
        val currentGoal = prefs[PrefsKeys.CAL_GOAL] ?: 2200

        val newEaten = currentEaten + kcalToAdd
        val newProtein = currentProtein + proteinToAdd
        val newFat = currentFat + fatToAdd
        val newCarbs = currentCarbs + carbsToAdd

        val savedEntriesRaw = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
        val entries = decodeEntries(savedEntriesRaw).toMutableList()

        entries.add(
            0,
            CalorieEntry(
                calories = kcalToAdd,
                time = LocalTime.now().format(timeFormatter)
            )
        )

        val today = LocalDate.now().toString()
        val history = decodeDailyProgressList(
            prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
        ).toMutableList()

        val updatedItem = DailyProgress(
            date = today,
            eatenCalories = newEaten,
            goalCalories = currentGoal,
            goalReached = newEaten >= currentGoal
        )

        val existingIndex = history.indexOfFirst { it.date == today }
        if (existingIndex >= 0) {
            history[existingIndex] = updatedItem
        } else {
            history.add(updatedItem)
        }

        context.prefsDataStore().edit { editPrefs ->
            editPrefs[PrefsKeys.CAL_EATEN] = newEaten
            editPrefs[PrefsKeys.PROTEIN_EATEN] = newProtein
            editPrefs[PrefsKeys.FAT_EATEN] = newFat
            editPrefs[PrefsKeys.CARBS_EATEN] = newCarbs
            editPrefs[PrefsKeys.CAL_ENTRIES] = encodeEntries(entries)
            editPrefs[PrefsKeys.DAILY_PROGRESS_HISTORY] =
                encodeDailyProgressList(history.sortedBy { it.date })
            editPrefs[PrefsKeys.ACH_FIRST_PRODUCT_ADDED] = true
        }
        if (!wasFirstProductAchievementUnlocked) {
            onFirstProductAchievementUnlocked()
        }

    }
}