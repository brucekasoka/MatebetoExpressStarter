package com.matebeto.express.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.matebeto.express.ui.components.BottomNavigationBar

@Composable
fun MainAppScaffold(
    navController: NavHostController,
    title: String = "Matebeto Express",
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    content: @Composable (paddingValues: androidx.compose.ui.unit.Dp) -> Unit
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                MatebetoTopBar(title = title) {
                    navController.navigate("settings")
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content(innerPadding.calculateTopPadding())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatebetoTopBar(
    title: String,
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}
