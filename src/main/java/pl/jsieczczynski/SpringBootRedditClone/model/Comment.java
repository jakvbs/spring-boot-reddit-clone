package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotEmpty
    private String body;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    private Instant createdAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "author_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_author_id")
    )
    private User author;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_comment_parent_id")
    )
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children;
}
