package org.dog.message.zookeeper.util;

import org.dog.core.entry.DogTcc;
import org.dog.core.entry.DogCall;

public class PathHelper {

    public static final String MONITOR = "MONITOR";

    public static final String NODES = "NODES";

    private static final String seperator = "/";

    private final String zookeepWorkPath;

    private final String applicationName;

    public  String lockerPath(String lockkey){

        return linkPath(zookeepWorkPath, lockkey);
    }

    public PathHelper(String zookeepWorkPath, String applicationName) {

        this.zookeepWorkPath = zookeepWorkPath;

        this.applicationName = applicationName;
    }

    private static String  normalPath(String str) {


        while (str.startsWith(seperator)) {

            str = str.substring(1, str.length());

        }

        while (str.endsWith(seperator)) {

            str = str.substring(0, str.length() - 1);

        }

        return seperator + str;

    }

    public static  String linkPath(String parent, String child) {

        parent = normalPath(parent);

        child = normalPath(child);

        if (parent.equals(seperator)) {

            return child;

        } else {

            return parent + child;
        }
    }


    public String zookeeperWorkPath() {
        return normalPath(zookeepWorkPath);
    }

    public String applicationPath() {

        return linkPath(zookeepWorkPath, applicationName);

    }

    public String tccNamePath(DogTcc transaction) {

        String applicationContent = linkPath(zookeepWorkPath, transaction.getApplication());

        return linkPath(applicationContent, transaction.getName());

    }

    public String tccKeyPath(DogTcc transaction) {

        String transactionsContent = tccNamePath(transaction);

        return linkPath(transactionsContent, transaction.getKey());

    }

    public String tccNodesPath(DogTcc transaction) {

        String transactionContent = tccKeyPath(transaction);

        return linkPath(transactionContent, NODES);

    }

    public String tccMonitorContent(DogTcc transaction) {

        String transactionContent = tccKeyPath(transaction);

        return linkPath(transactionContent, MONITOR);

    }

    public String subApplicationPath(DogTcc transaction, String applicationName){

          String nodesContent = tccNodesPath(transaction);

        return linkPath(nodesContent, applicationName);

    }

    public String callPath(DogTcc transaction, String applicationName, DogCall server){

        String subApplicationContent = subApplicationPath(transaction,applicationName);

        return linkPath(subApplicationContent, server.getName());

    }

    public String callMonitorPath(DogTcc transaction, String applicationName, DogCall server) {

        String subTranContent = callPath(transaction,applicationName,server);

        return  linkPath(subTranContent, MONITOR);


    }


}
