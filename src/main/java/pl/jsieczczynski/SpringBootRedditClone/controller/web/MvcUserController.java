package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.UserDto;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;
import pl.jsieczczynski.SpringBootRedditClone.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class MvcUserController extends BaseController {
    private final UserService userService;
    private final SubredditService subredditService;

    @GetMapping
    public String index(@RequestParam(required = false) Integer page,
                        @RequestParam(required = false) String sort,
                        @RequestParam(required = false) String search,
                        Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "username", "email", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        return findPaginated(pageNumber, sortField, sortDirection, search, model);
    }

//    @GetMapping("/{name}/delete")
//    public String deleteByName(@PathVariable(value = "name") String name) {
//        userService.deleteByName(name);
//        return "redirect:/subreddits";
//    }

    @GetMapping("/{name}")
    public String details(@PathVariable String name, Model model) {
        UserDto user = userService.getByName(name);
        model.addAttribute("user", user);
        return "user/details";
    }

    @GetMapping("/{name}/created-subreddits")
    public String createdSubreddits(@PathVariable String name,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) String sort,
                                    Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "name", "description", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        Page<SubredditDto> paged = subredditService.findPaginatedByAuthor(pageNumber, sortField, sortDirection, name);
        return findPaginatedSubreddits(pageNumber, sortField, sortDirection, paged, model);
    }

    @GetMapping("/{name}/joined-subreddits")
    public String joinedSubreddits(@PathVariable String name,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) String sort,
                                   Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "name", "description", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        Page<SubredditDto> paged = subredditService.findPaginatedByUser(pageNumber, sortField, sortDirection, name);
        return findPaginatedSubreddits(pageNumber, sortField, sortDirection, paged, model);
    }

    private static String findPaginatedSubreddits(int page,
                                                  String sortField,
                                                  Sort.Direction sortDirection,
                                                  Page<SubredditDto> paged,
                                                  Model model) {
        List<SubredditDto> subreddits = paged.getContent();
        if (page != 1 && subreddits.isEmpty()) {
            return findPaginatedSubreddits(page - 1, sortField, sortDirection, paged, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("subreddits", subreddits);
        return "user/subreddits";
    }

    private String findPaginated(int page,
                                 String sortField,
                                 Sort.Direction sortDirection,
                                 String search,
                                 Model model) {

        Page<UserDto> paged = userService.findPaginated(page, sortField, sortDirection, search);
        List<UserDto> users = paged.getContent();
        if (page != 1 && users.isEmpty()) {
            return findPaginated(page - 1, sortField, sortDirection, search, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("users", users);
        return "user/list_all";
    }

//    @GetMapping("/add")
//    public String createForm(Model model) {
//        SubredditDto subredditDto = new SubredditDto();
//        model.addAttribute("subreddit", subredditDto);
//        return "subreddit/create";
//    }
//
//    @PostMapping
//    public String addSubreddit(@ModelAttribute("subreddit") @Valid SubredditDto subreddit,
//                               BindingResult result
//    ) {
//        if (result.hasErrors()) {
//            return "subreddit/create";
//        }
//        userService.create(subreddit);
//        return "redirect:/subreddits/" + subreddit.getName();
//    }

    @GetMapping("/{name}/toggle-ban")
    public String banUser(@PathVariable String name,
                          @RequestParam(required = false) Integer page,
                          @RequestParam(required = false) String sort,
                          HttpServletRequest request) {
        userService.toggleBan(name);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/update/{username}")
    public String updateForm(@PathVariable(value = "username") String username, Model model) {
        UserDto user = userService.getByName(username);
        model.addAttribute("user", user);
        return "user/update";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") @Valid UserDto.Update user,
                             BindingResult result
    ) {
        if (result.hasErrors()) {
            return "user/update";
        }
        userService.update(user);
        return "redirect:/users/" + user.getUsername();
    }
}
