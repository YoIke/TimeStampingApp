package jp.co.h_t.servlets;

import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dto.TimeRecordDTO;
import jp.co.h_t.dto.UserDTO;
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

@WebServlet("/record/dateclick")
public class DateClickServlet extends HttpServlet {
    private TimeRecordsDAO timeRecordsDAO;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        timeRecordsDAO = (TimeRecordsDAO) context.getAttribute("timeRecordsDAO");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedDate = request.getParameter("selectedDate");
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");

        // selectedDateをLocalDateTimeに変換
        LocalDateTime record = LocalDateTime.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // その日のレコードを取得
        List<TimeRecordDTO> records = timeRecordsDAO.getByUserDate(user.getId(), record);

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

        var errorMessage = "";
        if (recordsJsonArray.length() == 0) {
            errorMessage = "打刻データがありません。";
        }


        // 処理結果をJSON形式で返す
        JSONObject resultJson = new JSONObject();
        resultJson.put("records", recordsJsonArray);
        resultJson.put("errorMessage", errorMessage);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(resultJson.toString());
    }
}

