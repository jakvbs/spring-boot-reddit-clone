package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.Data;
import org.springframework.lang.Nullable;
import pl.jsieczczynski.SpringBootRedditClone.utils.Helpers;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotBlank(message = "Post name cannot be empty or Null")
    private String title;

    @Nullable
    private String slug;

    @Lob
    @Nullable
    private String body;

    private Integer voteCount = 0;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_author_id")
    )
    private User author;

    Instant createdDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "subreddit_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_subreddit_id")

    )
    private Subreddit subreddit;

    @PrePersist
    public void makeSlug() {
        this.slug = Helpers.slugify(title);
    }
}
