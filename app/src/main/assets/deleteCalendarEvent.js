async function create(id) {
    try {
        var sess = YUI.config["global"]["M"]["cfg"]["sesskey"];
        var url = "https://cclms.kyoto-su.ac.jp/lib/ajax/service.php?sesskey=" + sess;
        var userId = document.getElementById("nav-notification-popover-container").getAttribute("data-userid");
        ajaxData = [];
        ajaxData.push({
            "index": 0, "methodname": "core_calendar_delete_calendar_events", "args":
                {"events":[{"eventid":id,"repeat":false}]}
        });
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
        Android3.delete();
        return;
    } catch (error) {
        console.log(JSON.stringify(error))
        Android3.error(error);
    }
}
async function wait(id) {
    console.log("script executed");
    let flag = true;
    if (typeof jQuery !== 'undefined' && typeof YUI !== 'undefined') {
        create(id);
    }
    else {
        await new Promise(resolve => setTimeout(resolve, 1000));
        Android3.error("アクセス不能");
    }
}