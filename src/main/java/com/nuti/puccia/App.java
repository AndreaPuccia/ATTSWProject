package com.nuti.puccia;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.transaction_manager.TransactionManager;
import com.nuti.puccia.transaction_manager.mysql.TransactionManagerMysql;
import com.nuti.puccia.view.swing.ExamReservationsSwingView;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exam Reservations App
 */
@Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

    private static EntityManagerFactory entityManagerFactory = null;
    private static EntityManager entityManager = null;

    public static EntityManager getEntityManager(Map<String, String> settings) {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory("APP", settings);
            entityManager = entityManagerFactory.createEntityManager();
        }
        return entityManager;
    }

    public static void closeConnection() {
        if (entityManagerFactory != null) {
            entityManager.close();
            entityManagerFactory.close();
        }
    }


    @Option(names = {"--mysql-host"}, description = "MySQL host address")
    private String mysqlHost = "localhost";

    @Option(names = {"--mysql-port"}, description = "MySQL host port")
    private int mysqlPort = 3306;

    @Option(names = {"--db-name"}, description = "Database name")
    private String databaseName = "attsw";

    @Option(names = {"--username"}, description = "Username")
    private String username = "root";

    @Option(names = {"--password"}, description = "Password")
    private String password = "";

    public static void main(String[] args) {
        new CommandLine(new App()).execute(args);
    }

    @Override
    public Void call() {
        Map<String, String> settings = new HashMap<>();
        settings.put("javax.persistence.jdbc.url", "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + databaseName);
        settings.put("javax.persistence.jdbc.user", username);
        settings.put("javax.persistence.jdbc.password", password);

        EntityManager em = App.getEntityManager(settings);

        EventQueue.invokeLater(() -> {
            try {
                TransactionManager transactionManager = new TransactionManagerMysql(em);
                ServiceLayer serviceLayer = new ServiceLayer(transactionManager);
                ExamReservationsSwingView view = new ExamReservationsSwingView();
                Controller controller = new Controller(view, serviceLayer);
                view.setController(controller);
                controller.showAllExams();
                controller.showAllStudents();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(App::closeConnection));
        return null;
    }

}
