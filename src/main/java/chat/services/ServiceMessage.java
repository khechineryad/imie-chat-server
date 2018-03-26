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
import java.util.List;

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
                PreparedStatement ps2 = connexion.prepareStatement("INSERT INTO Message (contenu, message_heure, message_minute, id_utilisateur, message_date) VALUES (?, ?, ?, ?, CURDATE()) ");
                ps2.setString(1, sendMessage.getText());
                ps2.setInt(2, sendMessage.getHour());
                ps2.setInt(3, sendMessage.getMinute());
                ps2.setInt(4, idUser);

                ps2.executeUpdate();

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

        // Chaque message est enregistré dans cette variable en JSON
        String returnContent = null;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        ResultSet resultat = null;

        // Création de l'objet gérant les requêtes
        Statement statement = null;
        Statement statement2 = null;

        try {
            statement = connexion.createStatement();
            statement2 = connexion.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Création d'une ArrayList pour stocker les messages présents en base
        List<Message> messageHistory = new ArrayList<>();

        // Exécution d'une requête de lecture pour récupérer les messages
        PreparedStatement ps3 = connexion.prepareStatement("SELECT * FROM Message ORDER BY id_message");

        resultat = ps3.executeQuery();
        System.out.println("Requête de lecture pour récupérer les messages effectuée !");

        // Récupération des données du résultat de la requête de lecture
        while (resultat != null && resultat.next()) {
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
            PreparedStatement ps4 = connexion.prepareStatement("SELECT pseudo FROM Utilisateur WHERE id_utilisateur = ? ");
            ps4.setInt(1, id_utilisateur);

            resultat2 = ps4.executeQuery();

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat2.next()) {
                username = resultat2.getString("pseudo");
            }
            System.out.println("Requête de lecture pour récupérer le pseudo utilisateur effectuée : "+username);

            // Exécution d'une requête de lecture pour récupérer le pseudo destinataire
            PreparedStatement ps5 = connexion.prepareStatement("SELECT pseudo FROM Utilisateur WHERE id_utilisateur = ? ");
            ps5.setInt(1, id_destinataire);

            resultat2 = ps5.executeQuery();

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat2.next()) {
                destinataire = resultat2.getString("pseudo");
            }
            System.out.println("Requête pour récupérer le pseudo destinataire effectuée : "+destinataire);

            // Exécution d'une requête de lecture pour récupérer le nom du groupe
            PreparedStatement ps6 = connexion.prepareStatement("SELECT nom_groupe FROM Groupe WHERE id_groupe = ? ");
            ps6.setInt(1, id_groupe);

            resultat2 = ps6.executeQuery();

            // Récupération du nom du groupe
            while (resultat2.next()) {
                groupName = resultat2.getString("nom_groupe");
            }

            System.out.println("Requête pour récupérer le nom du groupe effectuée : "+groupName);


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
            return MAPPER.writeValueAsString(messageHistory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
