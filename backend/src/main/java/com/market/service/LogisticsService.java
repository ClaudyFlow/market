package com.market.service;

import com.market.entity.LogisticsInfo;
import com.market.entity.LogisticsTrack;
import com.market.repository.LogisticsInfoRepository;
import com.market.repository.LogisticsTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 物流服务类
 */
@Service
public class LogisticsService {

    @Autowired
    private LogisticsInfoRepository logisticsInfoRepository;

    @Autowired
    private LogisticsTrackRepository logisticsTrackRepository;

    /**
     * 创建物流信息
     */
    @Transactional
    public LogisticsInfo createLogistics(Long orderId, String trackingNo, String companyCode, String companyName) {
        LogisticsInfo info = new LogisticsInfo();
        info.setOrderId(orderId);
        info.setTrackingNo(trackingNo);
        info.setCompanyCode(companyCode);
        info.setCompanyName(companyName);
        info.setStatus("PENDING");
        return logisticsInfoRepository.save(info);
    }

    /**
     * 添加物流轨迹
     */
    @Transactional
    public LogisticsTrack addTrack(String trackingNo, String description, String location, String status) {
        Optional<LogisticsInfo> infoOpt = logisticsInfoRepository.findByTrackingNo(trackingNo);
        if (infoOpt.isPresent()) {
            LogisticsInfo info = infoOpt.get();
            
            LogisticsTrack track = new LogisticsTrack();
            track.setTrackingNo(trackingNo);
            track.setTime(LocalDateTime.now());
            track.setDescription(description);
            track.setLocation(location);
            track.setStatus(status);
            
            info.addTrack(track);
            
            // 更新物流信息状态
            if ("DELIVERED".equals(status)) {
                info.setStatus("DELIVERED");
            } else if ("EXCEPTION".equals(status)) {
                info.setStatus("EXCEPTION");
            } else {
                info.setStatus("IN_TRANSIT");
            }
            
            logisticsInfoRepository.save(info);
            return logisticsTrackRepository.save(track);
        }
        throw new RuntimeException("物流信息不存在");
    }

    /**
     * 查询物流信息
     */
    public LogisticsInfo getLogisticsByOrderId(Long orderId) {
        return logisticsInfoRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("物流信息不存在"));
    }

    /**
     * 查询物流轨迹
     */
    public List<LogisticsTrack> getTrackingRecords(String trackingNo) {
        return logisticsTrackRepository.findByTrackingNoOrderByTimeDesc(trackingNo);
    }

    /**
     * 模拟物流轨迹数据（预留接口）
     */
    public List<LogisticsTrack> generateMockTrackingRecords(String trackingNo) {
        List<LogisticsTrack> records = new ArrayList<>();
        String[] locations = {"广州转运中心", "深圳分拣中心", "北京集散中心", "北京市朝阳区", "用户签收"};
        String[] descriptions = {"已收件", "已发出", "运输中", "派送中", "已签收"};
        String[] statuses = {"PICKUP", "IN_TRANSIT", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"};
        
        LocalDateTime baseTime = LocalDateTime.now().minusDays(4);
        
        for (int i = 0; i < locations.length; i++) {
            LogisticsTrack track = new LogisticsTrack();
            track.setTrackingNo(trackingNo);
            track.setTime(baseTime.plusDays(i));
            track.setLocation(locations[i]);
            track.setDescription(descriptions[i]);
            track.setStatus(statuses[i]);
            records.add(track);
        }
        
        return records;
    }

    /**
     * 获取快递公司列表（模拟数据）
     */
    public List<Map<String, String>> getExpressCompanies() {
        List<Map<String, String>> companies = new ArrayList<>();
        
        Map<String, String> sf = new HashMap<>();
        sf.put("code", "SF");
        sf.put("name", "顺丰速运");
        companies.add(sf);
        
        Map<String, String> yto = new HashMap<>();
        yto.put("code", "YTO");
        yto.put("name", "圆通速递");
        companies.add(yto);
        
        Map<String, String> zto = new HashMap<>();
        zto.put("code", "ZTO");
        zto.put("name", "中通快递");
        companies.add(zto);
        
        Map<String, String> sto = new HashMap<>();
        sto.put("code", "STO");
        sto.put("name", "申通快递");
        companies.add(sto);
        
        Map<String, String> yd = new HashMap<>();
        yd.put("code", "YD");
        yd.put("name", "韵达速递");
        companies.add(yd);
        
        Map<String, String> jt = new HashMap<>();
        jt.put("code", "JT");
        jt.put("name", "极兔速递");
        companies.add(jt);
        
        Map<String, String> ems = new HashMap<>();
        ems.put("code", "EMS");
        ems.put("name", "EMS");
        companies.add(ems);
        
        return companies;
    }
}
