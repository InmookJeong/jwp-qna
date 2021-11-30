package qna.domain;

import qna.UnAuthorizedException;
import qna.domain.field.Email;
import qna.domain.field.Name;
import qna.domain.field.Password;
import qna.domain.field.UserId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User extends BaseEntity {
    public static final GuestUser GUEST_USER = new GuestUser();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Name name;

    @Embedded
    private Password password;

    @Embedded
    private UserId userId;

    @Embedded
    private Answers answers;

    @OneToMany(mappedBy = "writer")
    private List<Question> question = new ArrayList<Question>();

    @Embedded
    private DeletedHistories deleteHistories;

    // Arguments가 없는 Default Constructor 생성
    protected User() {}

    public User(String userId, String password, String name, String email) {
        this(null, userId, password, name, email);
    }

    public User(Long id, String userId, String password, String name, String email) {
        this.id = id;
        this.userId = new UserId(userId);
        this.password = new Password(password);
        this.name = new Name(name);
        this.email = new Email(email);
    }

    public void update(User loginUser, User target) {
        if (!matchUserId(loginUser.userId)) {
            throw new UnAuthorizedException();
        }

        if (!matchPassword(target.password.getPassword())) {
            throw new UnAuthorizedException();
        }

        this.name = target.name;
        this.email = target.email;
    }

    /**
     * User Password 변경
     * @param loginUser
     * @param newPassword
     */
    public void updatePassword(User loginUser, String newPassword) {
        if (!matchUserId(loginUser.userId)) {
            throw new UnAuthorizedException();
        }

        if (!matchPassword(newPassword)) {
            this.password = new Password(newPassword);
        }
    }

    private boolean matchUserId(UserId userId) {
        return this.userId.equals(userId);
    }

    public boolean matchPassword(String targetPassword) {
        return this.password.isEqualsPassword(targetPassword);
    }

    public boolean isGuestUser() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId.getUserId();
    }

    public String getPassword() {
        return this.password.getPassword();
    }

    public String getName() {
        return this.name.getName();
    }

    public void registerName(String name) {
        this.name = new Name(name);
    }

    public String getEmail() {
        return this.email.getEmail();
    }

    public void registerEmail(String email) {
        this.email = new Email(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.id, user.id);
    }

    private static class GuestUser extends User {
        @Override
        public boolean isGuestUser() {
            return true;
        }
    }
}
