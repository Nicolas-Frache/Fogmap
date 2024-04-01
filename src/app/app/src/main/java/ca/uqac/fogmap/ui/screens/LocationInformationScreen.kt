package ca.uqac.fogmap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LocationInformation(name: String) {
    val map = mapOf(
        "Parlement de Bretagne" to "Le parlement de Bretagne est l'assemblée constituée dès le xiiie siècle par le duc de Bretagne. Initialement sous le nom de « parlement général » il devient un parlement de l'Ancien Régime créé — sous sa forme finale — en mars 15541 par un édit d'Henri II, à la demande des Bretons. Il est financé intégralement par la province. L'assemblée siège de manière permanente dans le palais du Parlement de Bretagne à Rennes à partir de 1655 jusqu'à sa dissolution par la Révolution française en février 1790 (sauf un exil à Vannes de 1675 à 1690). Le parlement de Bretagne disparaît avec la Révolution lors de la suppression générale des institutions judiciaires d'ancien régime.",
        "Mairie de Rennes" to "La mairie de Rennes, aussi nommée hôtel de ville de Rennes, désigne à la fois le bâtiment et l’administration et les élus municipaux qui l’occupe.")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )
        map[name]?.let {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
    }
}
