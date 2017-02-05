package sec.project.domain;

//import javax.persistence.Entity;
//import javax.persistence.Id;
//import org.springframework.data.jpa.domain.AbstractPersistable;

//@Entity
public class Signup  {//extends AbstractPersistable<Long> {

   // @Id
    private int id;
    private String username;
    private String password;

    public Signup(int id, String username, String address) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
