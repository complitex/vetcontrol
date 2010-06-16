if(typeof(logging) == "undefined"){
    logging = {};
}
if(typeof(logging.DetailsLink) == "undefined"){
    logging.DetailsLink = {
        details: function(event){
            var detailsRow = logging.DetailsLink.getDetailsRow($(event.target));
            var details = logging.DetailsLink.getDetailsSection(detailsRow);

            var isHidden = detailsRow.hasClass("change_details_collapsed");
            //            alert("display : "+detailsRow.css("display")+", is hidden : "+isHidden);

            details.slideToggle(600, function(){
                if(isHidden){
                    detailsRow.find("td").css("border-bottom-width", "1px");
                    detailsRow.removeClass("change_details_collapsed");
                    detailsRow.addClass("change_details_expanded");
                } else {
                    detailsRow.find("td").css("border-bottom-width", "0px");
                    detailsRow.removeClass("change_details_expanded");
                    detailsRow.addClass("change_details_collapsed");
                }
            });
            return false;
        },

        hide: function(detailsRow){
            var details = logging.DetailsLink.getDetailsSection(detailsRow);
            details.hide();
        },
        
        getDetailsSection: function(detailsRow){
            return $(detailsRow).find("span");
        },
        
        getDetailsRow: function(detailsLink){
            return $(detailsLink).closest('tr').next();
        }
    }
}

$(document).ready(function(){
//    $(".change_details").each(function(index, detailsRow){
//        logging.DetailsLink.hide(detailsRow);
//    });

    $(".logging_DetailsLink").each(function(index, detailsLink){
        var detailsRow = logging.DetailsLink.getDetailsRow(detailsLink);
        detailsRow.addClass("change_details_collapsed");
        logging.DetailsLink.hide(detailsRow);
    })
    
    $(".logging_DetailsLink").click(logging.DetailsLink.details);
});