async function a() {
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess;
        var time = new Date().getTime() / 1000 | 0;
        var results = [];
        var eventID = 0;
        for (var i = 0; i < 10; i++) {
            var result;
            ajaxData = [];
            ajaxData.push({ "index": 0, "methodname": "core_calendar_get_action_events_by_timesort", "args": { "limitnum": 50, "timesortfrom": time, "limittononsuspendedevents": false, "aftereventid": eventID } });
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
            var process = $.ajax(url, order).done((data) => (result = data));
            await process;
            var events = result[0]["data"]["events"];
            if (events.length == 0) {
                break;
            }
            results.push(result);
            eventID = results[results.length - 1][0]["data"]["lastid"];
        }
        Android.addActionEvents(JSON.stringify(results));
        return;
    } catch (error) {
        Android.error(error);
    }
}
(function () { a(); })();