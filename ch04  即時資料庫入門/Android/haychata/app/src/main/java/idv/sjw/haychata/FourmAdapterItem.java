package idv.sjw.haychata;

import java.util.ArrayList;
import java.util.Date;

public class FourmAdapterItem {
    public String subject;
    public Long lastUpdateDate;
    public String lastUpdateUserNickname;
    public String key;

    FourmAdapterItem(String subject,Long lastUpdateDate,String lastUpdateUserNickname,String key){
        this.subject = subject;
        this.lastUpdateDate = lastUpdateDate;
        this.lastUpdateUserNickname = lastUpdateUserNickname;
        this.key = key;
    }
}
