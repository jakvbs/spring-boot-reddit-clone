package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;
import pl.jsieczczynski.SpringBootRedditClone.utils.Helpers;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
@Builder
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

    @Nullable
    private String slug;

    @Lob
    @Nullable
    private String body;

    private Integer voteCount = 0;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_author_id")
    )
    private User author;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "subreddit_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_post_subreddit_id")

    )
    private Subreddit subreddit;

    @PrePersist
    public void makeSlug() {
        slug = Helpers.slugify(title);
    }
}
