async function create(jsonSubject) {
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess;
        var userId = document.getElementById("nav-notification-popover-container").getAttribute("data-userid");
        var subject = JSON.parse(jsonSubject);
        console.log(subject);
        var end = subject["endTime"];
        var endYear = end["year"];
        var endMonth = end["month"] + 1;
        var endDay = end["dayOfMonth"];
        var endhour = end["hourOfDay"];
        var endMinutes = end["minute"];
        ajaxData = [];
        if ("startTime" in subject) {

        } else {
            ajaxData.push({
                "index": 0, "methodname": "core_calendar_submit_create_update_form", "args":
                    { "formdata": "id=0&userid=" + userId + "&modulename=&instance=0&visible=1&eventtype=user&sesskey=" + sess + "&_qf__core_calendar_local_event_forms_create=1&mform_showmore_id_general=0&name=" + subject["subjectTitle"] + "&timestart%5Bday%5D=" + endDay + "&timestart%5Bmonth%5D=" + endMonth + "&timestart%5Byear%5D=" + endYear + "&timestart%5Bhour%5D=" + endhour + "&timestart%5Bminute%5D=" + endMinutes + "&description%5Btext%5D=%3Cp%3E" + end["description"] + "%3C%2Fp%3E&description%5Bformat%5D=1&description%5Bitemid%5D=191669908&duration=0" }
            });
        }
        console.log(ajaxData);
        ajaxData = JSON.stringify(ajaxData);
        var order = {
            type: "POST",
            data: ajaxData,
            context: "hoge",
            dataType: "json",
            processData: !1,
            async: true,
            contentType: "application/json"
        };
        var process = $.ajax(url, order).done((data) => (console.log("event created")));
        await process;
        //Android.addActionEvents(JSON.stringify(results));
        return;
    } catch (error) {
        //Android.error(error);
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
        Android.error("アクセス不能");
    }
} (function () { wait(jsonSubject); })();