package pl.jsieczczynski.SpringBootRedditClone.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.model.Post;
import pl.jsieczczynski.SpringBootRedditClone.model.Subreddit;
import pl.jsieczczynski.SpringBootRedditClone.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    @Mapping(target = "numberOfUsers", expression = "java(mapUsers(subreddit.getUsers()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    default Integer mapUsers(List<User> numberOfUsers) {
        return numberOfUsers.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "name", expression = "java(subredditDto.getName().trim())")
    @Mapping(target = "description", expression = "java(subredditDto.getDescription().trim())")
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
}
