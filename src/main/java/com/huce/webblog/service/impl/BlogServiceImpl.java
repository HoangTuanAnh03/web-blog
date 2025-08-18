package com.huce.webblog.service.impl;

import com.huce.webblog.advice.exception.BadRequestException;
import com.huce.webblog.dto.mapper.PostMapper;
import com.huce.webblog.dto.record.CategoryPostCountDTO;
import com.huce.webblog.dto.request.PostRequest;
import com.huce.webblog.dto.response.*;
import com.huce.webblog.entity.*;
import com.huce.webblog.repository.*;
import com.huce.webblog.service.IBlogRedisService;
import com.huce.webblog.service.IBlogService;
import com.huce.webblog.service.IPythonService;
import com.huce.webblog.service.UserService;
import com.huce.webblog.util.SlugUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogServiceImpl implements IBlogService {
    PostMapper postMapper;
    PostRepository postRepository;
    IPythonService pythonService;
    CategoryRepository categoryRepository;
    IBlogRedisService blogRedisService;
    CategoryBlogRepository categoryBlogRepository;
    FollowRepository followRepository;
    NotificationRepository notificationRepository;
    UserService userService;

    @Override
    public Page<PostSummaryResponse> getAllPostSummary(int page, int size, String search, List<Long> categoryIds) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Specification<Post> spec = Specification.where(null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isDeleted")));
        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(root.get("title"), "%" + search + "%"),
                            criteriaBuilder.like(root.get("content"), "%" + search + "%")
                    ));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Post, CategoryBlog> categoryBlogJoin = root.join("categoryBlogs", JoinType.INNER);
                Join<CategoryBlog, Category> categoryJoin = categoryBlogJoin.join("category", JoinType.INNER);

                Predicate categoryPredicate = categoryJoin.get("id").in(categoryIds);
                query.groupBy(root.get("id"));
                query.having(criteriaBuilder.equal(
                        criteriaBuilder.count(categoryJoin.get("id")),
                        categoryIds.size()
                ));

                return categoryPredicate;
            });
        }

        Page<Post> posts = postRepository.findAll(spec, pageable);
        List<String> uids = posts.stream().map(Post::getUid).distinct().toList();

        List<SimpInfoUserResponse> users = userService.fetchUserByIdIn(new ArrayList<>(uids));

        Map<String, SimpInfoUserResponse> userMap = users.stream().collect(Collectors.toMap(SimpInfoUserResponse::getId, Function.identity()));
        List<PostSummaryResponse> responses = posts.stream()
                .map(post -> postMapper.toPostSummaryResponse(post, userMap.get(post.getUid())))
                .toList();

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest post, String uid) {
        if (post.getCids().size() > 3) {
            throw new BadRequestException("Tối đa 3 chủ đề mỗi bài.");
        }
        FilterResponse filter = pythonService.filterContent(post.getContent());
        Post p = Post.builder()
                .id(SlugUtil.toSlug(post.getTitle()))
                .viewsCount(0)
                .commentsCount(0)
                .content(post.getContent())
                .title(post.getTitle())
                .uid(uid)
                .cover(post.getCover())
                .hasSensitiveContent(filter.isHas_sensitive_content())
                .rawContent(post.getContent())
                .content(filter.getFiltered_content())
                .build();
        List<CategoryBlog> categoryBlogs;
        if (post.getCids() != null && !post.getCids().isEmpty()) {
            List<Category> categories = categoryRepository.findByIdIn(post.getCids());
            categoryBlogs = categories.stream()
                    .map(c -> CategoryBlog.builder().post(p).category(c).build())
                    .toList();
            p.setCategoryBlogs(categoryBlogs);
        }

        List<Hashtag> hashtags = new ArrayList<>();
        if (post.getHashtags() != null && !post.getHashtags().isEmpty()) {
            hashtags = post.getHashtags().stream()
                    .map(tag -> Hashtag.builder().hashtag(tag).post(p).build())
                    .toList();
        }
        p.setHashtags(hashtags);
        PostResponse postResponse = postMapper.toPostResponse(postRepository.save(p), null);
        List<Follow> followers = followRepository.findAllByFollowingId(p.getUid());
        List<Notification> notifications = followers.stream()
                .map(follow -> Notification.builder().uid(follow.getFollowerId())
                        .post(p)
                        .message("Người bạn theo dõi vừa đăng một bài viết mới!")
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        return postResponse;
    }

    @Override
    public PostResponse view(String pid, String uid) {

        Post post = postRepository.findFirstByIdAndIsDeletedFalse(pid);
        if (post == null) {
            throw new BadRequestException("Post Not Found");
        }
//		System.out.println(uid);
        boolean isRead = uid != null && blogRedisService.isUserViewed(uid, pid);
        if (isRead) {
            post.incrementViewsCount();
            postRepository.save(post);
        }
        List<Post> relatedPost = postRepository.findRelatedPosts(pid);
        return postMapper.toPostResponse(post, relatedPost);
    }

    @Override
    public PostSummaryAIResponse viewSummary(String pid) {
        Post post = postRepository.findFirstByIdAndIsDeletedFalse(pid);
        if (post == null) {
            throw new BadRequestException("Post Not Found");
        }
        if (post.getSummaryAi() != null && !post.getSummaryAi().isEmpty())
            return PostSummaryAIResponse.builder().summary(post.getSummaryAi()).build();
        String summary = pythonService.summaryContent(post.getContent());
        post.setSummaryAi(summary);
        postRepository.save(post);
        return PostSummaryAIResponse.builder().summary(summary).build();
    }

    @Override
    public Map<String, Long> postStas() {
        Map<String, Long> res = new HashMap<>();

        res.put("total_post", postRepository.countPostByIsDeletedFalse());
        res.put("sensitive_post", postRepository.countPostByHasSensitiveContentIsTrueAndIsDeletedFalse());
        return res;
    }

    public List<Map<String, Object>> getPostCountByMonthInLastSixMonths() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(5).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<Object[]> results = postRepository.countPostsByMonthInLastSixMonths(startDate);

        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Integer year = ((Number) result[1]).intValue();
            Long count = ((Number) result[2]).longValue();
            String key = year + "-" + String.format("%02d", month);
            countMap.put(key, count);
        }

        List<Map<String, Object>> formattedResults = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime date = now.minusMonths(i);
            int month = date.getMonthValue();
            int year = date.getYear();
            String key = year + "-" + String.format("%02d", month);
            Long count = countMap.getOrDefault(key, 0L);

            Map<String, Object> entry = new HashMap<>();
            entry.put("month", month);
            entry.put("year", year);
            entry.put("count", count);
            formattedResults.add(entry);
        }

        return formattedResults;
    }

    public List<CategoryPostCountDTO> getCategoryPostStas() {
        return categoryBlogRepository.countPostsByCategory();
    }

    public PostResponse deletePost(String pid, boolean isAdmin, String uid) {
        Post p = postRepository.findFirstByIdAndIsDeletedFalse(pid);
        if (p == null) throw new BadRequestException("Post not found");
        if (p.isDeleted()) {
            throw new BadRequestException("Post already deleted");
        }
        if (isAdmin || uid.equals(p.getUid())) {
            p.setDeleted(true);
            postRepository.save(p);
            return postMapper.toPostResponse(p, List.of());
        }
        throw new BadRequestException("Not access");
    }

    public Page<PostSummaryResponse> getByUid(int page, int size, String uid) {
        Page<Post> posts = postRepository.findByUidAndIsDeletedFalse(uid, PageRequest.of(page, size));

        List<String> uids = posts.stream().map(Post::getUid).distinct().toList();

        List<SimpInfoUserResponse> users = userService.fetchUserByIdIn(new ArrayList<>(uids));

        Map<String, SimpInfoUserResponse> userMap = users.stream().collect(Collectors.toMap(SimpInfoUserResponse::getId, Function.identity()));
        List<PostSummaryResponse> responses = posts.stream()
                .map(post -> postMapper.toPostSummaryResponse(post, userMap.get(post.getUid())))
                .toList();

        return new PageImpl<>(responses, PageRequest.of(page, size), posts.getTotalElements());
    }
}