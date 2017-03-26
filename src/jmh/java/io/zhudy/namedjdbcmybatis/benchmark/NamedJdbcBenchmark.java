package io.zhudy.namedjdbcmybatis.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Warmup(iterations=3)
@Measurement(iterations=10)
@Fork(2)
public class NamedJdbcBenchmark extends Base {

    private static NamedParameterJdbcOperations jdbcOperations;

    private Map<Integer, ParsedSql> cacheSql = new ConcurrentHashMap<>();
    private RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);
    private RowMapper<Person> personRowMapper2 = (rs, rowNum) -> {
        Person p = new Person();
        p.setId(rs.getLong("id"));
        p.setFirstName(rs.getString("firstName"));
        p.setLastName(rs.getString("lastName"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getInt("gender"));
        p.setHeight(rs.getInt("height"));
        p.setWeight(rs.getInt("weight"));
        p.setAddress(rs.getString("address"));
        p.setHobby(rs.getString("hobby"));
        p.setCreatedTime(rs.getLong("createdTime"));
        return p;
    };

    protected void setup0(BenchmarkParams params) throws Exception {
        jdbcOperations = new NamedParameterJdbcTemplate(dataSource) {
            @Override
            protected ParsedSql getParsedSql(String sql) {
                ParsedSql parsedSql = cacheSql.get(sql.hashCode());
                if (parsedSql==null) {
                    parsedSql = NamedParameterUtils.parseSqlStatement(sql);
                    cacheSql.put(sql.hashCode(), parsedSql);
                }
                return parsedSql;
            }
        };
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void insert() throws Exception {
        String sql = "INSERT INTO `t_person` (firstName, lastName, age, gender, height, weight, address, hobby, createdTime)" +
                " VALUES (:firstName, :lastName, :age, :gender, :height, :weight, :address, :hobby, :createdTime)";
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

        KeyHolder key = new GeneratedKeyHolder();
        jdbcOperations.update(sql, new BeanPropertySqlParameterSource(p), key);
        p.setId(key.getKey().longValue());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void batchInsert() throws Exception {
        SqlParameterSource[] sqlParameterSources = new SqlParameterSource[1000];
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
            sqlParameterSources[i] = new BeanPropertySqlParameterSource(p);
        }

        String sql = "INSERT INTO `t_person` (firstName, lastName, age, gender, height, weight, address, hobby, createdTime)" +
                " VALUES (:firstName, :lastName, :age, :gender, :height, :weight, :address, :hobby, :createdTime)";
        jdbcOperations.batchUpdate(sql, sqlParameterSources);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void batchInsert2() throws Exception {
        StringBuilder sb = new StringBuilder(20 * 1000);
        sb.append("INSERT INTO `t_person` (firstName, lastName, age, gender, height, weight, address, hobby, createdTime)");
        sb.append(" VALUES ");
        Object[] args = new Object[1000 * 9];
        int j = 1;
        for (int i = 0; i < 1000; i++) {
            sb.append("(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            if (i < 999) {
                sb.append(",");
            }

            args[(j++) - 1] = "kevin";
            args[(j++) - 1] = "zou";
            args[(j++) - 1] = 99;
            args[(j++) - 1] = 0;
            args[(j++) - 1] = 175;
            args[(j++) - 1] = 65;
            args[(j++) - 1] = "Chinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna Shanghaaaaaaaaaaaaaaaaaaaaaaaaaaaai";
            args[(j++) - 1] = "Coooooooooooooooooooooooooooooooooooooooooooooooooooooooode";
            args[(j++) - 1] = Instant.now().toEpochMilli();
        }

        jdbcOperations.getJdbcOperations().update(sb.toString(), args);
//        jdbcOperations.getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                int j = 1;
//                ps.setString(j++, "kevin");
//                ps.setString(j++, "zou");
//                ps.setInt(j++, 99);
//                ps.setInt(j++, 0);
//                ps.setInt(j++, 175);
//                ps.setInt(j++, 65);
//                ps.setString(j++, "Chinnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna Shanghaaaaaaaaaaaaaaaaaaaaaaaaaaaai");
//                ps.setString(j++, "Coooooooooooooooooooooooooooooooooooooooooooooooooooooooode");
//                ps.setLong(j++, Instant.now().toEpochMilli());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return 1000;
//            }
//        });
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void queryById() throws Exception {
        String sql = "select * from t_person where id=:id";

        jdbcOperations.queryForObject(sql, new MapSqlParameterSource("id", selectId()), personRowMapper);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void queryByIdForManualMap() throws Exception {
        String sql = "select * from t_person where id=:id";

        jdbcOperations.queryForObject(sql, new MapSqlParameterSource("id", selectId()), personRowMapper2);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NamedJdbcBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(10)
                .threads(Runtime.getRuntime().availableProcessors())
                .forks(2)
                .build();
        new Runner(opt).run();
    }

}
