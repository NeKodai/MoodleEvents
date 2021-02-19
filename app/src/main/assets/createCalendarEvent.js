async function create(jsonSubject) {
    console.log(JSON.stringify(jsonSubject));
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess;
        var userId = document.getElementById("nav-notification-popover-container").getAttribute("data-userid");
        var subject = jsonSubject;
        var result;
        var end = subject["endTime"];
        var endYear = end["year"];
        var endMonth = end["month"] + 1;
        var endDay = end["dayOfMonth"];
        var endHour = end["hourOfDay"];
        var endMinutes = end["minute"];
        ajaxData = [];
        if ("startTime" in subject) {
            var start = subject["startTime"];
            var startYear = start["year"];
            var startMonth = start["month"]+1;
            var startDay = start["dayOfMonth"];
            var startHour = start["hourOfDay"];
            var startMinutes = start["minute"];
            ajaxData.push({
                "index": 0, "methodname": "core_calendar_submit_create_update_form", "args":
                    { "formdata": "id=0&userid=" + userId + "&modulename=&instance=0&visible=1&eventtype=user&sesskey=" + sess + "&_qf__core_calendar_local_event_forms_create=1&mform_showmore_id_general=0&name=" + subject["subjectTitle"] + "&timestart%5Bday%5D=" + startDay + "&timestart%5Bmonth%5D=" + startMonth + "&timestart%5Byear%5D=" + startYear + "&timestart%5Bhour%5D=" + startHour + "&timestart%5Bminute%5D=" + startMinutes + "&description%5Btext%5D=%3Cp%3E" + subject["description"] + "%3C%2Fp%3E&description%5Bformat%5D=1&description%5Bitemid%5D=191669908&duration=1&timedurationuntil%5Bday%5D="+endDay+"&timedurationuntil%5Bmonth%5D="+endMonth+"&timedurationuntil%5Byear%5D="+endYear+"&timedurationuntil%5Bhour%5D="+endHour+"&timedurationuntil%5Bminute%5D="+endMinutes }
            });
        } else {
            ajaxData.push({
                "index": 0, "methodname": "core_calendar_submit_create_update_form", "args":
                    { "formdata": "id=0&userid=" + userId + "&modulename=&instance=0&visible=1&eventtype=user&sesskey=" + sess + "&_qf__core_calendar_local_event_forms_create=1&mform_showmore_id_general=0&name=" + subject["subjectTitle"] + "&timestart%5Bday%5D=" + endDay + "&timestart%5Bmonth%5D=" + endMonth + "&timestart%5Byear%5D=" + endYear + "&timestart%5Bhour%5D=" + endHour + "&timestart%5Bminute%5D=" + endMinutes + "&description%5Btext%5D=%3Cp%3E" + subject["description"] + "%3C%2Fp%3E&description%5Bformat%5D=1&description%5Bitemid%5D=191669908&duration=0" }
            });
        }
        ajaxData = JSON.stringify(ajaxData);
        var order = {
            type: "POST",
            data: ajaxData,
            context: "hoge",
            dataType: "json",
            processData: false,
            async: true,
            contentType: "application/json"
        };
        var process = $.ajax(url, order).then((data) => (result = data)).catch((error)=>(console.log(JSON.stringify(error))));
        await process;
        Android2.create(JSON.stringify(result));
        return;
    } catch (error) {
        console.log(JSON.stringify(error))
        Android2.error(error);
    }
}
async function wait(jsonSubject) {
    console.log("script executed");
    let flag = true;
    if (typeof jQuery !== 'undefined' && typeof YUI !== 'undefined') {
        create(jsonSubject);
    }
    else {
        await new Promise(resolve => setTimeout(resolve, 1000));
        Android2.error("アクセス不能");
    }
}