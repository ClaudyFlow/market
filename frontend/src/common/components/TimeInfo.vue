<template>
  <div class="时间显示容器">
    <div v-for="项 in 时间列表" :key="项.类型" :class="['时间行', `时间-${项.类型}`]">
      <span class="时间标签">{{ 项.标签 }}</span>
      <span class="时间数值">{{ 项.值 }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";

// --- 常量区 (写死/缓存，绝不重复计算) ---
const 周几数组 = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
const 本地时区名 = Intl.DateTimeFormat().resolvedOptions().timeZone;

// 配置对象：定义每种类型的属性
// 包含：标签、是否UTC、日期分隔符逻辑、后缀
const 时间配置 = {
  standard: { 标签: "标准", 是UTC: true, 后缀: " UTC" },
  local: { 标签: "本地", 是UTC: false, 后缀: ` ${本地时区名}` },
} as const;

/**
 * 统一格式化函数
 * @param 日期对象 Date 对象
 * @param 类型 'standard' | 'local'
 */
function 格式化时间(日期对象: Date, 类型: keyof typeof 时间配置): string {
  const 配置项 = 时间配置[类型];
  const 前缀 = 配置项.是UTC ? "UTC" : ""; // 方法前缀

  // 动态获取时间组件：利用模板字符串动态调用 getUTCxxx 或 getxxx
  const 年 = 日期对象[`get${前缀}FullYear`]();
  const 月 = String(日期对象[`get${前缀}Month`]() + 1).padStart(2, "0");
  const 日 = String(日期对象[`get${前缀}Date`]()).padStart(2, "0");
  const 时 = String(日期对象[`get${前缀}Hours`]()).padStart(2, "0");
  const 分 = String(日期对象[`get${前缀}Minutes`]()).padStart(2, "0");
  const 秒 = String(日期对象[`get${前缀}Seconds`]()).padStart(2, "0");
  const 周几 = 周几数组[日期对象[`get${前缀}Day`]()];

  // 计算时差 (仅本地时间需要计算，标准时间固定 +00:00)
  let 时差串 = "+00:00";
  if (!配置项.是UTC) {
    const 偏移分 = -日期对象.getTimezoneOffset();
    const 符号 = 偏移分 >= 0 ? "+" : "-";
    const 绝对值 = Math.abs(偏移分);
    const 差时 = String(Math.floor(绝对值 / 60)).padStart(2, "0");
    const 差分 = String(绝对值 % 60).padStart(2, "0");
    时差串 = `${符号}${差时}:${差分}`;
  }

  // 构建日期部分字符串 (标准用年月日，本地用横杠)
  const 日期串 = 配置项.是UTC ? `${年}年${月}月${日}日` : `${年}-${月}-${日}`;

  // 最终拼接
  return `UTC${时差串} ${日期串} ${周几} ${时}:${分}:${秒}${配置项.后缀}`;
}

// --- 响应式数据 ---
const 当前时间 = ref(new Date());

const 时间列表 = computed(() => [
  {
    类型: "standard",
    标签: 时间配置.standard.标签,
    值: 格式化时间(当前时间.value, "standard"),
  },
  {
    类型: "local",
    标签: 时间配置.local.标签,
    值: 格式化时间(当前时间.value, "local"),
  },
]);

// --- 定时器 (RAF) ---
let 动画帧Id: number;
let 上次时间戳 = 0;

const 刷新时间 = (时间戳: number) => {
  if (时间戳 - 上次时间戳 >= 1000) {
    当前时间.value = new Date();
    上次时间戳 = 时间戳;
  }
  动画帧Id = requestAnimationFrame(刷新时间);
};

onMounted(() => {
  动画帧Id = requestAnimationFrame(刷新时间);
});

onUnmounted(() => {
  cancelAnimationFrame(动画帧Id);
});
</script>

<style scoped>
.时间显示容器 {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-variant-numeric: tabular-nums;
  padding: 10px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
}

.时间行 {
  display: flex;
  align-items: center;
  gap: 12px;
}

.时间标签 {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  font-weight: 600;
  min-width: 40px;
  text-align: right;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.时间数值 {
  font-family: "Courier New", monospace;
  font-size: 14px;
  font-weight: 500;
  font-feature-settings: "tnum";
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
}

.时间-standard .时间数值 {
  color: #00d4ff;
}

.时间-local .时间数值 {
  color: #00ff88;
}
</style>
