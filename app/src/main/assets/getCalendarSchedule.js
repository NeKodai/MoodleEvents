async function getProcess(ajaxData,url,results,errorIndex,i){
    console.log(i);
    console.log(ajaxData);
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
    const process = new Promise((resolve,reject) => {
                            $.ajax(url, order).then(function(data){
                                results.push(data);
                                resolve(1);
                             },
                             function(error){
                                 console.log("e"+i);
                                 errorIndex.push(i);
                                 resolve(new Error());
                             });});
    return process;
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
        var errorIndex = [];
        for (let i = 0; i < 12; i++) {
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
            const process = new Promise((resolve,reject) => {
                                    $.ajax(url, order).then(function(data){
                                        results.push(data);
                                        resolve(1);
                                     },
                                     function(error){
                                         console.log("e"+i);
                                         errorIndex.push(i);
                                         resolve(new Error());
                                     });});
            ajaxList.push(process);
            nowMonth += 1;
            if (nowMonth > 12) {
                nowMonth = 1;
                nowYear += 1;
            }
        }
        for(let i =0;i<5;i++){
            await Promise.all(ajaxList).then(function(error){
                console.log("promise done");
                ajaxList.splice(0);
                console.log(error);
                if(error){
                    console.log(errorIndex);
                    errorIndex.forEach(index => {ajaxList.push(getProcess(ajaxDataList[index],url,results,errorIndex,index));});
                    console.log(ajaxList);
                    errorIndex.splice(0);
                }
            });
            if(!ajaxList.length){
                break;
            }
        }
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
    await new Promise(resolve => setTimeout(resolve,1000));
    if(typeof jQuery !== 'undefined' && typeof YUI !=='undefined'){
         get();
     }
    else{
        Android.error("アクセス不能");
     }
}(function () { wait(); })();


