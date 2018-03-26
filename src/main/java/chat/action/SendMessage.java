package chat.action;

import chat.function.Connect;
import chat.object.Message;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SendMessage extends Action {
    private String text;
    private String groupeName;
    private String userKey;
    private String destinataireUsername;
    private Integer hour;
    private Integer minute;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroupeName() {
        return groupeName;
    }

    public void setGroupeName(String groupeName) {
        this.groupeName = groupeName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getDestinataireUsername() {
        return destinataireUsername;
    }

    public void setDestinataireUsername(String destinataireUsername) {
        this.destinataireUsername = destinataireUsername;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    // Ici la fonction qui permet de sérialiser : transformer un object en JSON (String)
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
                int statut = statement.executeUpdate("INSERT INTO Message (contenu, message_heure, message_minute, id_utilisateur) VALUES ('" + sendMessage.getText() + "', '" + sendMessage.getHour() + "', '" + sendMessage.getMinute() + "', '" + idUser + "');");
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
}
