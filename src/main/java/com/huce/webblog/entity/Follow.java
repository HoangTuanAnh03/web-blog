package com.huce.webblog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follow")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String followerId;

	private String followingId;
}
