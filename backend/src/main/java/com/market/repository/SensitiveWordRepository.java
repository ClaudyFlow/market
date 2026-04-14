package com.market.repository;

import com.market.entity.SensitiveWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 敏感词 Repository
 */
@Repository
public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {

    /**
     * 查找所有启用的敏感词
     */
    List<SensitiveWord> findByEnabledTrue();

    /**
     * 根据类型查找敏感词
     */
    List<SensitiveWord> findByTypeAndEnabledTrue(String type);

    /**
     * 分页查询敏感词
     */
    Page<SensitiveWord> findAll(Pageable pageable);

    /**
     * 根据类型分页查询
     */
    Page<SensitiveWord> findByType(String type, Pageable pageable);

    /**
     * 检查敏感词是否存在
     */
    boolean existsByWordAndEnabledTrue(String word);

    /**
     * 查找敏感词
     */
    Optional<SensitiveWord> findByWord(String word);

    /**
     * 获取匹配次数最多的敏感词（Top N）
     */
    @Query("SELECT s FROM SensitiveWord s WHERE s.enabled = true ORDER BY s.matchCount DESC")
    List<SensitiveWord> findTopMatchedWords(Pageable pageable);

    /**
     * 统计各类型敏感词数量
     */
    @Query("SELECT s.type, COUNT(s) FROM SensitiveWord s WHERE s.enabled = true GROUP BY s.type")
    List<Object[]> countByType();

    /**
     * 批量更新匹配次数
     */
    @Query("UPDATE SensitiveWord s SET s.matchCount = s.matchCount + 1 WHERE s.word = :word AND s.enabled = true")
    void incrementMatchCount(@Param("word") String word);
}
