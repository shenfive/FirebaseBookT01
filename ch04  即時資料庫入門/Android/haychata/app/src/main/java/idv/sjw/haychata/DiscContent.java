package idv.sjw.haychata;

public class DiscContent {
    public String content;
    public String nickname;
    public Long date;
    public String messageKey;

    DiscContent(String content,String nickname,Long date,String messageKey){
        this.content = content;
        this.nickname = nickname;
        this.date = date;
        this.messageKey = messageKey;
    }
}
