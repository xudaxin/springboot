package com.neuedu.schedule;

import com.neuedu.service.impl.OrderServicelmpl;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CloseOrderSchedule {

    @Autowired
    OrderServicelmpl orderServicelmpl;

    @Value("${order.close.timeout}")
    private int orderTimeout;

    @Scheduled(cron = "*/1 * * * * *")
    public void closeOrder(){

        Date closeOrderTime=DateUtils.addHours(new Date(),-orderTimeout);
        orderServicelmpl.closeOrder(com.neuedu.utils.DateUtils.dateToStr(closeOrderTime));

    }

}
