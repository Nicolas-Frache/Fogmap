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
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.uqac.fogmap.data.model.TripListModel
import ca.uqac.fogmap.data.model.TripState
import java.text.SimpleDateFormat
import java.util.Date


private fun getDateTime(s: Int): String {
    val sdf = SimpleDateFormat.getDateTimeInstance()
    val netDate = Date(s.toLong() * 1000)
    return sdf.format(netDate)
}

@Composable
fun TrajetListItem(trip: TripState, onDelete: () -> Unit) {
    val context = LocalContext.current
    val isVisible = remember { mutableStateOf(trip.isVisible) }

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
                text = getDateTime(trip.date),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(text = "Distance: ${String.format("%.2f", trip.distance.toDouble())} km")
            Text(text = "Nouvelle surface découverte : ${trip.surface} km²")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.End)
            ) {
                FloatingActionButton(
                    onClick = {
                        onDelete()
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
                        trip.isVisible = !trip.isVisible
                        trip.saveTrip(context)
                        isVisible.value = trip.isVisible
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .size(50.dp),
                    shape = CircleShape
                ) {
                    when {
                        isVisible.value -> {
                            Icon(
                                Icons.Filled.Visibility, contentDescription = "",
                                modifier = Modifier.size(30.dp),
                            )
                        }

                        else -> {
                            Icon(
                                Icons.Filled.VisibilityOff, contentDescription = "",
                                modifier = Modifier.size(30.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripList(trajets: ArrayList<TripState>) {
    val key = remember { mutableStateOf(true) }
    key(key.value){
        LazyColumn {
            items(trajets) { trajet ->
                TrajetListItem(
                    trip = trajet,
                    onDelete = {
                        TripListModel.getInstance().removeTrip(trajet)
                        trajets.remove(trajet)
                        key.value = !key.value
                    }
                )
            }
        }
    }
}

@Composable
fun TripHistoryScreen() {
    TripListModel.getInstance().initTripListModel(LocalContext.current)
    TripList(TripListModel.getInstance().trips)
}

@Preview
@Composable
fun PreviewTrajetList() {
    val trajets = ArrayList(listOf(
        TripState("a", true, 11, 21, 31, null),
        TripState("a", false, 12, 22, 32, null),
        TripState("a", true, 13, 23, 33, null),
    ))
    TripList(trajets = trajets)
}