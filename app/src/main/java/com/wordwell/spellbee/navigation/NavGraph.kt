package com.wordwell.spellbee.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wordwell.spellbee.ui.screens.onboarding.OnboardingScreen
import com.wordwell.spellbee.ui.screens.learn.LearnScreen
import com.wordwell.spellbee.ui.screens.practice.PracticeScreen
import com.wordwell.spellbee.ui.screens.wordbank.WordBankScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Learn : Screen("learn")
    object Practice : Screen("practice")
    object WordBank : Screen("word_bank")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Learn.route) {
            LearnScreen(navController)
        }
        composable(Screen.Practice.route) {
            PracticeScreen(navController)
        }
        composable(Screen.WordBank.route) {
            WordBankScreen(navController)
        }
    }
} 