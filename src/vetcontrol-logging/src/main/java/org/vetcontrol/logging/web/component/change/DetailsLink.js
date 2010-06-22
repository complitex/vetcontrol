if(typeof(logging_DetailsLink) == "undefined"){
    var logging_DetailsLink = {

        //        EXPANDED_CLASS: "change_details_expanded",

        details: function(event){
            var detailsLink = $(event.target);

            detailsLink.closest(".logging_DetailsLink").find(".message").each(function(index, element){
                var message = $(element);
                if(message.css("display") == "none"){
                    message.show();
                } else {
                    message.hide();
                }
            });

            var detailsRow = logging_DetailsLink.getDetailsRow(detailsLink);
            var details = logging_DetailsLink.getDetailsSection(detailsRow);

            var isHidden = logging_DetailsLink.isHidden(detailsRow);
            logging_DetailsLink.log("display : "+detailsRow.css("display")+", is hidden : "+isHidden);

            if(isHidden){
                detailsRow.show();
                //                logging_DetailsLink.switchVisibility(detailsRow);
                details.toggle('drop', {}, 1000);
            } else {
                details.toggle('drop', {}, 1000, function(){
                    //                    logging_DetailsLink.switchVisibility(detailsRow);
                    detailsRow.hide();
                });
            }            
            
            return false;
        },

        isHidden: function(detailsRow){
            return $(detailsRow).is(":hidden");
        //            return !$(detailsRow).hasClass(logging_DetailsLink.EXPANDED_CLASS);
        },

        //        switchVisibility: function(detailsRow){
        //            $(detailsRow).toggleClass(logging_DetailsLink.EXPANDED_CLASS);
        //        },

        getDetailsSection: function(detailsRow){
            return $(detailsRow).find(".logging_DetailsPanel");
        },
        
        getDetailsRow: function(detailsLink){
            return $(detailsLink).closest('tr').next();
        },
        
        isDebugEnabled: false,

        log: function(message){
            if(logging_DetailsLink.isDebugEnabled){
                if(typeof(console) != "undefined"){
                    console.log(message);
                } else {
                    alert(message);
                }
            }
        }
    }

    $(document).ready(function(){
        $(".logging_DetailsLink").each(function(index, detailsLink){
            logging_DetailsLink.getDetailsSection(logging_DetailsLink.getDetailsRow(detailsLink)).hide();
            $(detailsLink).click(logging_DetailsLink.details);
        });
    });
}

