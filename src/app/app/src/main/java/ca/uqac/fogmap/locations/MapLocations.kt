package ca.uqac.fogmap.locations

import ca.uqac.fogmap.ui.screens.questions.Question

object MapLocations {

    val map = mutableMapOf(
        "Parlement de Bretagne" to LocationImpl(
            "Parlement de Bretagne",
            "Le parlement de Bretagne est l'assemblée constituée dès le xiiie siècle par le duc de Bretagne. Initialement sous le nom de « parlement général » il devient un parlement de l'Ancien Régime créé — sous sa forme finale — en mars 15541 par un édit d'Henri II, à la demande des Bretons. Il est financé intégralement par la province. L'assemblée siège de manière permanente dans le palais du Parlement de Bretagne à Rennes à partir de 1655 jusqu'à sa dissolution par la Révolution française en février 1790 (sauf un exil à Vannes de 1675 à 1690). Le parlement de Bretagne disparaît avec la Révolution lors de la suppression générale des institutions judiciaires d'ancien régime."
        ),
        "Mairie de Rennes" to LocationImpl(
            "Mairie de Rennes",
            "La mairie de Rennes, aussi nommée hôtel de ville de Rennes, désigne à la fois le bâtiment et l’administration et les élus municipaux qui l’occupe.",
        ),
        "Test" to LocationImpl(
            "Test",
            "Test",
        ),
        "Test2" to LocationImpl(
            "Test2",
            "Test2",
        ),
        "Test3" to LocationImpl(
            "Test3",
            "Test3",
        ),
        "Test4" to LocationImpl(
            "Test4",
            "Test4",
        ),
        "Test5" to LocationImpl(
            "Test5",
            "Test5",
        )
    )

    val questions = mutableMapOf(
        "Parlement de Bretagne" to mutableListOf(
            Question(
                0,
                "Quelle est l'année de construction ?",
                listOf("1444", "1445", "1446", "1447"),
                0,
                "Le parlement a été construit en 1444"
            ),
            Question(
                1,
                "Quelle est l'année de l'incendie ?",
                listOf("1444", "1445", "1446", "1447"),
                0,
                "La mairie a été construite en 1444")
        ),
        "Mairie de Rennes" to mutableListOf(
            Question(
                2,
                "Quelle est l'année de construction ?",
                listOf("1444", "1445", "1446", "1447"),
                0,
                "La mairie a été construite en 1444"
            ),
            Question(
                3,
            "Quel bâtiment est en face de la mairie ?",
                listOf("Mairie", "Parlement", "Opéra", "McDonald"),
                2,
                "L'opéra fait face à la mairie"
            )
        ),
        "Test" to mutableListOf(
            Question(
                4,
                "Test ?",
                listOf("0", "1", "2", "3"),
                0,
                "0"
            ),
        )
    )

}