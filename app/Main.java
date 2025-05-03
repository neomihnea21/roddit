import java.util.Date;
import obj.User;
import obj.Roddit;
import obj.Subroddit;
import obj.Feed;
import obj.Post;
import obj.TopLevelPost;
import obj.Report;
public class Main {
    public static void main(String[] args) throws Exception {
        Roddit roddit=new Roddit();
        Date systemDate=new Date();
        User currentUser=new User("neomihnea", systemDate); //if more users are logged in at once, they all get a copy of this
        User alterUser=new User("neomihnea2", systemDate);
        roddit.signup(currentUser, "neomihnea");
        roddit.login(currentUser, "neomihnea");
        roddit.addSub(currentUser, "totul");
        //let's post something here, innit?
        Subroddit totul=roddit.getSubByName("totul");
        TopLevelPost firstPost=new TopLevelPost("Should have been a cowboy", "I bet you never heard old marshal Dillon say", totul, currentUser);
        totul.makePost(firstPost);
        totul.getPostByIndex(0).vote(currentUser, true);
        totul.getPostByIndex(0).makeComment("Miss Kitty, have you ever thought of running away?");
        System.out.println(currentUser.getKarma());

        Feed newFeed=new Feed(totul);
        newFeed.expose();
    }
}
//CAPABILITIES:
//1: create account W
//2: log into account W
//3: make a sub W
//4: post in a sub W
//5: Report a post W
//6: comment on a post W
//7: see all comments on a post W 
//8: see all posts in a sub W
//9: grant/revoke modness to other users W
//10: upvote/downvote posts W
//11: add a reply to comments (TBD?)
//12: find a user's karma (how many upvotes and downvotes he has on his posts) W
//13: (I dunno) see a random selecao of posts from all subs (Feed) 