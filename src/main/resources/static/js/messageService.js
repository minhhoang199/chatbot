var messageArea = document.querySelector('#messageArea');

// Function to retrieve messages from the API
async function getMessages() {
    const response = await fetch('/api/v1/messages/1');
    const messages = await response.json();
    return messages;
}

// Function to display messages in the chat room
function displayMessages(messages) {
    messages.forEach(message => handleMessageReceived(message));
}

// On page load, retrieve messages from the API and display them in the chat room
getMessages().then(messages => {
    displayMessages(messages);
});

function handleMessageReceived(message) {

    let messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
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