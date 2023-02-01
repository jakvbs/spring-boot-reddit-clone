package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Min(value = -1)
    @Max(value = 1)
    private int direction;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "post_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_vote_post_id")
    )
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "comment_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_vote_comment_id")
    )
    private Comment comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_vote_user_id")
    )
    private User user;
}
