async function a() {

    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess + "&info=core_calendar_get_calendar_monthly_view";
        var date = new Date();
        var nowMonth = date.getMonth() + 1;
        var nowYear = date.getFullYear();
        var ajaxList = [];
        var results = [];
        console.log($);
        for (let i = 0; i < 12; i++) {
            ajaxData = [];
            ajaxData.push({
                index: 0,
                methodname: "core_calendar_get_calendar_monthly_view",
                args: { "year": nowYear, "month": nowMonth, "courseid": 1, "categoryid": 0, "includenavigation": false, "mini": true }
            });
            ajaxData = JSON.stringify(ajaxData);
            var order = {
                type: "POST",
                data: ajaxData,
                context: "hoge",
                dataType: "json",
                processData: false,
                async: true,
                contentType: "application/json",
                timeout: 100000
            };
            ajaxList.push($.ajax(url, order).then((data) => (results.push(data)),
                                                 (error) => (console.log(JSON.stringify(error))))
                                                 );
            nowMonth += 1;
            if (nowMonth > 12) {
                nowMonth = 1;
                nowYear += 1;
            }
        }
        await Promise.all(ajaxList).then(() => { console.log("promise success")});
        Android.add(JSON.stringify(results));
    }
    catch (error) {
        console.log(error);
        console.log(error[0]);
        console.log("errorrrrrrrrrrrrrrrrrrrrrrrrrr");
        Android.error(error);
    }
    return;
}
(function () { a(); })();
