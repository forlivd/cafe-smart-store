<template>
  <div>
    <div>
      <b-button id="toggleBt" v-show="loginUser.id" v-b-toggle.sidebar-right @click="showOrderInfo"
        >장바구니 보기</b-button
      >
      <b-sidebar id="sidebar-right" title="주문 정보" right shadow backdrop>
        <div class="px-3 py-2">
          <b-list-group v-for="(p, idx) in orderProducts" :key="idx">
            <b-list-group-item>
              <span>
                <img :src="require('@/assets/menu/' + p.img)" height="100px" img-top alt="Image" />
              </span>
              <span>
                <div>제품명: {{ p.name }}</div>
                <div>개당 가격: {{ p.price }}</div>
                <div>주문량: {{ p.orderCnts }}</div>
              </span>
            </b-list-group-item>
          </b-list-group>
          <div v-show="!orderProducts[0]">선택한 제품이 없습니다!</div>
          <b-button
            id="orderButon"
            class="right"
            v-show="orderProducts[0]"
            variant="success"
            @click="doOrder"
            >주문</b-button
          >
        </div>
      </b-sidebar>
    </div>
    <b-container id="container">
      <b-alert id="notice" show variant="primary">
        <h3>커피</h3>
        <div>뛰면서 즐기는 커피 한잔의 여유</div>
        <div>{{ new Date() | yyyyMMdd }}</div>
        <hr />
        <div>갓 볶은 아라비카산 원두만 고집합니다.</div>
      </b-alert>
      <b-row>
        <b-col cols="3" v-for="(item, idx) in products" :key="idx" class="text-center">
          <b-card class="productSize">
            <b-card-img
              :src="require('@/assets/menu/' + item.img)"
              height="300px" 
              img-top
              alt="Image"
              @click="moveProductDetail(idx)"
            ></b-card-img>
            <b-card-title :title="item.name"></b-card-title>
            <b-card-text>{{ item.price }} 원</b-card-text>
            <b-button class="pdCnt" v-show="loginUser.id" variant="primary">{{
              orderNums[idx]
            }}</b-button>
            <b-button
              class="pdCnt"
              v-show="loginUser.id"
              variant="success"
              @click="increaseNums(idx)"
              >+</b-button
            >
            <b-button class="pdCnt" v-show="loginUser.id" variant="dark" @click="decreaseNums(idx)"
              >-</b-button
            >
            <b-button class="pdCnt" v-show="loginUser.id" variant="warning"
              ><img class="cart" src="@/assets/shopping_cart.png"
            /></b-button>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  name: "product-list-view",
  created() {
    this.$store.dispatch("selectProducts");
    for (let i = 0; i < this.products.length; i++) {
      this.orderNums[i] = 0;
    }
  },
  data() {
    return {
      orderNums: [], // 각 제품들의 기본 0개 주문 값
      orderProducts: [], // 제품 정보에 주문 량을 입력한 값, 0보다 큰 주문 량만 입력됨
      orderDates: [],
    };
  },

  computed: {
    products() { // 모든 제품 목록
      return this.$store.getters.getProducts;
    },
    loginUser() { // 로그인 한 유저 정보
      let loginUser = this.$store.getters.getLoginUser;
      return loginUser;
    },
    allOrders() {
      return this.$store.getters.getAllOrders;
    }
  },
    watch: {
    allOrders: function (newVal, oldVal) {
      console.log(`${oldVal} + 에서  + ${newVal} + 로 변경되었습니다.`)
      this.doOrder2()
    }
  },
  filters: {
    // filter로 쓸 filter ID 지정
    yyyyMMdd: function (value) {
      // 들어오는 value 값이 공백이면 그냥 공백으로 돌려줌
      if (value == "") return "";

      // 현재 Date 혹은 DateTime 데이터를 javaScript date 타입화
      var js_date = new Date(value);
      // alert(js_date)

      // 연도, 월, 일 추출
      var year = js_date.getFullYear();
      var month = js_date.getMonth() + 1;
      var day = js_date.getDate();
      var hour = js_date.getHours();
      var minutes = js_date.getMinutes();

      // 월, 일의 경우 한자리 수 값이 있기 때문에 공백에 0 처리
      if (month < 10) {
        month = "0" + month;
      }

      if (day < 10) {
        day = "0" + day;
      }

      // 최종 포맷 (ex - '2021-10-08')
      return year + "-" + month + "-" + day + "  " + hour + "시 " + minutes + "분";
    },
  },
  methods: {
    moveProductDetail(idx) {
      let loginUser = this.$store.getters.getLoginUser;
      if (loginUser.id) {
        this.$router.push({
          name: "product-detail-view",
          params: { idx: idx, user: loginUser.id },
        });
      }
    },
    doOrder() {
      this.$store.dispatch("getAllOrders");
      this.$store.dispatch("getLastMonthOrder", this.loginUser.id);
   
    },
    doOrder2() {
      let or = JSON.stringify(this.$store.getters.getAllOrders);
      let allOrders = JSON.parse(or);
      let newOrderId = allOrders[0].orderId + 1; // 새로운 orderId 만들기


      let newOrderDetailList = []; // orderdetail list 
      for (var i = 0; i < this.orderProducts.length; i++) {
        let orderDetail = {
          orderId: newOrderId,
          productId: this.orderProducts[i].id,
          quantity: this.orderProducts[i].orderCnts,
        };
        if(newOrderDetailList.length == 0) {
          newOrderDetailList[0] = orderDetail
        } else {
         newOrderDetailList[newOrderDetailList.length] = orderDetail;
        }
      } // orderDetails 완성

      let loginUser = this.$store.getters.getLoginUser;

      //table 일단 하드코딩

      var order = { // 새 주문
        id: newOrderId,
        userId: loginUser.id,
        orderTable: "100", // 웹주문 기본값 100
        completed: "N",
        details: newOrderDetailList
      };

      let form1 = JSON.stringify(order);
      let form2 = JSON.parse(form1)

      this.$store.dispatch("insertOrder", form2); // 새로운 주문을 insert 하기

      this.$router.push({ name: "user-info-view", params: { user: loginUser.id } });

    },
    increaseNums(idx) {
      this.orderNums[idx] += 1;
      let arr = this.orderNums.splice(0);
      this.orderNums = arr;
    },
    decreaseNums(idx) {
      if (this.orderNums[idx] === 0) {
        return;
      }
      this.orderNums[idx] -= 1;
      let arr = this.orderNums.splice(0);
      this.orderNums = arr;
    },
    showOrderInfo() {
      let orderNums = this.orderNums;
      let productInfos = this.products;
      let orderProducts = [];
      let orderDate = [];

      for (var i = 0; i < productInfos.length; i++) {
        if (orderNums[i] > 0) {
          productInfos[i].orderCnts = `${orderNums[i]}`;
          orderProducts.push(productInfos[i]);
        }
      }

      orderDate.push(new Date());
      console.log(orderDate);

      this.orderProducts = orderProducts;
      this.orderDates = orderDate;
    },
  },
};
</script>

<style scoped>
.productSize {
  width: 244px;
  height: 458px;
  margin: 10px;
}

.pdCnt {
  /* width:40px;
    margin: 3px; */
  margin: 3px;
  width: 22%;
  height: 40px;
}

.cart {
  width: 24px;
  height: 24px;
}

#notice {
  margin-top: 20px;
  margin-left: 7px;
}

#toggleBt {
  float: right;
  margin-top: 70px;
  margin-right: 40px;
  height: 120px;
  font:bold;
}

#sidebar-right {
  text-align: center;
}

#toggleBt {
  color: black;
  font-size: 15px;
  background-color: #c8d7ee;
  text-shadow: 1cm;
  position: fixed;
  text-align: center;
  writing-mode:sideways-lr;
  width: 5%;
  height: 50%;
  margin-top: 0px;
}

#container {
  opacity: 1;
}

#orderButon {
  width: 100%;
}
</style>
