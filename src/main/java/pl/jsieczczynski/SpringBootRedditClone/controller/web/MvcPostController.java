package pl.jsieczczynski.SpringBootRedditClone.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.CommentDto;
import pl.jsieczczynski.SpringBootRedditClone.dto.model.PostDto;
import pl.jsieczczynski.SpringBootRedditClone.service.PostService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        PostDto post = postService.getById(id);
        model.addAttribute("post", post);
        return "post/update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("post") @Valid PostDto post) {
        postService.update(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest request) {
        postService.deleteById(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @GetMapping("/{id}/add-comment")
    public String addCommentForm(@PathVariable Long id, Model model) {
        CommentDto comment = new CommentDto();
        comment.setPostId(id);
        model.addAttribute("comment", comment);
        return "comment/create";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @ModelAttribute("comment") @Valid CommentDto comment) {
        postService.addComment(comment);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/comments/{id}/update")
    public String updateCommentForm(@PathVariable Long id, Model model) {
        CommentDto comment = postService.getCommentById(id);
        model.addAttribute("comment", comment);
        return "comment/update";
    }

    @PostMapping("/comments/update")
    public String updateComment(@ModelAttribute("comment") @Valid CommentDto comment) {
        postService.updateComment(comment);
        return "redirect:/posts/comments/" + comment.getId();
    }

    @GetMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        postService.deleteCommentById(commentId);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/{id}/comments")
    public String getSubredditUsers(@PathVariable Long id,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) String sort,
                                    Model model) {
        int pageNumber = page != null && page >= 1 ? page : 1;
        Sort.Direction sortDirection = sort != null && sort.charAt(0) == '-' ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sort != null && sort.charAt(0) == '-' ? sort.substring(1) : sort == null ? "id" : sort;
        List<String> allowed = List.of("id", "body", "createdAt");
        if (!allowed.contains(sortField)) {
            sortField = "id";
        }
        Page<CommentDto> paged = postService.findPaginatedComments(pageNumber, sortField, sortDirection, id);
        return findPaginatedComments(pageNumber, sortField, sortDirection, paged, id, model);
    }

    private static String findPaginatedComments(int page,
                                                String sortField,
                                                Sort.Direction sortDirection,
                                                Page<CommentDto> paged,
                                                Long postId,
                                                Model model) {
        List<CommentDto> comments = paged.getContent();
        if (page != 1 && comments.isEmpty()) {
            return findPaginatedComments(page - 1, sortField, sortDirection, paged, postId, model);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paged.getTotalPages());
        model.addAttribute("totalItems", paged.getTotalElements());

        model.addAttribute("currentSort", sortDirection == Sort.Direction.ASC ? sortField : "-" + sortField);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        model.addAttribute("postId", postId);
        model.addAttribute("comments", comments);
        return "comment/list";
    }

    @GetMapping("/comments/{commentId}")
    private String commentDetails(@PathVariable Long commentId, Model model) {
        CommentDto comment = postService.getCommentById(commentId);
        model.addAttribute("comment", comment);
        return "comment/details";
    }
}
