function updateTargetCategory() {
    var targetBoard = document.getElementById("targetBoard").value;
    var targetCategory = document.getElementById("targetCategory");
    // 모든 옵션을 보이도록 초기화
    for (var i = 0; i < targetCategory.options.length; i++) {
        targetCategory.options[i].style.display = "block";
    }
    // 선택한 targetBoard에 따라 필요한 옵션들을 보이기/숨기기
    if (targetBoard === "tradeBoard") {
        targetCategory.options[3].style.display = "none";
        targetCategory.selectedIndex = 0;
    } else if (targetBoard === "commonBoard") {
        targetCategory.options[0].style.display = "none";
        targetCategory.options[1].style.display = "none";
        targetCategory.options[2].style.display = "none";
        targetCategory.selectedIndex = 3;
    }
}

function updateForm(){
    var targetCategoryVal = document.getElementById("targetCategory").value;
    var forms = [
        document.getElementById("sellForm"),
        document.getElementById("buyForm"),
        document.getElementById("emergencyJobForm"),
        document.getElementById("commonForm")
    ];
    // 모든 form 요소를 숨김
    for (var i = 0; i < forms.length; i++) {
        forms[i].style.display = "block";
    }
    if(targetCategoryVal==="sell"){
        forms[1].style.display="none";
        forms[2].style.display="none";
        forms[3].style.display="none";
    }else if(targetCategoryVal==="buy"){
        forms[0].style.display="none";
        forms[2].style.display="none";
        forms[3].style.display="none";
    }else if(targetCategoryVal==="emergencyJob"){
        forms[0].style.display="none";
        forms[1].style.display="none";
        forms[3].style.display="none";
    }else if(targetCategoryVal==="common"){
        forms[0].style.display="none";
        forms[1].style.display="none";
        forms[2].style.display="none";
    }

}
function updateTargetBoard() {
    updateTargetCategory();
    updateForm();
}

document.getElementById("targetBoard").addEventListener("change", updateTargetBoard);
document.getElementById("targetCategory").addEventListener("change", updateForm);
// 최초 로딩 시 초기 targetBoard 값에 따라 targetCategory 옵션 보이기/숨기기
window.addEventListener("DOMContentLoaded", function() {
    updateTargetBoard();
});

function browseFile(){
    document.getElementById('photo')
}

