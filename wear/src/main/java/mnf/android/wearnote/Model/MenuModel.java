package mnf.android.wearnote.Model;

/**
 * Created by muneef on 29/01/17.
 */

public class MenuModel {

    private String title;
    private String id;
    private int icon;
    public MenuModel(String id,String title,int icon){
        this.title=title;
        this.id=id;
        this.icon=icon;
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


    public void setIcon(int icon){
        this.icon=icon;
    }

    public int getIcon(){
        return icon;
    }

}
