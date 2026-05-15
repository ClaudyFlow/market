package com.market.service;

import com.market.dto.UserAddressRequest;
import com.market.dto.UserAddressResponse;
import com.market.entity.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户地址服务接口
 *
 * @author Market Team
 * @since 1.0.0
 */
public interface UserAddressService {

    /**
     * 获取用户地址列表
     *
     * @param userId 用户 ID
     * @return 地址列表
     */
    List<UserAddressResponse> getUserAddresses(Long userId);

    /**
     * 获取用户地址列表（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 地址列表分页
     */
    Page<UserAddressResponse> getUserAddressesPage(Long userId, Pageable pageable);

    /**
     * 获取用户默认地址
     *
     * @param userId 用户 ID
     * @return 默认地址
     */
    UserAddressResponse getDefaultAddress(Long userId);

    /**
     * 获取地址详情
     *
     * @param userId 用户 ID
     * @param addressId 地址 ID
     * @return 地址详情
     */
    UserAddressResponse getAddressDetail(Long userId, Long addressId);

    /**
     * 创建地址
     *
     * @param userId 用户 ID
     * @param request 地址请求
     * @return 创建的地址
     */
    UserAddressResponse createAddress(Long userId, UserAddressRequest request);

    /**
     * 更新地址
     *
     * @param userId 用户 ID
     * @param addressId 地址 ID
     * @param request 地址请求
     * @return 更新后的地址
     */
    UserAddressResponse updateAddress(Long userId, Long addressId, UserAddressRequest request);

    /**
     * 设置默认地址
     *
     * @param userId 用户 ID
     * @param addressId 地址 ID
     * @return 是否成功
     */
    boolean setDefaultAddress(Long userId, Long addressId);

    /**
     * 删除地址
     *
     * @param userId 用户 ID
     * @param addressId 地址 ID
     * @return 是否成功
     */
    boolean deleteAddress(Long userId, Long addressId);

    /**
     * 获取地址数量
     *
     * @param userId 用户 ID
     * @return 地址数量
     */
    long getAddressCount(Long userId);
}
