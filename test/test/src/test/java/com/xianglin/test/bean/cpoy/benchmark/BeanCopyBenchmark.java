package com.xianglin.test.bean.cpoy.benchmark;

import com.xianglin.test.bean.cpoy.Beans;
import com.xianglin.test.bean.cpoy.model.User;
import com.xianglin.test.bean.cpoy.model.UserVo;
import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-03-29 13:41
 */
@Fork(1)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class BeanCopyBenchmark {

    private static final User user = new User();

    static {
        user.setAge(1);
        user.setName("haha");
        user.setBirthday(new Date());
        user.setDesc("asdasasdasdasdasdasdasdasdasdasd");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BeanCopyBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void beanCopySpring() throws Exception {
        UserVo u = new UserVo();
        BeanUtils.copyProperties(u, user);
    }

    @Benchmark
    public void beanCopyJavassist() {
        UserVo u = new UserVo();
        Beans.copyProperties(u, user);
    }
}
