package chat.action;

import chat.function.Connect;
import chat.function.RandomKeyGen;
import chat.object.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignIn extends Action {
    private String email;
    private String password;


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Ici la fonction qui permet de sérialiser : transformer un object en JSON (String)
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setDefaultVisibility(JsonAutoDetect.Value.construct(JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Méthode qui permet la connexion d'un utilisateur
    public static String signInFunction(SignIn signIn) {

        String returnContent = null;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        ResultSet resultat;

        // Création de l'objet gérant les requêtes
        try {
            Statement statement = connexion.createStatement();

            // Exécution d'une requête de lecture
            resultat = statement.executeQuery("SELECT id_utilisateur, pseudo FROM Utilisateur WHERE email='" + signIn.getEmail() + "' AND mot_de_passe='" + signIn.getPassword() + "';");

            System.out.println("Requête SQL effectuée !");

            /* Récupération des données du résultat de la requête de lecture */
            while (resultat.next()) {
                int idUser = resultat.getInt("id_utilisateur");
                String username = resultat.getString("pseudo");

                System.out.println("Données retournées par la requête : id_utilisateur = " + idUser + ", pseudo = " + username + ".");

                // On génère une key
                StringBuffer key = null;

                try {
                    try {
                        key = RandomKeyGen.generate();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("Exception caught on generate key");
                    e.printStackTrace();
                }
                System.out.println(key);

                // Exécution d'une requête d'écriture de la key en base de données
                int statut = statement.executeUpdate("UPDATE Utilisateur SET cle_session ='" + key + "' WHERE email='" + signIn.getEmail() + "' AND mot_de_passe='" + signIn.getPassword() + "';");
                System.out.println("clé session utilisateur enregistrée");

                // On créer un objet user pour stocker l'id et le pseudo
                User user = new User();

                user.setType("return_connection");
                user.setUsername(username);
                user.setIdUser(idUser);
                user.setKey(key);

                try {
                    returnContent = MAPPER.writeValueAsString(user);
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
