package com.market.common.logging;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;

/**
 * 自定义的整数转换器，将索引从 1 开始而不是 0
 * 用于日志文件滚动时的序号生成
 */
public class OneBasedIncrementingConverter extends IntegerTokenConverter {

    @Override
    public String convert(Object event) {
        String result = super.convert(event);
        if (result != null) {
            try {
                int index = Integer.parseInt(result);
                // 将 0-based 索引转换为 1-based 索引
                return String.valueOf(index + 1);
            } catch (NumberFormatException e) {
                // 如果转换失败，返回原始值
                return result;
            }
        }
        return result;
    }
}
