const url = "http://localhost:8087/getchatting";                // 소켓 주소
let selectedUserOrGrup="10000000000000000";
let stompClient;
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
                messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-start mb-2">'+
                    '<div id="child_message" class="col-auto chat_message their_chat">'+'<p>'+ data.message +'</p>' +
                    '</div>' +
                    '</div>';
                $('#chat-body').append(messageTemplateHTML);
                scrollToBottom(); // 스크롤을 아래로 이동
                console.log("append success")
            } else {
                newMessages.set(data.fromLogin, data.message);

                // 기존에 있는 '+1' span 요소를 찾습니다.
                var newMessageElem = $('#newMessage_' + data.fromLogin);

                if (newMessageElem.length) {
                    // span 요소가 이미 존재하면, 현재 카운트를 증가시키고 값을 업데이트합니다.
                    var currentCount = parseInt(newMessageElem.text(), 10);
                    newMessageElem.text('+' + (currentCount + 1));
                } else {
                    // span 요소가 없으면, 새로운 요소를 생성하고 '+1' 값을 설정합니다.
                    $('#userNameAppender_' + data.fromLogin).append('<span id="newMessage_' + data.fromLogin + '" style="color: red">+1</span>');
                }

                console.log("kebuat")
                let messageTemplateHTML = "";
                messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
                    '<div class="col-auto chat_message their_chat">'+'<p>'+ data.message +'</p>' +
                    '</div>' +
                    '</div>';
                console.log("append success")
            }

        },{});

        $.get(url + "/fetchAllGroups/"+userName, function (response) {
            console.log(userName);
            let groups = response;
            console.log(groups);
            for (let i = 0; i < groups.length; i++) {
                // console.log(groups[i]['name'])
                stompClient.subscribe("/topic/messages/group/" + groups[i]["address"], function (response) {
                    let data = JSON.parse(response.body);
                    console.log(data);
                    console.log("selectedUserOrGrup = "+selectedUserOrGrup)
                    console.log("data.address = "+data.address)
                    console.log("data.nick_name = "+data.nick_name)
                    console.log("------------------------------------ : masuk get message group")
                    if (selectedUserOrGrup === data.address && groups[i]["user_id"] !== data.fromLogin ) {
                        console.log("selectedUserOrGrup === data.fromLogin")

                        let messageTemplateHTML = "";
                        messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-start mb-2">'+
                            '<div id="child_message" class="col-auto chat_message their_chat">'+'<p>'+ data.message +'</p>' +
                            '<span>'+ data.nick_name +'</span>' +
                            '</div>' +
                            '</div>';
                        $('#chat-body').append(messageTemplateHTML);
                        scrollToBottom(); // 스크롤을 아래로 이동
                        console.log("append success")
                    } else {
                        newMessages.set(data.fromLogin, data.message);

                        var modifiedAddress = data.address.replace(/\s+/g, '-');
                        // 기존에 있는 '+1' span 요소를 찾습니다.
                        var newMessageElemGroup = $('#newMessage_' + modifiedAddress);

                        if (newMessageElemGroup.length) {
                            // span 요소가 이미 존재하면, 현재 카운트를 증가시키고 값을 업데이트합니다.
                            var currentCount = parseInt(newMessageElemGroup.text(), 10);
                            newMessageElemGroup.text('+' + (currentCount + 1));
                        } else {
                            // span 요소가 없으면, 새로운 요소를 생성하고 '+1' 값을 설정합니다.
                            $('#userGroupAppender_' + modifiedAddress).append('<span id="newMessage_' + modifiedAddress + '" style="color: red">+1</span>');

                        }

                        console.log("kebuat")
                        let messageTemplateHTML = "";
                        messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
                            '<div class="col-auto chat_message their_chat">'+'<p>'+ data.message +'</p>' +
                            '</div>' +
                            '</div>';
                        console.log("append success")
                    }
                })
            }


        });


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

    fetchAll()
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

                usersTemplateHTML = usersTemplateHTML +
                    '<a class="list-group-item list-group-item-action-a" id="child_message" onclick="formMessageLauch(\'' + users[i]['my'] + '\', \'' + users[i]['myNickName'] + '\', \'user\', \'' + users[i]['title'] + '\', \'' + users[i]['content'] + '\', \'' + users[i]['price'] + '\', \'' + users[i]['thumbnail'] + '\')" data-userid="' + users[i]['myNickName'] + '" data-type="user" data-thumbnail="' + users[i]['thumbnail'] + '" data-title="' + users[i]['title'] + '" data-content="' + users[i]['content'] + '" data-price="' + users[i]['price'] + '">' +
                    '<img src="https://via.placeholder.com/50" alt="User Image" width="50px" height="50px">'+
                    '<div class="header" id="userNameAppender_' + users[i]['my'] + '">'+
                    '<span>'+users[i]['myNickName']+'</span>'+
                    '</div>'+
                    '</a>';
            } else  {

                usersTemplateHTML = usersTemplateHTML +
                    '<a class="list-group-item list-group-item-action-a" id="child_message" onclick="formMessageLauch(\'' + users[i]['other'] + '\', \'' + users[i]['otherNickName'] + '\', \'user\', \'' + users[i]['title'] + '\', \'' + users[i]['content'] + '\', \'' + users[i]['price'] + '\', \'' + users[i]['thumbnail'] + '\')" data-userid="' + users[i]['otherNickName'] + '" data-type="user" data-thumbnail="' + users[i]['thumbnail'] + '" data-title="' + users[i]['title'] + '" data-content="' + users[i]['content'] + '" data-price="' + users[i]['price'] + '">' +
                    '<img src="https://via.placeholder.com/50" alt="User Image" width="50px" height="50px">'+
                    '<div class="header" id="userNameAppender_' + users[i]['other'] + '">'+
                    '<span>'+users[i]['otherNickName']+'</span>'+
                    '</div>'+
                    '</a>';
            }
        }
        $('#chat-users').html(usersTemplateHTML);
        // 채팅 목록 로딩 후 첫 번째 채팅방 클릭
        $($('.list-group-item.list-group-item-action-a')[0]).click();
    });

    $.get(url + "/fetchAllGroups/"+userId, function (response) {

        let groups = response;
        console.log(groups);
        let groupsTemplateHTML = "";
        for (let i = 0; i < groups.length; i++) {
            var modifiedAddress = groups[i]['address'].replace(/\s+/g, '-');
            console.log(groups[i]['user_id'])
            groupsTemplateHTML = groupsTemplateHTML +
                '<a class="list-group-item list-group-item-action" id="child_message" onclick="formMessageLauch('+groups[i]['user_id']+',\''+groups[i]['address']+'\',\'group\')" data-groupid="'+groups[i]['user_id']+'" data-type="group">'+
                '<img src="https://via.placeholder.com/50" alt="User Image" width="50px" height="50px">'+
                '<div class="user_info" id="userGroupAppender_' + modifiedAddress + '">'+
                // '<span>'+groups[i]['address']+'</span>'+
                '<span>'+"지역 채팅방"+'</span>'+
                '</div>'+
                '</a>';
        }
        $('#groupList').html(groupsTemplateHTML);
        $($('.list-group-item.list-group-item-action-a')[0]).click();

    });

}


function sendMsgUser(from, text) {
    stompClient.send("/app/chat/" + selectedUserOrGrup, {}, JSON.stringify({
        fromLogin: from,
        message: text
    }));
}

function sendMsgGroup(from, text) {
    stompClient.send("/app/chat/group/" + selectedUserOrGrup, {}, JSON.stringify({
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
    } else if (type === "group") {
        sendMsgGroup(userId, imagePath);
    }

    let messageTemplateHTML = "";
    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
        '<div id="child_message" class="col-auto chat_message my_chat">'+'<p>' +
        '<a href="https://storage.googleapis.com/reboot-minty-storage/' + imagePath + '" target="_blank"><img src="https://storage.googleapis.com/reboot-minty-storage/' + imagePath + '" alt="이미지" width="100px" height="100px"/></a>' +
        '</p>' +
        '</div>'+
        '</div>';
    $('#chat-body').append(messageTemplateHTML);
    console.log("append success")

    scrollToBottom();

}
function sendNumberMessage(number,type) {
    var userId = localStorage.getItem("userId");
    console.log("number :"+number)
    console.log("type :"+type)

    if(type==="user"){
        sendMsgUser(userId, number);
    } else if (type === "group"){
        alert("지역채팅방에서는 전화번호 공유가 불가 합니다.");
        return;
    }

    let messageTemplateHTML = "";
    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
        '<div id="child_message" class="col-auto chat_message my_chat">'+ '<p>'+number +'</p>' +
        '</div>' +
        '</div>';
    $('#chat-body').append(messageTemplateHTML);
    console.log("append success")

    document.getElementById("chat-input").value="";

    scrollToBottom();

}

function sendMessage(type) {
    let username = $('#userName').attr("data-id");
    let message=$('#chat-input').val();
    var userId = localStorage.getItem("userId");
    selectedUserOrGrup=username;
    console.log("type :"+type)

    if (!message.trim()) {
        //alert("공백 문자만 있는 메시지는 전송할 수 없습니다.")
        return;
    }

    if(type==="user"){
        sendMsgUser(userId, message);
    }else if(type==="group"){
        sendMsgGroup(userId, message);
    }


    let messageTemplateHTML = "";
    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
        '<div id="child_message" class="col-auto chat_message my_chat">'+ '<p>'+message +'</p>' +
        '</div>' +
        '</div>';
    $('#chat-body').append(messageTemplateHTML);
    console.log("append success")

    document.getElementById("chat-input").value="";

    scrollToBottom();

}


function formMessageLauch(id,name,type,title,content,price,thumbnail){

    document.getElementById("formProductsBody").innerHTML = "";


    let buttonSend= document.getElementById("buttonSend");
    if(buttonSend!==null){
        buttonSend.parentNode.removeChild(buttonSend);
    }

    let imageAndNumber = document.getElementById("imageAndNumber");
    if(imageAndNumber!==null){
        imageAndNumber.parentNode.removeChild(imageAndNumber);
    }


    let nama = $('#formMessageHeader').find('span#userName');
    let nama1 = $('#formMessageHeader').find('span#title-content');

    if (type === "user") {
    nama.html('<a href="http://localhost:8087/usershop/' + id + '"><img src="https://via.placeholder.com/50" alt="Selected User Image" class="rounded-circle me-2"></a>' + name);
    nama1.html('<img src="https://storage.googleapis.com/reboot-minty-storage/' + thumbnail + '" alt="Thumbnail" class="rounded-circle user_img" width="50px" height="50px">'+'<div id="title-price"><span id="title-content-title" class="truncate">' +title+ '</span>' + '<span id="title-content-price">'+ new Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' }).format(price) + '원</span></div>' );
    nama.attr("data-id",id);

    nama1.show();
    document.getElementById("formProductsBody").style.display = 'block';
    document.getElementById("formMessageHeader").style.borderBottom = 'none';


    } else if ((type === "group")) {

        nama.html('<a href="#"><img src="https://via.placeholder.com/50" alt="Selected User Image" class="rounded-circle me-2"></a>' + "지역 채팅방");
        nama.attr("data-id",name);

    nama1.html(name);
    document.getElementById("formProductsBody").style.display = 'none';
    document.getElementById("formMessageHeader").style.borderBottom = '3px solid grey';
    document.getElementById("formMessageHeader").style.paddingBottom = '10px';

    }

    let isNew = document.getElementById("newMessage_" + id) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + id);
        element.parentNode.removeChild(element);

    }

    var modifiedName = name.replace(/\s+/g, '-');
    let isNewGroup = document.getElementById("newMessage_" + modifiedName) !== null;
    if (isNewGroup) {
        let element = document.getElementById("newMessage_" + modifiedName);
        element.parentNode.removeChild(element);

    }




    let username = $('#userName').attr("data-id");
    selectedUserOrGrup=username;
    console.log(username);

    let isHistoryMessage = document.getElementById("chat-body");
    if(isHistoryMessage!== null && isHistoryMessage.hasChildNodes()){
        isHistoryMessage.innerHTML="";

    }



    var userId = localStorage.getItem("userId");
    if(type==="user") {
        $.get(url + "/listmessage/" + userId + "/" + id, function (response) {

            let messages = response;
            let messageTemplateHTML = "";
            for (let i = 0; i < messages.length; i++) {

                let content = messages[i]["message_text"].endsWith("images")
                    ? '<a href="https://storage.googleapis.com/reboot-minty-storage/' + messages[i]["message_text"] + '" target="_blank"><img src="https://storage.googleapis.com/reboot-minty-storage/' + messages[i]["message_text"] + '" alt="이미지" width="100px" height="100px"/></a>'
                    : messages[i]["message_text"];

                if (messages[i]['message_from'] == userId) {
                    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">' +
                        '<div id="child_message" class="col-auto chat_message my_chat">' + '<p>' + content + '</p>' +
                        '</div>' +
                        '</div>';
                } else {
                    messageTemplateHTML = messageTemplateHTML + '<div id="child_message" class="row justify-content-start mb-2">' +
                        '<div id="child_message" class="col-auto chat_message their_chat">' + '<p>' + content + '</p>' +
                        '</div>' +
                        '</div>';
                }


            }
            $('#chat-body').append(messageTemplateHTML);
            scrollToBottom(); // 스크롤을 아래로 이동

        });


        $.get(url + "/listProducts/" + userId + "/" + id, function (response) {
            scrollToBottom();
            let products = response;
            console.log(products);
            console.log(">>>>>>>>>>>>>>>>>" + userId);
            console.log(">>>>>>>>>>>>>>>>" + id);
            let productTemplateHTML = "";
            for (let i = 0; i < products.length; i++) {
                //if (products[i]['my'] == userId && products[i]['other']== id || products[i]['my'] == id && products[i]['other']== userId) {
                productTemplateHTML += '<div class="product-item">' +
                    '<a href="http://localhost:8087/trade/' + products[i]['trade_id'] + '">' +
                    '<img src="https://storage.googleapis.com/reboot-minty-storage/' + products[i]['thumbnail'] + '" alt="Thumbnail" class="product-image">' +
                    '<div class="product-name">' + (products[i]["title"].length > 8 ? products[i]["title"].substring(0, 8) + '...' : products[i]["title"]) + '</div>' +
                    '<div class="product-price">' + new Intl.NumberFormat('ko-KR', {
                        style: 'currency',
                        currency: 'KRW'
                    }).format(products[i]["price"]) + '원</div>' +
                    '</a>' +
                    '</div>';
            }
            $('#formProductsBody').append(productTemplateHTML);
            scrollToBottom(); // 스크롤을 아래로 이동
        });

    }else if(type==="group"){
            $.get(url + "/listmessage/group/"+name, function (response) {
                let messagesGroup = response;
                console.log(messagesGroup);
                let messageGroupTemplateHTML = "";
                for (let i = 0; i < messagesGroup.length; i++) {
                    // console.log(messagesGroup[i]['messages'])
                    let content = messagesGroup[i]["messages"].endsWith("images")
                        ?  '<a href="https://storage.googleapis.com/reboot-minty-storage/' + messagesGroup[i]["messages"] + '" target="_blank"><img src="https://storage.googleapis.com/reboot-minty-storage/' + messagesGroup[i]["messages"] + '" alt="이미지" width="100px" height="100px"/></a>'
                        : messagesGroup[i]["messages"];

                    if(messagesGroup[i]['user_id']==userId){
                        messageGroupTemplateHTML = messageGroupTemplateHTML + '<div id="child_message" class="row justify-content-end mb-2">'+
                            '<div id="child_message" class="col-auto chat_message my_chat">'+ '<p>'+content+'</p>' +
                            '</div>'+
                            '</div>';
                    }else{
                        messageGroupTemplateHTML = messageGroupTemplateHTML + '<div id="child_message" class="row justify-content-start mb-2">'+
                            '<div id="child_message" class="col-auto chat_message their_chat">'+'<p>'+content+'</p>'+
                            '<span>'+messagesGroup[i]['nick_name']+'</span>' +
                            '</div>'+
                            '</div>';
                    }

                }
                $('#chat-body').append(messageGroupTemplateHTML);
                scrollToBottom(); // 스크롤을 아래로 이동

            });

        }

    let dataType = type;

    console.log(dataType);
    let submitButton=
        '<div class="input-group-append" id="buttonSend">'+
        '<button class="btn mint-background text-white" onclick="sendMessage(\''+dataType+'\')">▶</button>'+
        '</div>';
    $('#formSubmit').append(submitButton);

    $('#chat-input').on('keydown', function (e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            // buttonSend 클릭 이벤트를 트리거
            $('#buttonSend button').click();

            $('#chat-input').val(''); // 엔터키를 눌렀을 때 입력 필드 초기화
        }
    });

        let ImageAndNumber =
            '<div class="input-group-prepend" id="imageAndNumber">' +
            '<input type="file" id="imageFile" accept="image/*" onchange="uploadImage(\'' + dataType + '\')" hidden>' +
            '<label for="imageFile" class="input-group-text mint-border mint-background"><i class="fas fa-paperclip" style="color: white;"></i></label>' +
            '<input type="button" id="call-icon" onclick="getNumber(\'' + dataType + '\')" hidden>' +
            '<label for="call-icon" class="input-group-text mint-border mint-background"><i class="fas fa-phone" style="color: white;"></i></label>' +
            '</div>';
        $('#ImageAndNumber').append(ImageAndNumber);

}


function back(){
    history.back();

}


//스크롤을 추가할 대상 요소 선택
let messageContainer = $('#chat-body');

// 스크롤을 아래로 이동하는 함수
function scrollToBottom() {
    messageContainer.scrollTop(messageContainer[0].scrollHeight);
}

function uploadImage(type) {
    console.log(type);
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
            sendImageMessage(response, type);
        },
        error: function(error) {
            console.log(error);
        }
    });
}

function getNumber(type) {
    console.log(type);
    var confirmation = confirm("전화번호를 채팅방에 표시 하시겠습니까?");
    if (confirmation) {

        $.ajax({
            url: "/getchatting/getNumber",
            type: "GET",
            dataType: "text",
            success: function(response) {
                console.log(response);
                sendNumberMessage(response, type);

            },
            error: function() {
                alert("Error occurred while fetching phone number.");
            }
        });

    } else {
        alert("취소하였습니다.");
    }
}