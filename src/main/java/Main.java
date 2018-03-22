import action.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.imie.chat.specification.WebSocketServer;
import fr.imie.chat.specification.exceptions.SessionNotFoundException;
import fr.imie.chat.specification.listeners.CloseWebSocketListener;
import fr.imie.chat.specification.listeners.MessageWebSocketListener;
import fr.imie.chat.specification.listeners.OpenWebSocketListener;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setDefaultVisibility(JsonAutoDetect.Value.construct(JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT))
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static void main (String[] args) throws DeploymentException, IOException {
        new Main();
    }
    private Main () throws DeploymentException, IOException {
        WebSocketServer<String> webSocketServer = WebSocketServer.get("localhost", 8083, String.class);

        webSocketServer.addListener(new OpenWebSocketListener<String>() {
            @Override
            public void onOpen(String sessionId) {
                System.out.println("Open "+sessionId);
            }
        });

        webSocketServer.addListener(new CloseWebSocketListener<String>() {
            @Override
            public void onClose(String sessionId) {
                System.out.println("Close "+sessionId);
            }
        });
        webSocketServer.addListener(new MessageWebSocketListener<String>() {
            @Override
            public void onMessage(String sessionId, String message) {
                try {
                    Action typeAction = MAPPER.readValue(message, Action.class);

                    if (typeAction.getType().compareTo("connexion") == 0) {
                        SignIn signIn = MAPPER.readValue(message, SignIn.class);
                        System.out.println("Message de "+sessionId+": "+message);

                        System.out.println(signIn.getEmail());

                        // On se connecte à la base de données via la classe Connect
                        Connection connexion = Connect.getConnection();

                        ResultSet resultat = null;

                        // Création de l'objet gérant les requêtes
                        try {
                            Statement statement = connexion.createStatement();

                            // Exécution d'une requête de lecture
                            resultat = statement.executeQuery( "SELECT id_utilisateur, pseudo FROM Utilisateur WHERE email='"+signIn.getEmail()+"' AND mot_de_passe='"+signIn.getPassword()+"';" );

                            System.out.println( "Requête SQL effectuée !" );

                            /* Récupération des données du résultat de la requête de lecture */
                            while ( resultat.next() ) {
                                int idUser = resultat.getInt( "id_utilisateur" );
                                String username = resultat.getString( "pseudo" );

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


                                // On créer un objet user pour stocker l'id et le pseudo
                                User user = new User();

                                user.setType("return_connection");
                                user.setUsername(username);
                                user.setIdUser(idUser);
                                user.setKey(key);

                                String retour = MAPPER.writeValueAsString(user);
                                try {
                                    webSocketServer.send(sessionId, retour);
                                    System.out.println("On envoie ceci: "+retour);
                                } catch (SessionNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (typeAction.getType().compareTo("inscription") == 0) {
                        AddUser addUser = MAPPER.readValue(message, AddUser.class);
                        System.out.println("Message de "+sessionId+": "+message);

                        // On se connecte à la base de données via la classe Connect
                        Connection connexion = Connect.getConnection();

                        // Création de l'objet gérant les requêtes
                        try {
                            Statement statement = connexion.createStatement();
                            // Exécution d'une requête d'écriture
                            int statut = statement.executeUpdate( "INSERT INTO Utilisateur (pseudo, email, mot_de_passe) VALUES ('"+addUser.getUsername()+"', '"+addUser.getEmail()+"', '"+addUser.getPassword()+"');" );
                            System.out.println("Nouvel utilisateur ajouté");

                                String retour = "nouvel utilisateur enregistré";
                            try {
                                webSocketServer.send(sessionId, retour);
                            } catch (SessionNotFoundException e) {
                                e.printStackTrace();
                            }
                            System.out.println("On envoie ceci: "+retour);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (typeAction.getType().compareTo("message") == 0) {
                        SendMessage sendMessage = MAPPER.readValue(message, SendMessage.class);
                        System.out.println("Message de "+sessionId+": "+message);
                    }
                }
                catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        webSocketServer.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Appuyez sur une touche pour arrêter le serveur");
        reader.readLine();
    }
}
