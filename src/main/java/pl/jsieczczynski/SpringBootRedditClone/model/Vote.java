package pl.jsieczczynski.SpringBootRedditClone.model;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private VoteType voteType;

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
