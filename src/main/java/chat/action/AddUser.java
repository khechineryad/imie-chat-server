package chat.action;

import chat.function.Connect;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AddUser extends Action {

    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Méthode qui permet l'enregistrement en base de donnée de l'utilisateur
    public static String addUserFunction(AddUser addUser) {

        String returnContent;

        // On se connecte à la base de données via la classe Connect
        Connection connexion = Connect.getConnection();

        // Création de l'objet gérant les requêtes
        try {
            Statement statement = connexion.createStatement();
            // Exécution d'une requête d'écriture
            int statut = statement.executeUpdate("INSERT INTO Utilisateur (pseudo, email, mot_de_passe) VALUES ('" + addUser.getUsername() + "', '" + addUser.getEmail() + "', '" + addUser.getPassword() + "');");
            System.out.println("Nouvel utilisateur ajouté");

            returnContent = "nouvel utilisateur enregistré";

        } catch (SQLException e) {
            e.printStackTrace();
            returnContent = "problème lors de l'enregistrement de l'utilisateur";
        }
        return returnContent;
    }
}