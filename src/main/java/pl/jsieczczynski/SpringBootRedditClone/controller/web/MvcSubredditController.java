package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.SubredditDto;
import pl.jsieczczynski.SpringBootRedditClone.service.SubredditService;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/subreddits")
public class MvcSubredditController {
    private final SubredditService subredditService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("subreddits", subredditService.getAll());
        return "subreddits";
    }


    @GetMapping("/add")
    public String createForm(Model model) {
        SubredditDto subredditDto = new SubredditDto();
        model.addAttribute("subreddit", subredditDto);
        return "new_subreddit";
    }

    @PostMapping("/add")
    public String addSubreddit(@ModelAttribute("subreddit") @Valid SubredditDto subreddit, BindingResult result) {
        if (result.hasErrors()) {
            return "new_subreddit";
        }
        subredditService.save(subreddit);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable(value = "id") long id, Model model) {
        SubredditDto subreddit = subredditService.getById(id);
        model.addAttribute("subreddit", subreddit);
        return "update";
    }

    @PostMapping("/update")
    public String saveSubreddit(@ModelAttribute("subreddit") @Valid SubredditDto subreddit, BindingResult result) {
        if (result.hasErrors()) {
            return "update";
        }
        subredditService.save(subreddit);
        return "redirect:/";
    }

    @GetMapping("/deleteSubreddit/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        subredditService.deleteById(id);
        return "redirect:/";
    }
}
