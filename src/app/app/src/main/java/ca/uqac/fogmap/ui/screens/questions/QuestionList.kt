package ca.uqac.fogmap.ui.screens.questions

data class Question(
    val text: String,
    val options: List<String>,
    val answer: Int,
    val description: String
)

val questions = listOf(
    Question(
        "Quelle est la capitale de la France ?",
        listOf("Paris", "Londres", "Berlin", "Madrid"),
        0,
        "Paris est la capitale de la France depuis le XVIe siècle."
    ),
    Question(
        "Quel est le plus grand mammifère marin ?",
        listOf("Baleine bleue", "Requin blanc", "Dauphin", "Orca"),
        0,
        "La baleine bleue est le plus grand mammifère marin et peut mesurer jusqu'à 30 mètres de long."
    ),
    Question(
        "Qui a peint la Joconde ?",
        listOf("Leonard de Vinci", "Pablo Picasso", "Vincent van Gogh", "Michel-Ange"),
        0,
        "La Joconde a été peinte par Leonard de Vinci au début du XVIe siècle."
    )
)
