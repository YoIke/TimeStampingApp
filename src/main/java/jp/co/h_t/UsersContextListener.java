package jp.co.h_t;

import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dao.UsersDAO;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ウェブアプリケーションのコンテキストリスナー。
 * サーブレットコンテキストが初期化されたり破棄されたりする際に呼び出されます。
 */
@WebListener
public class UsersContextListener implements ServletContextListener {

    /**
     * サーブレットコンテキストが初期化される際に呼び出されます。
     * このメソッドでは、VelocityEngineとUsersDAOの初期化を行います。
     *
     * @param sce サーブレットコンテキストイベント
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // PostgreSQLドライバーのロード
            Class.forName("org.postgresql.Driver");
            ServletContext context = sce.getServletContext();

            // VelocityEngineインスタンスの初期化
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty("resource.loader", "class");
            velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");
            velocityEngine.setProperty("input.encoding", "UTF-8");
            velocityEngine.init();

            // サーブレットコンテキストにVelocityEngineインスタンスを格納
            context.setAttribute("velocityEngine", velocityEngine);

            String dbUrl = context.getInitParameter("dbUrl");
            String dbUsername = context.getInitParameter("dbUsername");
            String dbPassword = context.getInitParameter("dbPassword");
            // DAOインスタンスの生成
            UsersDAO usersDAO = new UsersDAO(dbUrl, dbUsername, dbPassword);
            TimeRecordsDAO timeRecordsDAO = new TimeRecordsDAO(dbUrl, dbUsername, dbPassword);

            // サーブレットコンテキストにUsersDAOインスタンスを格納
            context.setAttribute("usersDAO", usersDAO);
            context.setAttribute("timeRecordsDAO", timeRecordsDAO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * サーブレットコンテキストが破棄される際に呼び出されます。
     *
     * @param sce サーブレットコンテキストイベント
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
