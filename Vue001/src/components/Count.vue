<template>
    <div>
        <span>{{msg}}</span>
        <hr/>
        <h3>{{count}}</h3>
        <h3>{{arr}}</h3>
        <div>
            <button type="button" class="btn btn-primary" @click="$store.commit('add',2)">+</button>
            <button type="button" class="btn btn-primary" @click="$store.commit('reduce')">-</button>
            <p>Mutations同步方法：
                <button @click="add(2)">+</button>
                <button @click="reduce">-</button>
            </p>
            
            <p>actions:异步方法：
                <button @click="addAction">+</button>
                <button @click="reduceAction">-</button>
            </p>
        </div>
    </div>
</template>
<script>
// import store from "@/vuex/store";
import { mapState, mapMutations, mapGetters, mapActions } from "vuex";
import axios from "axios";
export default {
  data() {
    return {
      msg: "Hello Vuex"
    };
  },
  // store,
  //   computed: {
  //     count() {
  //       return this.$store.state.count;
  //     }
  //   }
  // computed:mapState({
  //         count:state=>state.count  //理解为传入state对象，修改state.count属性
  //      })

  computed: {
    ...mapState(["count", "arr"]),
    ...mapGetters(["count"])
  },
  mounted: function() {
    let that = this;
    axios
      .get("http://localhost:8081/findAll")
      .then(function(response) {
        that.msg = response.data;
      })
      .catch(function(response) {
        console.log(response);
      });
  },
  methods: {
    ...mapMutations(["add", "reduce"]),
    ...mapActions(["addAction", "reduceAction"])
  }
};
</script>