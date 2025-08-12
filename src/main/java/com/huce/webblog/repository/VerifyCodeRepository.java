package com.huce.webblog.repository;

import com.huce.webblog.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VerifyCodeRepository extends JpaRepository<VerificationCode, String> {
}
