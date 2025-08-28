package com.huce.webblog.service;

import com.huce.webblog.dto.response.CommentResponse;
import com.huce.webblog.entity.Comment;

import java.util.List;

public interface ICommentService {
    public Comment addComment(Long parentId, String content, String uid, String pid);

    public List<CommentResponse> getComments(String pid);

    public CommentResponse updateComment(Long commentId, String uid, String pid, String content);
    public CommentResponse deleteComment(Long commentId, String uid);
    }
