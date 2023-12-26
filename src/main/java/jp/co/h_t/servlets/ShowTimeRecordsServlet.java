package jp.co.h_t.servlets;

import jp.co.h_t.GroupedTimeRecord;
import jp.co.h_t.dao.TimeRecordsDAO;
import jp.co.h_t.dto.TimeRecordDTO;
import jp.co.h_t.dto.UserDTO;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/show")
public class ShowTimeRecordsServlet extends HttpServlet {
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
            request.getRequestDispatcher("/login").forward(request, response);
            return;
        }

        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");
        int year = yearParam != null ? Integer.parseInt(yearParam) : LocalDate.now().getYear();
        int month = monthParam != null ? Integer.parseInt(monthParam) : LocalDate.now().getMonthValue();
        LocalDateTime yearMonth = LocalDateTime.of(year, month, 1, 0, 0);

        List<TimeRecordDTO> records = timeRecordsDAO.getByUserMonth(user.getId(), yearMonth);
        Map<LocalDate, List<TimeRecordDTO>> groupedMap = records.stream()
                .collect(Collectors.groupingBy(record -> record.getRecord().toLocalDate()));

        LocalDate startDate = yearMonth.toLocalDate();
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<GroupedTimeRecord> groupedRecords = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<TimeRecordDTO> dateRecords = groupedMap.get(date);
            GroupedTimeRecord grouped = new GroupedTimeRecord();
            grouped.setDate(date);

            DayOfWeek dayOfWeek = date.getDayOfWeek();
            grouped.setWeekend(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

            if (dateRecords != null) {
                for (TimeRecordDTO record : dateRecords) {
                    if (record.getType() == 1) {
                        grouped.setArrivalTime(record.getRecord().toLocalTime());
                    } else {
                        grouped.setLeaveTime(record.getRecord().toLocalTime());
                    }
                }

                if (grouped.getArrivalTime() != null && grouped.getLeaveTime() != null) {
                    Duration workDuration = Duration.between(grouped.getArrivalTime(), grouped.getLeaveTime());
                    if (workDuration.toHours() >= 5) {
                        workDuration = workDuration.minusHours(1);
                    }
                    long hours = workDuration.toHours();
                    long minutes = workDuration.toMinutes() % 60;
                    grouped.setWorkingHours(String.format("%02d時間%02d分", hours, minutes));
                }
            }


            // エラー判定
            boolean isLastRecordedDatePassed = false;
            LocalDate lastRecordedDate = getLastRecordedDate(records);
            if (lastRecordedDate != null) {
                isLastRecordedDatePassed = !date.isAfter(lastRecordedDate);
            }
            boolean isIncomplete = false;

            if (grouped.getArrivalTime() != null && grouped.getLeaveTime() == null) {
                isIncomplete = true;
            } else if (isLastRecordedDatePassed && grouped.getArrivalTime() == null && grouped.getLeaveTime() == null && !grouped.isWeekend()) {
                isIncomplete = true;
            }
            grouped.setIncomplete(isIncomplete);

            groupedRecords.add(grouped);
        }
        groupedRecords = groupedRecords.stream()
                .sorted(Comparator.comparing(GroupedTimeRecord::getDate))
                .collect(Collectors.toList());

        Template template = null;
        try {
            template = velocityEngine.getTemplate("showTimeRecords.html");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        VelocityContext context = new VelocityContext();
        context.put("user", user);
        context.put("yearMonth", yearMonth);
        context.put("groupedRecords", groupedRecords);

        template.merge(context, response.getWriter());
    }

    private LocalDate getLastRecordedDate(List<TimeRecordDTO> allRecords) {
        if (allRecords == null || allRecords.isEmpty()) {
            return null;
        }

        TimeRecordDTO lastRecord = allRecords.stream()
                .max(Comparator.comparing(TimeRecordDTO::getRecord))
                .orElse(null);

        if (lastRecord == null) {
            return null;
        }

        return lastRecord.getRecord().toLocalDate();
    }

}

