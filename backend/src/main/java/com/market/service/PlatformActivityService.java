package com.market.service;

import com.market.entity.PlatformActivity;
import com.market.repository.PlatformActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlatformActivityService {

    @Autowired
    private PlatformActivityRepository platformActivityRepository;

    public List<PlatformActivity> getAllActivities() {
        return platformActivityRepository.findAll();
    }

    public List<PlatformActivity> getActiveActivities() {
        return platformActivityRepository.findActiveActivities();
    }

    public List<PlatformActivity> getActivitiesByStatus(String status) {
        return platformActivityRepository.findByStatusOrderBySortOrderAsc(status);
    }

    public PlatformActivity getById(Long id) {
        return platformActivityRepository.findById(id).orElse(null);
    }

    @Transactional
    public PlatformActivity create(PlatformActivity activity) {
        return platformActivityRepository.save(activity);
    }

    @Transactional
    public PlatformActivity update(Long id, PlatformActivity updates) {
        PlatformActivity existing = platformActivityRepository.findById(id).orElse(null);
        if (existing == null) return null;

        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getType() != null) existing.setType(updates.getType());
        if (updates.getImage() != null) existing.setImage(updates.getImage());
        if (updates.getDiscountRate() != null) existing.setDiscountRate(updates.getDiscountRate());
        if (updates.getStartTime() != null) existing.setStartTime(updates.getStartTime());
        if (updates.getEndTime() != null) existing.setEndTime(updates.getEndTime());
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getMaxPerUser() != null) existing.setMaxPerUser(updates.getMaxPerUser());
        if (updates.getTotalQuota() != null) existing.setTotalQuota(updates.getTotalQuota());
        if (updates.getSortOrder() != null) existing.setSortOrder(updates.getSortOrder());

        return platformActivityRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        platformActivityRepository.deleteById(id);
    }

    /**
     * 检查活动是否正在进行
     */
    public boolean isActivityActive(PlatformActivity activity) {
        if (activity == null) return false;
        if (!"ACTIVE".equals(activity.getStatus())) return false;
        LocalDateTime now = LocalDateTime.now();
        boolean started = activity.getStartTime() == null || !activity.getStartTime().isAfter(now);
        boolean notEnded = activity.getEndTime() == null || !activity.getEndTime().isBefore(now);
        return started && notEnded;
    }

    /**
     * 获取某商品适用的最优活动折扣
     * 商家自定义优先于平台活动
     */
    public BigDecimal getBestDiscountRate(Long merchantId, List<PlatformActivity> activeActivities) {
        BigDecimal bestRate = null; // null means no discount
        for (PlatformActivity activity : activeActivities) {
            bestRate = activity.getDiscountRate(); // platform-wide discount as baseline
        }
        return bestRate;
    }
}
