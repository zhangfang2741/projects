<template>
  <div id="app">
    <img src="./assets/logo.png">
    <p>{{ $route.name}}</p>
    <button @click="goback">后退</button>
    <button @click="go">前進</button>
    <button @click="goHome">回到首页</button>
    <div> 页面导航
      <router-link to="/">首页</router-link>
      <router-link to="/hi">Hi页面</router-link>
      <router-link to="/hi/hi1">-Hi页面1</router-link> |
      <router-link to="/hi/hi2">-Hi页面2</router-link>
      <router-link to="/elselect">Elselect页面</router-link>
      <router-link to="/count">Count页面</router-link>
      <router-link to="/use">Use页面</router-link>
    </div>
    <div> 页面传参
      <router-link :to="{name:'Hi1',params:{username:'jspang'}}">Hi页面</router-link>
    </div>
    <router-view/>
  </div>
</template>

<script>
// $(function() {
//   alert(123);
// });
export default {
  name: "App",
  methods: {
    goback() {
      this.$router.go(-1);
    },
    go() {
      this.$router.go(1);
    },
    goHome() {
      this.$router.push("/");
    }
  }
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
