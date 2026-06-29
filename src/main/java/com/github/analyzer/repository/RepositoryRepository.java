package com.github.analyzer.repository;

import com.github.analyzer.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    List<Repository> findByUserId(Long userId);

    List<Repository> findByUserIdAndLanguage(Long userId, String language);

    @Query("SELECT r FROM Repository r WHERE r.user.id = :userId AND " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:language IS NULL OR r.language = :language)")
    List<Repository> searchByUser(@Param("userId") Long userId,
                                  @Param("name") String name,
                                  @Param("language") String language);

    @Query("SELECT r.language, COUNT(c) FROM Contribution c JOIN c.repository r " +
           "WHERE r.user.id = :userId GROUP BY r.language")
    List<Object[]> countContributionsByLanguage(@Param("userId") Long userId);

    @Query("SELECT r, COUNT(c) as total FROM Repository r LEFT JOIN r.contributions c " +
           "WHERE r.user.id = :userId GROUP BY r ORDER BY total DESC")
    List<Object[]> findMostActiveRepositories(@Param("userId") Long userId);
}
