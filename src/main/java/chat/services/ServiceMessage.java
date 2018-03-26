package chat.services;

import chat.actions.SendMessage;
import chat.tools.Connect;
import chat.objects.Message;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;

public class ServiceMessage {

    // Ici la fonction qui permet de sérialiser : transformer un objects en JSON (String)
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setDefaultVisibility(JsonAutoDetect.Value.construct(JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Méthode qui permet de renvoyer un message
    public static String sendMessageFunction(SendMessage sendMessage) {

        String returnContent = null;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        ResultSet resultat = null;

        // Création de l'objet gérant les requêtes
        Statement statement = null;

        // Exécution d'une requête de lecture
        try {
            statement = connexion.createStatement();
            PreparedStatement ps = connexion.prepareStatement("SELECT id_utilisateur, pseudo FROM Utilisateur WHERE cle_session = ?;");
            ps.setString(1, sendMessage.getUserKey());

            resultat = ps.executeQuery();

            while (resultat.next()) {
                int idUser = resultat.getInt("id_utilisateur");
                String username = resultat.getString("pseudo");

                System.out.println("Données retournées par la requête : id_utilisateur = " + idUser + ", pseudo = " + username + ".");
                // Exécution d'une requête d'écriture
                int statut = statement.executeUpdate("INSERT INTO Message (contenu, message_heure, message_minute, id_utilisateur, message_date) VALUES ('" + sendMessage.getText() + "', '" + sendMessage.getHour() + "', '" + sendMessage.getMinute() + "', '" + idUser + "', CURDATE());");
                System.out.println("Nouveau message ajouté");

                // On créer un objet user pour stocker l'id et le pseudo
                Message messageReturn = new Message();

                messageReturn.setType("return_message");
                messageReturn.setUsername(username);
                messageReturn.setText(sendMessage.getText());
                messageReturn.setHour(sendMessage.getHour());
                messageReturn.setMinute(sendMessage.getMinute());

                try {
                    returnContent = MAPPER.writeValueAsString(messageReturn);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Requête SQL effectuée !");

        return returnContent;
    }

    // Méthode qui permet de renvoyer les messages
    public static String messageHistory() throws SQLException {
        String returnContent = null;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        ResultSet resultat = null;

        // Création de l'objet gérant les requêtes
        Statement statement = null;
        try {
            statement = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Création d'une ArrayList pour stocker les messages présents en base
        ArrayList messageHistory = new ArrayList();

        // Exécution d'une requête de lecture pour récupérer les messages
        try {
            resultat = statement.executeQuery("SELECT * FROM Message ORDER BY id_message;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Requête SQL effectuée !");

        /* Récupération des données du résultat de la requête de lecture */
        while (resultat.next()) {
            String contenu = resultat.getString("contenu");
            int message_heure = resultat.getInt("message_heure");
            int message_minute = resultat.getInt("message_minute");
            int id_utilisateur = resultat.getInt("id_utilisateur");
            int id_destinataire = resultat.getInt("id_destinataire");
            int id_groupe = resultat.getInt("id_groupe");

            // On créer les variables de récupération des pseudos utilisateurs et destinataires, et du nom du group
            String username = null;
            String destinataire = null;
            String groupName = null;

            ResultSet resultat2 = null;

            // Exécution d'une requête de lecture pour récupérer le pseudo utilisateur
            try {
                resultat2 = statement.executeQuery("SELECT pseudo FROM Utilisateur WHERE id_utilisateur='" + id_utilisateur + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat2.next()) {
                username = resultat2.getString("pseudo");
            }

            // Exécution d'une requête de lecture pour récupérer le pseudo destinataire
            try {
                resultat2 = statement.executeQuery("SELECT pseudo FROM Utilisateur WHERE id_utilisateur='" + id_destinataire + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat2.next()) {
                destinataire = resultat2.getString("pseudo");
            }

            // Exécution d'une requête de lecture pour récupérer le nom du groupe
            try {
                resultat2 = statement.executeQuery("SELECT nom_groupe FROM Groupe WHERE id_groupe='" + id_groupe + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat2.next()) {
                groupName = resultat2.getString("nom_groupe");
            }


            // On créer un objet user pour stocker l'id et le pseudo
            Message message = new Message();

            message.setType("return_history");
            message.setText(contenu);
            message.setHour(message_heure);
            message.setHour(message_minute);
            message.setUsername(username);
            message.setDestinataire(destinataire);
            message.setGroupName(groupName);

            // On ajoute le message à la liste
            messageHistory.add(message);

        }
        try {
            returnContent = MAPPER.writeValueAsString(messageHistory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return returnContent;
    }
}
