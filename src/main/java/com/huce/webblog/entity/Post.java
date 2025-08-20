package com.huce.webblog.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post extends BaseEntity{
	@Id
	private String id;

	@Column(name = "uid")
	private String uid;
	private String title;

	@Column(columnDefinition = "LONGTEXT")
	private String content;

	private int viewsCount;

	private int commentsCount;

	private boolean isDeleted;

	@Column(columnDefinition = "LONGTEXT")
	private String cover;

	@Column(columnDefinition = "LONGTEXT")
	private String summaryAi;

	private boolean hasSensitiveContent;

	@Column(columnDefinition = "LONGTEXT")
	private String rawContent;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Comment> comments;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Notification> notifications;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<Rating> ratings;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<CategoryBlog> categoryBlogs;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<Hashtag> hashtags;

	public void incrementViewsCount() {
		this.viewsCount++;
	}

	public void incrementCommentsCount() {
		this.commentsCount++;
	}
	public void decrementCommentsCount() {
		this.commentsCount--;
	}

}
