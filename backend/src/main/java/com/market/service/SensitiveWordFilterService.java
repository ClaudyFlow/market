package com.market.service;

import com.market.entity.SensitiveWord;
import com.market.repository.SensitiveWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 敏感词过滤服务
 * 使用 DFA 算法 (Deterministic Finite Automaton) 实现高效敏感词检测
 */
@Service
public class SensitiveWordFilterService {

    private static final Logger log = LoggerFactory.getLogger(SensitiveWordFilterService.class);

    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;

    /**
     * 敏感词字典树 (Trie Tree)
     */
    private final Map<Character, Object> sensitiveWordMap = new ConcurrentHashMap<>();

    /**
     * 缓存敏感词列表
     */
    private volatile List<SensitiveWord> cachedSensitiveWords = new ArrayList<>();

    /**
     * 服务启动时初始化敏感词库
     */
    @PostConstruct
    public void init() {
        loadSensitiveWords();
        log.info("敏感词过滤服务初始化完成，加载 {} 个敏感词", cachedSensitiveWords.size());
    }

    /**
     * 从数据库加载敏感词
     */
    public synchronized void loadSensitiveWords() {
        List<SensitiveWord> words = sensitiveWordRepository.findByEnabledTrue();
        cachedSensitiveWords = words;
        buildDfaTree(words);
        log.info("重新加载敏感词库，共 {} 个词", words.size());
    }

    /**
     * 构建 DFA 字典树
     */
    @SuppressWarnings("unchecked")
    private void buildDfaTree(List<SensitiveWord> words) {
        sensitiveWordMap.clear();

        for (SensitiveWord word : words) {
            String text = word.getWord();
            Map<Character, Object> currentMap = sensitiveWordMap;

            for (int i = 0; i < text.length(); i++) {
                char key = text.charAt(i);
                Map<Character, Object> nextMap = (Map<Character, Object>) currentMap.get(key);

                if (nextMap != null) {
                    currentMap = nextMap;
                } else {
                    Map<Character, Object> newMap = new ConcurrentHashMap<>();
                    currentMap.put(key, newMap);
                    currentMap = newMap;
                }

                // 最后一个字符标记为结束
                if (i == text.length() - 1) {
                    currentMap.put('i', "sEnd");
                    currentMap.put('w', word);
                }
            }
        }
    }

    /**
     * 检测文本是否包含敏感词
     * @param text 待检测文本
     * @return 检测结果 (包含敏感词信息)
     */
    @SuppressWarnings("unchecked")
    public DetectionResult detectSensitiveWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new DetectionResult(false, Collections.emptyList(), text);
        }

        List<FoundSensitiveWord> foundWords = new ArrayList<>();
        StringBuilder filteredText = new StringBuilder();
        int i = 0;

        while (i < text.length()) {
            Character ch = text.charAt(i);
            Map<Character, Object> currentMap = sensitiveWordMap;
            int start = i;
            boolean found = false;

            while (currentMap != null && i < text.length()) {
                ch = text.charAt(i);
                currentMap = (Map<Character, Object>) currentMap.get(ch);

                if (currentMap == null) {
                    break;
                }

                // 检查是否是敏感词结尾
                if (currentMap.containsKey('i')) {
                    SensitiveWord wordInfo = (SensitiveWord) currentMap.get('w');
                    String foundWord = text.substring(start, i + 1);

                    foundWords.add(new FoundSensitiveWord(
                            foundWord,
                            wordInfo.getType(),
                            wordInfo.getLevel(),
                            start,
                            i + 1
                    ));

                    // 更新匹配次数
                    updateMatchCount(foundWord);

                    found = true;
                    break;
                }

                i++;
            }

            if (found) {
                SensitiveWord wordInfo = (SensitiveWord) currentMap.get('w');
                filteredText.append(wordInfo != null ? wordInfo.getReplacement() : "***");
            } else {
                filteredText.append(text.charAt(start));
                i = start + 1;
            }
        }

        boolean hasSensitive = !foundWords.isEmpty();
        String resultText = hasSensitive ? filteredText.toString() : text;

        return new DetectionResult(hasSensitive, foundWords, resultText);
    }

    /**
     * 异步更新敏感词匹配次数
     */
    @Async
    private void updateMatchCount(String word) {
        try {
            sensitiveWordRepository.incrementMatchCount(word);
        } catch (Exception e) {
            log.error("更新敏感词匹配次数失败: {}", e.getMessage());
        }
    }

    /**
     * 过滤文本中的敏感词 (返回替换后的文本)
     */
    public String filterText(String text) {
        DetectionResult result = detectSensitiveWords(text);
        return result.getFilteredText();
    }

    /**
     * 检查文本是否包含高危敏感词
     */
    public boolean containsHighRiskWords(String text) {
        DetectionResult result = detectSensitiveWords(text);
        return result.getFoundWords().stream()
                .anyMatch(w -> "HIGH".equals(w.getLevel()));
    }

    /**
     * 添加敏感词
     */
    public SensitiveWord addSensitiveWord(String word, String type, String level, String replacement) {
        if (sensitiveWordRepository.existsByWordAndEnabledTrue(word)) {
            throw new RuntimeException("敏感词已存在: " + word);
        }

        SensitiveWord sensitiveWord = new SensitiveWord(word, type, level);
        sensitiveWord.setReplacement(replacement != null ? replacement : "***");
        SensitiveWord saved = sensitiveWordRepository.save(sensitiveWord);

        // 重新加载字典
        loadSensitiveWords();

        return saved;
    }

    /**
     * 删除敏感词
     */
    public void deleteSensitiveWord(Long id) {
        Optional<SensitiveWord> wordOpt = sensitiveWordRepository.findById(id);
        if (wordOpt.isPresent()) {
            SensitiveWord word = wordOpt.get();
            word.setEnabled(false);
            sensitiveWordRepository.save(word);
            loadSensitiveWords();
        }
    }

    /**
     * 批量导入敏感词
     */
    public int batchImportSensitiveWords(List<SensitiveWord> words) {
        int count = 0;
        for (SensitiveWord word : words) {
            if (!sensitiveWordRepository.existsByWordAndEnabledTrue(word.getWord())) {
                sensitiveWordRepository.save(word);
                count++;
            }
        }
        loadSensitiveWords();
        return count;
    }

    /**
     * 获取敏感词统计信息
     */
    public Map<String, Object> getSensitiveWordStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalWords", cachedSensitiveWords.size());

        // 按类型统计
        Map<String, Long> typeStats = cachedSensitiveWords.stream()
                .collect(Collectors.groupingBy(SensitiveWord::getType, Collectors.counting()));
        stats.put("typeDistribution", typeStats);

        // 按级别统计
        Map<String, Long> levelStats = cachedSensitiveWords.stream()
                .collect(Collectors.groupingBy(SensitiveWord::getLevel, Collectors.counting()));
        stats.put("levelDistribution", levelStats);

        // Top 10 高频敏感词
        List<Map<String, Object>> topWords = cachedSensitiveWords.stream()
                .sorted(Comparator.comparingInt(SensitiveWord::getMatchCount).reversed())
                .limit(10)
                .map(w -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("word", w.getWord());
                    map.put("type", w.getType());
                    map.put("level", w.getLevel());
                    map.put("matchCount", w.getMatchCount());
                    return map;
                })
                .collect(Collectors.toList());
        stats.put("topWords", topWords);

        return stats;
    }

    /**
     * 获取 Repository (供 Controller 使用)
     */
    public SensitiveWordRepository getSensitiveWordRepository() {
        return sensitiveWordRepository;
    }

    /**
     * 检测结果
     */
    public static class DetectionResult {
        private final boolean hasSensitive;
        private final List<FoundSensitiveWord> foundWords;
        private final String filteredText;

        public DetectionResult(boolean hasSensitive, List<FoundSensitiveWord> foundWords, String filteredText) {
            this.hasSensitive = hasSensitive;
            this.foundWords = foundWords;
            this.filteredText = filteredText;
        }

        public boolean hasSensitive() { return hasSensitive; }
        public List<FoundSensitiveWord> getFoundWords() { return foundWords; }
        public String getFilteredText() { return filteredText; }
    }

    /**
     * 发现的敏感词
     */
    public static class FoundSensitiveWord {
        private final String word;
        private final String type;
        private final String level;
        private final int start;
        private final int end;

        public FoundSensitiveWord(String word, String type, String level, int start, int end) {
            this.word = word;
            this.type = type;
            this.level = level;
            this.start = start;
            this.end = end;
        }

        public String getWord() { return word; }
        public String getType() { return type; }
        public String getLevel() { return level; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
    }
}
