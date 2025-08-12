package com.huce.webblog.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Notification extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uid")
	private String uid;

	@ManyToOne
	@JoinColumn(name = "pid")
	@JsonBackReference
	private Post post;

	private String message;

	private boolean isRead;
}
