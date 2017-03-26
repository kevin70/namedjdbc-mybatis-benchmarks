package io.zhudy.namedjdbcmybatis.benchmark;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import javax.sql.DataSource;
import java.util.Random;

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@State(Scope.Benchmark)
public class Base {

    public static final int DB_ROWS = 1000 * 1000;

    @Param("100")
    public int minPoolSize;
    @Param("100")
    public int maxPoolSize;

    public static DataSource dataSource;

    @Setup(Level.Trial)
    public void setup(BenchmarkParams params) throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/setaria?useUnicode=true&characterEncoding=UTF-8");
        config.setUsername("root");
        config.setConnectionTestQuery("select 1");
        config.setMinimumIdle(minPoolSize);
        config.setMaximumPoolSize(maxPoolSize);
        config.setConnectionTimeout(3000 * 10);
        config.setAutoCommit(true);

        dataSource = new HikariDataSource(config);

        // ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("init.sql"));
        setup0(params);
    }

    protected void setup0(BenchmarkParams params) throws Exception {
    }


    public long selectId() {
        Random r = new Random();
        return r.nextInt(1000 * 1000) + 1;
    }
}
