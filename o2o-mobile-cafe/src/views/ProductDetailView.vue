<template>
    <div>
        <div id="center"> 상품 평점 </div>
        <b-container class="bv-example-row">
            <b-row  class="text-center">
                <b-col >
                    <img id="pictureSize" :src="require(`@/assets/menu/${product.img}`)">
                </b-col>
                <b-col id="colFontSize" cols="8">
                    <b-list-group>
                        <b-list-group-item>상품명 : {{product.name}}</b-list-group-item>
                        <b-list-group-item>상품단가 : {{ product.price}} 원</b-list-group-item>
                        <b-list-group-item>총주문수량 : {{ this.totalOrderCount }}</b-list-group-item>
                        <b-list-group-item>평가 횟수 : {{co.length}} </b-list-group-item>
                        <b-list-group-item>평균 평점 : {{this.commentRate}}</b-list-group-item>
                    </b-list-group>
                </b-col>
            </b-row>
            <br><br>
            <div>
                <b-button id="rightandmargin" variant="success" v-b-modal.modal-prevent-closing>한줄평 남기기</b-button>
                <b-modal
                    id="modal-prevent-closing"
                    ref="modal"
                    title="한줄 평 남기기"
                    @show="resetModal"
                    @hidden="resetModal"
                    @ok="handleOk"
                    >
                    <form ref="form" @submit.stop.prevent="handleSubmit">
                        <b-form-group label="평점" label-for="name-input" invalid-feedback="평점 is required" :state="nameState">
                            <b-form-input id="name-input" v-model="name" :state="nameState" required></b-form-input>
                        </b-form-group>
                        <b-form-group label="한줄 평" label-for="name2-input" :state="name2State">
                            <b-form-input id="name2-input" v-model="name2" :state="name2State" required></b-form-input>
                        </b-form-group>
                    </form>
                </b-modal>
            </div>
            <br><br><br>
            <div >자신이 남긴 평가만 수정, 삭제할 수 있습니다.</div>
            <table class="table table-striped" > 
                <thead >
                    <tr>
                        <th>사용자 명</th>
                        <th>평점</th>
                        <th>한줄평</th>
                    </tr>
                </thead>
                    <tbody>
                    <tr v-for="(comentin, idx) in co" :key="idx">
                        <td>{{comentin.userId}}</td>
                        <td>{{comentin.rating}}</td>
                        <row>
                        <td>{{comentin.comment}}</td>
                        <b-button class="right" variant="primary" @click="removeComment(comentin.id)" v-show="user2.id == comentin.userId">나의 리뷰 삭제</b-button>                    
                        <b-button class="right" variant="success" @click="modifyComment(comentin.id)" v-show="user2.id == comentin.userId" v-b-modal.modal-prevent-modify >나의 리뷰 수정</b-button> 
                            <b-modal
                                id="modal-prevent-modify"
                                ref="modal"
                                title="나의 리뷰 수정"
                                @show="resetModal2"
                                @hidden="resetModal2"
                                @ok="handleOk2"
                                >
                                    <form ref="form" @submit.stop.prevent="handleSubmit2">
                                        <b-form-group
                                            label="평점"
                                            label-for="name3-input"
                                            invalid-feedback="평점 is required"
                                            :state="nameState3"
                                        >
                                            <b-form-input
                                                id="name3-input"
                                                v-model="name3"
                                                :state="nameState3"
                                                required
                                            ></b-form-input>
                                        </b-form-group>
                                         <b-form-group
                                            label="한줄 평"
                                            label-for="name4-input"
                                            invalid-feedback="한줄평 is required"
                                            :state="nameState4"
                                        >
                                            <b-form-input
                                                id="name4-input"
                                                v-model="name4"
                                                :state="nameState4"
                                                required
                                            ></b-form-input>
                                        </b-form-group>
                                    </form>
                            </b-modal>
                        </row>
                    </tr>
                    </tbody>
            </table> 
      </b-container>
        <MyModal @close="closeModal" v-if="modal">
      <!-- default 슬롯 콘텐츠 -->
      <p>Vue.js Modal Window!</p>
      <div><input v-model="message"></div>
      <!-- /default -->
      <!-- footer 슬롯 콘텐츠 -->
      <template slot="footer">
        <button @click="doSend">제출</button>
      </template>
      <!-- /footer -->
    </MyModal>
    </div>
</template>

<script>
export default {
    name: "product-detail-view", 
    props: {
        idx: {
            type: Number,
            default: 0,
        },
        user: {
            type: String,
            default: "",
        },
    },
    data(){
        return{
            product: {},

            totalOrderCount: 0,
            evaluationCount: 0,
            averageEvaluationScore: 0,
              name3: '',
              nameState3: null,
              submittedNames3: [],
                name4: '',
              nameState4: null,
              submittedNames4: [],
              name: '',
              nameState: null,
              submittedNames: [],
              name2: '',
              name2State: null,
              submittedNames2: [],
              commentsScore: [],
              comments: [],
              user2: {},
              commentCnt: 0,
              commentRate: 0,
              newComments: { 
                userId:'',
                produvtId:'',
                comment:'',
                modifyIdx: -1
            },
        }
    },
    computed: {
        commentCnt2(){
            
            return this.$store.state.productComments
        },
        co(){
            this.getCommentCnt()
            return this.$store.state.productComments
        },
        allDetailS() {
            return this.$store.state.allDetails
        }
        
    },
    watch : {
         allDetailS : function (newVal, oldVal) {
            console.log(`${oldVal} + 에서  + ${newVal} + 로 commentCnt2 변경되었습니다.`)
             let alld = newVal
              let productid = 10 - this.idx 

              var orderSum = 0;
              for(var k = 0; k < alld.length; k ++) {
                 if(alld[k].productId == productid) {
                 orderSum += alld[k].quantity
                 }
        }
        this.totalOrderCount = orderSum
         },
    },
    methods: {
        getCommentCnt() {
                let comments = this.$store.state.productComments;
                let cmlegth = comments.length;

                let commentNums = cmlegth;
                let commentRate1 = 0;
                for(let j = 0; j < cmlegth; j++) {
                    commentRate1 += comments[j].rating;
                }

                if(commentNums == 0) {
                    commentRate1 = 0;
                } else {
                    commentRate1 = commentRate1 /commentNums
                }

                this.comments = comments
                this.commentCnt = commentNums
                this.commentRate = commentRate1.toFixed(2);

        },
        removeComment(id) {
            this.$store.dispatch('removeComment' ,id)
            let productid1 = 10 - this.idx
            this.$store.dispatch('getComments', productid1);
            alert("해당 리뷰가 삭제되었습니다.")
            this.$router.push(`/`);

        },
        modifyComment(id1) {
            this.modifyIdx = id1
            // if (!this.checkFormValidity()) {
            // return
            // }
            // // Push the name to submitted names
            // this.submittedNames.push(this.name)
            // this.submittedNames2.push(this.name2)
            // let commentNew = {
            //     id: id1,
            //     userId: this.user2.id,
            //     productId: this.product.id,
            //     rating: this.name,
            //     comment: this.name2

            // }
            // var commentNew2 = JSON.stringify(commentNew);


            // this.$store.dispatch('modifyComment' , commentNew2)
            // let productid1 = 10 - this.idx
            // this.$store.dispatch('getComments', productid1);
            // alert("해당 리뷰가 수정되었습니다.")
            // this.$router.push(`/`);

        },
      checkFormValidity() {
        const valid = this.$refs.form.checkValidity()
        this.nameState = valid
        return valid
      },
     checkFormValidity2() {
        const valid = this.$refs.form.checkValidity()
        this.nameState3 = valid
        return valid
      },
      resetModal() {
        this.name = ''
        this.nameState = null
        this.name2= ''
        this.name2State = null
      },
      resetModal2() {
        this.name3 = ''
        this.nameState3 = null
        this.name4= ''
        this.nameState4 = null
      },
      handleOk(bvModalEvt) {
        // Prevent modal from closing
        bvModalEvt.preventDefault()
        // Trigger submit handler
        this.handleSubmit()
      },
      handleOk2(bvModalEvt) {
        // Prevent modal from closing
        bvModalEvt.preventDefault()
        // Trigger submit handler
        this.handleSubmit2()
      },
      handleSubmit2() { ////////////////////////// ????
        // Exit when the form isn't valid
        // Push the name to submitted names
        this.submittedNames3.push(this.name3)
        this.submittedNames4.push(this.name4)

        let commentModify = {
            id: this.modifyIdx,
            userId: this.user2.id,
            productId: this.product.id,
            rating: this.name3,
            comment: this.name4

        }

        var commentModify2 = JSON.stringify(commentModify);
        this.$store.dispatch('modifyComments', `${commentModify2}`);
        this.$store.dispatch('getComments', `${commentModify.productId}`);

            alert("해당 리뷰가 수정되었습니다.")
            this.$router.push(`/`);
        // Hide the modal manually
        this.$nextTick(() => {
          this.$bvModal.hide('modal-prevent-modify')
        })
      },
      handleSubmit() {
        // Exit when the form isn't valid
        if (!this.checkFormValidity()) {
          return
        }
        // Push the name to submitted names
        this.submittedNames.push(this.name)
        this.submittedNames2.push(this.name2)
        let commentNew = {
            userId: this.user2.id,
            productId: this.product.id,
            rating: this.name,
            comment: this.name2

        }

        
        // commentNew.userId =`${this.user2.id}`
        // commentNew.productId=`${this.product.id}`
        // commentNew.rating=`${this.name}`
        // commentNew.commment=`${this.name2}`
        // alert(commentNew.userId)
        //  alert(commentNew.productId)
        //           alert(commentNew.rating)
        //                             alert(commentNew.comment)





        var commentNew2 = JSON.stringify(commentNew);
        // var comment3 = JSON.parse(commentNew2)

        this.$store.dispatch('addComments', `${commentNew2}`);
        this.$store.dispatch('getComments', `${commentNew.productId}`);
        
        let cem = this.$store.state.productComments;
        let cemlg = cem.length
        // let thisComment = [];

        // for(let q = 0; q < cemlg; q++) {
        //     let tojC = cem[q]
        //     let jC = JSON.parse(tojC)
        //     thisComment.push(jC)
        // }

        let commentNums = cemlg;
        let commentRate = 0;
        for(let j = 0; j < cemlg; j++) {
            commentRate += cem[j].rating
          
        }

        if(commentNums == 0) {
             commentRate = 0;

        } else {
             commentRate = commentRate /commentNums
        }

        this.commentCnt = commentNums
        this.commnetRate = commentRate

            alert("해당 리뷰가 등록되었습니다.")
            this.$router.push(`/`);

        // Hide the modal manually
        this.$nextTick(() => {
          this.$bvModal.hide('modal-prevent-closing')
        })
      }
    },
    async created() {

        let products = this.$store.getters.getProducts;
        this.product = products[this.idx];
        this.productImg = this.product.img;

        // await this.$store.dispatch('productCntById', this.idx+1);
        // let totalCnt = this.$store.getters.getTotalCnt;
        // this.totalOrderCount = totalCnt;

        this.user2 = this.$store.getters.getLoginUser;



        let productid = 10 - this.idx 
                        console.log("productid");
                console.log(productid);
        this.$store.dispatch('getComments', productid);

        let comments = this.$store.state.productComments;
                                console.log("comments");
                 console.log(comments);


        let cmlegth = this.co.length;
                                        console.log("cmlegth");
                 console.log(cmlegth);

        let commentNums = cmlegth;
        let commentRate1 = 0;
        for(let j = 0; j < cmlegth; j++) {
            commentRate1 += comments[j].rating;
        }

        if(commentNums == 0) {
             commentRate1 = 0;
        } else {
             commentRate1 = commentRate1 /commentNums
        }

        this.comments = comments
        this.commentCnt = commentNums
        this.commentRate = commentRate1.toFixed(2);

        this.$store.dispatch('getAllDetails') 


    }
};
</script>

<style scoped>
#center{
    text-align: center;
    font-size: 45px;
}
#pictureSize{
    width: 270px;
    height: 400px;
}
#colFontSize{
    text-align: center;
    margin-top: 30px;
    font-size: 30px;
}
#rightandmargin{
      float: right;
      margin-bottom: 20px;
}
.right{
    float: right;
}
</style>