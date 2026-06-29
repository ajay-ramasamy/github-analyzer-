package com.github.analyzer.repository;

import com.github.analyzer.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    List<PullRequest> findByRepositoryId(Long repositoryId);

    List<PullRequest> findByRepositoryIdAndStatus(Long repositoryId, String status);

    List<PullRequest> findByRepositoryIdAndMerged(Long repositoryId, Boolean merged);

    @Query("SELECT pr FROM PullRequest pr WHERE pr.repository.id = :repoId AND " +
           "(:title IS NULL OR LOWER(pr.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:status IS NULL OR pr.status = :status) AND " +
           "(:merged IS NULL OR pr.merged = :merged)")
    List<PullRequest> search(@Param("repoId") Long repoId,
                             @Param("title") String title,
                             @Param("status") String status,
                             @Param("merged") Boolean merged);

    @Query("SELECT COUNT(pr) FROM PullRequest pr JOIN pr.repository r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
