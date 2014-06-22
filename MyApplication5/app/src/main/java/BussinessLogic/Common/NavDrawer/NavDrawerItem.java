package BussinessLogic.Common.NavDrawer;

/**
 * Created by Solomiia on 6/11/2014.
 */
public class NavDrawerItem {

    private boolean extra;
    private String title;
    private int icon;
    private boolean checked;
    private String count = "0";

    private boolean isCounterVisible = false;

    public NavDrawerItem(){}

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, int icon, boolean checked ){
        this.title = title;
        this.icon = icon;
        this.checked = checked;
    }


    public NavDrawerItem(String title, int icon, boolean checked, boolean extra ){
        this.title = title;
        this.icon = icon;
        this.checked = checked;
        this.extra = extra;
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

    public Boolean getChecked() { return checked; }

    public void setChecked(Boolean checked)
    {
        this.checked = checked;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

    public Boolean getExtra() {return extra;}

    public void setExtra() {this.extra = extra;}

}
