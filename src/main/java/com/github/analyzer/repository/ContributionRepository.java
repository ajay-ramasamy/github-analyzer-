package com.github.analyzer.repository;

import com.github.analyzer.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findByRepositoryId(Long repositoryId);

    @Query("SELECT MONTH(c.contributionDate), COUNT(c) FROM Contribution c " +
           "JOIN c.repository r WHERE r.user.id = :userId AND YEAR(c.contributionDate) = :year " +
           "GROUP BY MONTH(c.contributionDate) ORDER BY MONTH(c.contributionDate)")
    List<Object[]> countPerMonthByYear(@Param("userId") Long userId, @Param("year") int year);

    @Query("SELECT YEAR(c.contributionDate), COUNT(c) FROM Contribution c " +
           "JOIN c.repository r WHERE r.user.id = :userId " +
           "GROUP BY YEAR(c.contributionDate) ORDER BY YEAR(c.contributionDate)")
    List<Object[]> countPerYear(@Param("userId") Long userId);

    @Query("SELECT c.contributionDate, COUNT(c) FROM Contribution c " +
           "JOIN c.repository r WHERE r.user.id = :userId AND YEAR(c.contributionDate) = :year " +
           "GROUP BY c.contributionDate ORDER BY c.contributionDate")
    List<Object[]> dailyActivityByYear(@Param("userId") Long userId, @Param("year") int year);

    @Query("SELECT SUM(c.points) FROM Contribution c JOIN c.repository r WHERE r.user.id = :userId")
    Long sumPointsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Contribution c JOIN c.repository r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT c.type, COUNT(c) FROM Contribution c JOIN c.repository r WHERE r.user.id = :userId GROUP BY c.type")
    List<Object[]> countByType(@Param("userId") Long userId);

    @Query("SELECT c.contributor, SUM(c.points) FROM Contribution c JOIN c.repository r WHERE r.user.id = :userId GROUP BY c.contributor ORDER BY SUM(c.points) DESC")
    List<Object[]> topContributors(@Param("userId") Long userId);

    @Query("SELECT r.name, COUNT(c) FROM Contribution c JOIN c.repository r WHERE r.user.id = :userId GROUP BY r.name ORDER BY COUNT(c) DESC")
    List<Object[]> countByRepository(@Param("userId") Long userId);
}
