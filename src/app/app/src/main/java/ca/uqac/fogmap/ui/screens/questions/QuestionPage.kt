package ca.uqac.fogmap.ui.screens.questions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import ca.uqac.fogmap.locations.MapLocations
import ca.uqac.fogmap.ui.screens.Routes

@Composable
fun QuestionPage(navController: NavController, questionIndex: Int) {
    val question = remember { MapLocations.questions.values.flatten().find { it.index == questionIndex } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (question != null) {
            Text(
                text = question.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 24.sp)
            )
        }
        val selectedOption = remember { mutableStateOf(0) }
        if (question != null) {
            question.options.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedOption.value = index }
                        .background(
                            if (selectedOption.value == index) Color(0xFFBA9EDD) else Color.White,
                            RoundedCornerShape(50)
                        )
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(8.dp).padding(start = 16.dp),
                        color = if (selectedOption.value == index) Color.White else Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("${Routes.QUESTION}/${questionIndex}/${selectedOption.value}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Valider")
        }
    }
}
