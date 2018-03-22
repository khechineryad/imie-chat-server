
public class User {

    private String username;
    private Integer idUser;
    private String type;
    private StringBuffer key;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public StringBuffer getKey() {
        return key;
    }

    public void setKey(StringBuffer key) {
        this.key = key;
    }
}