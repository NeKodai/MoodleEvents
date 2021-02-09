setInterval(function (){
    var btn = document.getElementById('gakuninloginbtn');
    if(btn!=null){
        console.log("button clicked");
        btn.click();
        clearInterval();
    }
},1000);