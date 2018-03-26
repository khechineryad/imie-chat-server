package chat;

import chat.action.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
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

            private void sendResponse (String sessionId, String retour) {
                try {
                    try { webSocketServer.send(sessionId, retour); }
                    catch (IOException e) { e.printStackTrace(); }
                }
                catch (SessionNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(retour + "\nRéponse envoyée"); }

            @Override
            public void onMessage(String sessionId, String message) {

                Action typeAction = null;
                String retour = null;

                try { typeAction = MAPPER.readValue(message, Action.class); }
                catch (IOException e) { e.printStackTrace(); }

                switch (typeAction.getType()){

                    // Si c'est une inscription
                    case "inscription":
                        AddUser addUser = null;
                        try {
                            addUser = MAPPER.readValue(message, AddUser.class);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Message de " + sessionId + ": " + message +".\nBien reçu !");
                        // On appelle la méthode pour ajouter un utilisateur (méthode de la classe AddUser)
                        retour = AddUser.addUserFunction(addUser);
                        // On appelle la méthode pour envoyer une réponse
                        sendResponse(sessionId, retour);

                        break;

                    // Si c'est une connexion
                    case "connexion":
                        SignIn signIn = null;
                        try {
                            signIn = MAPPER.readValue(message, SignIn.class);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Demande de connection de " + sessionId + ": " + message + "\nBien reçu");
                        System.out.println("adresse email: " + signIn.getEmail());
                        // On appelle la méthode pour connecter un utilisateur (méthode de la classe SignIn)
                        retour = SignIn.signInFunction(signIn);
                        // On appelle la méthode pour envoyer une réponse
                        sendResponse(sessionId, retour);

                        break;

                    // Si l'action est de type "message"
                    case "message":
                        SendMessage sendMessage = null;
                        try {
                            sendMessage = MAPPER.readValue(message, SendMessage.class);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Message de " + sessionId + ": " + message);
                        // On appelle la méthode pour retourner un message (méthode de la classe SendMessage)
                        retour = SendMessage.sendMessageFunction(sendMessage);
                        // On appelle la méthode pour envoyer une réponse
                        sendResponse(sessionId, retour);

                        break;

                    // Si c'est une création de groupe
                    case "groupe":
                        CreateGroup createGroup = null;
                        try {
                            createGroup = MAPPER.readValue(message, CreateGroup.class);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Message de" + sessionId + ": " + message);
                        // On appelle la méthode pour ajouter un groupe (méthode de la classe CreateGroup)
                        retour = CreateGroup.createGroupFunction(createGroup);
                        // On appelle la méthode pour envoyer une réponse
                        sendResponse(sessionId, retour);

                        break;

                    // Si aucune action
                    default:
                        Error error = new Error();
                        error.setType("error");
                        error.setErrorInformation("Type d'action non reconnu");
                        try {
                            retour = MAPPER.writeValueAsString(error);
                        }
                        catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        // On appelle la méthode pour envoyer une réponse
                        sendResponse(sessionId, retour);

                        break;
                }
            }
        });

        webSocketServer.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Appuyez sur une touche pour arrêter le serveur");
        reader.readLine();
    }
}