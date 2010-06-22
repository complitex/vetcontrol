if(typeof(logging_DetailsPanel) == "undefined"){
    logging_DetailsPanel = {};
    
    $(document).ready(function(){
        $(".logging_DetailsPanel").each(function(index, element){
            $(element).find(".block:last").removeClass("block");
        });
    });
}