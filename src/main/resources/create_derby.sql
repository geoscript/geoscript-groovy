create procedure HATBOX.SPATIALIZE_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128),
    IN GEOM_COLUMN VARCHAR(128),
    IN GEOM_TYPE VARCHAR(128),
    IN SRID VARCHAR(9),
    IN EXPOSE_PK VARCHAR(5),
    IN MAX_ENTRIES VARCHAR(9)
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.spatializeProc'
    parameter style java
    modifies sql data;

create procedure HATBOX.BUILD_INDEX_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128)
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.buildIndexProc'
    parameter style java
    modifies sql data;

create procedure HATBOX.DE_SPATIALIZE_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128)
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.deSpatializeProc'
    parameter style java
    modifies sql data;

create procedure HATBOX.INS_SPATIAL_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128),
    IN ID BIGINT
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.insSpatial'
    parameter style java
    modifies sql data;

create procedure HATBOX.UPD_SPATIAL_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128),
    IN ID BIGINT
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.updSpatial'
    parameter style java
    modifies sql data;

create procedure HATBOX.DEL_SPATIAL_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128),
    IN ID BIGINT
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.delSpatial'
    parameter style java
    modifies sql data;

create procedure HATBOX.SAVE_ENTRY_PROC (
    IN SPATIAL_SCHEMA VARCHAR(128),
    IN SPATIAL_TABLE VARCHAR(128),
    IN ID BIGINT
)
    language java
    external name 'net.sourceforge.hatbox.jts.Proc.saveEntry'
    parameter style java
    reads sql data;

create function HATBOX.MBR_INTERSECTS_ENV (
    SCHEMA_NAME VARCHAR(128),
    TABLE_NAME VARCHAR(128),
    MINX DOUBLE,
    MAXX DOUBLE,
    MINY DOUBLE,
    MAXY DOUBLE
) returns TABLE (HATBOX_JOIN_ID BIGINT)
LANGUAGE JAVA PARAMETER STYLE DERBY_JDBC_RESULT_SET READS SQL DATA
EXTERNAL NAME 'net.sourceforge.hatbox.jts.Proc.mbrIntersectsEnvFunc';

create function HATBOX.INTERSECTS_WKB (
    SCHEMA_NAME VARCHAR(128),
    TABLE_NAME VARCHAR(128),
    WKB VARCHAR(32672) FOR BIT DATA
) returns TABLE (HATBOX_JOIN_ID BIGINT)
LANGUAGE JAVA PARAMETER STYLE DERBY_JDBC_RESULT_SET READS SQL DATA
EXTERNAL NAME 'net.sourceforge.hatbox.jts.Proc.queryIntersectsWkbFunc';

create function HATBOX.INTERSECTS_WKT (
    SCHEMA_NAME VARCHAR(128),
    TABLE_NAME VARCHAR(128),
    WKT VARCHAR(32672)
) returns TABLE (HATBOX_JOIN_ID BIGINT)
LANGUAGE JAVA PARAMETER STYLE DERBY_JDBC_RESULT_SET READS SQL DATA
EXTERNAL NAME 'net.sourceforge.hatbox.jts.Proc.queryIntersectsWktFunc';

create function HATBOX.QUERY_WITH_PREDICATE_WKB (
    SCHEMA_NAME VARCHAR(128),
    TABLE_NAME VARCHAR(128),
    PREDICATE VARCHAR(128),
    WKB VARCHAR(32672) FOR BIT DATA
) returns TABLE (HATBOX_JOIN_ID BIGINT)
LANGUAGE JAVA PARAMETER STYLE DERBY_JDBC_RESULT_SET READS SQL DATA
EXTERNAL NAME 'net.sourceforge.hatbox.jts.Proc.queryWithPredicateWkbFunc';

create function HATBOX.QUERY_WITH_PREDICATE_WKT (
    SCHEMA_NAME VARCHAR(128),
    TABLE_NAME VARCHAR(128),
    PREDICATE VARCHAR(128),
    WKT VARCHAR(32672)
) returns TABLE (HATBOX_JOIN_ID BIGINT)
LANGUAGE JAVA PARAMETER STYLE DERBY_JDBC_RESULT_SET READS SQL DATA
EXTERNAL NAME 'net.sourceforge.hatbox.jts.Proc.queryWithPredicateWktFunc';
