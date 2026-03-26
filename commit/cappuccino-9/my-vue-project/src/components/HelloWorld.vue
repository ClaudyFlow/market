<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  props: {
    msg: String,
  },
};
</script>

<style scoped>
.hello {
  text-align: center;
  margin-top: 60px;
}

h1 {
  color: #42b983;
}
</style>
