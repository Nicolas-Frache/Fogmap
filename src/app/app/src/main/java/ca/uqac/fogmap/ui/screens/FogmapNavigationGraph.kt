package ca.uqac.fogmap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ca.uqac.fogmap.data.model.LoggedAccountViewModel
import ca.uqac.fogmap.ui.screens.account.login.LoginScreen
import ca.uqac.fogmap.ui.screens.account.registration.RegistrationScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun FogmapNavigationGraph(
    navController: NavHostController,
    loggedAccountViewModel: LoggedAccountViewModel,
    user: FirebaseUser? = null
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

            composable(Routes.WELCOME_SCREEN) {
                WelcomeScreen()
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
            composable(Routes.MY_ACCOUNT) {
                MyAccountScreen(loggedAccountViewModel)
            }
        }
    }
}
