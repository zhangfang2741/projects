<template>
    <div class="hello">
        <p>{{ $route.name}}</p>
        <h1>
            <!-- {{msg}} -->
           
           <!-- // {{$route.params}} -->
            <transition 
    name="fade"
    enter-class="fade-in-enter"
    enter-active-class="fade-in-active"
    leave-class="fade-out-enter"
    leave-active-class="fade-out-active">
            <router-view class="aaa"></router-view>
          </transition>
            
        </h1>
        <Hi2/>
        <Uses a='11' b='22'></Uses>
    </div>
</template>
<script>
import Hi2 from "@/components/Hi.2";
import Use from "@/components/Use";
export default {
  name: "hi",
  data: function() {
    return {
      msg: "Hi I am JSPANG"
    };
  },
  components: { Hi2: Hi2,Uses:Use }
};
</script>
<style scoped>
.fade-in-active, .fade-out-active{
 transition: all 0.5s ease  
}
    
.fade-in-enter, .fade-out-active{
opacity: 0 
}
  
</style>
