package chat.services;

import chat.actions.SendMessage;
import chat.objects.MessageHistory;
import chat.tools.Connect;
import chat.objects.Message;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;

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
        try {
            Statement statement = null;
            try {
                statement = connexion.createStatement();
                PreparedStatement ps = connexion.prepareStatement("SELECT * FROM users WHERE id = ? AND apiKey = ?");
                ps.setInt(1, 10);
                ps.setString(2, "pouet");

            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Exécution d'une requête de lecture
            try {
                resultat = statement.executeQuery("SELECT id_utilisateur, pseudo FROM Utilisateur WHERE cle_session='" + sendMessage.getUserKey() + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
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
        return returnContent;
    }

    // Méthode qui permet de renvoyer les messages
    public static String messageHistory (SendMessage sendMessage) {
        String returnContent = null;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        ResultSet resultat = null;

        // Création de l'objet gérant les requêtes
        try {
            Statement statement = null;
            try {
                statement = connexion.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Exécution d'une requête de lecture
            try {
                resultat = statement.executeQuery("SELECT contenu, message_heure, message_minute, id_utilisateur, id_groupe FROM Message ORDER BY;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat.next()) {
                int idUser = resultat.getInt("id_utilisateur");
                String username = resultat.getString("pseudo");

                System.out.println("Données retournées par la requête : id_utilisateur = " + idUser + ", pseudo = " + username + ".");

                // On créer un objet user pour stocker l'id et le pseudo
                MessageHistory historyReturn = new MessageHistory();



                try {
                    returnContent = MAPPER.writeValueAsString(historyReturn);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnContent;
    }
}
