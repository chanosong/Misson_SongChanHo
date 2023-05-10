package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long> {
    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);

    // from~ 과 to~ 의 관계 반환
    Optional<LikeablePerson> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaMemberId, Long toInstaMemberId);

    // fromInstaMemberId 기준으로 보낸 호감표시 개수 반환
    Long countByFromInstaMemberId(Long fromInstaMemberId);

    // fromInstaMember의 성별, toInstaMemberId 기준으로 호감 리스트
    @Query(value = "SELECT LP FROM LikeablePerson LP JOIN FETCH InstaMember M ON LP.fromInstaMember = M " +
            "WHERE LP.toInstaMember.id = :toInstaMemberId AND LP.fromInstaMember.gender = :fromInstaMemberGender")
    List<LikeablePerson> findByToInstaMemberIdAndFromInstaMemberGender(@Param("toInstaMemberId")Long toInstaMemberId, @Param("fromInstaMemberGender") String fromInstaMemberGender);
}
