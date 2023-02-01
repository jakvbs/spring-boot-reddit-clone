package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.jsieczczynski.SpringBootRedditClone.dto.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.service.PostService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class MvcPostController extends BaseController {
    private final PostService postService;

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        PostDto post = postService.getById(id);
        model.addAttribute("post", post);
        return "post/details";
    }
}
