package com.huce.webblog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String cname;
	private String cdesc;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<CategoryBlog> categoryBlogs;
}
