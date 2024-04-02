package ca.uqac.fogmap.ui.screens

import TitledBubbleListPage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ca.uqac.fogmap.data.model.LoggedAccountViewModel
import ca.uqac.fogmap.ui.screens.account.login.LoginScreen
import ca.uqac.fogmap.ui.screens.account.registration.RegistrationScreen
import ca.uqac.fogmap.ui.screens.map.MapScreen_EntryPoint

@Composable
fun FogmapNavigationGraph(
    navController: NavHostController,
    loggedAccountViewModel: LoggedAccountViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        NavHost(navController = navController, startDestination = Routes.WELCOME_SCREEN) {
            composable(Routes.USER_INPUT_SCREEN) {
                UserInputScreen(navController)
            }

            composable(Routes.MAP_SCREEN) {
                MapScreen_EntryPoint()
            }

            composable(Routes.WELCOME_SCREEN) {
                WelcomeScreen()
            }

            composable(Routes.VISITED_LOCATION_SCREEN) {
                TitledBubbleListPage(navController)
            }

            composable(Routes.ADD_LOCATION) {
                AddLocation(navController)
            }

            composable(
                route = "${Routes.LOCATION_INFORMATION}/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "John Doe"
                LocationInformation(
                    name = name,
                )
            }

            composable(Routes.LOGIN_SCREEN) {
                LoginScreen(
                    onNavigateToRegistration = { navController.navigate(Routes.REGISTRATION_SCREEN) },
                    onNavigateToForgotPassword = { /*TODO*/ },
                    onNavigateToAuthenticatedRoute = { },
                    loggedAccountViewModel = loggedAccountViewModel,
                )
            }

            composable(Routes.REGISTRATION_SCREEN) {
                RegistrationScreen(
                    onNavigateBack = { navController.navigate(Routes.LOGIN_SCREEN) },
                    onNavigateToAuthenticatedRoute = { },
                )
            }
        }
    }
}
