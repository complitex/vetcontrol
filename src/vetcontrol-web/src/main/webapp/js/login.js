
var selectedMenuItemClassName = "SelectedMenuItem";
var mainMenuCookieName = "MainMenuCookie";
var menuCookiePrefix = "MenuPrefix_";

$(document).ready(function(){
    deleteCookie(selectedMenuItemClassName, "/", "");
    deleteCookie(mainMenuCookieName, "/", "");
    while(cookieName = getCookieName(menuCookiePrefix)){
        deleteCookie(cookieName, "/", "");
    }
});


