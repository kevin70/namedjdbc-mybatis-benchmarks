package io.zhudy.namedjdbcmybatis.benchmark;

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
public interface PersonMapper {

    int insert(Person person);

    int[] batchInsert(Person[] persons);

    Person queryById(long id);

    Person queryByIdForManualMap(long id);

}
