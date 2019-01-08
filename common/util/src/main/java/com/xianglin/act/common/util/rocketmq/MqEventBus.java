package com.xianglin.act.common.util.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.common.message.Message;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

/**
 * mq事件分发
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 9:36.
 */
public interface MqEventBus {

    void addMqListener(MqListenerContext listener);

    class MqListenerContext {

        private static final Logger logger = LoggerFactory.getLogger(MqListenerContext.class);

        private Object target;

        private String topic;

        private String tag;

        private MethodAccess methodAccess;

        private int methodIndex;

        private Method method;

        private Class<?>[] parameterTypes;

        //一定会有两个参数
        boolean invoke(Object... args) {

            //int paramsLength = parameterTypes.length;
            //Object[] params;
            //if (args == null && paramsLength == 0) {
            //    params = new Object[0];
            //}
            //int argLength = args.length;

            int length = parameterTypes.length;
            Preconditions.checkArgument(length <= 3);
            Object[] params = new Object[length];
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (Message.class.isAssignableFrom(parameterType)) {
                    params[i] = args[0];
                }
                if (parameterType == ConsumeConcurrentlyContext.class) {
                    params[i] = args[1];
                }
                if (parameterType == String.class) {
                    params[i] = new String(((Message) args[0]).getBody(), StandardCharsets.UTF_8);
                }
            }
            return doInvoke(params);
        }

        private boolean doInvoke(Object[] args) {

            try {
                Object invokeVal = methodAccess.invoke(target, methodIndex, args);
                if (invokeVal == null) {
                    return false;
                }
                if (invokeVal.getClass() == Boolean.class) {
                    return (boolean) invokeVal;
                }
                if (invokeVal.getClass() == Boolean.TYPE) {
                    return (boolean) invokeVal;
                } else {
                    throw new RuntimeException(MessageFormat.format("mq 监听方法执行异常，返回值不是布尔值：[[ {0} ]]：[[ {1} ]]", method.getDeclaringClass().getName(), method.getName()));
                }

            } catch (Exception e) {
                logger.info("===========mq 监听方法执行异常：[[ {} ]]：[[ {} ]]===========", method.getDeclaringClass().getName(), method.getName(), e);
                return false;
            }
        }

        public String getTopic() {

            return topic;
        }

        public void setTopic(String topic) {

            this.topic = topic;
        }

        public String getTag() {

            return tag;
        }

        public void setTag(String tag) {

            this.tag = tag;
        }

        public MethodAccess getMethodAccess() {

            return methodAccess;
        }

        public void setMethodAccess(MethodAccess methodAccess) {

            this.methodAccess = methodAccess;
        }

        public int getMethodIndex() {

            return methodIndex;
        }

        public void setMethodIndex(int methodIndex) {

            this.methodIndex = methodIndex;
        }

        public Method getMethod() {

            return method;
        }

        public void setMethod(Method method) {

            this.method = method;
        }

        public Class<?>[] getParameterTypes() {

            return parameterTypes;
        }

        public void setParameterTypes(Class<?>[] parameterTypes) {

            this.parameterTypes = parameterTypes;
        }

        public Object getTarget() {

            return target;
        }

        public void setTarget(Object target) {

            this.target = target;
        }

        @Override
        public String toString() {

            return "MqListenerContext{" +
                    "topic='" + topic + '\'' +
                    ", tag='" + tag + '\'' +
                    ", method=" + method.getName() + ", class=" + method.getDeclaringClass().getName() +
                    '}';
        }
    }

}
