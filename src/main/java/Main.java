import action.Action;
import action.AddUser;
import action.SendMessage;
import action.SignIn;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.imie.chat.specification.WebSocketServer;
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
            @Override
            public void onMessage(String sessionId, String message) {
                try {
                    Action typeAction = MAPPER.readValue(message, Action.class);

                    if (typeAction.getType().compareTo("connexion") == 0) {
                        SignIn signIn = MAPPER.readValue(message, SignIn.class);
                        System.out.println("Message de "+sessionId+": "+message);
                    }

                    if (typeAction.getType().compareTo("inscription") == 0) {
                        AddUser addUser = MAPPER.readValue(message, AddUser.class);
                        System.out.println("Message de "+sessionId+": "+message);
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
