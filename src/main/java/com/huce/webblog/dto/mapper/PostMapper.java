package com.huce.webblog.dto.mapper;

import com.huce.webblog.dto.response.PostResponse;
import com.huce.webblog.dto.response.PostSummaryResponse;
import com.huce.webblog.dto.response.SimpInfoUserResponse;
import com.huce.webblog.entity.Category;
import com.huce.webblog.entity.CategoryBlog;
import com.huce.webblog.entity.Post;
import com.huce.webblog.entity.Hashtag;

import com.huce.webblog.service.impl.FollowServiceImpl;
import com.huce.webblog.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostMapper {
    //	AuthClient authClient;
    FollowServiceImpl followService;
    CommentMapper commentMapper;
    UserServiceImpl userService;

    public PostResponse toPostResponse(Post post, List<Post> relatedPost){
        Set<String> userIds = new HashSet<>();
        userIds.add(post.getUid());
        if (relatedPost != null) {
            userIds.addAll(relatedPost.stream()
                    .map(Post::getUid)
                    .collect(Collectors.toSet()));
        }

        List<SimpInfoUserResponse> users = userService.fetchUserByIdIn(new ArrayList<>(userIds));
        Map<String, SimpInfoUserResponse> userMap = users.stream()
                .collect(Collectors.toMap(SimpInfoUserResponse::getId, Function.identity()));
//		Map<String, SimpInfoUserResponse> userMap = new HashMap<>();

        return PostResponse.builder()
                .id(post.getId())
                .category(getCategoryName(post.getCategoryBlogs()))
                .commentsCount(post.getCommentsCount())
                .rawContent(post.getRawContent())
                .title(post.getTitle())
                .summaryAi(post.getSummaryAi())
                .content(post.getContent())
                .uid(post.getUid())
                .userResponse(userMap.get(post.getUid()))
                .viewsCount(post.getViewsCount())
                .cover(post.getCover())
                .hashtags(post.getHashtags().stream().map(Hashtag::getHashtag).toList())
                .hasSensitiveContent(post.isHasSensitiveContent())
                .comments(commentMapper.buildTreeFromNestedSet(
                        Optional.ofNullable(post.getComments())
                                .orElse(List.of())
                                .stream()
                                .map(commentMapper::toCommentResponse)
                                .toList()
                ))
                .relatedPosts(
                        relatedPost == null ? List.of() :
                                relatedPost.stream()
                                        .map(p -> toPostSummaryResponse(p, userMap.get(p.getUid())))
                                        .toList()
                )
                .createdAt(post.getCreatedAt())
                .build();
    }

    public PostSummaryResponse toPostSummaryResponse(Post post, SimpInfoUserResponse user){
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .cover(post.getCover())
                .viewsCount(post.getViewsCount())
                .category(getCategoryName(post.getCategoryBlogs()))
                .commentsCount(post.getCommentsCount())
                .hasSensitiveContent(post.isHasSensitiveContent())
                .createdAt(post.getCreatedAt())
                .userResponse(user)
                .content(post.getContent())
                .excerpt(post.getContent().length() <= 70 ?
                        removeHtmlTags(post.getContent()) :
                        removeHtmlTags(post.getContent().substring(0, Math.min(post.getContent().length(), 100))))
                .build();
    }

    public List<String> getCategoryName(List<CategoryBlog> categoryBlogs){
        if(categoryBlogs.isEmpty()){
            return List.of();
        }
        List<Category> categories = categoryBlogs.stream().map(CategoryBlog::getCategory).toList();
        if(categories.isEmpty()) return null;
        return categories.stream().map(Category::getCname).toList();
    }

    public String removeHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", "").trim();
    }
}