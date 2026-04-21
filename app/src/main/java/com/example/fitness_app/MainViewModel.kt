package com.example.fitness_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_app.data.local.db.AppDatabase
import com.example.fitness_app.data.local.entity.FoodProductEntity
import com.example.fitness_app.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDb(application)
    private val dao = db.getDao()

    val items = dao.getAllItemDb()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertItem(item: ItemEntity) {
        viewModelScope.launch {
            dao.insertItemDb(item)
        }
    }

    val foodProducts = dao.getAllFoodProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun ensureFoodProductsSeeded() {
        viewModelScope.launch {
            if (dao.foodProductsCount() == 0) {
                dao.insertFoodProducts(
                    listOf(
                        FoodProductEntity(1, "Куриная грудка", "Мясо и птица", 113, 24, 2, 0),
                        FoodProductEntity(2, "Индейка филе", "Мясо и птица", 130, 20, 6, 0),
                        FoodProductEntity(3, "Говядина", "Мясо и птица", 218, 19, 16, 0),
                        FoodProductEntity(4, "Свинина", "Мясо и птица", 263, 16, 22, 0),
                        FoodProductEntity(5, "Печень куриная", "Мясо и птица", 140, 20, 6, 1),
                        FoodProductEntity(6, "Печень говяжья", "Мясо и птица", 125, 20, 4, 4),

                        FoodProductEntity(7, "Лосось", "Рыба и морепродукты", 208, 20, 13, 0),
                        FoodProductEntity(8, "Тунец", "Рыба и морепродукты", 144, 23, 5, 0),
                        FoodProductEntity(9, "Хек", "Рыба и морепродукты", 86, 17, 2, 0),
                        FoodProductEntity(10, "Минтай", "Рыба и морепродукты", 72, 16, 1, 0),
                        FoodProductEntity(11, "Треска", "Рыба и морепродукты", 69, 16, 1, 0),
                        FoodProductEntity(12, "Скумбрия", "Рыба и морепродукты", 191, 18, 13, 0),
                        FoodProductEntity(13, "Креветки", "Рыба и морепродукты", 97, 22, 1, 0),
                        FoodProductEntity(14, "Крабовые палочки", "Рыба и морепродукты", 95, 6, 1, 15),

                        FoodProductEntity(15, "Яйцо куриное", "Яйца и молочные продукты", 157, 13, 12, 1),
                        FoodProductEntity(16, "Белок яичный", "Яйца и молочные продукты", 44, 11, 0, 1),
                        FoodProductEntity(17, "Творог 5%", "Яйца и молочные продукты", 121, 17, 5, 2),
                        FoodProductEntity(18, "Творог 9%", "Яйца и молочные продукты", 157, 17, 9, 2),
                        FoodProductEntity(19, "Молоко 2.5%", "Яйца и молочные продукты", 52, 3, 3, 5),
                        FoodProductEntity(20, "Кефир 2.5%", "Яйца и молочные продукты", 53, 3, 3, 4),
                        FoodProductEntity(21, "Йогурт натуральный", "Яйца и молочные продукты", 66, 4, 3, 5),
                        FoodProductEntity(22, "Сметана 15%", "Яйца и молочные продукты", 158, 3, 15, 3),
                        FoodProductEntity(23, "Сыр твёрдый", "Яйца и молочные продукты", 350, 24, 28, 0),
                        FoodProductEntity(24, "Моцарелла", "Яйца и молочные продукты", 280, 18, 22, 2),

                        FoodProductEntity(25, "Овсянка", "Крупы, каши, макароны", 352, 12, 6, 60),
                        FoodProductEntity(26, "Гречка", "Крупы, каши, макароны", 313, 13, 3, 62),
                        FoodProductEntity(27, "Рис белый", "Крупы, каши, макароны", 344, 7, 1, 79),
                        FoodProductEntity(28, "Рис бурый", "Крупы, каши, макароны", 337, 8, 2, 73),
                        FoodProductEntity(29, "Перловка", "Крупы, каши, макароны", 320, 9, 1, 74),
                        FoodProductEntity(30, "Пшено", "Крупы, каши, макароны", 348, 12, 3, 69),
                        FoodProductEntity(31, "Макароны", "Крупы, каши, макароны", 334, 10, 1, 70),
                        FoodProductEntity(32, "Спагетти", "Крупы, каши, макароны", 344, 10, 1, 72),

                        FoodProductEntity(33, "Хлеб белый", "Хлеб и выпечка", 265, 8, 3, 49),
                        FoodProductEntity(34, "Хлеб ржаной", "Хлеб и выпечка", 210, 7, 1, 41),
                        FoodProductEntity(35, "Батон", "Хлеб и выпечка", 262, 8, 3, 52),
                        FoodProductEntity(36, "Лаваш", "Хлеб и выпечка", 275, 9, 1, 56),
                        FoodProductEntity(37, "Хлебцы", "Хлеб и выпечка", 320, 11, 3, 64),

                        FoodProductEntity(38, "Картофель", "Овощи и зелень", 77, 2, 0, 16),
                        FoodProductEntity(39, "Морковь", "Овощи и зелень", 32, 1, 0, 7),
                        FoodProductEntity(40, "Свёкла", "Овощи и зелень", 43, 2, 0, 10),
                        FoodProductEntity(41, "Огурец", "Овощи и зелень", 15, 1, 0, 3),
                        FoodProductEntity(42, "Помидор", "Овощи и зелень", 20, 1, 0, 4),
                        FoodProductEntity(43, "Капуста белокочанная", "Овощи и зелень", 27, 2, 0, 5),
                        FoodProductEntity(44, "Брокколи", "Овощи и зелень", 34, 3, 0, 7),
                        FoodProductEntity(45, "Цветная капуста", "Овощи и зелень", 30, 3, 0, 5),
                        FoodProductEntity(46, "Кабачок", "Овощи и зелень", 24, 1, 0, 5),
                        FoodProductEntity(47, "Лук репчатый", "Овощи и зелень", 41, 1, 0, 8),
                        FoodProductEntity(48, "Болгарский перец", "Овощи и зелень", 27, 1, 0, 5),
                        FoodProductEntity(49, "Листья салата", "Овощи и зелень", 16, 1, 0, 2),

                        FoodProductEntity(50, "Банан", "Фрукты и ягоды", 89, 2, 1, 21),
                        FoodProductEntity(51, "Яблоко", "Фрукты и ягоды", 47, 0, 0, 10),
                        FoodProductEntity(52, "Груша", "Фрукты и ягоды", 42, 0, 0, 11),
                        FoodProductEntity(53, "Апельсин", "Фрукты и ягоды", 43, 1, 0, 8),
                        FoodProductEntity(54, "Мандарин", "Фрукты и ягоды", 38, 1, 0, 8),
                        FoodProductEntity(55, "Виноград", "Фрукты и ягоды", 72, 1, 1, 16),
                        FoodProductEntity(56, "Клубника", "Фрукты и ягоды", 41, 1, 0, 8),
                        FoodProductEntity(57, "Черника", "Фрукты и ягоды", 44, 1, 1, 8),

                        FoodProductEntity(58, "Фасоль", "Бобовые, орехи, прочее", 298, 21, 2, 47),
                        FoodProductEntity(59, "Чечевица", "Бобовые, орехи, прочее", 295, 24, 2, 46),
                        FoodProductEntity(60, "Миндаль", "Бобовые, орехи, прочее", 609, 19, 54, 13),
                        FoodProductEntity(61, "Грецкий орех", "Бобовые, орехи, прочее", 654, 15, 65, 7),
                        FoodProductEntity(62, "Арахис", "Бобовые, орехи, прочее", 551, 26, 45, 10),
                        FoodProductEntity(63, "Семена подсолнечника", "Бобовые, орехи, прочее", 601, 21, 53, 11),
                        FoodProductEntity(64, "Оливковое масло", "Бобовые, орехи, прочее", 898, 0, 100, 0),
                        FoodProductEntity(65, "Подсолнечное масло", "Бобовые, орехи, прочее", 899, 0, 100, 0)
                    )
                )
            }
        }
    }
}