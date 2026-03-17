package com.market.service.impl;

import com.market.entity.LotteryPrize;
import com.market.entity.LotteryRecord;
import com.market.entity.User;
import com.market.repository.LotteryPrizeRepository;
import com.market.repository.LotteryRecordRepository;
import com.market.repository.UserRepository;
import com.market.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽奖服务实现类
 */
@Service
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    private LotteryPrizeRepository prizeRepository;

    @Autowired
    private LotteryRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    // 每抽 10 次必出保底奖品
    private static final int GUARANTEED_INTERVAL = 10;
    
    // 保底奖品名称
    private static final List<String> GUARANTEED_PRIZES = Arrays.asList("抽纸（一袋）", "洗衣液（一瓶）");

    @Override
    @Transactional
    public LotteryResult draw(User user) {
        LotteryResult result = new LotteryResult();

        // 检查积分是否足够
        if (user.getCredit() < 100) {
            result.setSuccess(false);
            result.setMessage("积分不足，需要 100 积分");
            result.setRemainingCredit(user.getCredit());
            return result;
        }

        // 获取所有可用奖品
        List<LotteryPrize> prizes = prizeRepository.findByAvailableTrue();
        if (prizes.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("暂无可用奖品");
            result.setRemainingCredit(user.getCredit());
            return result;
        }

        // 计算总权重
        int totalWeight = prizes.stream().mapToInt(LotteryPrize::getWeight).sum();
        
        // 获取用户抽奖次数
        Long totalDraws = recordRepository.countByUserId(user.getId());
        Long guaranteedDraws = recordRepository.countGuaranteedPrizes(user.getId());
        
        // 计算当前周期内的抽奖次数
        long currentCycleDraws = totalDraws - (guaranteedDraws * GUARANTEED_INTERVAL);
        
        LotteryPrize selectedPrize;
        
        // 如果当前周期已抽满 10 次，必出保底奖品
        if (currentCycleDraws >= GUARANTEED_INTERVAL) {
            List<LotteryPrize> guaranteedPrizes = prizes.stream()
                .filter(p -> GUARANTEED_PRIZES.contains(p.getName()))
                .collect(Collectors.toList());
            
            if (!guaranteedPrizes.isEmpty()) {
                selectedPrize = guaranteedPrizes.get(new Random().nextInt(guaranteedPrizes.size()));
            } else {
                selectedPrize = prizes.get(new Random().nextInt(prizes.size()));
            }
        } else {
            // 按权重随机选择奖品
            int random = new Random().nextInt(totalWeight);
            int currentWeight = 0;
            selectedPrize = prizes.get(0);
            
            for (LotteryPrize prize : prizes) {
                currentWeight += prize.getWeight();
                if (random < currentWeight) {
                    selectedPrize = prize;
                    break;
                }
            }
        }

        // 扣除积分
        user.setCredit(user.getCredit() - 100);
        
        // 如果是积分奖品，增加积分
        if (selectedPrize.getType() == 1) {
            int prizeCredit = parseCredit(selectedPrize.getName());
            user.setCredit(user.getCredit() + prizeCredit);
        }
        
        userRepository.save(user);

        // 保存抽奖记录
        LotteryRecord record = new LotteryRecord();
        record.setUserId(user.getId());
        record.setPrizeId(selectedPrize.getId());
        record.setPrizeName(selectedPrize.getName());
        record.setPrizeType(selectedPrize.getType());
        record.setCost(100);
        recordRepository.save(record);

        result.setSuccess(true);
        result.setMessage("抽奖成功！");
        result.setPrizeId(selectedPrize.getId());
        result.setPrizeName(selectedPrize.getName());
        result.setPrizeType(selectedPrize.getType());
        result.setCost(100);
        result.setRemainingCredit(user.getCredit());

        return result;
    }

    @Override
    public List<LotteryRecordDto> getRecords(Long userId) {
        List<LotteryRecord> records = recordRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return records.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<LotteryPrizeDto> getPrizes() {
        List<LotteryPrize> prizes = prizeRepository.findByAvailableTrue();
        return prizes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private int parseCredit(String name) {
        if (name.contains("50")) return 50;
        if (name.contains("75")) return 75;
        if (name.contains("100")) return 100;
        return 0;
    }

    private LotteryRecordDto convertToDto(LotteryRecord record) {
        LotteryRecordDto dto = new LotteryRecordDto();
        dto.setId(record.getId());
        dto.setPrizeName(record.getPrizeName());
        dto.setPrizeType(record.getPrizeType());
        dto.setCost(record.getCost());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }

    private LotteryPrizeDto convertToDto(LotteryPrize prize) {
        LotteryPrizeDto dto = new LotteryPrizeDto();
        dto.setId(prize.getId());
        dto.setName(prize.getName());
        dto.setDescription(prize.getDescription());
        dto.setType(prize.getType());
        dto.setImage(prize.getImage());
        return dto;
    }
}
