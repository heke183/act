package com.xianglin.test.bean.cpoy;

import com.xianglin.test.bean.cpoy.model.User;
import com.xianglin.test.bean.cpoy.model.UserVo;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

/**
 * @author yefei
 * @date 2018-03-29 12:46
 */
public class BeanUtilsTest {

    @Test
    public void beanCopySpring() throws Exception {
        User user = new User();
        user.setAge(1);
        user.setName("haha");

        UserVo u = new UserVo();
        BeanUtils.copyProperties(u, user);

        System.out.println(u);
    }

    @Test
    public void beanCopyJavassist() {
        User user = new User();
        user.setAge(1);
        user.setName("haha");

        UserVo u = new UserVo();
        Beans.copyProperties(u, user);
        System.out.println(u);
    }
}
