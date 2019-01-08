package com.xianglin.act.web.home;

import com.xianglin.act.biz.shared.RedPacketActService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;

/**
 * @author yefei
 * @date 2018-04-24 15:48
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "act/assembly/assembly/src/main/webapp")
@ContextConfiguration({
        "classpath*:/spring/common-*.xml",
        "classpath*:/spring/core-*.xml",
        "classpath*:/spring/biz-*.xml",
        "classpath*:/spring/web-*.xml"
})
public class RedPacketControllerTest {

    @Autowired
    private
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }

    @Test
    public void getDemoidTest1() throws Exception {
        RedPacketActService mock = mock(RedPacketActService.class);
        mock.getSharerQrCode(5199881);

        mockMvc.perform(MockMvcRequestBuilders.post("/act/api/activity/redpacket//selectSharer"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}
