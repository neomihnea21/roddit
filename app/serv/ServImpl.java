package serv;
import java.sql.Date;
import java.util.HashMap;
import obj.Roddit;
import obj.Subroddit;
import obj.TopLevelPost;
import obj.User;
import obj.Post;
import obj.Feed;
import obj.Report;
import obj.Vote;
import serv.VoteService.VoteType;
public class ServImpl implements Serv{
    Roddit roddit;
    Date systemDate;
    UserService users;
    SubrodditService subs;
    PostService posts;
    VoteService votes;
    HashMap<String, User> userReference;
    public ServImpl(){
        roddit=new Roddit();
        systemDate=new Date(System.currentTimeMillis());
        userReference=new HashMap<>();
        users=UserService.getInstance();
        subs=SubrodditService.getInstance();
        posts=PostService.getInstance();
        votes=VoteService.getInstance();
    }
    public void signup(String username, String password){
         User newUser=new User(username, systemDate);
         roddit.signup(newUser, password);
         userReference.put(username, newUser);
         users.readUsers();
    }
    public void login(String name, String password){
        User attemptedLogin=userReference.get(name);
        roddit.login(attemptedLogin, password);
    }
    public void makeSubreddit(String name, String founderName){
        User founder=userReference.get(founderName);
        roddit.addSub(founder, name);
        subs.createSubroddit("name");
    }
    public void grantModness(String subName, String newMod){
        User newModRef=userReference.get(newMod);
        Subroddit sub=roddit.getSubByName(subName);
        sub.addMod(newModRef);
    }
    public void revokeModness(String subName, String newMod){
        User newModRef=userReference.get(newMod);
        Subroddit sub=roddit.getSubByName(subName);
        sub.sackMod(newModRef);
    }
    public void reportPost(String username, Post reportedPost, String reason){
        User user=userReference.get(username);
        Report newReport=new Report(user, reportedPost, reason);
        newReport.log();
    }
    public void makePost(String subName, String title, String text, String authorName){
        TopLevelPost newThread=new TopLevelPost(title, text, roddit.getSubByName(subName), userReference.get(authorName));
        roddit.getSubByName(subName).makePost(newThread);
        int subId=subs.getSubrodditIdByTitle(subName);
        int userId=users.getUserIdByName(authorName);
        roddit.logPost(newThread);//HOLY SMOKES, IT WORKED
        int postId=posts.createPost(text, 8, subId, roddit.getCode(newThread));
    }
    public void comment(TopLevelPost post, String text){
        post.makeComment(text);
    }
    public void printThread(Post post){
        post.expose();
    }
    public void getFeed(String subName){
        Feed newFeed=new Feed(roddit.getSubByName(subName));
        newFeed.expose();
    }
    public void vote(String user, Post post, boolean isUpvote){
        post.vote(userReference.get(user), isUpvote);
        if(isUpvote){
            int userId=users.getUserIdByName(user);
            votes.createVote(1, 8, roddit.getCode(post));
        }
        else{
            int userId=users.getUserIdByName(user);
            votes.createVote(-1, 8, roddit.getCode(post));
        }
    }
    public void getKarma(String User){
        User sought=userReference.get(User);
        System.out.println(sought.getKarma());
    }
    public void editName(String oldName, String newName){
        User u=userReference.get(oldName);
        u.changeName(newName);
        userReference.remove(oldName);
        userReference.put(newName, u);
        users.updateUsername(oldName, newName);
    }
    public void deleteName(String name){
         userReference.remove(name);
         //users.deleteUserByUsername(name);
    }
    public Subroddit getSubByName(String name){
        return roddit.getSubByName(name);
    }
    public void listSubs(){
        subs.readSubroddits();
    }
}