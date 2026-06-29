package com.github.analyzer.repository;

import com.github.analyzer.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByRepositoryId(Long repositoryId);

    List<Issue> findByRepositoryIdAndStatus(Long repositoryId, String status);

    @Query("SELECT i FROM Issue i WHERE i.repository.id = :repoId AND " +
           "(:title IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:label IS NULL OR i.label = :label)")
    List<Issue> search(@Param("repoId") Long repoId,
                       @Param("title") String title,
                       @Param("status") String status,
                       @Param("label") String label);

    @Query("SELECT COUNT(i) FROM Issue i JOIN i.repository r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
