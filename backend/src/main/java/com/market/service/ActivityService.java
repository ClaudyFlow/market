package com.market.service;

import com.market.entity.Activity;
import com.market.entity.Product;
import com.market.repository.ActivityRepository;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Activity createActivity(Activity activity) {
        if (activity.getType() == null || activity.getType().isEmpty()) {
            throw new RuntimeException("活动类型不能为空");
        }
        if (activity.getStartTime() != null && activity.getEndTime() != null) {
            if (activity.getStartTime().isAfter(activity.getEndTime())) {
                throw new RuntimeException("开始时间不能晚于结束时间");
            }
        }
        if (activity.getMaxQuantity() != null && activity.getMaxQuantity() < 1) {
            throw new RuntimeException("最大数量必须大于0");
        }
        if (activity.getMaxPerUser() != null && activity.getMaxPerUser() < 1) {
            throw new RuntimeException("每人限领数量必须大于0");
        }
        if (activity.getStatus() == null) {
            activity.setStatus("DRAFT");
        }
        if (activity.getUsedQuantity() == null) {
            activity.setUsedQuantity(0);
        }
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity updateActivity(Long id, Activity activity) {
        Activity existing = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        if (activity.getName() != null) existing.setName(activity.getName());
        if (activity.getDescription() != null) existing.setDescription(activity.getDescription());
        if (activity.getType() != null) existing.setType(activity.getType());
        if (activity.getImage() != null) existing.setImage(activity.getImage());
        if (activity.getStartTime() != null) existing.setStartTime(activity.getStartTime());
        if (activity.getEndTime() != null) existing.setEndTime(activity.getEndTime());
        if (activity.getStatus() != null) existing.setStatus(activity.getStatus());
        if (activity.getDiscount() != null) existing.setDiscount(activity.getDiscount());
        if (activity.getDiscountType() != null) existing.setDiscountType(activity.getDiscountType());
        if (activity.getMaxQuantity() != null) existing.setMaxQuantity(activity.getMaxQuantity());
        if (activity.getMaxPerUser() != null) existing.setMaxPerUser(activity.getMaxPerUser());
        if (activity.getTags() != null) existing.setTags(activity.getTags());
        if (activity.getSortOrder() != null) existing.setSortOrder(activity.getSortOrder());

        return activityRepository.save(existing);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));
        activityRepository.delete(activity);
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    public Page<Activity> getAllActivities(Pageable pageable) {
        return activityRepository.findAll(pageable);
    }

    public Page<Activity> getActivitiesByStatus(String status, Pageable pageable) {
        return activityRepository.findByStatus(status, pageable);
    }

    public Page<Activity> getActivitiesByMerchant(Long merchantId, Pageable pageable) {
        return activityRepository.findByMerchantId(merchantId, pageable);
    }

    public Page<Activity> getActivitiesByType(String type, Pageable pageable) {
        return activityRepository.findByType(type, pageable);
    }

    public List<Activity> getActiveActivities() {
        LocalDateTime now = LocalDateTime.now();
        return activityRepository.findByStatusAndStartTimeBeforeAndEndTimeAfter("ACTIVE", now, now);
    }

    public List<Activity> getProductActivities(Long productId) {
        return activityRepository.findByProductId(productId);
    }

    @Transactional
    public Activity publishActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        if (activity.getStartTime() == null) {
            throw new RuntimeException("活动开始时间不能为空");
        }
        if (activity.getEndTime() == null) {
            throw new RuntimeException("活动结束时间不能为空");
        }

        activity.setStatus("ACTIVE");
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity pauseActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        activity.setStatus("PAUSED");
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity endActivity(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        activity.setStatus("ENDED");
        return activityRepository.save(activity);
    }

    @Transactional
    public void useActivity(Long activityId, int quantity) {
        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("活动不存在"));

        if (!"ACTIVE".equals(activity.getStatus())) {
            throw new RuntimeException("活动未激活");
        }

        int newUsed = activity.getUsedQuantity() + quantity;
        if (activity.getMaxQuantity() != null && newUsed > activity.getMaxQuantity()) {
            throw new RuntimeException("活动数量已用完");
        }

        activity.setUsedQuantity(newUsed);
        activityRepository.save(activity);
    }

    public boolean isProductInActivity(Long productId) {
        return activityRepository.existsByProductIdAndStatus(productId, "ACTIVE");
    }
}