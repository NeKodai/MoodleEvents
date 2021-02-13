class CalendarProcess {
    constructor(url) {
        this.ajaxData = [];
        this.ajaxList = [];
        this.results = [];
        this.errorIndex = [];
        this.url = url;
    }

    createCalendarProcess(self, orderData, index) {
        this.ajaxData[index] = orderData;
        var order = {
            type: "POST",
            data: orderData,
            context: "hoge",
            dataType: "json",
            processData: true,
            async: true,
            contentType: "application/json",
            timeout: 100000
        };
        var process = new Promise((resolve, reject) => {
            $.ajax(self.url, order).then(function (data) {
                self.results.push(data);
                resolve(1);
            }).catch(
                function (error) {
                    console.log(JSON.stringify(error));
                    console.log("e" + index);
                    self.errorIndex.push(index);
                    resolve(new Error("calendar"));
                });
        });
        this.ajaxList.push(process)
    }

    async processExcecute(self) {
        var errorCount = 0;
        for (let i = 0; i < 10; i++) {
            await Promise.all(this.ajaxList).then(function (error) {
                console.log("promise done");
                self.ajaxList.splice(0);
                console.log(error);
                if (error) {
                    console.log(self.errorIndex);
                    self.errorIndex.forEach(index => { self.createCalendarProcess(self, self.ajaxData[index], index); });
                    console.log(self.ajaxList);
                    errorCount += 1;
                    self.errorIndex.splice(0);
                }
            });
            if (!self.ajaxList.length) {
                break;
            }
            if (errorCount > 10) {
                throw new Error("calendar error");
            }
        }
        return 1;
    }
}
class ActionProcess {

    constructor(url, time) {
        this.results = [];
        this.eventID = 0;
        this.time = time;
        this.url = url;
    }

    async processExcecute(self) {
        var errorCount = 0;
        for (let i = 0; i < 10; i++) {
            var ajaxData = [];
            var result = null;
            ajaxData.push({ "index": 0, "methodname": "core_calendar_get_action_events_by_timesort", "args": { "limitnum": 10, "timesortfrom": this.time, "limittononsuspendedevents": false, "aftereventid": this.eventID } });
            ajaxData = JSON.stringify(ajaxData);
            var order = {
                type: "POST",
                data: ajaxData,
                context: "hoge",
                dataType: "json",
                processData: true,
                async: true,
                contentType: "application/json"
            };

            const process = new Promise((resolve, reject) => {
                $.ajax(self.url, order).then(function (data) {
                    result = data;
                    self.eventID = data[0]["data"]["lastid"];
                    resolve(1);
                }).catch(
                    function (error) {
                        resolve(new Error("action"));
                    });
            });
            await process.then(value => { console.log("action fin") });
            if (result == null) {
                errorCount += 1;
                continue;
            }
            if (errorCount > 10) {
                throw new Error("acton error");
            }
            var events = result[0]["data"]["events"];
            if (events.length == 0) {
                break;
            }
            this.results.push(result);
        }
        console.log(JSON.stringify(this.results));
        return 1
    }

}
async function get() {
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess + "&info=core_calendar_get_calendar_monthly_view";
        var date = new Date();
        var nowMonth = date.getMonth() + 1;
        var nowYear = date.getFullYear();
        var time = new Date().getTime() / 1000 | 0; // |0は小数点切り捨て用
        var ajaxClass = new CalendarProcess(url);
        var actionClass = new ActionProcess(url, 1609426800);
        for (let i = 0; i < 12; i++) {
            var ajaxData = [];
            ajaxData.push({
                index: 0,
                methodname: "core_calendar_get_calendar_monthly_view",
                args: { "year": nowYear, "month": nowMonth, "courseid": 1, "categoryid": 0 }
            });
            ajaxData = JSON.stringify(ajaxData);
            ajaxClass.createCalendarProcess(ajaxClass, ajaxData, i);
            nowMonth += 1;
            if (nowMonth > 12) {
                nowMonth = 1;
                nowYear += 1;
            }
        }
        await Promise.all([ajaxClass.processExcecute(ajaxClass), actionClass.processExcecute(actionClass)])
            .then(console.log("promise end"))
            .catch(error => {
                console.log(error);
                Android.error(error);
                return;
            }
            );
        console.log(JSON.stringify(ajaxClass.results));
        console.log(JSON.stringify(actionClass.results));
        console.log(ajaxClass.results.length);
        console.log(actionClass.results.length);
        //Android.add(JSON.stringify(ajaxClass.results));
    }
    catch (error) {
        console.log(JSON.stringify(error));
        console.log("error");
        Android.error(error);
    }
    return;
}
async function wait() {
    console.log("script executed");
    let flag = true;
    if (typeof jQuery !== 'undefined' && typeof YUI !== 'undefined') {
        get();
    }
    else {
        await new Promise(resolve => setTimeout(resolve, 1000));
        Android.error("アクセス不能");
    }
} (function () { wait(); })();
