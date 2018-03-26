package chat.actions;

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
}
