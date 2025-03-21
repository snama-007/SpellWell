package com.wordwell.spellbee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wordwell.spellbee.navigation.NavGraph
import com.wordwell.spellbee.navigation.Screen
import com.wordwell.spellbee.ui.theme.SpellBeeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpellBeeTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val showBottomBar = currentDestination?.hierarchy?.none { it.route == Screen.Onboarding.route } ?: false

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Book, contentDescription = "Learn") },
                                    label = { Text("Learn") },
                                    selected = currentDestination?.hierarchy?.any { it.route == Screen.Learn.route } == true,
                                    onClick = {
                                        navController.navigate(Screen.Learn.route) {
                                            popUpTo(Screen.Learn.route) { inclusive = true }
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Edit, contentDescription = "Practice") },
                                    label = { Text("Practice") },
                                    selected = currentDestination?.hierarchy?.any { it.route == Screen.Practice.route } == true,
                                    onClick = {
                                        navController.navigate(Screen.Practice.route) {
                                            popUpTo(Screen.Practice.route) { inclusive = true }
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "Word Bank") },
                                    label = { Text("Word Bank") },
                                    selected = currentDestination?.hierarchy?.any { it.route == Screen.WordBank.route } == true,
                                    onClick = {
                                        navController.navigate(Screen.WordBank.route) {
                                            popUpTo(Screen.WordBank.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
} 