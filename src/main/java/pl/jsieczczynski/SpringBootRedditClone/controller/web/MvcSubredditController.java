package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.UserDto;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subreddits")
public class MvcSubredditController extends BaseController {
    private final SubredditService subredditService;

    @GetMapping
    public String index(@RequestParam(required = false) Integer page,
                        @RequestParam(required = false) String sort,
                        @RequestParam(required = false) String search,
                        Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "name", "description", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        return findPaginated(pageNumber, sortField, sortDirection, search, model);
    }

    @GetMapping("/{subredditName}/delete")
    public String deleteByName(@PathVariable String subredditName) {
        subredditService.deleteByName(subredditName);
        return "redirect:/subreddits";
    }

    @GetMapping("/{subredditName}")
    public String details(@PathVariable String subredditName, Model model) {
        SubredditDto subreddit = subredditService.getByName(subredditName);
        model.addAttribute("subreddit", subreddit);
        return "subreddit/details";
    }

    private String findPaginated(int page,
                                 String sortField,
                                 Sort.Direction sortDirection,
                                 String search,
                                 Model model) {

        Page<SubredditDto> paged = subredditService.findPaginated(page, sortField, sortDirection, search);
        List<SubredditDto> subreddits = paged.getContent();
        if (page != 1 && subreddits.isEmpty()) {
            return findPaginated(page - 1, sortField, sortDirection, search, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("subreddits", subreddits);
        return "subreddit/list_all";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        SubredditDto.Create subredditDto = new SubredditDto.Create();
        model.addAttribute("subreddit", subredditDto);
        return "subreddit/create";
    }

    @PostMapping
    public String addSubreddit(@ModelAttribute("subreddit") @Valid SubredditDto.Create subreddit,
                               BindingResult result
    ) {
        if (result.hasErrors()) {
            return "subreddit/create";
        }
        subredditService.create(subreddit);
        return "redirect:/subreddits/" + subreddit.getName();
    }

    @GetMapping("/{subredditName}/update")
    public String updateForm(@PathVariable String subredditName, Model model) {
        SubredditDto subreddit = subredditService.getByName(subredditName);
        model.addAttribute("subreddit", subreddit);
        return "subreddit/update";
    }

    @PostMapping("/update")
    public String updateSubreddit(@ModelAttribute("subreddit") @Valid SubredditDto.Update subreddit,
                                  BindingResult result
    ) {
        if (result.hasErrors()) {
            return "subreddit/update";
        }
        subredditService.update(subreddit);
        return "redirect:/subreddits/" + subreddit.getName();
    }

    @GetMapping("/{subredditName}/add-user")
    public String addUserForm(@PathVariable String subredditName, Model model) {
        UserDto.Add user = new UserDto.Add();
        user.setSubredditName(subredditName);
        model.addAttribute("user", user);
        return "subreddit/add_user";
    }

    @PostMapping("/{subredditName}/users")
    public String addUser(@PathVariable String subredditName,
                          @ModelAttribute("user") @Valid UserDto.Add user,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "subreddit/add_user";
        }
        subredditService.addUser(subredditName, user);
        return "redirect:/subreddits/" + subredditName;
    }

    @GetMapping("/{subredditName}/add-post")
    public String addPostForm(@PathVariable String subredditName, Model model) {
        PostDto.Create post = new PostDto.Create();
        post.setSubredditName(subredditName);
        model.addAttribute("post", post);
        return "subreddit/add_post";
    }

    @PostMapping("/{subredditName}/posts")
    public String addPost(@PathVariable String subredditName,
                          @ModelAttribute("post") @Valid PostDto.Create post,
                          BindingResult result) {
        if (result.hasErrors()) {
            return "subreddit/add_post";
        }
        PostDto newPost = subredditService.addPost(subredditName, post);
        return "redirect:/posts/" + newPost.getId();
    }

    @GetMapping("/{subredditName}/users/{username}/delete")
    public String removeUser(@PathVariable String subredditName,
                             @PathVariable(value = "username") String username,
                             HttpServletRequest request) {
        subredditService.removeUser(subredditName, username);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/{subredditName}/users")
    public String getSubredditUsers(@PathVariable String subredditName,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) String sort,
                                    Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "username", "email", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        Page<UserDto> paged = subredditService.findPaginatedUsers(pageNumber, sortField, sortDirection, subredditName);
        return findPaginatedUsers(pageNumber, sortField, sortDirection, paged, subredditName, model);
    }

    private static String findPaginatedUsers(int page,
                                             String sortField,
                                             Sort.Direction sortDirection,
                                             Page<UserDto> paged,
                                             String subredditName,
                                             Model model) {
        List<UserDto> users = paged.getContent();
        if (page != 1 && users.isEmpty()) {
            return findPaginatedUsers(page - 1, sortField, sortDirection, paged, subredditName, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("users", users);
        model.addAttribute("subredditName", subredditName);
        return "user/list";
    }

    @GetMapping("/{subredditName}/posts")
    public String getSubredditPosts(@PathVariable String subredditName,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) String sort,
                                    Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "title", "voteScore", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        Page<PostDto> paged = subredditService.findPaginatedPosts(pageNumber, sortField, sortDirection, subredditName);
        return findPaginatedPosts(pageNumber, sortField, sortDirection, paged, subredditName, model);
    }

    private static String findPaginatedPosts(int page,
                                             String sortField,
                                             Sort.Direction sortDirection,
                                             Page<PostDto> paged,
                                             String subredditName,
                                             Model model) {
        List<PostDto> posts = paged.getContent();
        if (page != 1 && posts.isEmpty()) {
            return findPaginatedPosts(page - 1, sortField, sortDirection, paged, subredditName, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("posts", posts);
        model.addAttribute("subredditName", subredditName);
        return "post/list";
    }
}
