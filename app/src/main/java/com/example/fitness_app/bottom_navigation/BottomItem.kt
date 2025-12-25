package com.example.fitness_app.bottom_navigation

import com.example.fitness_app.R

sealed interface BottomItem {
    val title: String
    val iconId: Int
    val route: String
}

data object Home : BottomItem {
    override val title = "Главная"
    override val iconId = R.drawable.home1
    override val route = "home"
}
data object Page2 : BottomItem {
    override val title = "Страница 2"
    override val iconId = R.drawable.profil
    override val route = "page2"
}
data object Page3 : BottomItem {
    override val title = "Страница 3"
    override val iconId = R.drawable.chat
    override val route = "page3"
}
data object Page4 : BottomItem {
    override val title = "Страница 4"
    override val iconId = R.drawable.achiv
    override val route = "page4"
}

data object Page5 : BottomItem {
    override val title = "Страница 5"
    override val iconId = R.drawable.xleb
    override val route = "page5"
}