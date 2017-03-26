package io.zhudy.namedjdbcmybatis.benchmark;

import lombok.Data;

/**
 * @author Kevin Zou <kevinz@weghst.com>
 */
@Data
public class Person {

    private long id;
    private String firstName;
    private String lastName;
    private int age;
    private int gender;
    private int height;
    private int weight;
    private String address;
    private String hobby;
    private long createdTime;
}
