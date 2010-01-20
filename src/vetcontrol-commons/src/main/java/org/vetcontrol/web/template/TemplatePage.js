
//menus
var expandedMenuClassName = "expanded";
var collapsedMenuClassName = "collapsed";

var menuCookiePrefix = "MenuPrefix_";

menuLoad = function(){
    $(".block div").each(function(){
        t = getCookie(menuCookiePrefix+$(this).attr("id"))
        //t = 1 - expanded, t = 0 - collapsed
        if(t){
            if(t == 0){
                //collapsed
                element = $(this).find("h2");
                $(element).parent().find(".bottom").toggle("fast");
                $(element).removeClass(expandedMenuClassName).addClass(collapsedMenuClassName);
            } else{
        //expanded
        //nothing to do.
        }
        }
    });

    $(".block h2 div").hover(function(){
        $(this).addClass("hover");
    }, function(){
        $(this).removeClass("hover");
    });
    $(".block h2").click(function(){
        $(this).parent().find(".bottom").toggle("fast");
        if($(this).hasClass(expandedMenuClassName)){
            setCookie(menuCookiePrefix+$(this).parent().attr("id"), "0", "", "/", "");
            $(this).removeClass(expandedMenuClassName).addClass(collapsedMenuClassName);
        //            $(this).closest(".LeftBlock").addClass("LeftBlockOff").removeClass("LeftBlock");
        } else {
            setCookie(menuCookiePrefix+$(this).parent().attr("id"), "1", "", "/", "");
            $(this).removeClass(collapsedMenuClassName).addClass(expandedMenuClassName);
        //            $(this).closest(".LeftBlockOff").addClass("LeftBlock").removeClass("LeftBlockOff");
        }
    });
};

//selected menu item behaviour
var selectedMenuItemCookieName = "SelectedMenuItem";
var selectedMenuItemClassName = "SelectedMenuItem";

selectedMenuLoad = function(){
    $(".block li a").each(function(){
        id = $(this).attr("id");
        if(id){
            $(this).click(function(){
                setCookie(selectedMenuItemCookieName, $(this).attr("id"), "", "/", "");
            });
        }
    });

    selecteMenuItemID = getCookie(selectedMenuItemCookieName);
    if(selecteMenuItemID){
        $("#"+selecteMenuItemID).parent().addClass(selectedMenuItemClassName)
        $("#"+selecteMenuItemID).focus();
        $(".buttonLogin").focus();
    }
};

//main information panel and content panel

var expandedContentClassName = "ContentExpanded";
var collapsedContentClassName = "ContentCollapsed";
var expandedMainMenuClassName = "expanded";
var collapsedMainMenuClassName = "collapsed";

var mainMenuCookieName = "MainMenuCookie";

mainPanelLoad = function(){
    t = getCookie(mainMenuCookieName);
    //t = 1 - expanded, t = 0 - collapsed
    if(t){
        if(t == 0){
            //collapsed
            $("#LeftPanel").toggle("fast");
            $("#TopPanel #ButtonMain").removeClass(expandedMainMenuClassName).addClass(collapsedMainMenuClassName);
            $("#Content").removeClass(collapsedContentClassName).addClass(expandedContentClassName);
        } else{
    //expanded
    //nothing to do.
    }
    }

    $("#TopPanel #ButtonMain div").hover(function(){
        $(this).addClass("hover");
    }, function(){
        $(this).removeClass("hover");
    })
    $("#TopPanel #ButtonMain").click(function(){
        $("#LeftPanel").toggle("fast");
        if($(this).hasClass(expandedMainMenuClassName)){
            setCookie(mainMenuCookieName, "0", "", "/", "");
            $(this).removeClass(expandedMainMenuClassName).addClass(collapsedMainMenuClassName);
            $("#Content").removeClass(collapsedContentClassName).addClass(expandedContentClassName);
        } else {
            setCookie(mainMenuCookieName, "1", "", "/", "");
            $(this).addClass(expandedMainMenuClassName).removeClass(collapsedMainMenuClassName);
            $("#Content").removeClass(expandedContentClassName).addClass(collapsedContentClassName);
        }
    });
};

$(document).ready(function(){
    menuLoad();
    mainPanelLoad();
    selectedMenuLoad();
});


function setCookie( name, value, expires, path, domain)
{
    // set time, it's in milliseconds
    var today = new Date();
    today.setTime( today.getTime() );

    /*
    if the expires variable is set, make the correct
    expires time, the current script below will set
    it for x number of days, to make it for hours,
    delete * 24, for minutes, delete * 60 * 24
    */
    if ( expires )
    {
        expires = expires * 1000 * 60 * 60 * 24;
    }
    var expires_date = new Date( today.getTime() + (expires) );

    var cookie = name + "=" +escape( value )
    + ( ( expires ) ? ";expires=" + expires_date.toGMTString() : "" ) +
    ( ( path ) ? ";path=" + path : "" ) +
    ( ( domain ) ? ";domain=" + domain : "" );
    //    alert("cookie = "+cookie);

    document.cookie = cookie;
}

// this fixes an issue with the old method, ambiguous values
// with this test document.cookie.indexOf( name + "=" );
function getCookie( check_name ) {
    // first we'll split this cookie up into name/value pairs
    // note: document.cookie only returns name=value, not the other components
    var a_all_cookies = document.cookie.split( ';' );
    var a_temp_cookie = '';
    var cookie_name = '';
    var cookie_value = '';
    var b_cookie_found = false; // set boolean t/f default f

    for ( i = 0; i < a_all_cookies.length; i++ )
    {
        // now we'll split apart each name=value pair
        a_temp_cookie = a_all_cookies[i].split( '=' );


        // and trim left/right whitespace while we're at it
        cookie_name = a_temp_cookie[0].replace(/^\s+|\s+$/g, '');

        // if the extracted name matches passed check_name
        if ( cookie_name == check_name )
        {
            b_cookie_found = true;
            // we need to handle case where cookie has no value but exists (no = sign, that is):
            if ( a_temp_cookie.length > 1 )
            {
                cookie_value = unescape( a_temp_cookie[1].replace(/^\s+|\s+$/g, '') );
            }
            // note that in cases where cookie is initialized but no value, null is returned
            return cookie_value;
            break;
        }
        a_temp_cookie = null;
        cookie_name = '';
    }
    if ( !b_cookie_found )
    {
        return null;
    }
}

// this deletes the cookie when called
function deleteCookie(name, path, domain) {
    if ( getCookie( name ) ) document.cookie = name + "=" +
        ( ( path ) ? ";path=" + path : "") +
        ( ( domain ) ? ";domain=" + domain : "" ) +
        ";expires=Thu, 01-Jan-1970 00:00:01 GMT";

}

