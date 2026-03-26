<template>
  <div class="slot-machine">
    <div class="slot-header">
      <button class="spin-btn" @click="spin" :disabled="spinning">开始抽奖</button>
      <div v-if="result !== null" class="result">
        <span v-if="result">恭喜中奖！</span>
        <span v-else>很遗憾，未中奖</span>
      </div>
    </div>
    <div class="slot-row-wrapper">
      <div class="pointer">▼</div>
      <div class="slot-row">
        <div
          v-for="(symbol, idx) in row"
          :key="idx"
          class="slot-symbol"
          :class="{ active: idx === 14 }"
        >
          {{ symbol }}
        </div>

      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SlotMachine',
  data() {
    return {
      symbols: ['🍒', '🍋', '🔔', '⭐', '🍀', '7️⃣'],
      row: [],
      visibleCount: 9, // 可见格数，奇数，指针居中
      pointerIndex: 4, // 指针固定在正中间
      spinning: false,
      result: null,
      intervalId: null,
      finalIndex: 0,
    };
  },
  created() {
    this.resetRow();
  },
  methods: {
    resetRow() {
      // 生成一行随机符号，长度大于可见格数，方便滚动
      // 保证row长度为visibleCount+20，指针始终指向正中
      this.row = Array(this.visibleCount + 20).fill().map(() => this.symbols[Math.floor(Math.random() * this.symbols.length)]);
    },
    spin() {
      if (this.spinning) return;
      this.spinning = true;
      this.result = null;
      this.resetRow();
      let total = this.row.length;
      let currentOffset = 0;
      // 随机决定最终停在哪个位置，且指针始终指向正中
      this.finalIndex = Math.floor(Math.random() * (total - this.visibleCount)) + this.pointerIndex;
      let speed = 60;
      let step = 0;
      clearInterval(this.intervalId);
      this.intervalId = setInterval(() => {
        if (step < 30) {
          speed = 60;
        } else if (step < 50) {
          speed = 100;
        } else {
          speed = 180;
        }
        if (currentOffset < this.finalIndex - this.pointerIndex) {
          currentOffset++;
          this.row.push(this.symbols[Math.floor(Math.random() * this.symbols.length)]);
          this.row.shift();
          step++;
        } else {
          clearInterval(this.intervalId);
          this.spinning = false;
          this.checkResult();
        }
      }, speed);
    },
    checkResult() {
      // 中奖判定为第15号物品（索引14）且内容为7️⃣
      this.result = this.row[14] === '7️⃣';
    },
  },
};
</script>

<style scoped>
.slot-machine {
  width: 600px;
  margin: 40px auto;
  text-align: center;
  background: #fff;
  border-radius: 18px;
  box-shadow: none;
  padding-bottom: 32px;
}
.slot-row-wrapper {
  position: relative;
  width: 100%;
  height: 80px;
  margin-bottom: 20px;
}
.pointer {
  position: absolute;
  left: 50%;
  /* 指针底边对齐slot-symbol中心线，slot-symbol高60px，中心线30px */
  top: 6px;
  transform: translateX(-50%) translateY(-100%);
  font-size: 2em;
  color: #e74c3c;
  z-index: 2;
  pointer-events: none;
}
.slot-row {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #222;
  border-radius: 10px;
  overflow: hidden;
  padding: 10px 0;
  width: auto;
  margin: 0 auto;
  position: relative;
}
.slot-symbol {
  width: 128px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4em;
  color: #fff;
  opacity: 0.5;
  margin: 0 2px;
  border-radius: 8px;
  background: #333;
  transition: opacity 0.2s, color 0.2s, background 0.2s;
  font-family: 'Consolas', 'Menlo', 'Monaco', 'monospace';
  box-sizing: border-box;
}
.slot-symbol.active {
  opacity: 1;
  color: #ffd700;
  background: linear-gradient(180deg, #fffbe6 0%, #ffe066 60%, #ffd700 100%);
  font-weight: bold;
  box-shadow: 0 0 32px 12px #ffe066, 0 0 24px #ffd700, 0 0 0 4px #fffbe6 inset, 0 0 0 8px #ffd70033;
  border: 2.5px solid #ffd700;
  transform: scale(1.22);
  z-index: 3;
  transition: 
    opacity 0.2s, 
    color 0.2s, 
    background 0.2s, 
    box-shadow 0.2s, 
    border 0.2s, 
    transform 0.18s cubic-bezier(.4,1.4,.6,1);
}
.slot-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
}
.spin-btn {
  font-size: 1.6em;
  padding: 0.5em 2.2em;
  border-radius: 12px;
  background: linear-gradient(90deg, #ffd700 0%, #ffb300 100%);
  color: #a00;
  border: none;
  font-weight: bold;
  box-shadow: 0 2px 12px #ffd70055;
  cursor: pointer;
  margin-bottom: 10px;
  transition: background 0.2s, color 0.2s, box-shadow 0.2s;
}
.spin-btn:disabled {
  background: #eee;
  color: #aaa;
  cursor: not-allowed;
  box-shadow: none;
}
.result {
  font-size: 1.3em;
  margin-top: 0;
  margin-bottom: 0;
  color: #b80000;
  font-weight: bold;
  min-height: 1.5em;
}
</style>
