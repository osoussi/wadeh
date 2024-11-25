package com.exemple.backend_spring.service;

import com.exemple.backend_spring.Security.Entity.User;
import com.exemple.backend_spring.dto.CommentDTO;
import com.exemple.backend_spring.model.Comment;
import com.exemple.backend_spring.repository.CommentRepository;
import com.exemple.backend_spring.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ContentRepository contentRepository;

    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public List<CommentDTO> getCommentsByContentId(Long contentId) {
        return commentRepository.findByContentId(contentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(getLoggedInUser());
        comment.setContent(contentRepository.findById(commentDTO.getContentId())
                .orElseThrow(() -> new RuntimeException("Content not found")));
        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public CommentDTO updateComment(Long id, CommentDTO commentDTO) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (isCommentOwner(comment)) {
            throw new AccessDeniedException("You are not allowed to modify this comment");
        }
        comment.setText(commentDTO.getText());
        comment.setContent(contentRepository.findById(commentDTO.getContentId())
                .orElseThrow(() -> new RuntimeException("Content not found")));
        Comment updatedComment = commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    public void deleteComment(Long id) throws AccessDeniedException {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if (isCommentOwner(comment)) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.deleteById(id);
    }

    private boolean isCommentOwner(Comment comment) {
        User loggedInUser = getLoggedInUser();
        return comment.getUser().getId().equals(loggedInUser.getId());
    }

    /**
     * Retrieve the logged-in user from the security context.
     */
    private User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (User) principal;
        }
        throw new RuntimeException("Logged-in user not found");
    }

    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setUserId(comment.getUser().getId());
        dto.setContentId(comment.getContent().getId());
        return dto;
    }
}

