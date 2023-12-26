$(document).ready(function () {
    const fpInstance = flatpickr("#datetimePicker", {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        inline: true,
        time_24hr: true,
        locale: "ja",
        defaultDate: now,
        onChange: function (selectedDates, dateStr) {
            $("#selectedDate").val(dateStr);

            fetch(`record/dateclick?selectedDate=${dateStr}`)
                .then(response => response.json())
                .then(result => {
                    updateAttendanceTable(result.records);
                    let errorMessage = $("#errorMessage");
                    errorMessage.text(result.errorMessage);
                })
                .catch(error => {
                    console.error("Error fetching records:", error);
                });
            // タイムカードリンクのURLを更新
            const timecardLink = document.getElementById("timecard-link");
            const selectedYear = selectedDates[0].getFullYear();
            const selectedMonth = selectedDates[0].getMonth() + 1; // getMonth()は0から始まるため、1を加える
            timecardLink.href = `show?year=${selectedYear}&month=${selectedMonth}`;
        },
        clickOpens: false,


    });

    // 初回読み込み時にonChangeイベントを強制的に発行
    const selectedDates = [fpInstance.selectedDates[0]];
    const dateStr = fpInstance.formatDate(selectedDates[0], "Y-m-d H:i");
    fpInstance.setDate(selectedDates[0], true);

    $("form").on("submit", async (event) => {
        event.preventDefault();

        const selectedDate = $("#selectedDate").val();
        const selectedType = $("#selectedType").val();
        await sendAttendance(selectedDate, selectedType);
    });
});

function updateAttendanceTable(records) {
    let tableBody = $("#attendanceResult");
    tableBody.empty();

    records.forEach(record => {
        let row = $("<tr></tr>");
        let typeCell = $("<td></td>").text(record.type);
        let timeCell = $("<td></td>").text(record.time);
        row.append(typeCell).append(timeCell);
        tableBody.append(row);
    });
}

function setSelectedType(type) {
    $("#selectedType").val(type);
}

async function sendAttendance(date, type) {
    const url = "record";
    const data = {
        date: date,
        type: type
    };

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        updateAttendanceTable(result.records);
        let errorMessage = $("#errorMessage");
        errorMessage.text(result.errorMessage);

    } catch (error) {
        console.error("Error sending attendance:", error);
    }
}
