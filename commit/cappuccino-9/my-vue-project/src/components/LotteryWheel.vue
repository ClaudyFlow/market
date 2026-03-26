<template>
<div class="lottery-wheel">
  <div class="wheel" :style="wheelStyle" ref="wheel">
    <!-- 奖项列表 -->
    <div v-for="(item, index) in items" :key="index" class="item" :style="{ transform: `rotate(${item.deg}deg) translate(150px) rotate(-${item.deg}deg)` }">
      {{ item.text }}
    </div>
  </div>
  <button @click="spin">开始抽奖</button>
</div>
</template>

<script>
import { animate } from 'animejs';

export default {
  name: 'LotteryWheel',
  data() {
    return {
      items: [
        { text: '一等奖', deg: 0 },
        { text: '二等奖', deg: 30 },
        { text: '三等奖', deg: 60 },
        { text: '四等奖', deg: 90 },
        { text: '五等奖', deg: 120 },
        { text: '六等奖', deg: 150 },
        { text: '七等奖', deg: 180 },
        { text: '八等奖', deg: 210 },
        { text: '九等奖', deg: 240 },
        { text: '谢谢参与', deg: 270 },
        { text: '十等奖', deg: 300 },
        { text: '十一等奖', deg: 330 },
      ],
      degrees: 0, // 转盘当前角度
      spinning: false, // 是否正在旋转
    };
  },
  computed: {
    wheelStyle() {
      return {
        transform: `rotate(${this.degrees}deg)`,
        transition: this.spinning ? 'transform 10s ease-in-out' : 'none',
      };
    },
  },
  methods: {
    spin() {
      if (this.spinning) return; // 如果已经在旋转，不响应点击
      this.spinning = true;
      const randomDegree = Math.floor(Math.random() * 3600) + 1080; // 旋转3.5圈到4圈之间，并且随机位置结束
      animate(this.$refs.wheel, {
        transform: `rotate(${randomDegree}deg)` ,
        duration: 10000, // 10秒
        easing: 'easeInOutQuad',
        complete: () => {
          this.spinning = false;
          const finalDegree = randomDegree % 360;
          this.determineWinner(finalDegree);
        },
      });
      this.degrees = randomDegree;
    },
    determineWinner(deg) {
      const sectorSize = 360 / this.items.length; // 每个奖项的扇区大小
      let winnerIndex = Math.floor((deg / sectorSize) % this.items.length);
      if (deg % sectorSize === 0) {
        winnerIndex = (winnerIndex - 1 + this.items.length) % this.items.length;
      }
      alert(`恭喜你，抽中了${this.items[winnerIndex].text}`);
    },
  },
};
</script>

<style scoped>
.lottery-wheel {
  position: relative;
  width: 300px;
  height: 300px;
  margin: 0 auto;
}

.wheel {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #f0f0f0;
  border: 2px solid #ccc;
}

.item {
  position: absolute;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  color: #000;
  border: 1px solid #ccc;
  border-radius: 50%;
  transform-origin: 50% 150px;
}
</style>
