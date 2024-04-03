package ca.uqac.fogmap.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uqac.fogmap.R
import ca.uqac.fogmap.common.customComposableViews.NormalButton
import ca.uqac.fogmap.common.customComposableViews.TextComponent
import ca.uqac.fogmap.data.model.LoggedAccountViewModel
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


// Définir la classe représentant un élément d'informations personnelles
data class PersonalInfoItem(val title: String, val value: String)
@Composable
fun MyAccountScreen(
    loggedAccountViewModel: LoggedAccountViewModel
) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            loggedAccountViewModel.user = result.user
            user = result.user
        },
        onAuthError = {
            Log.d("FOGMAP", it.toString())
            user = null
        }
    )

    var nickname by remember { mutableStateOf("") }
    val showToast = remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("My Account Screen")
            user?.let { currentUser ->
                Log.d("FOGMAP", "PHOTO : ${currentUser.photoUrl}")
                if (currentUser.photoUrl != null) {
                    UserProfileImage(url = currentUser.photoUrl.toString())
                }
                PersonalInfoList(user = currentUser)
                TextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Pseudonyme") },
                    modifier = Modifier.fillMaxWidth()
                )
                SaveNicknameButton(nickname = nickname, userId = currentUser.uid, showToast = showToast)
                LogoutButton(onClick = { Firebase.auth.signOut() })

                if (showToast.value) {
                    Toast.makeText(LocalContext.current, "Pseudonyme enregistré avec succès", Toast.LENGTH_SHORT).show()
                    showToast.value = false // Réinitialiser l'état du Toast après l'affichage
                }
            }
        }
    }
}

@Composable
fun SaveNicknameButton(nickname: String, userId: String, showToast: MutableState<Boolean>) {
    Button(
        onClick = {
            updateNicknameInFirestore(nickname, userId)
            // Définir l'état pour afficher le Toast
            showToast.value = true
        }
    ) {
        Text("Enregistrer Pseudonyme")
    }
}

fun updateNicknameInFirestore(nickname: String, userId: String) {
    val db = FirebaseFirestore.getInstance()
    val userDocumentRef = db.collection("users").document(userId)

    userDocumentRef
        .update("nickname", nickname)
        .addOnSuccessListener {
            Log.d("FOGMAP", "Nickname updated successfully")
        }
        .addOnFailureListener { e ->
            Log.e("FOGMAP", "Error updating nickname", e)
        }
}

@Composable
fun UserProfileImage(url: String) {
    val painter: Painter = rememberImagePainter(
        data = url,
        builder = {
            placeholder(R.drawable.placeholder)
            error(R.drawable.error)
        }
    )

    Image(
        painter = painter,
        contentDescription = "User Profile Image",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop // Redimensionnement de l'image pour s'adapter à la taille
    )
}

@Composable
fun PersonalInfoList(user: FirebaseUser) {
    val personalInfoList = listOf(
        PersonalInfoItem("Nom", user.displayName ?: ""),
        PersonalInfoItem("Email", user.email ?: "")
    )

    personalInfoList.forEach { item ->
        Text(text = "${item.title}: ${item.value}", fontSize = 16.sp)
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Se déconnecter")
    }
}