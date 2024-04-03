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
import ca.uqac.fogmap.ui.screens.Routes

@Composable
fun AnswerPage(navController: NavController, questionIndex: Int, selectedOption: Int) {
    val question = remember { questions[questionIndex] }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (selectedOption == question.answer) {
                "Bravo, c'est la bonne réponse !"
            } else {
                "Désolé, ce n'est pas la bonne réponse."
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 24.sp)
        )
        Text(
            text = "La bonne réponse est : ${question.options[question.answer]}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 20.sp)
        )
        Text(
            text = question.description,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Justify,
            style = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate(Routes.QUESTION)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour")
        }
    }
}