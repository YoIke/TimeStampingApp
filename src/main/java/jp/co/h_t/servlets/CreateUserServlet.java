package jp.co.h_t.servlets;

import jp.co.h_t.dao.UsersDAO;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ユーザー作成処理を行うサーブレットクラス。
 */
@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {
    private VelocityEngine velocityEngine;
    private UsersDAO usersDAO;

    /**
     * サーブレットの初期化処理。VelocityEngineとUsersDAOオブジェクトを取得します。
     */
    @Override
    public void init() {
        ServletContext context = getServletContext();
        velocityEngine = (VelocityEngine) context.getAttribute("velocityEngine");
        usersDAO = (UsersDAO) context.getAttribute("usersDAO");
    }

    /**
     * GETリクエストを処理し、ユーザー作成フォームを表示します。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Template template = null;
        try {
            template = velocityEngine.getTemplate("createUserForm.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        VelocityContext context = new VelocityContext();
        template.merge(context, response.getWriter());
    }

    /**
     * POSTリクエストを処理し、ユーザー作成処理を実行し、結果を表示します。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ユーザー作成フォームから送信されたデータを取得
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String realname = request.getParameter("realname");

        // ユーザー作成処理を実行し、結果を取得
        int result = usersDAO.create(username, password, realname);

        // 結果表示用のテンプレートを取得
        Template template = null;
        try {
            template = velocityEngine.getTemplate("createUserResult.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 結果表示用のコンテキストに結果を格納し、テンプレートにマージ
        VelocityContext context = new VelocityContext();
        context.put("result", result);
        template.merge(context, response.getWriter());
    }
}
