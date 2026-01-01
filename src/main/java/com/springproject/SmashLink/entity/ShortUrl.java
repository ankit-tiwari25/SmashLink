package com.springproject.SmashLink.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_url")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrl extends AuditClass{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String shortKey;

    @Column(nullable = false)
    private  String originalURL;

    private Long clickCount;

    private Boolean isPrivate;

    private LocalDateTime expiresAt;
}
