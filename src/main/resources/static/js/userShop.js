$(".tab-button").on("click", function () {
        $(".tab-button").removeClass("select");
        $(this).addClass("select");
        var target = $(this).attr("class").split(" ")[1];
        $(".tab-content").removeClass("show");
        $("." + target).addClass("show");
 });