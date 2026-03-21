package com.project.smashlink.url.repository;

import com.project.smashlink.url.entity.Url;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    Page<Url> findByUserId(Long userId, Pageable pageable);
    boolean existsByShortCode(String shortCode);
}
