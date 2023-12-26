package jp.co.h_t.servlets;

import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dto.TimeRecordDTO;
import jp.co.h_t.dto.UserDTO;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/record")
public class CreateTimeRecordServlet extends HttpServlet {

    private VelocityEngine velocityEngine;
    private TimeRecordsDAO timeRecordsDAO;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        velocityEngine = (VelocityEngine) context.getAttribute("velocityEngine");
        timeRecordsDAO = (TimeRecordsDAO) context.getAttribute("timeRecordsDAO");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");

        if (user == null) {
            request.getRequestDispatcher("login").forward(request, response);
            return;
        }
        Template template = null;
        try {
            template = velocityEngine.getTemplate("stampingForm.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        VelocityContext context = new VelocityContext();
        context.put("user", user);

        // 現在の日時を取得し、指定のフォーマットで文字列に変換
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String nowString = now.format(formatter);
        context.put("now", nowString);

        template.merge(context, response.getWriter());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // リクエストからJSON形式のデータを取得
        String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject jsonObject = new JSONObject(jsonString);
        // 1. パラメータを取得
        String dateStringPram = jsonObject.getString("date");
        String typeStringPram = jsonObject.getString("type");
        HttpSession session = request.getSession();
        var user = (UserDTO) session.getAttribute("user");

        // 2. パラメータを変換
        LocalDateTime record = LocalDateTime.parse(dateStringPram, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int type = Integer.parseInt(typeStringPram);

        // 3. 打刻情報を追加
        List<TimeRecordDTO> records = timeRecordsDAO.getByUserDate(user.getId(), record);

        boolean isValidRecord = true;
        String errorMessage = "";
        if (type == 0 && records.isEmpty()) { // 退勤の場合、出勤レコードが存在しない場合はエラー
            isValidRecord = false;
            errorMessage = "出勤レコードが存在しないため、退勤打刻ができません。";
        } else {
            for (TimeRecordDTO existingRecord : records) {
                if (existingRecord.getType() == type) {
                    isValidRecord = false;
                    errorMessage = "同じ日で重複する出勤または退勤打刻です。";
                    break;
                }

                if (type == 0 && existingRecord.getRecord().isAfter(record)) {
                    isValidRecord = false;
                    errorMessage = "同じ日で出勤打刻よりも前の時刻の退勤打刻です。";
                    break;
                }

                if (type == 1 && existingRecord.getRecord().isBefore(record)) {
                    isValidRecord = false;
                    errorMessage = "同じ日で退勤打刻よりも後の時刻の出勤打刻です。";
                    break;
                }
            }
        }
        JSONObject resultJson = new JSONObject();
        if (isValidRecord) {
            int newId = timeRecordsDAO.create(record, type, user.getId());


            // JSON形式でレスポンスを返す

            if (newId != -1) {
                // 今登録したデータを取得するために取り直す
                records = timeRecordsDAO.getByUserDate(user.getId(), record);

            } else {
                // エラーメッセージをJSON形式で返す
                resultJson.put("errorMessage", "不正な打刻です。");

            }
        }
        
        resultJson.put("errorMessage", errorMessage);
        // レコードをJSON形式で返す
        JSONArray recordsJsonArray = new JSONArray();
        for (TimeRecordDTO timeRecord : records) {
            JSONObject recordJson = new JSONObject();
            LocalDateTime dateTime = timeRecord.getRecord();
            String typeString = timeRecord.getType() == 1 ? "出勤" : "退勤";
            String timeString = dateTime.getHour() + ":" + String.format("%02d", dateTime.getMinute());

            recordJson.put("type", typeString);
            recordJson.put("time", timeString);
            recordsJsonArray.put(recordJson);
        }

        // 処理結果をJSON形式で返す
        resultJson.put("records", recordsJsonArray);

        // レスポンスヘッダを設定
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // JSON形式でレスポンスを返す
        response.getWriter().write(resultJson.toString());
    }

}
