<template>
    <div>
        <b-container>
            <b-alert id="notice" show variant="success">
                <div>우리 {{this.loginUser.name}}회원님은요~~</div>
                <hr id="lineStyle">
                <div>로그인 할 때 아이디는 {{loginUser.id}}을 사용합니다.</div>
                <div>현재 모은 스탬프는 총 {{uiStamps}}개로 {{this.curLevel}}입니다.</div>
                <div>앞으로 {{this.remainStamp - this.stampCnt}}개만 더 모으면 다음 단계입니다!~~~~~</div>
            </b-alert>

           <hr id="lineStyle">
           <b-list-group >
                <b-list-group-item id=grayColor>이제까지 주문 내역은 아래와 같습니다.</b-list-group-item>
                <b-list-group-item>
                    <div id='listgroup'> 주문 정보를 클릭하면 주문 내역을 살펴볼 수 있습니다. </div>
                    <b-list-group  v-for="(dates, idx) in oidDateS" v-bind:value=idx :key="idx">
                        <b-list-group-item @click="showOrderDetail(idx)" button>{{dates.orderTime | yyyyMMdd}}에 {{dates.table}} 주문</b-list-group-item>
                         <b-list-group-item v-show="isOpen[idx]" >
                             <order-detail-view v-bind:orderValue="idx" name="order-detail-view"/>
                         </b-list-group-item>
                        <!-- <router-view name="order-detail-view" class="mb-2" /> -->
                    </b-list-group>
                </b-list-group-item>
            </b-list-group>
        </b-container>
    </div>
</template>

<script>
import OrderDetailView from './OrderDetailView.vue';

export default{
    name: "user-info-view",
  components: { 
      OrderDetailView
    },
    data(){
        return{
            loginUser: {},
            isOpen: [false,false,false,false,false,false,false,false,false,false,false],
            
            stampCnt: 0,
            remainStamp: 0,
            curLevel:'',
            oidGroup: [],
            oidDate: [],
            oidWeb: []
        }
    },
    computed: {
        isWeb(){
            return this.oidWeb;
        },
        orderDates() {
            return this.$store.getters.getOrderDates;
        },
        orderHistory() {
            return this.$store.getters.getMonthHistory;
        },
        oidDateS() {
            return this.$store.getters.getOidDateS;
        },
        stamps() {
            return this.$store.getters.getUserStamp;
        },
        uiStamps() {
            return this.loginUser.stamps
        }
        
    },
    watch : {
        stamps : function (newVal, oldVal) {
            console.log(`${oldVal} + 에서  + ${newVal} + 로 stamps 변경되었습니다.`)
              this.loginUser.stamps = newVal
            this.findLevel();
        }
    },
    mounted() {


        this.loginUser = this.$store.getters.getLoginUser;

        this.findLevel();

        // let user = JSON.stringify(this.loginUser);
        // let jUser = JSON.parse(user);

        this.$store.dispatch("getLastMonthOrder", this.loginUser.id);
        this.$store.dispatch("getStamps", this.loginUser.id)




    },
    filters : {  
        // filter로 쓸 filter ID 지정
        yyyyMMdd : function(value){ 
            // 들어오는 value 값이 공백이면 그냥 공백으로 돌려줌
            if(value == '') return '';
        
            // 현재 Date 혹은 DateTime 데이터를 javaScript date 타입화
            var js_date = new Date(value);

            // 연도, 월, 일 추출
            var year = js_date.getFullYear();
            var month = js_date.getMonth() + 1;
            var day = js_date.getDate();
            var hourj = js_date.getHours();
            var hour = 0


            var minutes = js_date.getMinutes();

            // 월, 일의 경우 한자리 수 값이 있기 때문에 공백에 0 처리
            if(month < 10){
                month = '0' + month;
            }

            if(day < 10){
                day = '0' + day;
            }

                        
            if(hourj-9 < 0) {
                hour = 24 + (hourj-9)
                day -= 1
                if(day <= 0) {
                    month -= 1
                    if(month == 1) {
                        day = 31;
                    } else if( month == 2) {
                        day = 28;
                    }else if( month == 3) {
                        day = 31;
                    }else if( month == 4) {
                        day = 30;
                    }else if( month == 5) {
                        day = 31;
                    }else if( month == 6) {
                        day = 30;
                    }else if( month == 7) {
                        day = 31;
                    }else if( month == 8) {
                        day = 31;
                    }else if( month == 9) {
                        day = 30;
                    }else if( month == 10) {
                        day = 31;
                    }else if( month == 11) {
                        day = 30;
                    }else if( month == 12) {
                        day = 31;
                    }
                    
                }
            } else {
                hour = hourj - 9
            }

            // 최종 포맷 (ex - '2021-10-08')
            return year + '-' + month + '-' + day + "  " + hour + "시 " + minutes + "분";
        },
    },
    methods: {
        getOrderList() {
            let oids = []
            for(var i = 0; i < this.orderHistory.length; i ++) {
                if(oids.length == 0) {
                    oids.push(this.orderHistory[i].o_id);
                } else  if(this.orderHistory[i].o_id ==  oids[oids.length-1]) {
                    continue
                } else {
                                    oids.push(this.orderHistory[i].o_id);
                }
            
            }
             this.oidGroup = oids
        },
        showOrderDetail(idx) {
                if(this.isOpen[idx]==false){
                    this.$set(this.isOpen,idx,true);
                }else{
                    this.$set(this.isOpen,idx,false);
                }
            }
       ,
        findLevel(){
            if(this.loginUser.stamps < 10){
                this.remainStamp=10;
                this.curLevel = "나무";
                this.stampCnt = this.loginUser.stamps;
            }
            else if(10 <= this.loginUser.stamps <= 64){
                this.remainStamp=10;
                if(this.loginUser.stamps<20){
                    this.curLevel = "씨앗1단계";
                    this.stampCnt = this.loginUser.stamps-10;
                }else if(this.loginUser.stamps<30){
                    this.curLevel = "씨앗2단계";
                    this.stampCnt = this.loginUser.stamps-20;
                }else if(this.loginUser.stamps<40){
                    this.curLevel = "씨앗3단계";
                    this.stampCnt = this.loginUser.stamps-30;
                }else if(this.loginUser.stamps<50){
                    this.curLevel = "씨앗4단계";
                    this.stampCnt = this.loginUser.stamps-40;
                }else if(this.loginUser.stamps<65){
                    this.curLevel = "씨앗5단계";
                    this.remainStamp=15;
                    this.stampCnt = this.loginUser.stamps-50;
                }
            }
            else if(65<= this.loginUser.stamps <= 144){
                    this.remainStamp=15;
                if(this.loginUser.stamps<80){
                    this.curLevel = "꽃1단계";
                    this.stampCnt = this.loginUser.stamps-65;
                }else if(this.loginUser.stamps<95){
                    this.curLevel = "꽃2단계";
                    this.stampCnt = this.loginUser.stamps-80;
                }else if(this.loginUser.stamps<110){
                    this.curLevel = "꽃3단계";
                    this.stampCnt = this.loginUser.stamps-95;
                }else if(this.loginUser.stamps<125){
                    this.curLevel = "꽃4단계";
                    this.stampCnt = this.loginUser.stamps-110;
                }else if(this.loginUser.stamps<145){
                    this.curLevel = "꽃5단계";
                    this.remainStamp=20;
                    this.stampCnt = this.loginUser.stamps-125;
                }
            }
            else if(145<= this.loginUser.stamps <= 249){
                  this.remainStamp=20;
                if(this.loginUser.stamps<165){
                    this.curLevel = "열매1단계";
                    this.stampCnt = this.loginUser.stamps-145;
                }else if(this.loginUser.stamps<185){
                    this.curLevel = "열매2단계";
                    this.stampCnt = this.loginUser.stamps-165;
                }else if(this.loginUser.stamps<205){
                    this.curLevel = "열매3단계";
                    this.stampCnt = this.loginUser.stamps-185;
                }else if(this.loginUser.stamps<225){
                    this.curLevel = "열매4단계";
                    this.stampCnt = this.loginUser.stamps-205;
                }else if(this.loginUser.stamps<250){
                    this.curLevel = "열매5단계";
                    this.remainStamp=25;
                    this.stampCnt = this.loginUser.stamps-225;
                }
            }
            else if(250<= this.loginUser.stamps){
                     this.remainStamp=25;
                if(this.loginUser.stamps<275){
                    this.curLevel = "커피콩1단계";
                    this.stampCnt = this.loginUser.stamps-250;
                }else if(this.loginUser.stamps<300){
                    this.curLevel = "커피콩2단계";
                    this.stampCnt = this.loginUser.stamps-275;
                }else if(this.loginUser.stamps<325){
                    this.curLevel = "커피콩3단계";
                    this.stampCnt = this.loginUser.stamps-300;
                }else if(this.loginUser.stamps<350){
                    this.curLevel = "커피콩4단계";
                    this.stampCnt = this.loginUser.stamps-325;
                }else if(350 <= this.loginUser.stamps){
                    this.curLevel = "커피콩5단계";
                    this.remainStamp=100000;
                    this.stampCnt = this.loginUser.stamps-350;
                }
            }
        },
    }
};
</script>

<style scoped>
#lineStyle{
    height:2px;
    color:green;
    background-color:lightgray;
}
#grayColor{
    background: lightgray;
}
#notice{
    margin-top: 15px;
}
#listgroup{
    padding-top: 9px;
    padding-bottom: 20px;
}
</style>
