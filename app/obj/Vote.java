package obj;
public class Vote{
    private User voter;
    private boolean isUp;
    public Vote(User x, boolean isUp){
       voter=x;
       this.isUp=isUp;
    }
}