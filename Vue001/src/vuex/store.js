import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

function _setTimeout(callback, timeout, param) {
  var args = Array.prototype.slice.call(arguments, 2);
  var _cb = function () {
    callback.apply(null, args);
  }
  setTimeout(_cb, timeout);
}

function _setInterval(callback, interval, param) {
  var args = Array.prototype.slice.call(arguments, 2);
  var _cb = function () {
    callback.apply(null, args);
  }
  setInterval(_cb, interval);
}
export default new Vuex.Store({
  state: {
    count: 1,
    arr: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
  },
  mutations: {
    add(state, n) {
      state.count += n;
    },
    reduce(state) {
      state.count -= 1;
    }
  },
  getters: {
    count: function (state) {
      return state.count += 100;
    }
  },
  actions: {
    addAction(context) {
      context.commit('add', 10)
    },
    reduceAction({
      commit
    }) {
      _setInterval(function () {
        commit("reduce")
      }, 1000);
      console.log('我比reduce提前执行');
    }
  }
});
