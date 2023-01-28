package pl.jsieczczynski.SpringBootRedditClone.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface SubredditMapper {
//    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
//    @Mapping(target = "numberOfUsers", expression = "java(mapUsers(subreddit.getUsers()))")
//    @Mapping(target = "author", expression = "java(mapUsers(subreddit.getAuthor().getUsername()))")
//    SubredditDto mapSubredditToDto(Subreddit subreddit);
//
//    default int mapPosts(List<Post> numberOfPosts) {
//        return numberOfPosts.size();
//    }
//
//    default int mapUsers(List<User> numberOfUsers) {
//        return numberOfUsers.size();
//    }
//
//    default String mapUsers(String author) {
//        return author;
//    }
//
//    @InheritInverseConfiguration
//    @Mapping(target = "posts", ignore = true)
//    @Mapping(target = "users", ignore = true)
//    @Mapping(target = "name", expression = "java(subredditDto.getName().trim())")
//    @Mapping(target = "description", expression = "java(subredditDto.getDescription().trim())")
//    Subreddit mapDtoToSubreddit(CreateSubredditDto subredditDto);
//
//    @InheritInverseConfiguration
//    @Mapping(target = "posts", ignore = true)
//    @Mapping(target = "users", ignore = true)
//    @Mapping(target = "description", expression = "java(subredditDto.getDescription().trim())")
//    Subreddit mapDtoToSubreddit(UpdateSubredditDto subredditDto);
}
