package com.huce.webblog.service.impl;

import com.huce.webblog.advice.exception.BadRequestException;
import com.huce.webblog.dto.mapper.CommentMapper;
import com.huce.webblog.dto.response.CommentResponse;
import com.huce.webblog.entity.Comment;
import com.huce.webblog.entity.Post;
import com.huce.webblog.repository.CommentRepository;
import com.huce.webblog.repository.PostRepository;
import com.huce.webblog.service.ICommentService;
import com.huce.webblog.service.IPythonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements ICommentService {
    CommentRepository commentRepository;
    IPythonService pythonService;
    PostRepository postRepository;
    CommentMapper commentMapper;

    @Override
    public Comment addComment(Long parentId, String content, String uid, String pid) {
        Post post = postRepository.findFirstByIdAndIsDeletedFalse(pid);
//				.orElseThrow(() -> new BadRequestException("Post not found"));
        if (post == null) {
            throw new BadRequestException("Post not found");
        }

        int insertLeft;
        if (parentId == null) {
            int maxRight = commentRepository.findMaxRightByPost(pid);
            insertLeft = maxRight + 1;
        } else {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new BadRequestException("Parent comment not found"));

            int depth = commentRepository.countParents(parent.getLeftValue(), parent.getRightValue(), pid);
            if (depth >= 1) {
                throw new BadRequestException("Only original comments are allowed.");
            }

            insertLeft = parent.getRightValue();
            commentRepository.shiftLeftValues(pid, insertLeft);
            commentRepository.shiftRightValues(pid, insertLeft);
        }
        Comment comment = commentRepository.save(Comment.builder()
                .uid(uid)
                .rightValue(insertLeft + 1)
                .leftValue(insertLeft)
                .content(pythonService.filterContent(content).getFiltered_content())
                .post(post)
                .build());
        post.incrementCommentsCount();
        postRepository.save(post);
        return comment;
    }

    @Override
    public List<CommentResponse> getComments(String pid) {
        List<CommentResponse> commentResponses = commentRepository.findByPostIdAndIsDeletedOrderByLeftValueAsc(pid, false)
                .stream()
                .map(commentMapper::toCommentResponse)
                .toList();

        return commentMapper.buildTreeFromNestedSet(commentResponses);
    }

    public CommentResponse updateComment(Long commentId, String uid, String pid, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(null);

        if (comment == null) {
            throw new BadRequestException("Comment not found");
        }

        String userId = comment.getUid();
        if (!userId.equals(uid))
            throw new BadRequestException("Not access");

        comment.setContent(pythonService.filterContent(content).getFiltered_content());
//        comment.setContent(content);

        commentRepository.save(comment);
        return commentMapper.toCommentResponse(comment);
    }

    public CommentResponse deleteComment(Long commentId, String uid) {
        Comment existingComment = commentRepository.findById(commentId).orElse(null);

        if (existingComment == null) {
            throw new BadRequestException("Comment not found");
        }

        Post post = postRepository.findFirstByIdAndIsDeletedFalse(existingComment.getPost().getId());
        String userId = existingComment.getUid();

        if (!userId.equals(uid))
            throw new BadRequestException("Not access");

        existingComment.setDeleted(true);
        post.decrementCommentsCount();
        postRepository.save(post);

        return commentMapper.toCommentResponse(commentRepository.save(existingComment));
    }
}
