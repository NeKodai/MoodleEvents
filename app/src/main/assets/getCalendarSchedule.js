async function getProcess(ajaxData,url,results,i){
    console.log(i);
    console.log(ajaxData);
    if(i>5){
        console.log("limit exceeded");
        return false;
    }
    var order = {
        type: "POST",
        data: ajaxData,
        context: ajaxData,
        dataType: "json",
        processData: true,
        async: true,
        contentType: "application/json",
        timeout: 100000
    };
    var process = $.ajax(url, order).then((data) => (results.push(data)),
                                         (error) => (getProcess(ajaxData,url,results,i+1)));
    return await process;
}

async function get() {
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess + "&info=core_calendar_get_calendar_monthly_view";
        var date = new Date();
        var nowMonth = date.getMonth() + 1;
        var nowYear = date.getFullYear();
        var ajaxList = [];
        var ajaxDataList = [];
        var results = [];
        for (let i = 0; i < 12; i++) {
            console.log(i);
            var ajaxData = [];
            ajaxData.push({
                index: 0,
                methodname: "core_calendar_get_calendar_monthly_view",
                args: { "year": nowYear, "month": nowMonth, "courseid": 1, "categoryid": 0}
            });
            ajaxData = JSON.stringify(ajaxData);
            ajaxDataList.push(ajaxData);
            var order = {
                type: "POST",
                data: ajaxData,
                context: ajaxData,
                dataType: "json",
                processData: true,
                async: true,
                contentType: "application/json",
                timeout: 100000
            };
            var process = $.ajax(url, order).then((data) => (results.push(data)),
                                                 function(error){
                                                 console.log("e"+i);
                                                 getProcess(ajaxDataList[i],url,results,0);
                                                 });
            ajaxList.push(process);
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
        console.log(JSON.stringify(error));
        console.log("error");
        Android.error(error);
    }
    return;
}
async function wait (){
    console.log("script executed");
    let flag = true;
    for(let i =0; i<5;i++){
        await new Promise(resolve => setTimeout(resolve,1000));
        if(typeof jQuery !== 'undefined' && typeof YUI !=='undefined'){
             get();
             flag = false;
             break;
         }
         console.log("error load "+i);
     }
    if(flag){
        Android.error("アクセス不能");
     }
}(function () { wait(); })();


