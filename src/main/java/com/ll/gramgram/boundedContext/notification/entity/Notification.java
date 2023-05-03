package com.ll.gramgram.boundedContext.notification.entity;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Notification extends BaseEntity {
    private LocalDateTime readDate;

    @ManyToOne
    @ToString.Exclude
    private InstaMember toInstaMember; // 호감을 받는 사람

    @ManyToOne
    @ToString.Exclude
    private InstaMember fromInstaMember; // 호감을 보내는 사람

    private String typeCode; // 호감표시일시 Like, 사유변경일시 ModifyAttractiveType
    private String oldGender; // 해당사항 없을 시 null
    private int oldAttributeTypeCode; // 해당사항 없을 시 0
    private String newGender; // 해당사항 없을 시 null
    private int newAttractiveTypeCode; // 해당사항 없을 시 0

}
