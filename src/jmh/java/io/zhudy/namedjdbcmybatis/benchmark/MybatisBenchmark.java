package io.zhudy.namedjdbcmybatis.benchmark;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Warmup(iterations=10)
@Measurement(iterations=10)
@Fork(2)
public class MybatisBenchmark extends Base {

    private SqlSessionFactory sessionFactory;

    @Override
    protected void setup0(BenchmarkParams params) throws Exception {
        ClassPathResource mapper = new ClassPathResource("PersonMapper.xml");
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new Resource[]{mapper});
        factoryBean.setTransactionFactory(new JdbcTransactionFactory());

        factoryBean.afterPropertiesSet();
        sessionFactory = factoryBean.getObject();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void insert() throws Exception {
        Person p = new Person();
        p.setFirstName("kevin");
        p.setLastName("zou");
        p.setAge(99);
        p.setGender(0);
        p.setHeight(175);
        p.setWeight(65);
        p.setAddress("Chinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna Shanghaaaaaaaaaaaaaaaaaaaaaaaaaaaai");
        p.setHobby("Coooooooooooooooooooooooooooooooooooooooooooooooooooooooode");
        p.setCreatedTime(Instant.now().toEpochMilli());

        SqlSession session = sessionFactory.openSession();
        session.insert("io.zhudy.namedjdbcmybatis.benchmark.PersonMapper.insert", p);
        session.close();
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void batchInsert() throws Exception {
        Person[] persons = new Person[1000];
        for (int i = 0; i < 1000; i++) {
            Person p = new Person();
            p.setFirstName("kevin");
            p.setLastName("zou");
            p.setAge(99);
            p.setGender(0);
            p.setHeight(175);
            p.setWeight(65);
            p.setAddress("Chinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna Shanghaaaaaaaaaaaaaaaaaaaaaaaaaaaai");
            p.setHobby("Coooooooooooooooooooooooooooooooooooooooooooooooooooooooode");
            p.setCreatedTime(Instant.now().toEpochMilli());
            persons[i] = p;
        }

        SqlSession session = sessionFactory.openSession();
        session.insert("io.zhudy.namedjdbcmybatis.benchmark.PersonMapper.batchInsert", persons);
        session.close();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void queryById() throws Exception {
        SqlSession session = sessionFactory.openSession();
        session.selectOne("io.zhudy.namedjdbcmybatis.benchmark.PersonMapper.queryById", selectId());
        session.close();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void queryByIdForManualMap() throws Exception {
        SqlSession session = sessionFactory.openSession();
        session.selectOne("io.zhudy.namedjdbcmybatis.benchmark.PersonMapper.queryByIdForManualMap", selectId());
        session.close();
    }

    public static void main(String[] args) throws Exception {
//        Options opt = new OptionsBuilder()
//                .include(MybatisBenchmark.class.getSimpleName())
//                .warmupIterations(3)
//                .measurementIterations(10)
//                .threads(Runtime.getRuntime().availableProcessors())
//                .forks(2)
//                .build();
//        new Runner(opt).run();

        MybatisBenchmark benchmark = new MybatisBenchmark();
        benchmark.setup(null);
        benchmark.batchInsert();
    }
}
