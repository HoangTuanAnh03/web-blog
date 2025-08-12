package com.huce.webblog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "category_blog")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CategoryBlog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "cid")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "pid")
	private Post post;
}
