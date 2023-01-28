package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;


@Data
@Entity
@Builder
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

    @NotBlank(message = "Community name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @Nullable
    String imageUrl = DEFAULT_IMAGE_URL;

    @Nullable
    String bannerUrl;

    @OneToMany(mappedBy = "subreddit", fetch = LAZY, cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @ManyToMany(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "subreddits_users",
            joinColumns = @JoinColumn(name = "subreddit_id", foreignKey = @ForeignKey(name = "fk_subreddit_id")),
            inverseJoinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_id"))
    )
    private List<User> users = new ArrayList<>();

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_subreddit_author_id")
    )
    private User author;
}
