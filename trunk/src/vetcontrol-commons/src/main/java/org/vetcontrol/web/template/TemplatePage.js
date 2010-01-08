
//menus
var expandedMenuClassName = "expanded";
var collapsedMenuClassName = "collapsed";

menuLoad = function(){
    $(".block h2 div").hover(function(){
        $(this).addClass("hover");
    }, function(){
        $(this).removeClass("hover");
    });
    $(".block h2").click(function(){
        $(this).parent().find(".bottom").toggle("fast");
        if($(this).hasClass(expandedMenuClassName)){
            $(this).removeClass(expandedMenuClassName).addClass(collapsedMenuClassName);
        //            $(this).closest(".LeftBlock").addClass("LeftBlockOff").removeClass("LeftBlock");
        } else {
            $(this).removeClass(collapsedMenuClassName).addClass(expandedMenuClassName);
        //            $(this).closest(".LeftBlockOff").addClass("LeftBlock").removeClass("LeftBlockOff");
        }
    });
};

//main information panel and content panel

var expandedContentClassName = "ContentExpanded";
var collapsedContentClassName = "ContentCollapsed";
var expandedMainMenuClassName = "expanded";
var collapsedMainMenuClassName = "collapsed";

mainPanelLoad = function(){
    $("#TopPanel #ButtonMain").addClass(expandedMainMenuClassName);
    $("#TopPanel #ButtonMain div").hover(function(){
        $(this).addClass("hover");
    }, function(){
        $(this).removeClass("hover");
    })
    $("#TopPanel #ButtonMain").click(function(){
        $("#LeftPanel").toggle("fast");
        if($(this).hasClass(expandedMainMenuClassName)){
            $(this).removeClass(expandedMainMenuClassName).addClass(collapsedMainMenuClassName);
            $("#Content").removeClass(collapsedContentClassName).addClass(expandedContentClassName);
        } else {
            $(this).addClass(expandedMainMenuClassName).removeClass(collapsedMainMenuClassName);
            $("#Content").removeClass(expandedContentClassName).addClass(collapsedContentClassName);
        }
    });
};

$(document).ready(function(){
    menuLoad();
    mainPanelLoad();
});