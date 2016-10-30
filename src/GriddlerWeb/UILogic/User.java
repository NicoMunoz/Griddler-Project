package GriddlerWeb.UILogic;

public class User
{
    private String userName;
    private String userType;
    private String score;
    private boolean isPlaying = false;

    public User (String name, String type)
    {
        this.userName = name;
        this.userType = type;
    }

    public User (String name, String type, String score)
    {
        this.userName = name;
        this.userType = type;
        this.score = score;
    }

    public String GetName(){return userName;}
    public String GetType(){return userType;}
    public String GetScore(){return score;}
    public void setPlay(boolean isPlaying){
        this.isPlaying = isPlaying;
    }
    public boolean isPlaying(){return isPlaying;}
}