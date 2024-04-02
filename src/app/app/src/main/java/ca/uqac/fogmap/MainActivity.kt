package ca.uqac.fogmap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ca.uqac.fogmap.common.customComposableViews.TitleText
import ca.uqac.fogmap.data.model.LoggedAccountViewModel
import ca.uqac.fogmap.ui.screens.FogmapNavigationGraph
import ca.uqac.fogmap.ui.screens.Routes
import ca.uqac.fogmap.ui.screens.rememberFirebaseAuthLauncher
import ca.uqac.fogmap.ui.theme.FogmapTheme
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FogmapTheme {
                FogmapApp()
            }
        }
        val currentUser = Firebase.auth.currentUser
        Log.d("MainActivity", "Current user: $currentUser")

        addUserToFirestore(currentUser) { success, exception ->
            if (success) {
                Log.d("MainActivity", "User added to Firestore successfully")
            } else {
                Log.e("MainActivity", "Error adding user to Firestore", exception)
            }
        }
    }

    data class NavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val badgeCount: Int? = null,
        val route: String,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FogmapApp() {
        var user by remember { mutableStateOf(Firebase.auth.currentUser) }
        val items = listOf(
            NavigationItem(
                title = "Accueil",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                route = Routes.WELCOME_SCREEN,
            ),
            NavigationItem(
                title = "Mon Compte",
                selectedIcon = Icons.Filled.AccountCircle,
                unselectedIcon = Icons.Outlined.AccountCircle,
                route = Routes.LOGIN_SCREEN,
            ),
            NavigationItem(
                title = "Settings",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                route = Routes.USER_INPUT_SCREEN,
            ),
        )
        val navController = rememberNavController()
        val loggedAccountViewModel = viewModel { LoggedAccountViewModel() }
        val loginState by remember {
            loggedAccountViewModel.loggedState
        }

        val launcher = rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                loggedAccountViewModel.currentUser = result.user
                user = result.user
            },
            onAuthError = {
                Log.d("FOGMAP", it.toString())
                user = null
            }
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var selectedItemIndex by rememberSaveable {
                mutableIntStateOf(0)
            }

            // Template de navigationDrawer :
            // https://www.youtube.com/watch?v=aYSarwALlpI
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet {
                        TitleText(text = loginState.username)
                        Spacer(modifier = Modifier.height(16.dp))
                        items.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                label = {
                                    Text(text = item.title)
                                },
                                selected = index == selectedItemIndex,
                                onClick = {
                                    navController.navigate(item.route)
                                    selectedItemIndex = index
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                badge = {
                                    item.badgeCount?.let {
                                        Text(text = item.badgeCount.toString())
                                    }
                                },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                },
                drawerState = drawerState
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Fogmap !")
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValue ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValue)
                    ) {
                        FogmapNavigationGraph(navController, loggedAccountViewModel)
                    }
                }
            }
        }
    }
    // https://youtu.be/dEEyZkZekvI?si=HkFDP_s9SgX-GD84&t=1976
}
fun addUserToFirestore(user: FirebaseUser?, onComplete: (Boolean, Exception?) -> Unit) {
    if (user == null) {
        onComplete(false, IllegalArgumentException("User is null"))
        return
    }

    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")

    // Créer un objet avec les informations de l'utilisateur
    val userData = hashMapOf(
        "uid" to user.uid,
        "displayName" to user.displayName,
        "email" to user.email
        // Ajoutez d'autres champs si nécessaire
    )

    // Ajouter l'utilisateur à la collection "users" avec l'UID comme ID du document
    userCollection.document(user.uid)
        .set(userData)
        .addOnSuccessListener {
            onComplete(true, null)
        }
        .addOnFailureListener { e ->
            onComplete(false, e)
        }
}