
//menu

var expandedImage = "url(/images/leftBlock-top.gif)";
var collapsedImage = "url(/images/leftBlock-off.gif)";

$(document).ready(function(){
    $(".block h2").addClass("expanded");
    $(".block h2").hover(function(){
        $(this).css("cursor", "pointer");
    });
    $(".block h2").click(function(){
        $(this).parent().find(".bottom").toggle("fast");
        if($(this).hasClass("expanded")){
            $(this).removeClass("expanded");
            //$(this).closest(".LeftBlock").addClass("LeftBlockOff").removeClass("LeftBlock");
            $(this).css("background-image", collapsedImage);
            $(this).css("height", "25px");
        } else {
            $(this).addClass("expanded");
            //$(this).closest(".LeftBlockOff").addClass("LeftBlock").removeClass("LeftBlockOff");
            $(this).css("background-image", expandedImage);
            $(this).css("height", "24px"); 
        }
    });
});

