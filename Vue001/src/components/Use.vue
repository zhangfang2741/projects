<template>
   <div>
    <div ref="msgDiv">{{msg}}</div>
    <div v-if="msg1">Message got outside $nextTick: {{msg1}}</div>
    <div v-if="msg2">Message got inside $nextTick: {{msg2}}</div>
    <div v-if="msg3">Message got outside $nextTick: {{msg3}}</div>
    <button @click="changeMsg">
        Change the Message
    </button>
</div>
</template>

<script>
export default {
  data: function() {
    return {
      msg: "Hello Vue.",
      msg1: "",
      msg2: "",
      msg3: ""
    };
  },
  created () {
    this.$on('tip', () => {
        this.$message({
                message: 'Hell Tip'
                });
        })
    },
  props:['a','b'],
  mounted:function(){
      console.log("props:"+this._props)
  },
  methods: {
    changeMsg() {
      this.msg = "Hello world.";
      console.log("1");
      this.msg1 = this.$refs.msgDiv.innerHTML;
      console.log("2");
      this.$nextTick(() => {
          console.log("3");
        this.msg2 = this.$refs.msgDiv.innerHTML;
      });
     
      console.log("4");
      this.msg3 = this.$refs.msgDiv.innerHTML;
      this.$emit("tip");
    }

  },
  nextTick:function(){
       console.log("6");
  }
  
};
</script>
