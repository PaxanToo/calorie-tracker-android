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


@Composable
fun ScreenFood(
    viewModel: MainViewModel = viewModel()
) {
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
                        .clickable {
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
}
