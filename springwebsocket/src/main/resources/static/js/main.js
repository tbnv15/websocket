'use strict';

var nameInput = $('#name');
var roomInput = $('#room-id');
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdDisplay = document.querySelector('#room-id-display');

var stompClient = null;
var currentSubscription;
var username = null;
var roomId = null;
var topic = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(document).ready(function() {
  var savedName = Cookies.get('name');
  if (savedName) {
    nameInput.val(savedName);
  }

  var savedRoom = Cookies.get('roomId');
  if (savedRoom) {
    roomInput.val(savedRoom);
  }

  usernamePage.classList.remove('hidden');
  $(".username-submit").click(function(){
      connect();
    });
  //usernameForm.addEventListener('submit', connect, true);
  messageForm.addEventListener('submit', sendMessage, true);
  function connect() {
  console.log('enter into connect');

                $.ajax({
                           url: 'http://localhost:8081/getChatPools',
                           type: "GET",
                           success: function (response) {
                             console.log(response);
                             validateRooms(response);
                           },
                           error: function (xhr, status) {
                             console.log(status);
                           }
                         });
        //console.log("data-->"+ data.room[0].isOccupied);

  }
  function validateRooms(data)
  {
        var flag = "f";
          var roomId =  roomInput.val();
          console.log(roomId);
          for(var i =0 ; i < data.room.length; i++)
          {
            console.log("inside loop");
            if(data.room[i].roomName === roomId && data.room[i].isOccupied === "N")
            {
                flag = "t";
                console.log("Inside if");
                break;
            }
          }

          if(flag === "f")
          {
            console.log("No rooms available");
            roomOccupied();
            return;
          }
          else
          {
          console.log("first connect request")
                              username = nameInput.val().trim();
                              Cookies.set('name', username);
                              if (username) {
                                usernamePage.classList.add('hidden');
                                chatPage.classList.remove('hidden');

                                var socket = new SockJS('/ws');
                                stompClient = Stomp.over(socket);

                                stompClient.connect({}, onConnected, onError);
                              }
                              event.preventDefault();
          }
  }
  function enterRoom(newRoomId) {
    roomId = newRoomId;
    Cookies.set('roomId', roomId);
    roomIdDisplay.textContent = roomId;
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
      currentSubscription.unsubscribe();
    }

      currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);
            stompClient.send(`${topic}/addUser`,
              {},
              JSON.stringify({sender: username, type: 'JOIN'})
            );
  }

  function onConnected() {
    console.log("first join request")
    enterRoom(roomInput.val());
    connectingElement.classList.add('hidden');
  }

  function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
  }

  function roomOccupied() {
      console.log("In Room occupied");
      document.getElementById("err").innerHTML = "Room Already Occupied";
    connectingElement.textContent = 'Room already occupied';
    connectingElement.style.color = 'red';
  }

  function sendMessage(event) {
    console.log("message request")
    var messageContent = messageInput.value.trim();
    if (messageContent.startsWith('/join ')) {
      var newRoomId = messageContent.substring('/join '.length);
      enterRoom(newRoomId);
      while (messageArea.firstChild) {
        messageArea.removeChild(messageArea.firstChild);
      }
    } else if (messageContent && stompClient) {
      var chatMessage = {
        sender: username,
        content: messageInput.value,
        type: 'CHAT'
      };
      stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    }
    messageInput.value = '';
    event.preventDefault();
  }

  function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type == 'JOIN') {
      messageElement.classList.add('event-message');
      message.content = message.sender + ' joined!';
    } else if (message.type == 'LEAVE') {
      messageElement.classList.add('event-message');
      message.content = message.sender + ' left!';
    } else {
      messageElement.classList.add('chat-message');

      var avatarElement = document.createElement('i');
      var avatarText = document.createTextNode(message.sender[0]);
      avatarElement.appendChild(avatarText);
      avatarElement.style['background-color'] = getAvatarColor(message.sender);

      messageElement.appendChild(avatarElement);

      var usernameElement = document.createElement('span');
      var usernameText = document.createTextNode(message.sender);
      usernameElement.appendChild(usernameText);
      messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
  }

  function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
  }
});


