package com.nxquant.exchange.base.service;

import com.nxquant.exchange.base.entity.UserBean;
import com.nxquant.exchange.base.service.event.UserRegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 用户注册方法
     * @param user
     */
    public void register(UserBean user)
    {
        //../省略其他逻辑

        //发布UserRegisterEvent事件
        applicationContext.publishEvent(new UserRegisterEvent(this,user));
    }

    /**
     *  方式一:@EventListener，无序的(AnnotationRegisterListener, RegisterUserEmailListener)
     */
    @Component
    public class AnnotationRegisterListener {

        /**
         * 注册监听实现方法
         * @param userRegisterEvent 用户注册事件
         */
        @EventListener
        public void register(UserRegisterEvent userRegisterEvent)
        {
            //获取注册用户对象
            UserBean user = userRegisterEvent.getUser();
            //省略逻辑

            //输出注册用户信息
            logger.info("@EventListener---注册信息，用户名：{}，密码：{}", user.getName(), user.getPassword());
        }
    }

    @Component
    public class RegisterUserEmailListener
    {
        /**
         * 发送邮件监听实现
         * @param userRegisterEvent 用户注册事件
         */
        @EventListener
        public void sendMail(UserRegisterEvent userRegisterEvent)
        {
            logger.info("@EventListener---用户注册成功，发送邮件。");
        }
    }

    /**
     * 方式二:继承ApplicationListener
     */
    @Component
    public class RegisterListener implements ApplicationListener<ApplicationEvent>
    {
        /**
         * 实现监听
         * @param applicationEvent
         */
        @Override
        public void onApplicationEvent(ApplicationEvent applicationEvent) {
            //获取注册用户对象
            if(applicationEvent instanceof  UserRegisterEvent) {
                UserBean user = ((UserRegisterEvent) applicationEvent).getUser();
                //省略逻辑

                //输出注册用户信息
                logger.info("继承---注册信息，用户名：{}，密码：{}", user.getName(), user.getPassword());
            }
        }
    }


    /**
     * 方式三:继承SmartApplicationListener，有序监听
     */
    @Component
    public class UserRegisterListener implements SmartApplicationListener
    {
        /**
         *  该方法返回true&supportsSourceType同样返回true时，才会调用该监听内的onApplicationEvent方法
         * @param aClass 接收到的监听事件类型
         * @return
         */
        @Override
        public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
            //只有UserRegisterEvent监听类型才会执行下面逻辑
            return aClass == UserRegisterEvent.class;
        }

        /**
         *  该方法返回true&supportsEventType同样返回true时，才会调用该监听内的onApplicationEvent方法
         * @param aClass
         * @return
         */
        @Override
        public boolean supportsSourceType(Class<?> aClass) {
            //只有在UserService内发布的UserRegisterEvent事件时才会执行下面逻辑
            return aClass == UserService.class;
        }

        /**
         *  supportsEventType & supportsSourceType 两个方法返回true时调用该方法执行业务逻辑
         * @param applicationEvent 具体监听实例，这里是UserRegisterEvent
         */
        @Override
        public void onApplicationEvent(ApplicationEvent applicationEvent) {

            //转换事件类型
            UserRegisterEvent userRegisterEvent = (UserRegisterEvent) applicationEvent;
            //获取注册用户对象信息
            UserBean user = userRegisterEvent.getUser();
            //省略逻辑
            logger.info("有序监听---注册信息，用户名：{}，密码：{}", user.getName(), user.getPassword());
        }

        /**
         * 同步情况下监听执行的顺序
         * @return
         */
        @Override
        public int getOrder() {
            return 0;
        }
    }


    @Component
    public class UserRegisterSendMailListener implements SmartApplicationListener {
        /**
         * 该方法返回true&supportsSourceType同样返回true时，才会调用该监听内的onApplicationEvent方法
         *
         * @param aClass 接收到的监听事件类型
         * @return
         */
        @Override
        public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
            //只有UserRegisterEvent监听类型才会执行下面逻辑
            return aClass == UserRegisterEvent.class;
        }

        /**
         * 该方法返回true&supportsSourceType同样返回true时，才会调用该监听内的onApplicationEvent方法
         *
         * @param aClass
         * @return
         */
        @Override
        public boolean supportsSourceType(Class<?> aClass) {
            //只有在UserService内发布的UserRegisterEvent事件时才会执行下面逻辑
            return aClass == UserService.class;
        }

        /**
         * supportsEventType & supportsSourceType 两个方法返回true时调用该方法执行业务逻辑
         *
         * @param applicationEvent 具体监听实例，这里是UserRegisterEvent
         */
        @Override
        public void onApplicationEvent(ApplicationEvent applicationEvent) {
            //转换事件类型
            UserRegisterEvent userRegisterEvent = (UserRegisterEvent) applicationEvent;
            //获取注册用户对象信息
            UserBean user = userRegisterEvent.getUser();
            logger.info("有序监听---用户：{}，注册成功，发送邮件通知。", user.getName());
        }

        /**
         * 同步情况下监听执行的顺序
         *
         * @return
         */
        @Override
        public int getOrder() {
            return 1;
        }
    }


    @Async
    public String printAsync(){
        logger.info("线程名称：{} be ready to read data!", Thread.currentThread().getName());
        try {
            Thread.sleep(1000 * 1);
            logger.info("--------------------->>>无返回值延迟3秒：");
        } catch (InterruptedException e) {
            logger.error("printAsync interrupted", e);
            Thread.currentThread().interrupt();
        }
        return "已进入到异步";
    }

}
