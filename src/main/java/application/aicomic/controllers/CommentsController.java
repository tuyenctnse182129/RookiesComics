package application.aicomic.controllers;

import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.CurrentChapterDTO;
import application.aicomic.models.Comments;
import application.aicomic.models.CurrentChapter;
import application.aicomic.services.CommentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comment")
@RestController
public class CommentsController {
    private  CommentsService commentsService;

    @GetMapping
    public List<Comments> getAllComments() {
        return commentsService.getAllComments();
    }
    @PostMapping
    public Comments addComment(@RequestBody Comments comments) {
        return commentsService.addComment(comments);
    }

    @PutMapping("/{id}")
    public Comments updateComment(@PathVariable String id, @RequestBody CommentsDTO commentsDTO) {
        return commentsService.updateComment(id, commentsDTO);
    }
    @GetMapping("/{id}")
    public Comments getCommentById(@PathVariable String id) {
        return commentsService.getCommentById(id);
    }
    @DeleteMapping("/{id}")
    public Comments deleteComment(@PathVariable String id) {
        return commentsService.deleteComment(id);
    }
}
