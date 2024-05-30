'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var signupPage = document.querySelector('#signup-page');
var loginPage = document.querySelector('#login-page');
var messageForm = document.querySelector('#messageForm');
var btnLeave = document.querySelector('#button-leave');
var messageInput = document.querySelector('#message');
var connectingElement = document.querySelector('.connecting');
const formSignUp = document.getElementById('signup-form');
const formLogin = document.getElementById('login-form');

var stompClient = null;
var username = null;
var isNewClient = false;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

formLogin.addEventListener('submit', async (event) => {
    event.preventDefault();

    username = formLogin.elements.username.value;
    let password = formLogin.elements.password.value;

    try {
        let response = await fetch('http://localhost:8020/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({username, password})
        });

        if (response.ok) {
            // window.location.href = './js/index2.js';
            openChatPage(username);
            await getMessages();
        } else {
            alert('Login failed');
        }
    } catch (error) {
        console.error(error);
        alert('Login failed');
    }
});

formSignUp.addEventListener('submit', async (event) => {
    event.preventDefault();

    username = formSignUp.elements.username.value;
    let phone = formSignUp.elements.phone.value;
    let password = formSignUp.elements.password.value;

    try {
        let response = await fetch('http://localhost:8020/api/auth/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({username, phone, password})
        });

        if (response.ok) {
            // window.location.href = './js/index2.js';
            openChatPage(username);
            isNewClient = true;
            await getMessages();
        } else {
            alert('Signup failed');
        }
    } catch (error) {
        console.error(error);
        alert('Signup failed');
    }
});

// function connect(event) {
//   username = document.querySelector('#name').value.trim();
//
//   if(username) {
//       signupPage.classList.add('hidden');
//       chatPage.classList.remove('hidden');
//
//       var socket = new SockJS('/ws');
//       stompClient = Stomp.over(socket);
//
//       stompClient.connect({}, onConnected, onError);
//   }
//   event.preventDefault();
// }

function openChatPage(username) {
    if (username) {
        loginPage.classList.add('hidden');
        signupPage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        let socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
}

//Connect to message broker
function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    if (isNewClient){
        stompClient.send("/app/chat.addUser",
            {},
            JSON.stringify({sender: username, type: 'JOIN'})
        )
    }
    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

//Send a new message
function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function leaveRoom() {
    if (stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'LEAVE'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
        stompClient.disconnect();

        loginPage.classList.remove('hidden');
        chatPage.classList.add('hidden');
    }
}

//Get new message from broker
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    handleMessageReceived(message);
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

messageForm.addEventListener('submit', sendMessage, true)
btnLeave.addEventListener('click', leaveRoom)