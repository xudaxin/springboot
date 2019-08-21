package com.neuedu.common;



public class Const {

    //订单状态
    public enum OrderStatusENUM{

        ORDER_CANCELED(0,"已取消"),
        ORDER_UN_PAY(10,"未付款"),
        ORDER_PAYED(20,"已付款"),
        ORDER_SEND(40,"已发货"),
        ORDER_SUCCESS(50,"交易成功"),
        ORDER_ClOSED(60,"交易关闭");
        
        private int code;
        private String desc;
        private OrderStatusENUM(int code,String desc){
            this.code=code;
            this.desc=desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static OrderStatusENUM codeOf(Integer code){
            for(OrderStatusENUM orderStatusENUM:values()){
                if(code==orderStatusENUM.getCode()){
                    return orderStatusENUM;
                }
            }
            return null;
        }
    }

    //商品状态
    public enum ProductStatusENUM{

        PRODUCT_ONSELL(1,"在售"),
        ORDER_UN_PAY(2,"下架"),
        ORDER_PAYED(3,"删除");

        private int code;
        private String desc;
        private ProductStatusENUM(int code,String desc){
            this.code=code;
            this.desc=desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    //支付类型
    public enum PaymentENUM{


        ONLINE(1,"线上支付");

        private int code;
        private String desc;
        private PaymentENUM(int code,String desc){
            this.code=code;
            this.desc=desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static PaymentENUM codeOf(Integer code){
            for(PaymentENUM paymentENUM:values()){
                if(code==paymentENUM.getCode()){
                    return paymentENUM;
                }
            }
            return null;
        }
    }


    public enum CartCheckedEnum{


        PRODUCT_CHECKED(1,"已勾选"),
        PRODUCT_UNCHECKED(0,"未勾选");

        private int code;
        private String desc;
        private CartCheckedEnum(int code,String desc){
            this.code=code;
            this.desc=desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }


}
