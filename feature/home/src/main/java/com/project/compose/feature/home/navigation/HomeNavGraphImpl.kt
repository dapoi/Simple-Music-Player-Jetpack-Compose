package com.project.compose.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.project.compose.core.navigation.base.BaseNavGraph
import com.project.compose.core.navigation.helper.composableScreen
import com.project.compose.core.navigation.route.HomeGraph
import com.project.compose.feature.home.screen.HomeScreen
import javax.inject.Inject

class HomeNavGraphImpl @Inject constructor() : BaseNavGraph {
    override fun NavGraphBuilder.createGraph(navController: NavController) {
        composableScreen<HomeGraph.HomeLandingRoute> { HomeScreen() }
    }
}