/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package hivemetastore;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.HiveMetaStoreUtils;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.hive.metastore.MetaStoreUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.apache.hadoop.hive.metastore.MetaStoreUtils.getDeserializer;


public class App {


    static String METASTORE_THRIFT_URIS="metastore.thrift.uris";
    static String METASTORE_USE_SSL="metastore.use.SSL";
    static String METASTORE_SSL_TRUSTSTORE_PATH="metastore.truststore.path";
    static String METASTORE_SSL_TRUSTSTORE_PASSWORD="metastore.truststore.password";
    static String METASTORE_CLIENT_AUTH_MODE="metastore.client.auth.mode";
    static String METASTORE_CLIENT_PLAIN_USERNAME="metastore.client.plain.username";
    static String METASTORE_CLIENT_PLAIN_PASSWORD="metastore.client.plain.password";
    static String METASTORE_EXECUTE_SETUGI="metastore.execute.setugi";

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        queryTablesAndColumns();
    }

    static Logger log = LoggerFactory.getLogger(App.class);    // write some code to query tables and columns from Hive Metastore
    public static void queryTablesAndColumns() {

        HiveMetaStoreClient client = null;

        String propPath = "hms.properties";

        Properties props = new Properties();
        try {
           props.load(ClassLoader.getSystemResourceAsStream(propPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Configuration conf = new Configuration();
        conf.set(METASTORE_THRIFT_URIS,props.getProperty(METASTORE_THRIFT_URIS, "thrift://catalog.eu-de.dataengine.cloud.ibm.com:9083"));
        conf.set(METASTORE_USE_SSL,props.getProperty(METASTORE_USE_SSL, "true"));
        conf.set(METASTORE_SSL_TRUSTSTORE_PATH, props.getProperty(METASTORE_SSL_TRUSTSTORE_PATH,"file:///" + System.getProperty("java.home") + "/lib/security/cacerts"));
        conf.set(METASTORE_SSL_TRUSTSTORE_PASSWORD, props.getProperty(METASTORE_SSL_TRUSTSTORE_PASSWORD,"changeit"));
        conf.set(METASTORE_CLIENT_AUTH_MODE, props.getProperty(METASTORE_CLIENT_AUTH_MODE,"PLAIN"));
        conf.set(METASTORE_CLIENT_PLAIN_USERNAME, props.getProperty(METASTORE_CLIENT_PLAIN_USERNAME,"**CHANGEIT**"));
        conf.set(METASTORE_CLIENT_PLAIN_PASSWORD, props.getProperty(METASTORE_CLIENT_PLAIN_PASSWORD,"**CHANGEIT**"));
        conf.set(METASTORE_EXECUTE_SETUGI, props.getProperty(METASTORE_EXECUTE_SETUGI,"false"));


        try {
            client = new HiveMetaStoreClient(conf, null, false);
        } catch (MetaException e) {
            throw new RuntimeException(e);
        }

        try {
            List<String> tableNames = client.getTables("spark", "default", "*");
            for (String tableName : tableNames) {
                log.info("Found table: " + tableName);
                Table hmsTable = null;
                hmsTable = client.getTable("spark", "default", tableName);

                log.info(hmsTable.toString());

                // get serializer
                //String serializationLibrary = hmsTable.getSd().getSerdeInfo().getSerializationLib();
                List<FieldSchema> cols;
                //if (serializationLibrary != null || !serializationLibrary.isEmpty()) {

                    //log.info("Serialization library: " + serializationLibrary);
                    // we only support org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe
                    //Deserializer deserializer = (Deserializer) Class.forName(serializationLibrary).newInstance();
                    //deserializer.initialize(conf, ;
                    //cols = MetaStoreUtils.getFieldsFromDeserializer(hmsTable.getTableName(), deserializer);
                //}
                //else
                cols = hmsTable.getSd().getCols();

                Iterator<FieldSchema> colsIterator = hmsTable.getSd().getColsIterator();

                while (colsIterator.hasNext()) {
                    FieldSchema fieldSchema = colsIterator.next();
                    String columnName = fieldSchema.getName();
                    String dataType = fieldSchema.getType();
                    log.info("Found column: " + columnName);
                    log.info(fieldSchema.toString());
                }

            }
        } catch (TException e) {
            throw new RuntimeException(e);
        //} catch (ClassNotFoundException e) {
        //    throw new RuntimeException(e);
        //} catch (SerDeException e) {
        //    throw new RuntimeException(e);
        //} catch (InstantiationException e) {
        //    throw new RuntimeException(e);
        //} catch (IllegalAccessException e) {
        //    throw new RuntimeException(e);
        }


    }

    public String getGreeting() {
        return "Hello World!";
    }
}
