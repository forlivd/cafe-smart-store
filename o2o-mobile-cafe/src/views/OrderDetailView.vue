<template>
    <div>
        <div>주문 상세</div>
               <div v-for="(p, idx) in list" :key="idx">
                    <img :src="require('@/assets/menu/'+p.img)" height="50px" alt="이미지"/>
                         품명 : {{p.name}}  &nbsp; &nbsp; &nbsp; &nbsp; 단가 : {{p.price}}원   &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  {{p.quantity}}잔    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 가격 : {{p.price * p.quantity}} 원      
                </div>
        <div>총 가격: {{ moneyC }}원  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 스탬프 적립: {{stampC}} 개 </div>
    </div>

</template>


<script>
export default {
    name: 'order-detail-view',
     props: ["orderValue"],

    data(){
        return {
            orderHistory: [],
            orderList:[],
            oidGroup: [],
            oid: 0,
            money : 0,
            stamp : 0,
            loginUser: [],
            isWeb: 'Web'
        }
    },
    methods: {

    },
    computed : {
        web() {
            return this.isWeb
        },
        moneyC() {
            return this.money
        },
        stampC() {
            return this.stamp
        },
        list() {
            return this.orderList
        },
        monthHistory() {
            return this.$store.state.monthHistory;
        }

    },
    watch: {
    monthHistory : function (newVal, oldVal) {
        console.log(`${oldVal} + 에서  + ${newVal} + 로 orderDeatail로 변경되었습니다.`)
                let orders = this.monthHistory
      
                let oidD = this.$store.getters.getOidGroupS[this.orderValue];
                
                
                        let list = [];
                        for(var b = 0; b < orders.length; b ++) {
                            if(orders[b].o_id == oidD){
                                list.push(orders[b]);
                            }
                        }                  
                        let orderList1 = list;
                  
                        var addedStamps = 0;
                        var totalPrice = 0;
                  
                        for(var k = 0; k < orderList1.length; k ++) {
                            addedStamps += orderList1[k].quantity
                            totalPrice += orderList1[k].price * orderList1[k].quantity
                        }
                  
                  this.orderList = orderList1
                  this.money = totalPrice
                  this.stamp = addedStamps    
                  }
  },
    mounted(){
        
        // this.$store.state.orderValue = this.orderValue

                this.loginUser = this.$store.state.loginUser

                let orders = this.monthHistory
      
                let oidD = this.$store.getters.getOidGroupS[this.orderValue];
                
                
                        let list = [];
                        for(var b = 0; b < orders.length; b ++) {
                            if(orders[b].o_id == oidD){
                                list.push(orders[b]);
                            }
                        }                  
                        let orderList1 = list;
                  
                        var addedStamps = 0;
                        var totalPrice = 0;
                  
                        for(var k = 0; k < orderList1.length; k ++) {
                            addedStamps += orderList1[k].quantity
                            totalPrice += orderList1[k].price * orderList1[k].quantity
                        }
                  
                  this.orderList = orderList1
                  this.money = totalPrice
                  this.stamp = addedStamps







        // let oids = []
        // for(var i = 0; i < this.orderHistory.length; i ++) {
        //     if(oids.length == 0) {
        //         oids.push(this.orderHistory[i].o_id);
        //     } else  if(this.orderHistory[i].o_id ==  oids[oids.length-1]) {
        //         continue
        //     } else {
        //         oids.push(this.orderHistory[i].o_id);
        //     }
           
        // }
        // this.oidGroup = oids
        // this.oid = this.oidGroup[this.orderValue];

        // let list = [];
        // for(var z = 0; z < this.orderHistory.length; z ++) {
        //     if(this.orderHistory[z].o_id == this.oid){
        //         list.push(this.orderHistory[z]);
        //     }
        // }

        // this.orderList = list;
        // console.log("list")

        // console.log(list)

        // var addedStamps = 0;
        // var totalPrice = 0;

        // for(var y = 0; y < list.length; y ++) {
        //     addedStamps += list[y].quantity
        //     totalPrice += list[y].price * list[y].quantity
        // }

        // this.totalMoney = totalPrice
        // this.totalStamp = addedStamps





    }

}
</script>
