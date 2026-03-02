package com.nxquant.exchange.match.controller;

import com.nxquant.exchange.match.core.MainWorker;
import com.nxquant.exchange.match.core.OrderBookManager;
import com.nxquant.exchange.match.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author shilf
 * Match接受HTTP消息接口
 */
@RestController
public class MatchController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MainWorker mainWorker;

    @GetMapping(value = "/stop")
    public String stopMainWork(HttpServletRequest request) {
        String rspStr;
        if(mainWorker!=null){
            mainWorker.setMainWorkerStop(true);
            rspStr = "ok";
        }else {
            rspStr = "error";
        }
        return rspStr;
    }


    @GetMapping(value = "/exportOrderBook")
    public String exportOrderBook(@RequestParam(value = "symbol") String symbol,
                                  HttpServletRequest request) {
        return "";
    }

    @GetMapping(value = "/insertOrder")
    public String insertOrder(HttpServletRequest request) {
        Order order = new Order();
        mainWorker.getWorkContext().getMatchService().insertOrder(order, false);
        return "";
    }
}
