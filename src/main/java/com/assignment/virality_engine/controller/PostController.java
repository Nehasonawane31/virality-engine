package com.assignment.virality_engine.controller;

import com.assignment.virality_engine.dto.CommentCreateRequest;
import com.assignment.virality_engine.dto.LikeRequest;
import com.assignment.virality_engine.dto.PostCreateRequest;
import com.assignment.virality_engine.entity.Comment;
import com.assignment.virality_engine.entity.Post;
import com.assignment.virality_engine.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final InteractionService interactionService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostCreateRequest request) {
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setAuthorType(request.getAuthorType());
        post.setContent(request.getContent());
        
        return ResponseEntity.ok(interactionService.createPost(post));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setAuthorId(request.getAuthorId());
        comment.setAuthorType(request.getAuthorType());
        comment.setContent(request.getContent());
        comment.setDepthLevel(request.getDepthLevel() != null ? request.getDepthLevel() : 0);
        
        return ResponseEntity.ok(interactionService.addComment(postId, comment));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @RequestBody LikeRequest request) {
        interactionService.likePost(postId, request.getAuthorType(), request.getAuthorId());
        return ResponseEntity.ok().build();
    }
}
