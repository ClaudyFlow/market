package com.market.service;

import com.market.dto.UserAddressRequest;
import com.market.dto.UserAddressResponse;
import com.market.entity.UserAddress;
import com.market.repository.UserAddressRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户地址服务实现类
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Override
    public List<UserAddressResponse> getUserAddresses(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserAddressResponse> getUserAddressesPage(Long userId, Pageable pageable) {
        return userAddressRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public UserAddressResponse getDefaultAddress(Long userId) {
        UserAddress address = userAddressRepository.findByUserIdAndIsDefaultTrue(userId);
        return address != null ? convertToResponse(address) : null;
    }

    @Override
    public UserAddressResponse getAddressDetail(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));
        
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该地址");
        }
        
        return convertToResponse(address);
    }

    @Override
    @Transactional
    public UserAddressResponse createAddress(Long userId, UserAddressRequest request) {
        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault()) {
            cancelAllDefaultAddresses(userId);
        }

        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(request.getIsDefault());
        address.setAddressTag(request.getAddressTag());

        UserAddress savedAddress = userAddressRepository.save(address);
        return convertToResponse(savedAddress);
    }

    @Override
    @Transactional
    public UserAddressResponse updateAddress(Long userId, Long addressId, UserAddressRequest request) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该地址");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (request.getIsDefault() && !address.getIsDefault()) {
            cancelAllDefaultAddresses(userId);
        }

        BeanUtils.copyProperties(request, address, "id", "userId", "createdAt");
        address.setUpdatedAt(java.time.LocalDateTime.now());

        UserAddress updatedAddress = userAddressRepository.save(address);
        return convertToResponse(updatedAddress);
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该地址");
        }

        // 取消所有默认地址
        cancelAllDefaultAddresses(userId);

        // 设置当前地址为默认
        address.setIsDefault(true);
        userAddressRepository.save(address);

        return true;
    }

    @Override
    @Transactional
    public boolean deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该地址");
        }

        userAddressRepository.delete(address);
        return true;
    }

    @Override
    public long getAddressCount(Long userId) {
        return userAddressRepository.countByUserId(userId);
    }

    /**
     * 取消用户的所有默认地址
     */
    private void cancelAllDefaultAddresses(Long userId) {
        List<UserAddress> defaultAddresses = userAddressRepository.findByUserId(userId)
                .stream()
                .filter(UserAddress::getIsDefault)
                .collect(Collectors.toList());

        for (UserAddress address : defaultAddresses) {
            address.setIsDefault(false);
            userAddressRepository.save(address);
        }
    }

    /**
     * 将 UserAddress 转换为 UserAddressResponse
     */
    private UserAddressResponse convertToResponse(UserAddress address) {
        UserAddressResponse response = new UserAddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUserId());
        response.setReceiverName(address.getReceiverName());
        response.setReceiverPhone(address.getReceiverPhone());
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetailAddress(address.getDetailAddress());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault());
        response.setAddressTag(address.getAddressTag());
        response.setFullAddress(address.getFullAddress());
        response.setCreatedAt(address.getCreatedAt());
        return response;
    }
}
