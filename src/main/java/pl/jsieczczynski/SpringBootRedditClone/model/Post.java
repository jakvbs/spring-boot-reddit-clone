package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import pl.jsieczczynski.SpringBootRedditClone.utils.Helpers;

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
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @NotBlank(message = "Post name cannot be empty or Null")
    private String title;

    @Lob
    @Nullable
    private String body;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_author_id")
    )
    private User author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "subreddit_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_subreddit_id")

    )
    private Subreddit subreddit;

    @OneToMany(mappedBy = "post", fetch = LAZY, cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post", fetch = LAZY, cascade = CascadeType.ALL)
    private Set<Vote> votes = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", author=" + author +
                ", subreddit=" + subreddit +
                ", votes=" + votes +
                '}';
    }
}
