package com.huce.webblog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rating")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "pid")
	private Post post;

	@Column(name = "uid")
	private String uid;

	private int vote;
}
