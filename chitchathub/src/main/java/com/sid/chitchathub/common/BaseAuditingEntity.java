package com.sid.chitchathub.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
//By this we can set this field (cant
public class BaseAuditingEntity {

    @CreatedDate
    @Column(name ="created_date",nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name ="last_modified_date",insertable = false)
    private LocalDateTime lastModifiedDate;


}
