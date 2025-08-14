package com.huce.webblog.repository;

import com.huce.webblog.entity.VerificationCode;
import com.huce.webblog.util.constant.VerifyTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface VerifyCodeRepository extends JpaRepository<VerificationCode, String> {
    Optional<VerificationCode> findFirstByCodeAndType(String code, VerifyTypeEnum type);

    Optional<VerificationCode> findFirstByEmail(String email);
}
