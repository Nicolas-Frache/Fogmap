package ca.uqac.fogmap.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uqac.fogmap.data.model.TripListModel
import ca.uqac.fogmap.data.model.TripState


@Composable
fun TrajetListItem(trip: TripState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = trip.date.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = "Distance: ${trip.distance}")
            Text(text = "Nouvelle surface découverte : ${trip.surface}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.End)
            ) {
                FloatingActionButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(45.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Filled.DeleteForever, contentDescription = "",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                FloatingActionButton(
                    onClick = {
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(50.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Filled.Visibility, contentDescription = "",
                        modifier = Modifier.size(30.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun TripList(trajets: List<TripState>) {
    LazyColumn {
        items(trajets) { trajet ->
            TrajetListItem(trip = trajet)
        }
    }
}

@Composable
fun TripHistoryScreen(){
    TripListModel.getInstance().initTripListModel(LocalContext.current)
    TripList(TripListModel.getInstance().trips)
}

@Preview
@Composable
fun PreviewTrajetList() {
    val trajets = listOf(
        TripState(true, 11, 21, 31, null),
        TripState(false, 12, 22, 32, null),
        TripState(true, 13, 23, 33, null),
    )
    TripList(trajets = trajets)
}