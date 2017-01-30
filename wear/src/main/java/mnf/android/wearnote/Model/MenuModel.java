package mnf.android.wearnote.Model;

/**
 * Created by muneef on 29/01/17.
 */

public class MenuModel {

    private String title;
    private String id;
    public MenuModel(String id,String title){
        this.title=title;
        this.id=id;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }

    public void setId(String id){
        this.id=id;
    }

    public String getId(){
        return id;
    }

}
