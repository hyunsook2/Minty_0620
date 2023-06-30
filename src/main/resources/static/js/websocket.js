const url = "http://localhost:8087/getchatting";                // 소켓 주소
let stompClient;
let selectedUserOrGrup="10000000000000000";
let newMessages = new Map();



function connectToChat(userName) {                                  // userId
    console.log(userName);
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/ws');// 소켓 연결
    stompClient = Stomp.over(socket);                                   //stomp 연결
    console.log(socket);
    console.log(stompClient);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);

        stompClient.subscribe("/topic/messages/"+userName, function (response) {
            let data = JSON.parse(response.body);

            console.log(data);

            console.log("selectedUserOrGrup = "+selectedUserOrGrup);

            console.log("data.fromLogin = "+data.fromLogin);

            if (selectedUserOrGrup == data.fromLogin) {
                console.log("selectedUserOrGrup === data.fromLogin")

                let messageTemplateHTML = "";
                messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-end mb-4">'+
                    '<div id="child_message" class="msg_cotainer_send">'+data.message+
                    '</div>'+
                    '</div>';
                $('#formMessageBody').append(messageTemplateHTML);
                console.log("append success")
            } else {
                newMessages.set(data.fromLogin, data.message);
                $('#userNameAppender_' + data.fromLogin).append('<span id="newMessage_' + data.fromLogin + '" style="color: red">+1</span>');

                console.log("kebuat")
                let messageTemplateHTML = "";
                messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-end mb-4">'+
                    '<div class="msg_cotainer_send">'+data.message+
                    '</div>'+
                    '</div>';
                console.log("append success")
            }
        },{});

    },onError);
}
function onError() {
    console.log("Disconed from console")
}

window.onload = function() {

    if (localStorage.getItem("userId") === null) {
        window.location.href = "http://localhost:8087";
        return false;
    }

    fetchAll();
    connectToChat(localStorage.getItem("userId"));

  };

function fetchAll() {
    var userId = localStorage.getItem("userId");

    console.log(userId);
    $.get(url + "/fetchAllUsers/"+userId, function (response) {
        console.log(response);

        let users = response;

        let usersTemplateHTML = "";
        for (let i = 0; i < users.length; i++) {
            console.log(users[i]['other'])
                if (userId==users[i]['other']){

                    usersTemplateHTML = usersTemplateHTML + '<li class="active" id="child_message" onclick="formMessageLauch(\'' + users[i]['my'] + '\', \'' + users[i]['myNickName'] + '\', \'user\', \'' + users[i]['title'] + '\', \'' + users[i]['content'] + '\', \'' + users[i]['price'] + '\', \'' + users[i]['thumbnail'] + '\')" data-userid="' + users[i]['myNickName'] + '" data-type="user" data-thumbnail="' + users[i]['thumbnail'] + '" data-title="' + users[i]['title'] + '" data-content="' + users[i]['content'] + '" data-price="' + users[i]['price'] + '">' +
                        '<div class="d-flex bd-highlight">'+
                        '<div class="img_cont">'+
                        '<img src="https://static.turbosquid.com/Preview/001292/481/WV/_D.jpg" class="rounded-circle user_img">'+
                        '</div>'+
                        '<div class="user_info" id="userNameAppender_' + users[i]['myNickName'] + '">'+
                        '<span>'+users[i]['myNickName']+'</span>'+
                        '</div>'+
                        '<div class="img_cont">'+
                        '</div>'+
                        '<div class="img_cont">'+
                        '<img src="https://storage.googleapis.com/reboot-minty-storage/' + users[i]['thumbnail'] + '" class="rounded-circle user_img"/>' +
                        '</div>'+
                        '<div class="user_info" id="userNameAppender_' + users[i]['myNickName'] + '">'+
                        '<span>제목'+users[i]['title']+'</span>'+
                        '<span>가격'+users[i]['price']+'</span>'+
                        '<p>내용'+users[i]['content']+'</p>'+
                        '</div>'+
                        '</div>'+
                        '</li>';
                } else  {

                    usersTemplateHTML = usersTemplateHTML + '<li class="active" id="child_message" onclick="formMessageLauch(\'' + users[i]['other'] + '\', \'' + users[i]['otherNickName'] + '\', \'user\', \'' + users[i]['title'] + '\', \'' + users[i]['content'] + '\', \'' + users[i]['price'] + '\', \'' + users[i]['thumbnail'] + '\')" data-userid="' + users[i]['otherNickName'] + '" data-type="user" data-thumbnail="' + users[i]['thumbnail'] + '" data-title="' + users[i]['title'] + '" data-content="' + users[i]['content'] + '" data-price="' + users[i]['price'] + '">' +
                        '<div class="d-flex bd-highlight">'+
                        '<div class="img_cont">'+
                        '<img src="https://static.turbosquid.com/Preview/001292/481/WV/_D.jpg" class="rounded-circle user_img">'+
                        '</div>'+
                        '<div class="user_info" id="userNameAppender_' + users[i]['otherNickName'] + '">'+
                        '<span>'+users[i]['otherNickName']+'</span>'+
                        '</div>'+
                        '<div class="img_cont">'+
                        '</div>'+
                        '<div class="img_cont">'+
                        '<img src="https://storage.googleapis.com/reboot-minty-storage/' + users[i]['thumbnail'] + '" class="rounded-circle user_img"/>' +
                        '</div>'+
                        '<div class="user_info" id="userNameAppender_' + users[i]['otherNickName'] + '">'+
                        '<span>제목'+users[i]['title']+'</span>'+
                        '<span>가격'+users[i]['price']+'</span>'+
                        '<p>내용'+users[i]['content']+'</p>'+
                        '</div>'+
                        '</div>'+
                        '</li>';
                }
        }
        $('#usersList').html(usersTemplateHTML);

    });

}



function sendMsgUser(from, text) {
    stompClient.send("/app/chat/" + selectedUserOrGrup, {}, JSON.stringify({
        fromLogin: from,
        message: text
    }));


}

function sendImageMessage(imagePath, type) {
    let userId = localStorage.getItem("userId");
    if (type === "user") {
        console.log(imagePath);
        console.log(type);
        console.log(imagePath);

        sendMsgUser(userId, imagePath);
    }

    let messageTemplateHTML = "";
    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-start mb-4">'+
        '<div id="child_message" class="msg_cotainer">'+'<a href="https://storage.googleapis.com/reboot-minty-storage/' + imagePath + '" target="_blank"><img src="https://storage.googleapis.com/reboot-minty-storage/' + imagePath + '" alt="이미지" class="rounded-circle user_img"/></a>' +
        '</div>'+
        '</div>';
    $('#formMessageBody').append(messageTemplateHTML);
    console.log("append success")
}
function sendMessage(type) {
    let username = $('#userName').attr("data-id");
    let message=$('#message-to-send').val();
    var userId = localStorage.getItem("userId");
    selectedUserOrGrup=username;
    console.log("type :"+type)
    if(type==="user"){
        sendMsgUser(userId, message);
    }else if(type==="group"){
        sendMsgGroup(userId, message);
    }


    let messageTemplateHTML = "";
    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-start mb-4">'+
        '<div id="child_message" class="msg_cotainer">'+message+
        '</div>'+
        '</div>';
    $('#formMessageBody').append(messageTemplateHTML);
    console.log("append success")

    document.getElementById("message-to-send").value="";

    scrollToBottom();

}




function formMessageLauch(id,name,type,title,content,price,thumbnail){
    document.getElementById("formProductsBody").innerHTML = "";


    let buttonSend= document.getElementById("buttonSend");
    if(buttonSend!==null){
        buttonSend.parentNode.removeChild(buttonSend);
    }

    let nama=$('#formMessageHeader .user_info').find('span')

    nama.html("Chat With "+name +"제목"+ title +"내용"+ content + "가격"+price + '<img src="https://storage.googleapis.com/reboot-minty-storage/' + thumbnail + '" alt="Thumbnail" class="rounded-circle user_img">');

    nama.attr("data-id",id);
    let isNew = document.getElementById("newMessage_" + id) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + id);
        element.parentNode.removeChild(element);


    }
    let username = $('#userName').attr("data-id");
    selectedUserOrGrup=username;

    let isHistoryMessage = document.getElementById("formMessageBody");
    if(isHistoryMessage!== null && isHistoryMessage.hasChildNodes()){
        isHistoryMessage.innerHTML="";

    }



    var userId = localStorage.getItem("userId");
    if(type==="user"){
        $.get(url + "/listmessage/"+userId+"/"+id, function (response) {
            let messages = response;
            let messageTemplateHTML = "";
            for (let i = 0; i < messages.length; i++) {

                let content = messages[i]["message_text"].endsWith("images")
                    ?  '<a href="https://storage.googleapis.com/reboot-minty-storage/' + messages[i]["message_text"] + '" target="_blank"><img src="https://storage.googleapis.com/reboot-minty-storage/' + messages[i]["message_text"] + '" alt="이미지" class="rounded-circle user_img"/></a>'

                    : messages[i]["message_text"];
                if(messages[i]['message_from']==userId){
                    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-start mb-4">'+
                        '<div id="child_message" class="msg_cotainer">'+content+
                        '</div>'+
                        '</div>';
                }else{
                    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="d-flex justify-content-end mb-4">'+
                        '<div id="child_message" class="msg_cotainer_send">'+content+
                        '</div>'+
                        '</div>';
                }

            }
            $('#formMessageBody').append(messageTemplateHTML);

        });


        $.get(url + "/listProducts/" + userId + "/" + id, function(response) {
            let products = response;
            console.log(products);
            console.log(">>>>>>>>>>>>>>>>>"+userId);
            console.log(">>>>>>>>>>>>>>>>"+id);
            let productTemplateHTML = "";
            for (let i = 0; i < products.length; i++) {
                //if (products[i]['my'] == userId && products[i]['other']== id) {
                    productTemplateHTML += '<a href="http://localhost:8087/trade/' + products[i]['trade_id'] + '">' +
                        '<div id="child_message" class="d-flex justify-content-start mb-4">' +
                        '<div id="child_message" class="msg_cotainer">' +
                        '<img src="https://storage.googleapis.com/reboot-minty-storage/' + products[i]['thumbnail'] + '" alt="Thumbnail" class="rounded-circle user_img">' + '<br>' +
                        products[i]['title'] + '<br>' +
                        products[i]['content'] + '<br>' +
                        products[i]['price'] + '<br>' +
                        '</div>' +
                        '</div>' +
                        '</a>';
                // } else {
                //     productTemplateHTML += '<div id="child_message" class="d-flex justify-content-end mb-4">' +
                //         '<div id="child_message" class="msg_cotainer_send">' +
                //         '<img src="https://storage.cloud.google.com/reboot-minty-storage/' + products[i]['thumbnail'] + '" alt="Thumbnail" class="rounded-circle user_img">' + '<br>' +
                //         products[i]['title'] + '<br>' +
                //         products[i]['content'] + '<br>' +
                //         products[i]['price'] + '<br>' +
                //         '</div>' +
                //         '</div>';
                // }
            }
            $('#formProductsBody').append(productTemplateHTML);
        });

    }

    let dataType = type;

    let submitButton='<div class="input-group-append" id="buttonSend">'+
        '<button class="input-group-text send_btn" onclick="sendMessage(\''+dataType+'\')"><i class="fas fa-location-arrow"></i></button>'+
        '</div>';
    $('#formSubmit').append(submitButton)

    setTimeout(function() {
        scrollToBottom();
    }, 100);
}


function home(){
history.back();

}


// 스크롤을 추가할 대상 요소 선택
let messageContainer = $('#formMessageBody');
let productsContainer = $('#formProductsBody');

// 스크롤을 아래로 이동하는 함수
function scrollToBottom() {
    messageContainer.scrollTop(messageContainer[0].scrollHeight);
    productsContainer.scrollTop(productsContainer[0].scrollHeight);
}

function uploadImage() {
    let imageFile = document.getElementById('imageFile').files[0];
    if (!imageFile) {
        alert("이미지 파일을 선택하세요.");
        return;
    }

    // 이미지를 서버로 업로드하는 Ajax
    let csrfTokenName = document.querySelector('meta[name="_csrf_header"]').content;
    let csrfTokenValue = document.querySelector('meta[name="_csrf"]').content;

    let formData = new FormData();
    formData.append("image", imageFile);

    $.ajax({
        url: "/sendImage",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfTokenName, csrfTokenValue);
        },
        success: function(response) {
            console.log(response);
            sendImageMessage(response, "user");
        },
        error: function(error) {
            console.log(error);
        }
    });
}