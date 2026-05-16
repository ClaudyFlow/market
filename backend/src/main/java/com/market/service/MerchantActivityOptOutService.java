package com.market.service;

import com.market.entity.MerchantActivityOptOut;
import com.market.entity.PlatformActivity;
import com.market.repository.MerchantActivityOptOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class MerchantActivityOptOutService {

    @Autowired
    private MerchantActivityOptOutRepository optOutRepository;

    public Optional<MerchantActivityOptOut> getByMerchantAndActivity(Long merchantId, Long activityId) {
        return optOutRepository.findByMerchantIdAndActivityId(merchantId, activityId);
    }

    public List<MerchantActivityOptOut> getByMerchant(Long merchantId) {
        return optOutRepository.findByMerchantId(merchantId);
    }

    public List<MerchantActivityOptOut> getByActivity(Long activityId) {
        return optOutRepository.findByActivityId(activityId);
    }

    /**
     * 检查商家是否退出了某个活动
     */
    public boolean hasOptedOut(Long merchantId, Long activityId) {
        return optOutRepository.findByMerchantIdAndActivityId(merchantId, activityId)
                .map(MerchantActivityOptOut::getOptedOut)
                .orElse(false);
    }

    /**
     * 计算商品在活动中的最终价格
     * @param originalPrice 商品原价
     * @param merchantId 商家ID
     * @param activity 平台活动
     * @return 折扣后的价格（如果商家退出活动则返回原价的BigDecimal.ZERO标记，但实际返回null表示不打折）
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Long merchantId, PlatformActivity activity) {
        if (activity == null) return originalPrice;

        Optional<MerchantActivityOptOut> optOut = optOutRepository.findByMerchantIdAndActivityId(merchantId, activity.getId());

        if (optOut.isPresent()) {
            MerchantActivityOptOut setting = optOut.get();
            if (Boolean.TRUE.equals(setting.getOptedOut())) {
                return null; // 商家退出活动，不打折
            }
            // 商家有自定义设置
            if ("MONEY".equals(setting.getDiscountType()) && setting.getCustomDiscountAmount() != null) {
                BigDecimal discounted = originalPrice.subtract(setting.getCustomDiscountAmount());
                return discounted.compareTo(BigDecimal.ZERO) > 0 ? discounted : BigDecimal.ZERO;
            } else if ("DISCOUNT".equals(setting.getDiscountType()) && setting.getCustomDiscountRate() != null) {
                return originalPrice.multiply(setting.getCustomDiscountRate()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        // 没有自定义，使用平台活动的折扣率
        if (activity.getDiscountRate() != null) {
            return originalPrice.multiply(activity.getDiscountRate()).setScale(2, RoundingMode.HALF_UP);
        }

        return null; // 无折扣
    }

    @Transactional
    public MerchantActivityOptOut saveOrUpdate(MerchantActivityOptOut optOut) {
        return optOutRepository.save(optOut);
    }

    @Transactional
    public void deleteByMerchantAndActivity(Long merchantId, Long activityId) {
        optOutRepository.findByMerchantIdAndActivityId(merchantId, activityId)
                .ifPresent(optOutRepository::delete);
    }
}
