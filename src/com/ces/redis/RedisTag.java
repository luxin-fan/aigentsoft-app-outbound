package com.ces.redis;

public interface RedisTag {

    /*
     * 各种与主业务逻辑无关的活动
     */
    public interface Activity {
        public static final String IDFA = "idfa"; // 多盟广告推广链接的点击记录
        public static final String LOGIN_TRY_TIME = "login_try_time"; // 登陆尝试次数
        public static final String LIULIANG_RESENDCARD = "liuliang"; //流量重发次数
        public static final String VALENTINES = "valentines"; // 易信白色情人节

        public static final String CONTACTERS = "contacters"; // 手机联系人中未注册名单
        public static final String PRIORITY = "priority"; // 短信礼包中优先被选中的名单
        public static final String CONTACTERS_INIT_TIME = "contacters_init_time"; // 联系人初始化时间
        public static final String SEND_LIBAO_TIME = "send_libao_time"; // 礼物送出时间
        public static final String SMS_LIBAO_USER = "sms_libao_user_list"; // 被送礼包的用户名单
        public static final String USER_SEND_LIBAO_TIMES = "user_send_libao_times"; // 用户送礼次数
        public static final String ONEDAY_SEND_LIBAO_COUNT = "oneday_send_libao_count"; // 每天送礼人数
        
        public static final String ONE_DAY_LUCK_USER_COUNT = "one_day_luck_user_count"; // 表示一天内已抽中奖品的人数
        public static final String LUCK_USER_LIST = "luck_user_list"; // 中奖名单
        public static final String BACKUP_LUCK_USER_LIST = "backup_luck_user_list"; // 优先中奖名单
    }

}
