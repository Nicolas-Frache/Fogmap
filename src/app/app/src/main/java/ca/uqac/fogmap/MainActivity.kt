package ca.uqac.fogmap

import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
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
import androidx.compose.runtime.DisposableEffect
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
import ca.uqac.fogmap.common.customComposableViews.MediumTitleText
import ca.uqac.fogmap.data.model.LoggedAccountViewModel
import ca.uqac.fogmap.ui.screens.FogmapNavigationGraph
import ca.uqac.fogmap.ui.screens.Routes
import ca.uqac.fogmap.ui.screens.rememberFirebaseAuthLauncher
import ca.uqac.fogmap.ui.theme.FogmapTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    public var PACKAGE_NAME: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMockData()
        PACKAGE_NAME = applicationContext.packageName;
        setContent {
            FogmapTheme {
                FogmapApp()
            }
        }
        Log.d("FOGMAP", applicationContext.packageName)

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


    fun initMockData() {
        val files: Array<String> = applicationContext.fileList()
        Log.d("FOGMAP", "${files.size} files in local storage")
        if (files.size < 3) {
            copyFileToLocalStorage(R.raw.sample_track_1, "sample_track_1.geojson")
            copyFileToLocalStorage(R.raw.sample_track_2, "sample_track_2.geojson")
            copyFileToLocalStorage(R.raw.sample_track_3, "sample_track_3.geojson")
            Log.d("FOGMAP", "Mock data copied to local storage")
        }
    }

    fun copyFileToLocalStorage(ressourceId: Int, filename: String) {
        val fileContents =
            applicationContext.resources.openRawResource(ressourceId).bufferedReader()
                .use { it.readText() }
        applicationContext.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    private fun Intent.withComponent(packageName: String, exampleName: String): Intent {
        component = ComponentName(packageName, exampleName)
        return this
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
                title = "Carte",
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Outlined.Map,
                route = Routes.MAP_SCREEN,
            ),
            NavigationItem(
                title = "Mon Compte",
                selectedIcon = Icons.Filled.AccountCircle,
                unselectedIcon = Icons.Outlined.AccountCircle,
                route = Routes.LOGIN_SCREEN,
            ),
            NavigationItem(
                title = "Lieux visités",
                selectedIcon = Icons.Filled.Checklist,
                unselectedIcon = Icons.Outlined.Checklist,
                route = Routes.VISITED_LOCATION_SCREEN,
            ),
            NavigationItem(
                title = "Ajouter un lieu",
                selectedIcon = Icons.Filled.Add,
                unselectedIcon = Icons.Outlined.Add,
                route = Routes.ADD_LOCATION,
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

        // Observer pour les changements de l'utilisateur FirebaseAuth
        var user by remember { mutableStateOf<FirebaseUser?>(FirebaseAuth.getInstance().currentUser) }
        DisposableEffect(Unit) {
            val auth = FirebaseAuth.getInstance()
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                user = firebaseAuth.currentUser
            }
            auth.addAuthStateListener(listener)
            onDispose {
                auth.removeAuthStateListener(listener)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

            // Template de navigationDrawer :
            // https://www.youtube.com/watch?v=aYSarwALlpI
            ModalNavigationDrawer(
                gesturesEnabled = false,
                drawerContent = {
                    ModalDrawerSheet {
                        if (user?.displayName == null) {
                            MediumTitleText(
                                text = "Non connecté",
                                modifier = Modifier.padding(10.dp)
                            )
                        } else {
                            MediumTitleText(
                                text = "Bienvenue : ${user?.displayName}",
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(16.dp),
                        )

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