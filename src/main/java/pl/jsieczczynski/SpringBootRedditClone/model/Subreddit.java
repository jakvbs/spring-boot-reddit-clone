package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subreddits", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "subreddit_name_unique")
})
@EntityListeners(AuditingEntityListener.class)
public class Subreddit {
    public static final String DEFAULT_IMAGE_URL = "https://www.redditstatic.com/icon.png";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @NotBlank(message = "Subreddit name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl = DEFAULT_IMAGE_URL;

    private String bannerUrl;

    @OneToMany(mappedBy = "subreddit", fetch = LAZY, cascade = CascadeType.ALL)
    private Set<Post> posts = new HashSet<>();

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name = "subreddits_users",
            joinColumns = @JoinColumn(name = "subreddit_id", foreignKey = @ForeignKey(name = "fk_subreddit_id")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_id"))
    )
    private Set<User> users = new HashSet<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_subreddit_author_id")
    )
    private User author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subreddit subreddit = (Subreddit) o;
        return id.equals(subreddit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "name = " + name + ", " +
                "description = " + description + ", " +
                "imageUrl = " + imageUrl + ", " +
                "bannerUrl = " + bannerUrl + ")";
    }
}
