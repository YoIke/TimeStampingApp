package jp.co.h_t.servlets;

import jp.co.h_t.CsvRecord;
import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dto.TimeRecordDTO;
import jp.co.h_t.dto.UserDTO;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/export/csv")
public class ExportCsvServlet extends HttpServlet {
    private TimeRecordsDAO timeRecordsDAO;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        timeRecordsDAO = (TimeRecordsDAO) context.getAttribute("timeRecordsDAO");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");
        int userId = user.getId();
        String realName = user.getRealname();
        int year = Integer.parseInt(request.getParameter("year"));
        int month = Integer.parseInt(request.getParameter("month"));

        // Get attendance data for the given user, year and month
        List<TimeRecordDTO> records = timeRecordsDAO.getByUserMonth(userId, year, month);

        // Generate CSV data
        String csvData = generateCsvData(records, userId, year, month);

        // Set response headers and content
        response.setContentType("text/csv");
        String fileName = year + "年" + month + "月_" + realName + ".csv";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        response.setCharacterEncoding("UTF-8");

        // Write CSV data to the response
        try (PrintWriter writer = response.getWriter()) {
            writer.write(csvData);
        }
    }

    private String generateCsvData(List<TimeRecordDTO> records, int userId, int year, int month) {
        Map<LocalDate, List<TimeRecordDTO>> dailyRecordsMap = new HashMap<>();
        for (TimeRecordDTO record : records) {
            LocalDate date = record.getRecord().toLocalDate();
            dailyRecordsMap.computeIfAbsent(date, k -> new ArrayList<>()).add(record);
        }

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        List<CsvRecord> csvRecords = new ArrayList<>();

        for (LocalDate currentDate = firstDayOfMonth; !currentDate.isAfter(lastDayOfMonth); currentDate = currentDate.plusDays(1)) {
            List<TimeRecordDTO> dailyRecords = dailyRecordsMap.getOrDefault(currentDate, new ArrayList<>());

            String dateString = String.format("%02d/%02d", currentDate.getMonthValue(), currentDate.getDayOfMonth());

            String startTime = "";
            String endTime = "";

            for (TimeRecordDTO record : dailyRecords) {
                if (record.getType() == 1) {
                    startTime = String.format("%02d:%02d", record.getRecord().getHour(), record.getRecord().getMinute());
                } else if (record.getType() == 0) {
                    endTime = String.format("%02d:%02d", record.getRecord().getHour(), record.getRecord().getMinute());
                }
            }

            CsvRecord csvRecord = new CsvRecord(dateString, userId, startTime, endTime);
            csvRecords.add(csvRecord);
        }

        return generateCsvFromCsvRecords(csvRecords);
    }


    private String generateCsvFromCsvRecords(List<CsvRecord> csvRecords) {
        StringBuilder csvData = new StringBuilder();
        csvData.append("月/日,userID,出勤時:分,退勤時:分\n");

        for (CsvRecord csvRecord : csvRecords) {
            csvData.append(csvRecord.getDate()).append(",")
                    .append(csvRecord.getUserId()).append(",")
                    .append(csvRecord.getStartTime()).append(",")
                    .append(csvRecord.getEndTime()).append("\n");
        }

        return csvData.toString();
    }
}

