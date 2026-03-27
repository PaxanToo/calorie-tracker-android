package com.example.fitness_app.app.navigation

import com.example.fitness_app.R

sealed interface BottomDestination {
    val title: String
    val iconId: Int
    val route: String
}

data object HomeDestination : BottomDestination {
    override val title = "Главная"
    override val iconId = R.drawable.home1
    override val route = "home"
}

data object ProfileDestination : BottomDestination {
    override val title = "Профиль"
    override val iconId = R.drawable.profil
    override val route = "profile"
}

data object ChatDestination : BottomDestination {
    override val title = "Чат"
    override val iconId = R.drawable.chat
    override val route = "chat"
}

data object AchievementsDestination : BottomDestination {
    override val title = "Достижения"
    override val iconId = R.drawable.achiv
    override val route = "achievements"
}

data object FoodDestination : BottomDestination {
    override val title = "Питание"
    override val iconId = R.drawable.xleb
    override val route = "food"
}