package com.soto.coffeelog_huila.ui

sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Onboarding : Screens("com/soto/coffeelog_huila/ui/onboarding")
    object Login : Screens("login")
    object Register : Screens("register")
    object RoleSelection : Screens("role_selection")
    object HomeProductor : Screens("home_productor")
    object HomeCatador : Screens("home_catador")
    object HomeAdmin : Screens("home_admin")
}
