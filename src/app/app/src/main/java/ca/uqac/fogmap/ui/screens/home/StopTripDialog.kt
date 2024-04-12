package ca.uqac.fogmap.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ca.uqac.fogmap.common.customComposableViews.MediumTitleText
import ca.uqac.fogmap.data.FogLayerDataProvider

@Composable
fun StopTripDialog(
    onDontSaveTrip: () -> Unit,
    onSaveTrip: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    val distance = FogLayerDataProvider.getInstance().getCurrentTripDistance()

    Dialog(onDismissRequest = onDismissDialog) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MediumTitleText(
                    text = "Sauvegarde du trajet",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "Souhaitez-vous enregistrer le trajet actuel de $distance km afin qu'il soit" +
                            " pris en compte dans votre historique d'exploration du monde ?",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDontSaveTrip() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Ne pas enregistrer")
                    }
                    TextButton(
                        onClick = { onSaveTrip() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Oui")
                    }
                }
            }
        }
    }
}