<template>
  <div class="时间显示容器 time-display">
    <!-- 内容区域：标签列 + 状态列 + UTC 列 + 时差列 + 年月日周时分列 -->
    <div class="内容区域">
      <!-- 左侧标签列 -->
      <div class="标签列">
        <span class="标签项">标准<StatusDot status="success" /></span>

        <span class="标签项">本地<StatusDot status="success" /></span>
      </div>
      <div class="分隔符"></div>
      <!-- UTC 标签列 -->
      <div class="单行">
        <span>UTC</span>
      </div>

      <!-- 时差列 -->
      <div class="双行">
        <span class="标准时差">+00:00</span>
        <span class="本地时差">{{ localOffset }}</span>
      </div>
      <div class="分隔符"></div>

      <!-- 年标签 -->
      <div class="标签列">
        <template v-if="isSameYear">
          <span class="单行">{{ standardYear }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardYear }}</span>
          <span class="双行 本地">{{ localYear }}</span>
        </template>
      </div>
      <div class="单行">年</div>

      <!-- 月标签 -->
      <div class="标签列">
        <template v-if="isSameMonth">
          <span class="单行">{{ standardMonth }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardMonth }}</span>
          <span class="双行 本地">{{ localMonth }}</span>
        </template>
      </div>
      <div class="单行">月</div>

      <!-- 日标签 -->
      <div class="标签列">
        <template v-if="isSameDay">
          <span class="单行">{{ standardDay }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardDay }}</span>
          <span class="双行 本地">{{ localDay }}</span>
        </template>
      </div>
      <div class="单行">日</div>
      <div class="分隔符"></div>

      <!-- 周标签 -->
      <div class="单行">周</div>
      <div class="标签列">
        <template v-if="isSameWeekday">
          <span class="单行">{{ standardWeekday }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardWeekday }}</span>
          <span class="双行 本地">{{ localWeekday }}</span>
        </template>
      </div>
      <div class="分隔符"></div>

      <!-- 时标签 -->
      <div class="标签列">
        <template v-if="isSameHour">
          <span class="单行">{{ standardHour }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardHour }}</span>
          <span class="双行 本地">{{ localHour }}</span>
        </template>
      </div>
      <div class="单行">时</div>

      <!-- 分标签 -->
      <div class="标签列">
        <template v-if="isSameMinute">
          <span class="单行">{{ standardMinute }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardMinute }}</span>
          <span class="双行 本地">{{ localMinute }}</span>
        </template>
      </div>
      <div class="单行">分</div>

      <!-- 秒标签 -->
      <div class="标签列">
        <template v-if="isSameSecond">
          <span class="单行">{{ standardSecond }}</span>
        </template>
        <template v-else>
          <span class="双行 标准">{{ standardSecond }}</span>
          <span class="双行 本地">{{ localSecond }}</span>
        </template>
      </div>
      <div class="单行">秒</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from "vue";
import StatusDot from "./StatusDot.vue";

const 周几数组 = ["日", "一", "二", "三", "四", "五", "六"];

const 当前时间 = ref(new Date());

// 获取年月日时分秒
const standardYear = computed(() => 当前时间.value.getUTCFullYear());
const localYear = computed(() => 当前时间.value.getFullYear());
const standardMonth = computed(() =>
  String(当前时间.value.getUTCMonth() + 1).padStart(2, "0")
);
const localMonth = computed(() => String(当前时间.value.getMonth() + 1).padStart(2, "0"));
const standardDay = computed(() => String(当前时间.value.getUTCDate()).padStart(2, "0"));
const localDay = computed(() => String(当前时间.value.getDate()).padStart(2, "0"));
const standardWeekday = computed(() => 周几数组[当前时间.value.getUTCDay()]);
const localWeekday = computed(() => 周几数组[当前时间.value.getDay()]);
const standardHour = computed(() =>
  String(当前时间.value.getUTCHours()).padStart(2, "0")
);
const localHour = computed(() => String(当前时间.value.getHours()).padStart(2, "0"));
const standardMinute = computed(() =>
  String(当前时间.value.getUTCMinutes()).padStart(2, "0")
);
const localMinute = computed(() => String(当前时间.value.getMinutes()).padStart(2, "0"));
const standardSecond = computed(() =>
  String(当前时间.value.getUTCSeconds()).padStart(2, "0")
);
const localSecond = computed(() => String(当前时间.value.getSeconds()).padStart(2, "0"));

const isSameYear = computed(() => standardYear.value === localYear.value);
const isSameMonth = computed(() => standardMonth.value === localMonth.value);
const isSameDay = computed(() => standardDay.value === localDay.value);
const isSameWeekday = computed(() => standardWeekday.value === localWeekday.value);
const isSameHour = computed(() => standardHour.value === localHour.value);
const isSameMinute = computed(() => standardMinute.value === localMinute.value);
const isSameSecond = computed(() => standardSecond.value === localSecond.value);

const localOffset = computed(() => {
  const 偏移分 = -当前时间.value.getTimezoneOffset();
  const 符号 = 偏移分 >= 0 ? "+" : "-";
  const 绝对值 = Math.abs(偏移分);
  const 差时 = String(Math.floor(绝对值 / 60)).padStart(2, "0");
  const 差分 = String(绝对值 % 60).padStart(2, "0");
  return `${符号}${差时}:${差分}`;
});

let rafId: number;
const 刷新时间 = () => {
  当前时间.value = new Date();
  rafId = requestAnimationFrame(刷新时间);
};

onMounted(() => {
  rafId = requestAnimationFrame(刷新时间);
});

onUnmounted(() => {
  cancelAnimationFrame(rafId);
});
</script>

<style scoped>
.时间显示容器 {
  display: flex;
  flex-direction: column;

  align-items: center;
  height: fit-content;
}

/* 内容区域 */
.内容区域 {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 0;
}

/* 分隔符 */
.分隔符 {
  width: 0.5em;
}

/* 左侧标签列 */
.标签列 {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.标签项 {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
  font-weight: 600;
  text-transform: uppercase;
  display: inline-flex;
  align-items: center;
  transform-origin: center;
}

.标准时差 {
  font-family: "Courier New", monospace;
  font-size: 14px;
  font-weight: 500;
  font-feature-settings: "tnum";
  display: flex;
  align-items: center;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  color: #00d4ff;
}

.本地时差 {
  font-family: "Courier New", monospace;
  font-size: 14px;
  font-weight: 500;
  font-feature-settings: "tnum";
  display: flex;
  align-items: center;
  text-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  color: #00ff88;
}

/* 单行样式：渐变 +2 倍拉伸，1 倍行距 */
.单行 {
  font-family: "Courier New", monospace;
  font-weight: 600;
  background: linear-gradient(180deg, #00d4ff 0%, #00ff88 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transform: scaleY(2);
  transform-origin: center;
}

/* 双行样式：1 倍行距，无拉伸，上下行颜色不同 */
.双行 {
  font-family: "Courier New", monospace;
  font-weight: 600;
  font-size: 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.标准 {
  background: linear-gradient(180deg, #00d4ff 0%, #0099cc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: inline-block;
}

.本地 {
  background: linear-gradient(180deg, #00ff88 0%, #00cc66 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: inline-block;
}
</style>
