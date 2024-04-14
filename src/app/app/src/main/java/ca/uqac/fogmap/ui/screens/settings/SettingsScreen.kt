package ca.uqac.fogmap.ui.screens.settings

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ca.uqac.fogmap.common.customComposableViews.MediumTitleText
import ca.uqac.fogmap.common.customComposableViews.TitleText
import ca.uqac.fogmap.locations.deleteTripHistory

@Composable
fun SettingsScreen(navController: NavHostController) {
    val openTripDeletionDialog = remember { mutableStateOf(false) }

    when {
        openTripDeletionDialog.value -> {
            ConfirmationDialog(
                title = "Suppression de l'historique de trajets",
                description = "Êtes-vous sûr de vouloir supprimer définitivement l'historique de " +
                        "trajets ?\nToutes les données seront supprimées de votre appareil et" +
                        " l'exploration de la carte recommencera à zéro.",
                onConfirm = {
                    openTripDeletionDialog.value = false
                    deleteTripHistory(context = navController.context)
                },
                onCancel = {
                    openTripDeletionDialog.value = false
                },
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(19.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp, Alignment.Top)
        )
        {
            TitleText(text = "Paramètres")
            HorizontalDivider()
            Spacer(modifier = Modifier.height(3.dp))
            MediumTitleText(text = "Mes trajets")
            Button(
                onClick = {

                }) {
                Image(
                    // R.drawable.ic_menu_view
                    painter = painterResource(id = R.drawable.ic_menu_view),
                    contentDescription = ""
                )
                Text(text = "Consulter mes trajets")
            }

            Button(
                onClick = {
                    openTripDeletionDialog.value = true
                }
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_menu_delete),
                    contentDescription = ""
                )
                Text(
                    text = "Supprimer l'historique des trajets",
                )
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(3.dp))
            MediumTitleText(text = "Affichage de la carte")

        }
    }
}

@Preview
@Composable
fun UserInputScreenPreview() {
    SettingsScreen(rememberNavController())
}