import Vue from 'vue'
import Vuex from 'vuex'
import http from '@/util/http-common';

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    isUsed: {},
    user: {},
    loginUser: {
      id: '',
      name: '',
      pass: '',
      stampList:[],
      stamps: 0
    },
    order: [],
    allOrders: [],
    products: [],
    orderDate: [],
    orderDetails: [],
    orderCnt: 0,
    orderNumsHistory: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
    comments: [],
    monthHistory: [],
    allDetails: [],
    productComments: [],
    oidDateS: [],
    orderList: [],
    orderValue: 0,
    totalMoneyS: 0,
    totalStampS: 0,
    oidGroupS: [],
    userStamp: 0,
    proOrderSum: 0
  },
  getters: {
    getProOrderSum(state) {
      return state.proOrderSum
    },
    getUserStamp(state) {
      return state.userStamp
    },
    getOidGroupS(state) {
      return state.oidGroupS
    },
    getTotalMoneyS(state) {
      return state.totalMoneyS
    },
    getTotalStamps(state) {
      return state.totalStampS
    },
    getOrderValue(state) {
      return state.orderValue
    },
    getOrderList(state) {
      return state.orderList
    },
    getOidDateS(state) {
      return state.oidDateS
    },
    getProductComments(state) {
      return state.productComments
    },
    getAllDetails(state) {
      return state.allDetails
    },
    getMonthHistory(state) {
      return state.monthHistory
    },
    getUser(state) {
      return state.user;
    },
    getLoginUser(state) {
      return state.loginUser;
    },
    getProducts(state) {
      return state.products;
    },
    getOrderDates(state) {
      return state.orderDate;
    },
    getOrderDetails(state) {
      return state.orderDetails;
    },
    getOrderHistory(state) {
      return state.orderNumsHistory;
    },
    getComments(state) {
      return state.comments;
    },
    getTotalCnt(state) {
      return state.orderCnt;
    },
    getIsUsed(state) {
      return state.isUsed;
    },
    getAllOrders(state) {
      return state.allOrders;
    }
  },
  mutations: {
    INSERT_USER(state, user) {
      state.user = user;
    },
    LOGIN_USER(state, loginUser) {
      state.loginUser.id = loginUser.id;
      state.loginUser.name = loginUser.name;
      state.loginUser.pass = loginUser.pass;
      state.loginUser.stampList = loginUser.stampList;
      state.loginUser.stamps = loginUser.stamps;
    },
    SELECT_PRODUCT(state, products) {
      state.products = products;
    },
    STORE_ORDERDATES(state, orderDates) {
      state.orderDate.push(orderDates)
    },
    async STORE_ORDERDDETAILS(state, orderDetails) {
      await state.orderDetails.push(orderDetails)
    },
    STORE_ORDERHISTORY(state, orderHistory) {
      state.orderHistory = orderHistory;
    },
    STORE_COMMENT(state, comment) {
      state.comments.push(comment);
    },
    STORE_TOTALCNT(state, cnt) {
      state.orderCnt = cnt;
    },
    STORE_COMMENTS(state, comments) {
      state.productComments = comments;
    },
    CHECK_ID(state, checkResult) {
      state.isUsed = checkResult;
    },
    STORE_GETALLORDERS(state, orders) {
      state.allOrders = orders;
    },
    STORE_MONTHORDER(state, orders) {


      // productlist list
      let oids = []

      for(var i = 0; i < orders.length; i ++) {
          if(oids.length == 0) {
              oids.push(orders[i].o_id);
          } else  if(orders[i].o_id ==  oids[oids.length-1]) {
              continue
          } else {
              oids.push(orders[i].o_id);
          }
         
      }

      let oidGroup = oids
      let dates = []
      for(var j = 0; j < oidGroup.length; j ++) {
          for(var z = 0; z < orders.length; z ++ ) {
              if(orders[z].o_id == oidGroup[j]){
                  if(z != 0 && orders[z-1].o_id == orders[z].o_id) {
                      continue
                  }
                  let info = {
                    orderTime: orders[z].order_time,
                    table: orders[z].order_table
                  };
                 if (info.table == 100) {
                   info.table = 'Web';
                 } else {
                    info.table = 'Mobile';
                  }
                  dates.push(info)
                  continue
              }
          }
      }
      let oidDate = dates
      console.log("mothhistory 가져오기")
      console.log(dates)
      console.log(orders)      
      state.oidGroupS = oidGroup
      state.oidDateS = oidDate
      state.monthHistory = orders;

    },
    STORE_ALLDETAILS(state, details) {
      state.allDetails = details

    },
    STORE_STAMP(state, stamp) {
      state.userStamp = stamp;
    }
    // STORE_ORDERDETAILPROPERTY(state, idx) {
    //               // orderDetailList list
    //               let orders =  state.monthHistory;
      
    //               let oidD = oidGroup[idx];
    //               alert(`mutation value ${oidD}`)
                  
    //                     let list = [];
    //                     for(var b = 0; b < orders.length; b ++) {
    //                         if(orders[b].o_id == oidD){
    //                             list.push(orders[b]);
    //                         }
    //                     }
    //                     alert(oidD)
                  
    //                     let orderList = list;
                  
    //                     var addedStamps = 0;
    //                     var totalPrice = 0;
                  
    //                     for(var k = 0; k < list.length; k ++) {
    //                         addedStamps += list[k].quantity
    //                         totalPrice += list[k].price * list[k].quantity
    //                     }
                  
    //               state.totalMoneyS = totalPrice
    //               state.totalStampS = addedStamps
    //               state.orderList = orderList
    // }

  },
  actions: {
    async checkId({ commit }, id1) {
      await http.get('rest/user/isUsed', {
        params: {
          id: id1
        }
      })
      .then((response) => {
          var a = response.data;
          console.log(a);
          console.log("checkId");

        commit("CHECK_ID", response.data);
      })
      .catch((error) => {
        console.log(error);
      });
    },
    async insertUser({ commit }, user) {

      await http.post('rest/user/', user)
        .then((response) => {
          var a = response.data;
          console.log(a);
        commit("INSERT_USER", response.data);
      })
      .catch((error) => {
        console.log(error);
      });
    },
    async login({ commit }, user) {
      await http.post('rest/user/login', user)
              .then((loginUser) => {
                console.log(loginUser.data.id);
          commit('LOGIN_USER', loginUser.data);
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // resetLoginUser() {
    //   commit('STORE_LOGOUT', "");
    // },
    selectProducts({ commit }) {
      //TODO: 아래 코드는 서버 대신 작성한 코드이므로 추후 삭제
      http.get('/rest/product')
      .then((response) => {
        var a = response.data;
        console.log(a);
        commit('SELECT_PRODUCT', response.data);
      })
    .catch((error) => {
      console.log(error);
    });
    },
    storeOrderDate({ commit }, orderDates) {
      commit('STORE_ORDERDATES', orderDates);
    },
    storeOrderDetail({ commit }, orderDetails) {
      commit('STORE_ORDERDDETAILS', orderDetails);
    },
    storeOrderHistory({ commit }, orderDate) {
      commit('STORE_ORDERHISTORY', orderDate);
      console.log(orderDate);
    },
    storeComments({ commit }, payload) {
      commit('STORE_COMMENT', payload);
    },
    async productCntById({commit}, no) {
          await http.get(`/product/${no}`)
          .then((response) => {
            var a = response.data;
            console.log(a);
          commit("STORE_TOTALCNT", response.data["quantity"]);
        })
        .catch((error) => {
          console.log(error);
        });
    },
     async insertOrder({ commit }, payload) {
       await http.post("/rest/order", payload)
          .then((response) => {
            console.log(response); 
            commit("STORE_ORDERDDETAILS", response.data);
          })
        .catch((error) => {
          console.log(error);
        });
    },
    async getComments({ commit }, productId) {
      await http.get(`/rest/comment/${productId}`)
        .then((response) => {
          console.log(response.data);
            commit("STORE_COMMENTS", response.data);
        })
        .catch((error) => {
          console.log(error);
        });
    },
    async getAllOrders({ commit }) {
      await http.get(`/rest/order/all/`)
        .then((response) => {
          console.log("getAllOrders");
          console.log(response.data);
            commit("STORE_GETALLORDERS", response.data);
        })
        .catch((error) => {
          console.log(error);
        });
    },
    async getLastMonthOrder({ commit }, id1) {
      await http.get("/rest/order/byUser", {
        params: {
          id: id1
        }
      })
        .then((response) => {
           console.log(response); 
           commit("STORE_MONTHORDER", response.data);
         })
       .catch((error) => {
         console.log(error);
       });
    },
    async getAllDetails({ commit }) {
      await http.get("/rest/order/all")
        .then((response) => {
           console.log("getAllDetails");
           console.log(response); 
           commit("STORE_ALLDETAILS", response.data);
         })
       .catch((error) => {
         console.log(error);
       });
    },
    async removeComment({commit}, id) {
      await http.delete(`/rest/comment/${id}`)
      .then((response) => {
        var a = response.data;
        console.log(a);
      commit("STORE_ORDERDDETAILS", response.data);
    })
    .catch((error) => {
      console.log(error);
    });
    },
    async addComments({ commit }, payload) {
      await http.post("/rest/comment", payload)
         .then((response) => {
           console.log(response); 
           commit("STORE_ORDERDDETAILS", response.data);
         })
       .catch((error) => {
         console.log(error);
       });
    },
    async modifyComments({ commit }, payload) {
      await http.put("/rest/comment", payload)
         .then((response) => {
           console.log(response); 
           commit("STORE_ORDERDDETAILS", response.data);
         })
       .catch((error) => {
         console.log(error);
       });
    },
    async getStamps({commit}, userId) {
      await http.get(`/rest/user/stamp/${userId}`)
      .then((response) => {
        var a = response.data;
        console.log("Stamp get 호출");
        console.log(a);
      commit("STORE_STAMP", response.data);
    })
    .catch((error) => {
      console.log(error);
    });
    },
  },
  modules: {
  }
})
