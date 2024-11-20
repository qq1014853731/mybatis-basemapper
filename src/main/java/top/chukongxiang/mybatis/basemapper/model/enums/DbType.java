package top.chukongxiang.mybatis.basemapper.model.enums;

public enum DbType {
    jtds,
    hsql,
    db2,
    postgresql,

    sqlserver,
    oracle,
    mysql,
    mariadb,
    derby,

    hive,
    h2,
    dm, // dm.jdbc.driver.DmDriver
    kingbase,
    gbase,

    oceanbase,
    informix,
    odps,
    teradata,
    phoenix,

    edb,
    kylin, // org.apache.kylin.jdbc.Driver
    sqlite,
    ads,
    presto,

    elastic_search, // com.alibaba.xdriver.elastic.jdbc.ElasticDriver
    hbase,
    drds,

    clickhouse,
    blink,
    antspark,
    oceanbase_oracle,
    polardb,

    ali_oracle,
    mock,
    sybase,
    highgo,
    /**
     * 非常成熟的开源mpp数据库
     */
    greenplum,
    /**
     * 华为的mpp数据库
     */
    gaussdb,

    trino,

    oscar,

    tidb,

    tydb,

    starrocks,

    ingres,
    cloudscape,
    timesten,
    as400,
    sapdb,
    kdb,
    log4jdbc,
    xugu,
    firebirdsql,
    JSQLConnect,
    JTurbo,
    interbase,
    pointbase,
    edbc,
    mimer
    ;

}
