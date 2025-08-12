package com.huce.webblog.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String content;

	@Column(name = "uid")
	private String uid;

	@ManyToOne
	@JoinColumn(name = "pid")
	@JsonBackReference
	private Post post;

	@Column(name = "left_value")
	private int leftValue;

	@Column(name = "right_value")
	private int rightValue;
}
