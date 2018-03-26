package chat.services;

import chat.actions.CreateGroup;
import chat.tools.Connect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ServiceGroup {

    // Méthode qui permet l'enregistrement en base de donnée du groupe
    public static String createGroupFunction(CreateGroup createGroup) {

        String returnContent;

// On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        // Création de l'objet gérant les requêtes
        ResultSet resultat = null;
        try {
            Statement statement = connexion.createStatement();
            // Exécution d'une requête de lecture
            resultat = statement.executeQuery("SELECT id_utilisateur FROM Utilisateur WHERE cle_session='" + createGroup.getUserKey() + "';");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat.next()) {

                int idUser = resultat.getInt("id_utilisateur");
                System.out.println("Données retournées par la requête : ID = " + idUser + ".");

                statement.executeUpdate("INSERT INTO Groupe (nom_groupe, id_utilisateur) VALUES ('" + createGroup.getGroupName() + "', '" + idUser + "');");
                System.out.println("Nouveau groupe ajouté : " + createGroup.getGroupName() + ".");
            }

            returnContent = "nouveau groupe enregistré";

        } catch (SQLException e) {
            e.printStackTrace();
            returnContent = "problème lors de l'enregistrement du groupe";
        }
        return returnContent;
    }
}