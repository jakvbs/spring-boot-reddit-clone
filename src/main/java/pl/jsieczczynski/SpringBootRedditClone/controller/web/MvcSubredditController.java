package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.CreateSubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.UpdateSubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.InvalidRequestException;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subreddits")
public class MvcSubredditController {
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

    @GetMapping("/{name}")
    public String details(@PathVariable String name, Model model) {
        SubredditDto subreddit = subredditService.getByName(name);
        model.addAttribute("subreddit", subreddit);
        return "subreddit";
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
        return "index";
    }

    @GetMapping("/add")
    public String createForm(Model model) {
        CreateSubredditDto subredditDto = new CreateSubredditDto();
        model.addAttribute("subreddit", subredditDto);
        return "new_subreddit";
    }

    @PostMapping("/add")
    public String addSubreddit(@ModelAttribute("subreddit") @Valid CreateSubredditDto subreddit,
                               BindingResult result,
                               Model model
    ) {
        if (result.hasErrors()) {
            return "new_subreddit";
        }
        subredditService.create(subreddit);
        return "redirect:/subreddits/" + subreddit.getName();
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(value = "id") long id, Model model) {
        SubredditDto subreddit = subredditService.getById(id);
        model.addAttribute("subreddit", subreddit);
        return "update_subreddit";
    }

    @PostMapping("/update")
    public String saveSubreddit(@ModelAttribute("subreddit") @Valid UpdateSubredditDto subreddit,
                                BindingResult result,
                                Model model
    ) {
        if (result.hasErrors()) {
            return "update_subreddit";
        }
        subredditService.update(subreddit);
        return "redirect:/subreddits/" + subreddit.getName();
    }

    @GetMapping("/deleteSubreddit/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        subredditService.deleteById(id);
        return "redirect:/";
    }

    @ExceptionHandler({InvalidRequestException.class})
    public String handleInvalidRequestException(InvalidRequestException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error/500";
    }
}
